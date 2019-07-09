package org.ethereum.mock;

import com.google.protobuf.ByteString;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.consensus.pow.TargetStateFactory;
import org.wisdom.consensus.pow.Miner;
import org.wisdom.protobuf.tcp.ProtocolModel;
import org.wisdom.util.Arrays;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.wisdom.core.*;

import java.util.*;

public class MockNode {
    private WisdomBlockChain bc;
    private Set<MockNode> peers;
    private TargetStateFactory factory;
    private static final int MAX_BLOCKS_IN_TRANSIT_PER_PEER = 500;
    private Miner manager;
    private String name;
    private OrphanBlocksManager orphanBlocksManager;
    private PendingBlocksManager pendingBlocksManager;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void handleMsg(ProtocolModel.ProtocolMessage msg, MockNode sender) {
        switch (msg.getType()) {
            case STATUS:
                handleStatus(msg.getStatusMessage(), sender);
                break;
            case BLOCKS:
                handleBlocks(msg.getBlocksMessage(), sender);
                break;
            case GET_BLOCKS:
                handleGetBlocks(msg.getGetBlocksMessage(), sender);
                break;
        }
    }

    // start height must great than 0
    private void handleStatus(ProtocolModel.StatusMessage msg, MockNode sender) {
        Block header = bc.currentHeader();
        if (header.nHeight > msg.getCurrentHeight()) {
            return;
        }
        if (header.nHeight == msg.getCurrentHeight() && Arrays.areEqual(header.getHash(), msg.getCurrentBlockHash().toByteArray())) {
            return;
        }
        long startHeight = header.nHeight;
        if (startHeight <= 0) {
            startHeight = 1;
        }
        long stopHeight = msg.getCurrentHeight();
        if (stopHeight <= 0) {
            stopHeight = MAX_BLOCKS_IN_TRANSIT_PER_PEER + startHeight - 1;
        }
        if (stopHeight - startHeight + 1 > MAX_BLOCKS_IN_TRANSIT_PER_PEER) {
            stopHeight = startHeight + MAX_BLOCKS_IN_TRANSIT_PER_PEER - 1;
        }
        System.out.println(name + ": " + "status message received, try to fetch blocks start height = " + startHeight + " stop height = " + stopHeight);
        sendGetBlocks(startHeight, stopHeight, false, sender);
    }

    // TODO: orphan blocks pool
    // ignore height 0 block here
    private void handleBlocks(ProtocolModel.BlocksMessage msg, MockNode sender) {
        List<Block> blocks = new ArrayList<>();
        for (ProtocolModel.Block b : msg.getBlocksList()) {
            if (b == null || b.getHeight() == 0) {
                continue;
            }
            Block newBlock = Block.fromProto(b);
            blocks.add(newBlock);
        }
        System.out.println(name + ": receive blocks from " + sender.name + " start from " + blocks.get(0).nHeight + " stop at " + blocks.get(blocks.size() - 1).nHeight);
        BlocksCache cache = new BlocksCache(blocks);
        for (Block init : cache.getInitials()) {
            List<Block> descendantBlocks = new ArrayList<>();
            descendantBlocks.add(init);
            descendantBlocks.addAll(cache.getDescendantBlocks(init));
            if (!bc.hasBlock(init.hashPrevBlock)) {
//                orphanBlocksManager.addBlocks(descendantBlocks);
                System.out.println(name + ": orphans received start from " + init.nHeight + " stop at " + descendantBlocks.get(descendantBlocks.size() - 1).nHeight);
                continue;
            }
            BlocksCache cache1 = new BlocksCache(descendantBlocks);
            for (List<Block> fork : cache1.getAllForks()) {
                // the fork has written into the chain
                if (bc.hasBlock(fork.get(fork.size() - 1).getHash())) {
                    continue;
                }
                System.out.println(name + ": writable chain found start from " + fork.get(0).nHeight + " stop at " + fork.get(fork.size() - 1).nHeight);
//                pendingBlocksManager.addPendingBlocks(fork);
            }
        }

    }

    // 从后往前同步
    private void handleGetBlocks(ProtocolModel.GetBlocksMessage msg, MockNode sender) {
        // cannot locate start block hash
        long startHeight = msg.getStartHeight();
        long stopHeight = msg.getStopHeight();
        boolean clipFromStop = msg.getClipFromStop();

        System.out.println(name + ": get blocks received from " + sender.name + " start height = " + startHeight + " stop height = " + stopHeight);

        if (startHeight <= 0) {
            startHeight = 1;
        }
        if (stopHeight < 0) {
            stopHeight = startHeight + MAX_BLOCKS_IN_TRANSIT_PER_PEER - 1;
        }

        boolean isOverFlow = stopHeight - startHeight + 1 > MAX_BLOCKS_IN_TRANSIT_PER_PEER;

        if (isOverFlow & clipFromStop) {
            startHeight = stopHeight - MAX_BLOCKS_IN_TRANSIT_PER_PEER + 1;
        }

        if (isOverFlow & !clipFromStop) {
            stopHeight = startHeight + MAX_BLOCKS_IN_TRANSIT_PER_PEER - 1;
        }

        List<Block> blocksToSend = bc.getBlocks(startHeight, stopHeight, MAX_BLOCKS_IN_TRANSIT_PER_PEER, clipFromStop);
        if (blocksToSend != null && blocksToSend.size() > 0) {
            sendBlocks(blocksToSend, sender);
            System.out.println(name + ": send blocks to " + sender.name + " blocks start from " + blocksToSend.get(0).nHeight + " stop at " + blocksToSend.get(blocksToSend.size() - 1).nHeight);
        }
    }

