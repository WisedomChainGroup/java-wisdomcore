package org.wisdom.p2p;

import com.google.protobuf.AbstractMessage;
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
import java.util.stream.Collectors;

public class Util {
    private static final long MAX_MESSAGE_SIZE = 4 * (1 << 20) - 1024;

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

    private static <T> void addIfNotEmpty(List<List<T>> lists, List<T> list){
        if (list != null && list.size() > 0){
            lists.add(list);
        }
    }

    private static <T extends AbstractMessage> List<List<T>> split(Iterable<T> msgs) {
        List<T> tmp = new ArrayList<>();
        List<List<T>> divided = new ArrayList<>();

        for (T o : msgs) {
            if (tmp
                    .stream()
                    .map(AbstractMessage::getSerializedSize)
                    .reduce(Integer::sum).orElse(0) + o.getSerializedSize() > MAX_MESSAGE_SIZE
            ) {
                addIfNotEmpty(divided, tmp);
                tmp = new ArrayList<>();
                tmp.add(o);
            } else {
                tmp.add(o);
            }
        }
        addIfNotEmpty(divided, tmp);
        return divided;
    }

    public static List<WisdomOuterClass.Blocks> split(WisdomOuterClass.Blocks msg) {
        List<List<WisdomOuterClass.Block>> blockLists = split(msg.getBlocksList());
        return blockLists.stream().map(blocks -> WisdomOuterClass.Blocks.newBuilder().addAllBlocks(blocks).build())
                .collect(Collectors.toList());
    }

    public static List<WisdomOuterClass.Transactions> split(WisdomOuterClass.Transactions msg) {
        List<List<WisdomOuterClass.Transaction>> transactionLists = split(msg.getTransactionsList());
        return transactionLists.stream().map(transactions -> WisdomOuterClass.Transactions.newBuilder().addAllTransactions(transactions).build())
                .collect(Collectors.toList());
    }

    // List<WisdomOutClass.Message> 16M
    public static WisdomOuterClass.Message buildMessage(Peer self, long nonce, long ttl, AbstractMessage msg) {
        if (msg instanceof WisdomOuterClass.Nothing) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.NOTHING);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Ping) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PING);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Pong) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PONG);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Lookup) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.LOOK_UP);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Peers) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PEERS);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetStatus) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_STATUS);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Status) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.STATUS);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetBlocks) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_BLOCKS);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Blocks) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.BLOCKS);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Proposal) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.PROPOSAL);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Transactions) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.TRANSACTIONS);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetTreeNodes) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_TREE_NODES);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.TreeNodes) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.TREE_NODES);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetMerkleTransactions) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.GET_MERKELE_TRANSACTIONS);
            return sign(self, builder.setBody(msg.toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.MerkleTransactions) {
            WisdomOuterClass.Message.Builder builder = buildMessageBuilder(self, nonce, ttl);
            builder.setCode(WisdomOuterClass.Code.MERKLE_TRANSACTIONS);
            return sign(self, builder.setBody(msg.toByteString())).build();
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
