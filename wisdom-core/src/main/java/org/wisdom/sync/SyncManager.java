package org.wisdom.sync;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ChainCache;
import org.tdf.common.util.ChainedWrapper;
import org.tdf.common.util.HexBytes;
import org.wisdom.core.Block;
import org.wisdom.core.OrphanBlocksManager;
import org.wisdom.core.PendingBlocksManager;
import org.wisdom.core.event.NewBlockMinedEvent;
import org.wisdom.core.validate.BasicRule;
import org.wisdom.core.validate.CheckPointRule;
import org.wisdom.core.validate.Result;
import org.wisdom.db.BlockWrapper;
import org.wisdom.db.WisdomRepository;
import org.wisdom.p2p.*;
import org.wisdom.p2p.entity.GetBlockQuery;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author sal 1564319846@qq.com
 * wisdom protocol block synchronize manager
 */
@Component
public class SyncManager implements Plugin, ApplicationListener<NewBlockMinedEvent> {
    private PeerServer server;
    private static final int CACHE_SIZE = 64;
    private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    private Cache<HexBytes, Boolean> proposalCache;

    @Value("${p2p.max-blocks-per-transfer}")
    private int maxBlocksPerTransfer;

    @Autowired
    private Block genesis;

    @Autowired
    private PendingBlocksManager pendingBlocksManager;

    @Autowired
    private BasicRule rule;

    @Autowired
    private WisdomRepository repository;

    @Value("${wisdom.consensus.allow-fork}")
    private boolean allowFork;

    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;

    @Value("${wisdom.consensus.rate-limits}")
    private Map<String, Double> rateLimits;

    @Value("${wisdom.consensus.lock-timeout}")
    private long lockTimeOut;

    @Value("${wisdom.consensus.block-write-rate}")
    private long blockWriteRate;

    @Autowired
    private CheckPointRule checkPointRule;

    private Limiters limiters;

    private final TreeSet<Block> queue = new TreeSet<>(Block.FAT_COMPARATOR);

    private Lock blockQueueLock = new ReentrantLock();

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public SyncManager() {
        this.proposalCache = Caffeine
                .newBuilder()
                .maximumSize(CACHE_SIZE).build();
        this.limiters = new Limiters(rateLimits);
    }

    @PostConstruct
    public void init() {
        executorService.scheduleWithFixedDelay(
                this::tryWrite, 0,
                blockWriteRate, TimeUnit.SECONDS);

        executorService.scheduleWithFixedDelay(
                this::getStatus, 0,
                30, TimeUnit.SECONDS
        );
    }


    @SneakyThrows
    @Override
    public void onMessage(Context context, PeerServer server) {
        switch (context.getPayload().getCode()) {
            case GET_STATUS:
                onGetStatus(context, server);
                return;
            case STATUS:
                if (limiters.status() != null && !limiters.status().tryAcquire()) {
                    logger.error("receive status message too frequent");
                    return;
                }
                onStatus(context, server);
                return;
            case GET_BLOCKS:
                if (limiters.getBlocks() != null && !limiters.getBlocks().tryAcquire()) {
                    logger.error("receive get-blocks message too frequent");
                    return;
                }
                onGetBlocks(context, server);
                return;
            case BLOCKS:
                onBlocks(context, server);
                return;
            case PROPOSAL:
                onProposal(context, server);
        }
    }

    @Override
    public void onStart(PeerServer server) {
        this.server = server;
    }

    public void getStatus() {
        if (server == null) {
            return;
        }
        // check checkpoint in db
        Result result = checkPointRule.validateDBCheckPoint();
        if (!result.isSuccess()) {
            logger.info("cannot try to fetch block, reason: " + result.getMessage());
            return;
        }
        List<Peer> ps = server.getPeers();
        if (ps == null || ps.size() == 0) {
            return;
        }
        int index = Math.abs(ThreadLocalRandom.current().nextInt()) % ps.size();

        server.dial(ps.get(index), WisdomOuterClass.GetStatus.newBuilder().build());
    }

    private void onGetBlocks(Context context, PeerServer server) {
        WisdomOuterClass.GetBlocks getBlocks = context.getPayload().getGetBlocks();
        GetBlockQuery query = new GetBlockQuery(getBlocks.getStartHeight(), getBlocks.getStopHeight()).clip(maxBlocksPerTransfer, getBlocks.getClipDirection() == WisdomOuterClass.ClipDirection.CLIP_INITIAL);

        logger.info("get blocks received start height = " + query.start + " stop height = " + query.stop);
        List<Block> blocksToSend = repository.getBlocksBetween(query.start, query.stop, maxBlocksPerTransfer, getBlocks.getClipDirectionValue() > 0);
        if (blocksToSend == null || blocksToSend.size() == 0) {
            return;
        }
        WisdomOuterClass.Blocks resp = WisdomOuterClass.Blocks.newBuilder().addAllBlocks(Utils.encodeBlocks(blocksToSend)).build();
        List<WisdomOuterClass.Blocks> divided = Util.split(resp);
        if (divided.size() == 0) {
            return;
        }
        context.response(divided.get(0));
        divided.subList(1, divided.size()).forEach(o -> server.dial(context.getPayload().getRemote(), o));
    }

