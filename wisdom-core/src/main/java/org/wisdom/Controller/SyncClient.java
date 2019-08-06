package org.wisdom.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.consensus.pow.ConsensusConfig;
import org.wisdom.core.*;
import org.wisdom.core.validate.BasicRule;
import org.wisdom.core.validate.Result;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.p2p.entity.GetBlockQuery;
import org.wisdom.p2p.entity.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@ConditionalOnProperty(name = "p2p.mode", havingValue = "rest")
@Component
public class SyncClient {
    private static final int MAX_BLOCKS_IN_TRANSIT_PER_PEER = 50;
    private static final Logger logger = LoggerFactory.getLogger(SyncClient.class);

    private AtomicInteger counter = new AtomicInteger();

    @Value("${wisdom.consensus.enable-mining}")
    boolean enableMining;

    @Autowired
    private RPCClient rpcClient;

    @Autowired
    private JSONEncodeDecoder codec;

    @Autowired
    private ConsensusConfig consensusConfig;

    @Autowired
    private BasicRule rule;

    @Autowired
    private OrphanBlocksManager orphanBlocksManager;

    @Autowired
    private PendingBlocksManager pendingBlocksManager;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private Block genesis;

    @Async
    public void getBlocks(long start, long stop, boolean clipFromStop) {
        Map<String, String> params = new HashMap<>();
        params.put("start", Long.toString(start));
        params.put("stop", Long.toString(stop));
        params.put("clipFromStop", Boolean.toString(clipFromStop));
        for (String hostPort : consensusConfig.getPeers()) {
            rpcClient.get("http://" + hostPort + "/consensus/blocks", params, (byte[] body) -> {
                List<Block> blocks = codec.decodeBlocks(body);
                if (blocks == null) {
                    logger.error("get blocks from " + hostPort + " failed, consider correct your boot nodes");
                    return null;
                }
                receiveBlocks(blocks);
                return null;
            });
        }
    }

    @Async
    public Object receiveBlocks(List<Block> blocks) {
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
            logger.info("receive block = " + new String(codec.encode(b)));
            validBlocks.add(b);
        }
        if (validBlocks.size() > 0) {
            logger.info("receive blocks startListening from " + validBlocks.get(0).nHeight + " stop at " + validBlocks.get(validBlocks.size() - 1).nHeight);
            BlocksCache blocksWritable = orphanBlocksManager.removeAndCacheOrphans(validBlocks);
            pendingBlocksManager.addPendingBlocks(blocksWritable);
        }
        return null;
    }

    @Scheduled(fixedRate = 15 * 1000)
    public void sendStatus() {
        if (!enableMining) {
            return;
        }
        ConsensuEntity.Status status = new ConsensuEntity.Status();
        Block best = bc.currentHeader();
        status.version = best.nVersion;
        status.currentHeight = best.nHeight;
        status.bestBlockHash = best.getHash();
        status.genesisHash = genesis.getHash();
        rpcClient.broadcast("/consensus/status", codec.encode(status));
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void getStatus() {
        if (enableMining) {
            return;
        }
        String hostPort = consensusConfig.getPeers().get(counter.incrementAndGet() % consensusConfig.getPeers().size());
        rpcClient.get("http://" + hostPort + "/consensus/status", new HashMap<>(), (byte[] resp) -> {
            Status status = codec.decode(resp, Status.class);
            if (status == null) {
                logger.error("invalid status received " + new String(resp));
                return null;
            }
            Block header = bc.currentHeader();
            if (status.currentHeight <= header.nHeight) {
                return null;
            }
            // clip interval
            GetBlockQuery query = new GetBlockQuery(header.nHeight, status.currentHeight)
                    .clip(ConsensusController.MAX_BLOCKS_IN_TRANSIT_PER_PEER, false);
            getBlocks(query.start, query.stop, false);
            return null;
        });
    }

    @Async
    public void proposalBlock(Block block) {
        rpcClient.broadcast("/consensus/blocks", codec.encodeBlock(block));
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void syncOrphan() {
        for (Block b : orphanBlocksManager.getInitials()) {
            logger.info("try to sync orphans");
            long startHeight = b.nHeight - MAX_BLOCKS_IN_TRANSIT_PER_PEER + 1;
            if (startHeight <= 0) {
                startHeight = 1;
            }
            getBlocks(startHeight, b.nHeight, true);
        }
    }

}
