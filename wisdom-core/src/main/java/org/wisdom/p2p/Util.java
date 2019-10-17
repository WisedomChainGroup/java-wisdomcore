package org.wisdom.p2p;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.Arrays;

import java.nio.charset.StandardCharsets;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static byte[] getRawForSign(WisdomOuterClass.Message msg) {
        return Arrays.concatenate(new byte[][]{
                        BigEndian.encodeUint32(msg.getCode().getNumber()),
                        BigEndian.encodeUint64(msg.getCreatedAt().getSeconds()),
                        msg.getRemotePeer().getBytes(StandardCharsets.UTF_8),
                        BigEndian.encodeUint64(msg.getTtl()),
                        BigEndian.encodeUint64(msg.getNonce()),
                        msg.getBody().toByteArray()
                }
        );
    }

    public static WisdomOuterClass.Message buildMessage(Peer self, long nonce, long ttl, Object msg){
        WisdomOuterClass.Message.Builder builder = WisdomOuterClass.Message.newBuilder();
        builder.setCreatedAt(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build());
        builder.setRemotePeer(self.toString());
        builder.setTtl(ttl);
        builder.setNonce(nonce);
        if (msg instanceof WisdomOuterClass.Nothing) {
            builder.setCode(WisdomOuterClass.Code.NOTHING);
            return sign(self, builder.setBody(((WisdomOuterClass.Nothing) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Ping) {
            builder.setCode(WisdomOuterClass.Code.PING);
            return sign(self, builder.setBody(((WisdomOuterClass.Ping) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Pong) {
            builder.setCode(WisdomOuterClass.Code.PONG);
            return sign(self, builder.setBody(((WisdomOuterClass.Pong) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Lookup) {
            builder.setCode(WisdomOuterClass.Code.LOOK_UP);
            return sign(self, builder.setBody(((WisdomOuterClass.Lookup) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Peers) {
            builder.setCode(WisdomOuterClass.Code.PEERS);
            return sign(self, builder.setBody(((WisdomOuterClass.Peers) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetStatus) {
            builder.setCode(WisdomOuterClass.Code.GET_STATUS);
            return sign(self, builder.setBody(((WisdomOuterClass.GetStatus) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Status) {
            builder.setCode(WisdomOuterClass.Code.STATUS);
            return sign(self, builder.setBody(((WisdomOuterClass.Status) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetBlocks) {
            builder.setCode(WisdomOuterClass.Code.GET_BLOCKS);
            return sign(self, builder.setBody(((WisdomOuterClass.GetBlocks) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Blocks) {
            builder.setCode(WisdomOuterClass.Code.BLOCKS);
            return sign(self, builder.setBody(((WisdomOuterClass.Blocks) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Proposal) {
            builder.setCode(WisdomOuterClass.Code.PROPOSAL);
            return sign(self, builder.setBody(((WisdomOuterClass.Proposal) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Transactions) {
            builder.setCode(WisdomOuterClass.Code.TRANSACTIONS);
            return sign(self, builder.setBody(((WisdomOuterClass.Transactions) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetTreeNodes) {
            builder.setCode(WisdomOuterClass.Code.GET_TREE_NODES);
            return sign(self, builder.setBody(((WisdomOuterClass.GetTreeNodes) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.TreeNodes) {
            builder.setCode(WisdomOuterClass.Code.TREE_NODES);
            return sign(self, builder.setBody(((WisdomOuterClass.TreeNodes) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetMerkleTransactions) {
            builder.setCode(WisdomOuterClass.Code.GET_MERKELE_TRANSACTIONS);
            return sign(self, builder.setBody(((WisdomOuterClass.GetMerkleTransactions) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.MerkleTransactions) {
            builder.setCode(WisdomOuterClass.Code.MERKLE_TRANSACTIONS);
            return sign(self, builder.setBody(((WisdomOuterClass.MerkleTransactions) msg).toByteString())).build();
        }
        logger.error("cannot deduce message type " + msg.getClass().toString());
        builder.setCode(WisdomOuterClass.Code.NOTHING).setBody(WisdomOuterClass.Nothing.newBuilder().build().toByteString());
        return sign(self, builder).build();
    }

    public static WisdomOuterClass.Message.Builder sign(Peer self, WisdomOuterClass.Message.Builder builder) {
        return builder.setSignature(
                ByteString.copyFrom(
                        self.privateKey.sign(Util.getRawForSign(builder.build()))
                )
        );
    }
}