    private void onBlocks(Context context, PeerServer server) throws InterruptedException {
        WisdomOuterClass.Blocks blocksMessage = context.getPayload().getBlocks();
        List<Block> blocks = Utils.parseBlocks(blocksMessage.getBlocksList());
        logger.info("blocks received start from " + blocks.get(0).nHeight + " stop at " + blocks.get(blocks.size() - 1).nHeight);
        Block best = repository.getBestBlock();
        blocks.sort(Block.FAT_COMPARATOR);
        if (!blockQueueLock.tryLock(lockTimeOut, TimeUnit.SECONDS))
            return;
        try {
            for (Block block : blocks) {
                if (Math.abs(block.getnHeight() - best.getnHeight()) > maxBlocksPerTransfer)
                    break;
                if (repository.containsBlock(block.getHash()))
                    continue;
                queue.add(block);
            }
        } finally {
            blockQueueLock.unlock();
        }
    }

    private void onProposal(Context context, PeerServer server) throws InterruptedException {
        if (!allowFork) {
            return;
        }
        WisdomOuterClass.Proposal proposal = context.getPayload().getProposal();
        Block block = Utils.parseBlock(proposal.getBlock());
        if (proposalCache
                .asMap()
                .containsKey(HexBytes.fromBytes(block.getHash()))
        ) {
            return;
        }
        proposalCache.put(HexBytes.fromBytes(block.getHash()), true);
        context.relay();
        if (Math.abs(block.nHeight - repository.getBestBlock().getnHeight()) > maxBlocksPerTransfer) {
            return;
        }
        if (repository.containsBlock(block.getHash()))
            return;
        if (!blockQueueLock.tryLock(lockTimeOut, TimeUnit.SECONDS))
            return;
        try {
            queue.add(block);
        } finally {
            blockQueueLock.unlock();
        }
    }

    private void onStatus(Context context, PeerServer server) throws InterruptedException {
        WisdomOuterClass.Status status = context.getPayload().getStatus();
        Block best = repository.getBestBlock();

        // 拉黑创世区块不相同的节点
        if (!Arrays.equals(genesis.getHash(), status.getGenesisHash().toByteArray())) {
            context.block();
            context.exit();
            return;
        }
        if (status.getCurrentHeight() >= best.nHeight
                && !Arrays.equals(
                status.getBestBlockHash().toByteArray(), best.getHash())
        ) {
            long stopHeight = status.getCurrentHeight();
            if (stopHeight >= best.nHeight + maxBlocksPerTransfer) {
                stopHeight = best.nHeight + maxBlocksPerTransfer - 1;
            }
            GetBlockQuery getBlockQuery = new GetBlockQuery(best.nHeight, status.getCurrentHeight());
            getBlockQuery.clip(maxBlocksPerTransfer, false);
            WisdomOuterClass.GetBlocks req = WisdomOuterClass.GetBlocks.newBuilder()
                    .setStartHeight(getBlockQuery.start)
                    .setStopHeight(getBlockQuery.stop)
                    .setClipDirection(WisdomOuterClass.ClipDirection.CLIP_TAIL).build();
            logger.info("require blocks start from " + req.getStartHeight() + " stop at " + req.getStopHeight());
            server.dial(context.getPayload().getRemote(), req);
        }
    }

    private void onGetStatus(Context context, PeerServer server) {
        Block best = repository.getBestBlock();
        WisdomOuterClass.Status resp = WisdomOuterClass.Status.newBuilder()
                .setBestBlockHash(ByteString.copyFrom(best.getHash()))
                .setCurrentHeight(best.nHeight)
                .setGenesisHash(ByteString.copyFrom(genesis.getHash()))
                .build();
        context.response(resp);
    }

    @SneakyThrows
    private void tryWrite() {
        Set<HexBytes> orphans = new HashSet<>();
        Block best = repository.getBestBlock();
        if(!blockQueueLock.tryLock(lockTimeOut, TimeUnit.SECONDS))
            return;
        try {
            while (true) {
                Block b = null;
                try {
                    b = queue.first();
                } catch (NoSuchElementException ignored) {
                }
                if (b == null) return;
                if (Math.abs(best.getnHeight() - b.getnHeight()) > maxBlocksPerTransfer
                ) {
                    queue.remove(b);
                    continue;
                }
                if (repository.containsBlock(b.getHash())) {
                    queue.remove(b);
                    continue;
                }
                if(orphans.contains(HexBytes.fromBytes(b.hashPrevBlock))){
                    orphans.add(HexBytes.fromBytes(b.getHash()));
                    continue;
                }
                Block block = repository.getBlockByHash(b.hashPrevBlock);
                if (block == null) {
                    orphans.add(HexBytes.fromBytes(b.getHash()));
                    continue;
                }
                Result res = rule.validateBlock(b);
                if (!res.isSuccess()) {
                    queue.remove(b);
                    logger.error("invalid block received reason = " + res.getMessage());
                    continue;
                }
                Result resCheckPointRule = checkPointRule.validateBlock(b);
                if (!resCheckPointRule.isSuccess()) {
                    queue.remove(b);
                    logger.error("invalid block received reason = " + resCheckPointRule.getMessage());
                    continue;
                }
                queue.remove(b);
                pendingBlocksManager.addPendingBlock(b);
                repository.writeBlock(b);
            }
        } finally {
            blockQueueLock.unlock();
        }
    }

    @Override
    public void onApplicationEvent(NewBlockMinedEvent event) {
        if (server == null) {
            return;
        }
        proposalCache.put(HexBytes.fromBytes(event.getBlock().getHash()), true);
        server.broadcast(WisdomOuterClass.Proposal.newBuilder().setBlock(Utils.encodeBlock(event.getBlock())).build());
    }
}