    private void sendMessage(ProtocolModel.ProtocolMessage msg, MockNode peer) {
        peer.notify(msg, this);
    }

    @Scheduled(fixedRate = 5000)
    private void broadcastMessage(ProtocolModel.ProtocolMessage msg) {
        for (MockNode peer : peers) {
            sendMessage(msg, peer);
        }
    }

    // send blocks message to a peer
    private void sendBlocks(List<Block> blocks, MockNode peer) {
        ProtocolModel.BlocksMessage.Builder blocksMsgBuilder = ProtocolModel.BlocksMessage.newBuilder();
        blocksMsgBuilder.setCount(blocks.size());
        List<ProtocolModel.Block> protocolBlocks = new ArrayList<>();
        for (Block b : blocks) {
            protocolBlocks.add(b.encode());
        }
        blocksMsgBuilder.addAllBlocks(protocolBlocks);
        ProtocolModel.ProtocolMessage.Builder builder = ProtocolModel.ProtocolMessage.newBuilder();
        builder.setType(ProtocolModel.ProtocolMessage.Type.BLOCKS);
        builder.setBlocksMessage(blocksMsgBuilder.build());
        sendMessage(builder.build(), peer);
    }

    private ProtocolModel.ProtocolMessage buildGetBlocksMessage(long startHeight, long stopHeight, boolean clipFromStop) {
        ProtocolModel.GetBlocksMessage.Builder builder = ProtocolModel.GetBlocksMessage.newBuilder();
        builder.setStartHeight((int) startHeight);
        builder.setStopHeight((int) stopHeight);
        builder.setClipFromStop(clipFromStop);
        ProtocolModel.ProtocolMessage.Builder msgBuilder = ProtocolModel.ProtocolMessage.newBuilder();
        msgBuilder.setType(ProtocolModel.ProtocolMessage.Type.GET_BLOCKS);
        msgBuilder.setGetBlocksMessage(builder.build());
        return msgBuilder.build();
    }

    private void sendGetBlocks(long startHeight, long stopHeight, boolean clipFromStop, MockNode peer) {
        sendMessage(buildGetBlocksMessage(startHeight, stopHeight, clipFromStop), peer);
    }

    // broadcast status message to all peers
    private void broadcastStatus() {
        ProtocolModel.ProtocolMessage.Builder builder = ProtocolModel.ProtocolMessage.newBuilder();
        builder.setType(ProtocolModel.ProtocolMessage.Type.STATUS);
        ProtocolModel.ProtocolMessage statusMessage = buildStatusMessage();
        broadcastMessage(statusMessage);
        System.out.println(name + ": broadcast status to peers current height = " + statusMessage.getStatusMessage().getCurrentHeight() + " best block hash = " + Hex.encodeHexString(statusMessage.getStatusMessage().getCurrentBlockHash().toByteArray()).substring(0, 8));
    }

    // generate status message
    private ProtocolModel.ProtocolMessage buildStatusMessage() {
        ProtocolModel.StatusMessage.Builder builder = ProtocolModel.StatusMessage.newBuilder();
        Block header = bc.currentHeader();
        builder.setCurrentBlockHash(ByteString.copyFrom(header.getHash()));
        builder.setCurrentHeight((int) header.nHeight);
        builder.setGenesisHash(ByteString.copyFrom(bc.getGenesis().getHash()));
        builder.setTotalWeight((int)bc.getCurrentTotalWeight());
        ProtocolModel.ProtocolMessage.Builder builder2 = ProtocolModel.ProtocolMessage.newBuilder();
        builder2.setType(ProtocolModel.ProtocolMessage.Type.STATUS);
        builder2.setStatusMessage(builder.build());
        return builder2.build();
    }


    public void addPeer(MockNode node) {
        peers.add(node);
    }

    public MockNode(Miner minerManager, WisdomBlockChain bc, TargetState genesisState, OrphanBlocksManager orphanBlocksManager, PendingBlocksManager pendingBlocksManager) {
        this.bc = bc;
        this.factory = new TargetStateFactory(bc, genesisState, 20);
        this.manager = minerManager;
        this.pendingBlocksManager = pendingBlocksManager;
        this.orphanBlocksManager = orphanBlocksManager;
        this.peers = new HashSet<>();
    }

    @Async
    public void notify(ProtocolModel.ProtocolMessage msg, MockNode sender) {
        handleMsg(msg, sender);
    }

    @Scheduled(fixedRate = 3000)
    private void syncOrphan() {
        List<ProtocolModel.ProtocolMessage> msgs = new ArrayList<>();
        for (Block b : orphanBlocksManager.getInitials()) {
            long startHeight = b.nHeight - 500 + 1;
            if (startHeight <= 0) {
                startHeight = 1;
            }
            msgs.add(buildGetBlocksMessage(startHeight, b.nHeight, true));
            System.out.println(name + ": try to sync orphans");
        }
        for (ProtocolModel.ProtocolMessage msg : msgs) {
            broadcastMessage(msg);
        }
    }

}
