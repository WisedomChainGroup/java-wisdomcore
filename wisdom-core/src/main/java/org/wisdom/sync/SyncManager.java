package org.wisdom.sync;

import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.p2p.Context;
import org.wisdom.p2p.PeerServer;
import org.wisdom.p2p.Plugin;
import org.wisdom.p2p.WisdomOuterClass;
import org.wisdom.p2p.entity.GetBlockQuery;

import java.util.Arrays;
import java.util.List;

import static org.wisdom.Controller.ConsensusResult.ERROR;

@Component
public class SyncManager implements Plugin {
    private PeerServer server;
    private static final int MAX_BLOCKS_PER_TRANSFER = 256;
    private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    @Autowired
    private WisdomBlockChain bc;


    @Autowired
    private Block genesis;

    @Override
    public void onMessage(Context context, PeerServer server) {

    }

    @Override
    public void onStart(PeerServer server) {
        this.server = server;
    }

    @Scheduled(fixedRate = 15 * 1000)
    public void getStatus(){
        if(server == null){
            return;
        }
        server.broadcast(WisdomOuterClass.GetStatus.newBuilder().build());
    }

    private void onGetBlocks(Context context, PeerServer server){
        WisdomOuterClass.GetBlocks getBlocks = context.getPayload().getGetBlocks();
        GetBlockQuery query = new GetBlockQuery(getBlocks.getStartHeight(), getBlocks.getStopHeight()).clip(MAX_BLOCKS_PER_TRANSFER, getBlocks.getClipDirectionValue() > 0);

        logger.info("get blocks received startListening height = " + query.start + " stop height = " + query.stop);
        List<Block> blocksToSend = bc.getBlocks(query.start, query.stop, MAX_BLOCKS_PER_TRANSFER, getBlocks.getClipDirectionValue() > 0);
        if (blocksToSend != null && blocksToSend.size() > 0) {
            Object resp = WisdomOuterClass.Blocks.newBuilder().build();
            context.response(resp);
        }
    }

    private void onStatus(Context context, PeerServer server){
        WisdomOuterClass.Status status = context.getPayload().getStatus();
        Block best = bc.currentHeader();

        // 拉黑创世区块不相同的节点
        if(!Arrays.equals(genesis.getHash(), status.getGenesisHash().toByteArray())){
            context.block();
            context.exit();
            return;
        }

        if(status.getCurrentHeight() >= best.nHeight
                && !Arrays.equals(
                        status.getBestBlockHash().toByteArray(), best.getHash())
        ){
            long stopHeight = status.getCurrentHeight();
            if(stopHeight >= best.nHeight + MAX_BLOCKS_PER_TRANSFER){
                stopHeight = best.nHeight + MAX_BLOCKS_PER_TRANSFER - 1;
            }
            Object req = WisdomOuterClass.GetBlocks.newBuilder()
                    .setStartHeight(best.nHeight)
                    .setStopHeight(stopHeight)
                    .setClipDirection(WisdomOuterClass.ClipDirection.CLIP_TAIL);
            server.dial(context.getPayload().getRemote(), req);
        }
    }

    private void onGetStatus(Context context, PeerServer server){
        Block best = bc.currentHeader();
        Object resp = WisdomOuterClass.Status.newBuilder()
                .setBestBlockHash(ByteString.copyFrom(best.getHash()))
                .setCurrentHeight(best.nHeight)
                .setGenesisHash(ByteString.copyFrom(genesis.getHash()))
                .build();
        context.response(resp);
    }
}
