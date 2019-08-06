/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.crypto.HashUtil;
import org.wisdom.encoding.BigEndian;
import org.wisdom.genesis.Genesis;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.merkletree.MerkleTree;
import org.wisdom.protobuf.tcp.ProtocolModel;
import org.wisdom.util.Arrays;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

@Component
public class Block {
    public static final int MAX_NOTICE_LENGTH = 32;
    public static final int HASH_SIZE = 32;
    public static final int MAX_BLOCK_SIZE = 8 * (1 << 20);
    private static final Logger logger = LoggerFactory.getLogger(Block.class);

    public static byte[] calculatePOWHash(Block block) {
        byte[] raw = Block.getHeaderRaw(block);
        raw = HashUtil.whirlPool(raw);
        raw = HashUtil.ripemd256(raw);
        raw = HashUtil.blake2b256(raw);
        raw = HashUtil.sha3256(raw);
        raw = HashUtil.keccak256(raw);
        return HashUtil.skein256256(raw);
    }


    public static byte[] getHeaderRaw(Block block) {
        return Arrays.concatenate(
                new byte[][]{
                        BigEndian.encodeUint32(block.nVersion),
                        block.hashPrevBlock,
                        block.hashMerkleRoot,
                        block.hashMerkleState,
                        block.hashMerkleIncubate,
                        BigEndian.encodeUint32(block.nHeight),
                        BigEndian.encodeUint32(block.nTime),
                        block.nBits,
                        block.nNonce
                });
    }

    public static byte[] calculateMerkleRoot(List<Transaction> txs) {
        List<String> hashes = new ArrayList<>();
        for (Transaction tx : txs) {
            hashes.add(tx.getHashHexString());
        }
        try {
            return Hex.decodeHex(new MerkleTree(hashes).getRoot().getHash().toCharArray());
        } catch (Exception e) {
            logger.error("error occured when calculate merkle root");
        }
        return new byte[32];
    }

    public static byte[] calculateMerkleState(List<Account> accounts) {
        List<String> hashes = new ArrayList<>();
        for (Account account : accounts) {
            hashes.add(account.getIdHexString());
        }
        if (hashes.size() > 0) {
            try {
                return Hex.decodeHex(new MerkleTree(hashes).getRoot().getHash().toCharArray());
            } catch (Exception e) {
                logger.error("error occured when calculate merkle state");
            }
        }
        return new byte[32];
    }

    public static byte[] calculateMerkleIncubate(List<Incubator> incubatorList) {
        List<String> hashes = new ArrayList<>();
        for (Incubator incubator : incubatorList) {
            hashes.add(incubator.getIdHexString());
        }
        if (hashes.size() > 0) {
            try {
                return Hex.decodeHex(new MerkleTree(hashes).getRoot().getHash().toCharArray());
            } catch (Exception e) {
                logger.error("error occured when calculate merkle incubate");
            }
        }
        return new byte[32];
    }

    public static Block fromProto(ProtocolModel.Block block) {
        Block b = new Block();
        b.nVersion = block.getVersion();
        if (block.getHashPrevBlock() != null) {
            b.hashPrevBlock = block.getHashPrevBlock().toByteArray();
        }
        if (block.getHashMerkleRoot() != null) {
            b.hashMerkleRoot = block.getHashMerkleRoot().toByteArray();
        }
        if (block.getHashMerkleState() != null) {
            b.hashMerkleState = block.getHashMerkleState().toByteArray();
        }
        if (block.getHashMerkleIncubate() != null) {
            b.hashMerkleIncubate = block.getHashMerkleIncubate().toByteArray();
        }
        b.nHeight = block.getHeight();
        b.nTime = block.getCreatedAt();
        if (block.getNonce() != null) {
            b.nNonce = block.getNonce().toByteArray();
        }
        if (block.getNBits() != null) {
            b.nBits = block.getNBits().toByteArray();
        }
        if (block.getBlockNotice() != null) {
            b.blockNotice = block.getBlockNotice().toByteArray();
        }
        b.body = new ArrayList<>();
        for (ProtocolModel.Transaction tx : block.getBodyList()) {
            b.body.add(Transaction.fromProto(tx));
        }
        return b;
    }

    @JsonProperty("blockSize")
    public int size() {
        int size = getHeaderRaw().length;
        if (body == null) {
            return size;
        }
        for (Transaction tx : body) {
            size += tx.size();
        }
        return size;
    }

    @JsonProperty("blockHash")
    public byte[] getHash() {
        if (hashCache == null) {
            hashCache = HashUtil.keccak256(getHeaderRaw());
        }
        return hashCache;
    }

    // 防止 jackson 解析时报错
    private int blockSize;
    private byte[] blockHash;

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public void setBlockHash(byte[] blockHash) {
        this.blockHash = blockHash;
    }

    // block version 0 ~ 2^32-1
    @Min(0)
    @Max(BigEndian.MAX_UINT_32)
    public long nVersion;

    // parent block hash
    @NotNull
    @Size(max = HASH_SIZE, min = HASH_SIZE)
    public byte[] hashPrevBlock;

