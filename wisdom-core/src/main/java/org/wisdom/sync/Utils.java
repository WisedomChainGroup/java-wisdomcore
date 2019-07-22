package org.wisdom.sync;

import com.google.protobuf.ByteString;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.p2p.WisdomOuterClass;

import java.util.ArrayList;
import java.util.List;

// proto buf utils
public class Utils {
    public static Transaction parseTransaction(WisdomOuterClass.Transaction tx) {
        Transaction t = new Transaction();
        t.version = tx.getVersion();
        t.type = tx.getTransactionTypeValue();
        t.nonce = tx.getNonce();
        t.from = tx.getFrom().toByteArray();
        t.gasPrice = tx.getGasPrice();
        t.amount = tx.getAmount();
        t.payload = tx.getPayload().toByteArray();
        t.to = tx.getTo().toByteArray();
        t.signature = tx.getSignature().toByteArray();
        return t;
    }

    public static List<Block> parseBlocks(List<WisdomOuterClass.Block> bks){
        List<Block> res = new ArrayList<>();
        for(WisdomOuterClass.Block bk: bks){
            res.add(parseBlock(bk));
        }
        return res;
    }

    public static Block parseBlock(WisdomOuterClass.Block bk) {
        Block b = new Block();
        b.nVersion = bk.getVersion();
        b.hashPrevBlock = bk.getHashPrevBlock().toByteArray();
        b.hashMerkleRoot = bk.getHashMerkleRoot().toByteArray();
        b.hashMerkleState = bk.getHashMerkleState().toByteArray();
        b.hashMerkleIncubate = bk.getHashMerkleIncubate().toByteArray();
        b.nHeight = bk.getHeight();
        b.nTime = bk.getCreatedAt();
        b.nBits = bk.getNBits().toByteArray();
        b.nNonce = bk.getNonce().toByteArray();
        b.body = new ArrayList<>();
        if (bk.getBodyList() == null || bk.getBodyList().size() == 0) {
            return b;
        }
        for (WisdomOuterClass.Transaction tx : bk.getBodyList()) {
            b.body.add(parseTransaction(tx));
        }
        return b;
    }

    // avoid null check
    private static byte[] getBytes(byte[] in) {
        if (in == null) {
            return new byte[]{};
        }
        return in;
    }


    public static WisdomOuterClass.Transaction encodeTransaction(Transaction tx) {
        return WisdomOuterClass.Transaction.newBuilder()
                .setVersion(tx.version)
                .setTransactionType(WisdomOuterClass.TransactionType.forNumber(tx.type))
                .setNonce(tx.nonce)
                .setFrom(ByteString.copyFrom(tx.from))
                .setGasPrice(tx.gasPrice)
                .setAmount(tx.amount)
                .setSignature(ByteString.copyFrom(getBytes(tx.signature)))
                .setTo(ByteString.copyFrom(tx.to))
                .setPayload(ByteString.copyFrom(getBytes(tx.payload))).build();
    }

    public static WisdomOuterClass.Block encodeBlock(Block block) {
        WisdomOuterClass.Block.Builder bd = WisdomOuterClass.Block.newBuilder()
                .setVersion((int) block.nVersion)
                .setHashPrevBlock(ByteString.copyFrom(getBytes(block.hashPrevBlock)))
                .setHashMerkleRoot(ByteString.copyFrom(getBytes(block.hashMerkleRoot)))
                .setHashPrevBlock(ByteString.copyFrom(getBytes(block.hashPrevBlock)))
                .setHashMerkleState(ByteString.copyFrom(getBytes(block.hashMerkleState)))
                .setHashMerkleIncubate(ByteString.copyFrom(getBytes(block.hashMerkleIncubate)))
                .setHeight((int) block.nHeight)
                .setCreatedAt((int) block.nTime)
                .setNBits(ByteString.copyFrom(getBytes(block.nBits)))
                .setNonce(ByteString.copyFrom(getBytes(block.nNonce)));

        if (block.body == null || block.body.size() == 0) {
            return bd.build();
        }
        for (Transaction tx : block.body) {
            bd.addBody(encodeTransaction(tx));
        }
        return bd.build();
    }

    public static List<WisdomOuterClass.Block> encodeBlocks(List<Block> bks) {
        List<WisdomOuterClass.Block> res = new ArrayList<>();
        for (Block b : bks) {
            res.add(encodeBlock(b));
        }
        return res;
    }
}
