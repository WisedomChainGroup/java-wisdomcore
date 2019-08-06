package org.wisdom.sync;

import com.google.protobuf.ByteString;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.core.*;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.event.NewBlockMinedEvent;
import org.wisdom.core.validate.BasicRule;
import org.wisdom.core.validate.Result;
import org.wisdom.p2p.Context;
import org.wisdom.p2p.PeerServer;
import org.wisdom.p2p.Plugin;
import org.wisdom.p2p.WisdomOuterClass;
import org.wisdom.p2p.entity.GetBlockQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


/**
 * @author sal 1564319846@qq.com
 * wisdom protocol block synchronize manager
 */
@Component
public class SyncManager implements Plugin, ApplicationListener<NewBlockMinedEvent> {
    private PeerServer server;
    private static final int MAX_BLOCKS_PER_TRANSFER = 256;
    private static final int CACHE_SIZE = 64;
    private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    private ConcurrentMap<String, Boolean> proposalCache;

    private ConcurrentMap<String, Boolean> transactionCache;

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

    public SyncManager() {
        this.proposalCache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(CACHE_SIZE).build();
        this.transactionCache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(CACHE_SIZE).build();
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
                return;
            case TRANSACTION:
                onTransaction(context, server);
        }
    }

    @Override
    public void onStart(PeerServer server) {
        this.server = server;
    }

    @Scheduled(fixedRate = 15 * 1000)
    public void getStatus() {
        if (server == null) {
            return;
        }
        server.broadcast(WisdomOuterClass.GetStatus.newBuilder().build());
        for (Block b : orphanBlocksManager.getInitials()) {
            long startHeight = b.nHeight - MAX_BLOCKS_PER_TRANSFER + 1;
            if (startHeight <= 0) {
                startHeight = 1;
            }
            WisdomOuterClass.GetBlocks getBlocks = WisdomOuterClass.GetBlocks.newBuilder()
                    .setClipDirection(WisdomOuterClass.ClipDirection.CLIP_INITIAL)
                    .setStartHeight(startHeight)
                    .setStopHeight(b.nHeight).build();
            logger.info("sync orphans: try to fetch block start from " + getBlocks.getStartHeight() + " stop at " + getBlocks.getStopHeight());
            server.broadcast(getBlocks);
        }
    }

    private void onGetBlocks(Context context, PeerServer server) {
        WisdomOuterClass.GetBlocks getBlocks = context.getPayload().getGetBlocks();
        GetBlockQuery query = new GetBlockQuery(getBlocks.getStartHeight(), getBlocks.getStopHeight()).clip(MAX_BLOCKS_PER_TRANSFER, getBlocks.getClipDirectionValue() > 0);

        logger.info("get blocks received start height = " + query.start + " stop height = " + query.stop);
        List<Block> blocksToSend = bc.getBlocks(query.start, query.stop, MAX_BLOCKS_PER_TRANSFER, getBlocks.getClipDirectionValue() > 0);
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
        WisdomOuterClass.Proposal proposal = context.getPayload().getProposal();
        Block block = Utils.parseBlock(proposal.getBlock());
        if (proposalCache.containsKey(block.getHashHexString())) {
            return;
        }
        proposalCache.put(block.getHashHexString(), true);
        receiveBlocks(Collections.singletonList(block));
        context.relay();
    }

    private void onTransaction(Context context, PeerServer server) {
        WisdomOuterClass.Transaction tx = context.getPayload().getTransaction();
        Transaction t = Utils.parseTransaction(tx);
        if (transactionCache.containsKey(t.getHashHexString())) {
            return;
        }
        transactionCache.put(t.getHashHexString(), true);
        // TODO: 收到广播后的事务要进行处理
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
            if (stopHeight >= best.nHeight + MAX_BLOCKS_PER_TRANSFER) {
                stopHeight = best.nHeight + MAX_BLOCKS_PER_TRANSFER - 1;
            }
            WisdomOuterClass.GetBlocks req = WisdomOuterClass.GetBlocks.newBuilder()
                    .setStartHeight(best.nHeight)
                    .setStopHeight(stopHeight)
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

    private void receiveBlocks(List<Block> blocks) {
        logger.info("blocks received start from " + blocks.get(0).nHeight + " stop at " + blocks.get(blocks.size() - 1).nHeight);
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

    // TODO: 增加分叉容错后要开启该功能
    @Override
    public void onApplicationEvent(NewBlockMinedEvent event) {
//        server.broadcast(WisdomOuterClass.Proposal.newBuilder().setBlock(Utils.encodeBlock(event.getBlock())).build());
    }
}