    // merkle root of transactions
    @NotNull
    @Size(max = HASH_SIZE, min = HASH_SIZE)
    public byte[] hashMerkleRoot;

    @NotNull
    @Size(max = HASH_SIZE, min = HASH_SIZE)
    public byte[] hashMerkleState;

    @NotNull
    @Size(max = HASH_SIZE, min = HASH_SIZE)
    public byte[] hashMerkleIncubate;

    // 32bit unsigned block number
    @Min(0)
    @Max(BigEndian.MAX_UINT_32)
    public long nHeight;

    // 32bit unsigned unix epoch
    @Min(0)
    @Max(BigEndian.MAX_UINT_32)
    public long nTime;

    @NotNull
    @Size(max = HASH_SIZE, min = HASH_SIZE)
    public byte[] nBits;

    // random value from proposer 256bit, next block's seed
    @NotNull
    @Size(max = HASH_SIZE, min = HASH_SIZE)
    public byte[] nNonce;

    @Size(max = MAX_NOTICE_LENGTH)
    public byte[] blockNotice;

    public List<Transaction> body;

    @JsonIgnore
    public long totalWeight;

    @JsonIgnore
    public long weight;

    @JsonIgnore
    private byte[] hashCache;

    public void setHashCache(byte[] hashCache) {
        this.hashCache = hashCache;
    }

    @JsonIgnore
    private String hashHexCache;

    @JsonIgnore
    public byte[] getHeaderRaw() {
        return getHeaderRaw(this);
    }

    @JsonIgnore
    public String getHashHexString() {
        if (hashHexCache == null) {
            hashHexCache = Hex.encodeHexString(getHash());
        }
        return hashHexCache;
    }

    public long getnHeight() {
        return nHeight;
    }

    public Block() {
    }

    @Autowired
    public Block(Genesis genesis) throws Exception {
        body = new ArrayList<>();

        // init emtpy hash
        byte[] emptyHash = new byte[32];
        hashPrevBlock = emptyHash;
        nNonce = emptyHash;
        blockNotice = genesis.extraData.getBytes();
        nTime = genesis.timestamp;
        // init initial pow target
        nBits = Hex.decodeHex(genesis.nBits.toCharArray());

        // add coin base
        for (Genesis.InitAmount el : genesis.alloc.initAmount) {
            Transaction tx = Transaction.createEmpty();
            tx.to = KeystoreAction.addressToPubkeyHash(el.address);
            tx.amount = el.balance.multiply(BigDecimal.valueOf(EconomicModel.WDC)).longValue();
            tx.nonce = 1;
            body.add(tx);
        }
        Map<String, Integer> noncemap = new HashMap<>();
        // add incubate apply
        for (Genesis.UserIncubateAmount uia : genesis.alloc.userIncubateAmount) {
            int nonce;
            if (noncemap.containsKey(uia.address)) {
                nonce = noncemap.get(uia.address);
                nonce++;
            } else {
                nonce = 1;
            }
            noncemap.put(uia.address, nonce);
            Transaction tx = Transaction.fromIncubateAmount(uia, nonce);
            body.add(tx);
        }
        hashMerkleRoot = calculateMerkleRoot(body);
        hashMerkleState = emptyHash;
        hashMerkleIncubate = emptyHash;
    }

    public ProtocolModel.Block encode() {
        ProtocolModel.Block.Builder blockBuilder = ProtocolModel.Block.newBuilder();
        blockBuilder.setVersion((int) nVersion);
        blockBuilder.setHashPrevBlock(ByteString.copyFrom(hashPrevBlock));
        blockBuilder.setHashMerkleRoot(ByteString.copyFrom(hashMerkleRoot));
        blockBuilder.setHashMerkleState(ByteString.copyFrom(hashMerkleState));
        blockBuilder.setHashMerkleIncubate(ByteString.copyFrom(hashMerkleIncubate));
        blockBuilder.setHeight((int) nHeight);
        blockBuilder.setCreatedAt((int) nTime);
        blockBuilder.setNBits(ByteString.copyFrom(nBits));
        blockBuilder.setNonce(ByteString.copyFrom(nNonce));
        if (blockNotice != null) {
            blockBuilder.setBlockNotice(ByteString.copyFrom(blockNotice));
        }
        if (body == null) {
            return blockBuilder.build();
        }
        for (Transaction tx : body) {
            blockBuilder.addBody(tx.encode());
        }
        return blockBuilder.build();
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public Block toHeader(){
        Block h = new Block();
        h.nVersion = nVersion;
        h.hashPrevBlock = hashPrevBlock;
        h.hashMerkleRoot = hashMerkleRoot;
        h.hashMerkleState = hashMerkleState;
        h.hashMerkleIncubate = hashMerkleIncubate;
        h.nHeight = nHeight;
        h.nTime = nTime;
        h.nBits = nBits;
        h.nNonce = nNonce;
        h.blockNotice = blockNotice;
        return h;
    }
}