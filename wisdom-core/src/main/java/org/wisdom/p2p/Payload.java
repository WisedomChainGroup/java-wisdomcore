package org.wisdom.p2p;

import com.google.protobuf.InvalidProtocolBufferException;

public class Payload {
    long createdAt;
    WisdomOuterClass.Code code;
    Peer remote;
    Peer recipient;
    long ttl;
    long nonce;
    byte[] signature;

    private byte[] body;

    private WisdomOuterClass.Message message;

    public WisdomOuterClass.Message getMessage() {
        return message;
    }

    public Payload(WisdomOuterClass.Message msg) throws Exception {
        createdAt = msg.getCreatedAt().getSeconds();
        code = msg.getCode();
        remote = Peer.parse(msg.getRemotePeer());
        recipient = Peer.parse(msg.getRecipient());
        ttl = msg.getTtl();
        nonce = msg.getNonce();
        signature = msg.getSignature().toByteArray();
        body = msg.getBody().toByteArray();
        message = msg;
    }

    public Object getBody() throws InvalidProtocolBufferException {
        switch (code) {
            case PING:
                return getPing();
            case PONG:
                return getPong();
            case LOOK_UP:
                return getLookup();
            case PEERS:
                return getPeers();
            case GET_STATUS:
                return getGetStatus();
            case STATUS:
                return getStatus();
            case GET_BLOCKS:
                return getGetBlocks();
            case BLOCKS:
                return getBlocks();
            case PROPOSAL:
                return getProposal();
            case TRANSACTION:
                return getTransaction();
            default:
                return WisdomOuterClass.Nothing.newBuilder().build();
        }
    }

    public WisdomOuterClass.Ping getPing() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Ping.parseFrom(body);
    }

    public WisdomOuterClass.Pong getPong() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Pong.parseFrom(body);
    }

    public WisdomOuterClass.Lookup getLookup() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Lookup.parseFrom(body);
    }

    public WisdomOuterClass.Peers getPeers() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Peers.parseFrom(body);
    }

    public WisdomOuterClass.GetStatus getGetStatus() throws InvalidProtocolBufferException {
        return WisdomOuterClass.GetStatus.parseFrom(body);
    }

    public WisdomOuterClass.Status getStatus() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Status.parseFrom(body);
    }

    public WisdomOuterClass.GetBlocks getGetBlocks() throws InvalidProtocolBufferException {
        return WisdomOuterClass.GetBlocks.parseFrom(body);
    }

    public WisdomOuterClass.Blocks getBlocks() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Blocks.parseFrom(body);
    }

    public WisdomOuterClass.Proposal getProposal() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Proposal.parseFrom(body);
    }

    public WisdomOuterClass.Transaction getTransaction() throws InvalidProtocolBufferException {
        return WisdomOuterClass.Transaction.parseFrom(body);
    }
}
