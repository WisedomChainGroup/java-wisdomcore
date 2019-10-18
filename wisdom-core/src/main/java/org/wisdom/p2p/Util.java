package org.wisdom.p2p;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.Arrays;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static WisdomOuterClass.Message.Builder buildMessageBuilder(Peer self, long nonce, long ttl) {
        WisdomOuterClass.Message.Builder builder = WisdomOuterClass.Message.newBuilder();
        builder.setCreatedAt(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build());
        builder.setRemotePeer(self.toString());
        builder.setTtl(ttl);
        builder.setNonce(nonce);
        return builder;
    }

    public static List<Object> split(Object msg){
        List<Object> messages = new ArrayList<>();
        if (msg instanceof WisdomOuterClass.Transactions) {
            long size = 0;
            WisdomOuterClass.Transactions.Builder transactionBuilder = WisdomOuterClass.Transactions.newBuilder();
            for (WisdomOuterClass.Transaction t : ((WisdomOuterClass.Transactions) msg).getTransactionsList()) {
                size += t.getSerializedSize();
                if (size > 8 * (1 << 20)) {
                    size = 0;
                    messages.add(transactionBuilder.build());
                    transactionBuilder.clear();
                } else {
                    transactionBuilder.addTransactions(t);
                }
            }
            return messages;
        }
        if (msg instanceof WisdomOuterClass.Blocks) {
            long size = 0;
            WisdomOuterClass.Blocks.Builder blocksBuilder = WisdomOuterClass.Blocks.newBuilder();
            for (WisdomOuterClass.Block b : ((WisdomOuterClass.Blocks) msg).getBlocksList()) {
                size += b.getSerializedSize();
                if (size > 8 * (1 << 20)) {
                    messages.add(blocksBuilder.build());
                    size = 0;
                    blocksBuilder.clear();
                } else {
                    blocksBuilder.addBlocks(b);
                }
            }
            return messages;
        }
        return Collections.singletonList(msg);
    }

    // List<WisdomOutClass.Message> 16M
    public static WisdomOuterClass.Message buildMessage(Peer self, long nonce, long ttl, Object msg) {
        if (msg instanceof WisdomOuterClass.Nothing) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.NOTHING);
            return sign(self, builder.setBody(((WisdomOuterClass.Nothing) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Ping) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PING);
            return sign(self, builder.setBody(((WisdomOuterClass.Ping) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Pong) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PONG);
            return sign(self, builder.setBody(((WisdomOuterClass.Pong) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Lookup) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.LOOK_UP);
            return sign(self, builder.setBody(((WisdomOuterClass.Lookup) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Peers) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PEERS);
            return sign(self, builder.setBody(((WisdomOuterClass.Peers) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetStatus) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_STATUS);
            return sign(self, builder.setBody(((WisdomOuterClass.GetStatus) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Status) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.STATUS);
            return sign(self, builder.setBody(((WisdomOuterClass.Status) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetBlocks) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_BLOCKS);
            return sign(self, builder.setBody(((WisdomOuterClass.GetBlocks) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Blocks) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.BLOCKS);
            return sign(self, builder.setBody(((WisdomOuterClass.Blocks) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Proposal) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PROPOSAL);
            return sign(self, builder.setBody(((WisdomOuterClass.Proposal) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Transactions) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.TRANSACTIONS);
            return sign(self, builder.setBody(((WisdomOuterClass.Transactions) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetTreeNodes) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_TREE_NODES);
            return sign(self, builder.setBody(((WisdomOuterClass.GetTreeNodes) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.TreeNodes) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.TREE_NODES);
            return sign(self, builder.setBody(((WisdomOuterClass.TreeNodes) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetMerkleTransactions) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_MERKELE_TRANSACTIONS);
            return sign(self, builder.setBody(((WisdomOuterClass.GetMerkleTransactions) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.MerkleTransactions) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.MERKLE_TRANSACTIONS);
            return sign(self, builder.setBody(((WisdomOuterClass.MerkleTransactions) msg).toByteString())).build();
        }
        logger.error("cannot deduce message type " + msg.getClass().toString());
        WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
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
