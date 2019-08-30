package org.wisdom.sync;

import com.google.protobuf.ByteString;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.core.*;
import org.wisdom.core.event.NewBlockMinedEvent;
import org.wisdom.core.validate.BasicRule;
import org.wisdom.core.validate.Result;
import org.wisdom.db.StateDB;
import org.wisdom.p2p.*;
import org.wisdom.p2p.entity.GetBlockQuery;
import org.wisdom.service.Impl.CommandServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author sal 1564319846@qq.com
 * wisdom protocol block synchronize manager
 */
@Component
public class SyncManager implements Plugin, ApplicationListener<NewBlockMinedEvent> {
    private PeerServer server;
    private static final int CACHE_SIZE = 64;
    private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    private ConcurrentMap<String, Boolean> proposalCache;

    @Value("${p2p.max-blocks-per-transfer}")
    private int maxBlocksPerTransfer;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private Block genesis;

    @Autowired
    private OrphanBlocksManager orphanBlocksManager;

    @Autowired
    private PendingBlocksManager pendingBlocksManager;

    @Autowired
    private BasicRule rule;

    @Autowired
    private StateDB stateDB;

    @Value("${wisdom.consensus.allow-fork}")
    private boolean allowFork;

    public SyncManager() {
        this.proposalCache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(CACHE_SIZE).build();
    }

    @Override
    public void onMessage(Context context, PeerServer server) {
        switch (context.getPayload().getCode()) {
            case GET_STATUS:
                onGetStatus(context, server);
                return;
            case STATUS:
                onStatus(context, server);
                return;
            case GET_BLOCKS:
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

    @Scheduled(fixedRate = 30 * 1000)
    public void getStatus() {
        if (server == null) {
            return;
        }
        List<Peer> ps = server.getPeers();
        if (ps == null || ps.size() == 0) {
            return;
        }
        int index = Math.abs(ThreadLocalRandom.current().nextInt()) % ps.size();

        server.dial(ps.get(index), WisdomOuterClass.GetStatus.newBuilder().build());
        for (Block b : orphanBlocksManager.getInitials()) {
            long startHeight = b.nHeight - maxBlocksPerTransfer + 1;
            if (startHeight <= 0) {
                startHeight = 1;
            }
            WisdomOuterClass.GetBlocks getBlocks = WisdomOuterClass.GetBlocks.newBuilder()
                    .setClipDirection(WisdomOuterClass.ClipDirection.CLIP_INITIAL)
                    .setStartHeight(startHeight)
                    .setStopHeight(b.nHeight).build();
            logger.info("sync orphans: try to fetch block start from " + getBlocks.getStartHeight() + " stop at " + getBlocks.getStopHeight());
            server.dial(ps.get(index), getBlocks);
        }
    }

    private void onGetBlocks(Context context, PeerServer server) {
        WisdomOuterClass.GetBlocks getBlocks = context.getPayload().getGetBlocks();
        GetBlockQuery query = new GetBlockQuery(getBlocks.getStartHeight(), getBlocks.getStopHeight()).clip(maxBlocksPerTransfer, getBlocks.getClipDirection() == WisdomOuterClass.ClipDirection.CLIP_INITIAL);

        logger.info("get blocks received start height = " + query.start + " stop height = " + query.stop);
        List<Block> blocksToSend;
        if (server.getBootstraps().contains(context.getPayload().getRemote())) {
            blocksToSend = stateDB.getBlocks(query.start, query.stop, maxBlocksPerTransfer, getBlocks.getClipDirectionValue() > 0);
        } else {
            blocksToSend = bc.getBlocks(query.start, query.stop, maxBlocksPerTransfer, getBlocks.getClipDirectionValue() > 0);
        }
        if (blocksToSend != null && blocksToSend.size() > 0) {
            Object resp = WisdomOuterClass.Blocks.newBuilder().addAllBlocks(Utils.encodeBlocks(blocksToSend)).build();
            context.response(resp);
        }
    }

    private void onBlocks(Context context, PeerServer server) {
        WisdomOuterClass.Blocks blocksMessage = context.getPayload().getBlocks();
        receiveBlocks(Utils.parseBlocks(blocksMessage.getBlocksList()));
    }

    private void onProposal(Context context, PeerServer server) {
        if (!allowFork) {
            return;
        }
        WisdomOuterClass.Proposal proposal = context.getPayload().getProposal();
        Block block = Utils.parseBlock(proposal.getBlock());
        if (proposalCache.containsKey(block.getHashHexString())) {
            return;
        }
        proposalCache.put(block.getHashHexString(), true);
        receiveBlocks(Collections.singletonList(block));
        context.relay();
    }

    private void onStatus(Context context, PeerServer server) {
        WisdomOuterClass.Status status = context.getPayload().getStatus();
        Block best = bc.currentHeader();

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
        Block best = bc.currentHeader();
        Object resp = WisdomOuterClass.Status.newBuilder()
                .setBestBlockHash(ByteString.copyFrom(best.getHash()))
                .setCurrentHeight(best.nHeight)
                .setGenesisHash(ByteString.copyFrom(genesis.getHash()))
                .build();
        context.response(resp);
    }

    private synchronized void receiveBlocks(List<Block> blocks) {
        logger.info("blocks received start from " + blocks.get(0).nHeight + " stop at " + blocks.get(blocks.size() - 1).nHeight);
        blocks = blocks.subList(0, maxBlocksPerTransfer > blocks.size() ? blocks.size() : maxBlocksPerTransfer);
        List<Block> validBlocks = new ArrayList<>();
        for (Block b : blocks) {
            if (b == null || b.nHeight == 0) {
                continue;
            }
            Result res = rule.validateBlock(b);
            if (!res.isSuccess()) {
                logger.error("invalid block received reason = " + res.getMessage());
                continue;
            }
            validBlocks.add(b);
        }
        if (validBlocks.size() > 0) {
            BlocksCache blocksWritable = orphanBlocksManager.removeAndCacheOrphans(validBlocks);
            pendingBlocksManager.addPendingBlocks(blocksWritable);
        }
    }

    @Override
    public void onApplicationEvent(NewBlockMinedEvent event) {
        if (server == null) {
            return;
        }
        server.broadcast(WisdomOuterClass.Proposal.newBuilder().setBlock(Utils.encodeBlock(event.getBlock())).build());
    }
}
