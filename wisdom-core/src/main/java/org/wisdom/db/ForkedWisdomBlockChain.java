package org.wisdom.db;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tdf.common.util.*;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.encoding.BigEndian;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class ForkedWisdomBlockChain {
    private ChainCache<BlockWrapper> chainCache;

    // block confirms
    private Map<byte[], Set<byte[]>> confirms = new ByteArrayMap<>();

    // least to confirm
    private Map<byte[], Integer> leastConfirms = new ByteArrayMap<>();

    // the latest confirmed
    @Getter
    private Block latestConfirmed;

    // block hash -> transaction hashes
    private Map<byte[], Set<byte[]>> transactionIndex = new ByteArrayMap<>();

    private Map<byte[], Transaction> transactionCache = new ByteArrayMap<>();

    private WisdomBlockChain bc;

    private Block current;

    private TriesSyncManager triesSyncManager;

    public ForkedWisdomBlockChain(WisdomBlockChain bc, TriesSyncManager triesSyncManager) {
        this.bc = bc;
        chainCache = new ChainCache<>();
        this.chainCache = chainCache
                .withComparator((x, y) -> compareBlock(x.get(), y.get()));
        this.triesSyncManager = triesSyncManager;
    }

    private void clearCurrentCache() {
        this.current = null;
    }

    private void deleteCache(Block b) {
        chainCache.remove(b.getHash());
        confirms.remove(b.getHash());
        leastConfirms.remove(b.getHash());
        transactionIndex.remove(b.getHash());
        b.body.forEach(t -> transactionCache.remove(t.getHash()));
    }

    private int compareBlock(Block a, Block b) {
        if (a.nHeight != b.nHeight) {
            return Long.compare(a.nHeight, b.nHeight);
        }

        if (a.body.get(0).amount != b.body.get(0).amount) {
            return (int) (a.body.get(0).amount - b.body.get(0).amount);
        }
        // pow 更小的占优势
        return -BigEndian.decodeUint256(Block.calculatePOWHash(a))
                .compareTo(
                        BigEndian.decodeUint256(Block.calculatePOWHash(b))
                );
    }

    public Block getGenesis() {
        return bc.getGenesis();
    }

    public Block currentHeader() {
        return currentBlock();
    }

    public Block currentBlock() {
        if (current != null) return current;
        List<BlockWrapper> blockWrappers = chainCache.getLeaves();
        current = blockWrappers.get(blockWrappers.size() - 1).get();
        return current;
    }

    public Block getHeader(byte[] blockHash) {
        return chainCache.get(blockHash)
                .map(ChainedWrapper::get)
                .orElse(bc.getHeader(blockHash));
    }

    public Block getBlock(byte[] blockHash) {
        return chainCache.get(blockHash)
                .map(ChainedWrapper::get)
                .orElse(bc.getBlock(blockHash));
    }

    public Block findAncestorHeader(byte[] hash, long height) {
        Block bHeader = getHeader(hash);
        if (bHeader.nHeight < height) {
            return null;
        }
        while (bHeader != null && bHeader.nHeight != height) {
            bHeader = getHeader(bHeader.hashPrevBlock);
        }
        return bHeader;
    }

    public List<Block> getAncestorBlocks(byte[] bhash, long anum) {
        if (FastByteComparisons.equal(bhash, latestConfirmed.getHash())) {
            return bc.getAncestorBlocks(bhash, anum);
        }
        Optional<Block> o = chainCache.get(bhash).map(ChainedWrapper::get);
        if (!o.isPresent()) return bc.getAncestorBlocks(bhash, anum);
        ChainCache<BlockWrapper> ret = new ChainCache<>();

        List<BlockWrapper> blocks = chainCache.getAncestors(bhash)
                .stream().filter(bl -> bl.get().nHeight >= anum).collect(toList());
        ret.put(blocks);
        bc.getAncestorBlocks(ret.getAll().get(0).get().hashPrevBlock, anum)
                .stream().map(BlockWrapper::new)
                .forEach(ret::put);

        return ret.getAll().stream().map(BlockWrapper::get).collect(toList());
    }

    public boolean hasBlockInCache(byte[] hash) {
        return chainCache.contains(hash);
    }

    public boolean hasBlock(byte[] hash) {
        return chainCache.contains(hash) ||
                FastByteComparisons.equal(latestConfirmed.getHash(), hash) ||
                bc.hasBlock(hash);
    }

    // the block or the ancestor has the transaction
    public boolean hasTransaction(byte[] blockHash, byte[] transactionHash) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return bc.hasTransaction(transactionHash);
        }
        if (!transactionCache.containsKey(transactionHash)) return bc.hasTransaction(transactionHash);
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            throw new RuntimeException("unreachable");
        }
        if (transactionIndex.containsKey(blockHash) && transactionIndex.get(blockHash).contains(transactionHash)) {
            return true;
        }
        return hasTransaction(o.get().hashPrevBlock, transactionHash);
    }

    // get transaction from block or the ancestor
    private Transaction getTransaction(byte[] blockHash, byte[] txHash) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return bc.getTransaction(txHash);
        }
        Set<byte[]> txs = transactionIndex.get(blockHash);
        if (txs == null) throw new RuntimeException("unreachable");
        if (txs.contains(txHash)) return transactionCache.get(txHash);
        byte[] parent = chainCache.get(blockHash)
                .map(BlockWrapper::get)
                .map(b -> b.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException("unreachable"));
        return getTransaction(parent, txHash);
    }

    private boolean hasPayload(byte[] blockHash, int type, byte[] payload) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return bc.hasPayload(type, payload);
        }
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            throw new RuntimeException("unreachable");
        }
        for (Transaction t : o.get().body) {
            if (t.payload != null && t.type == type && FastByteComparisons.equal(t.payload, payload)) {
                return true;
            }
        }
        return hasPayload(o.get().hashPrevBlock, type, payload);
    }

    // 写入区块
    public void writeBlock(Block block) {
        // the block had been confirmed
        if (block.nHeight <= latestConfirmed.nHeight) {
            return;
        }
        // filter orphans
        if (!FastByteComparisons.equal(this.latestConfirmed.getHash(), block.hashPrevBlock)
                && !chainCache.contains(block.hashPrevBlock)) {
            return;
        }
        // had written
        if (chainCache.contains(block.getHash())) {
            return;
        }

        chainCache.put(new BlockWrapper(block));
        triesSyncManager.commit(block);

        // 写入事务索引
        if (!transactionIndex.containsKey(block.getHash())) {
            transactionIndex.put(block.getHash(), new ByteArraySet());
        }

        block.body.forEach(t -> {
            t.height = block.nHeight;
            t.blockHash = block.getHash();
            transactionIndex.get(block.getHash()).add(t.getHash());
            transactionCache.put(t.getHash(), t);
        });

//        leastConfirms.put(block.getHash(),
//                (int) Math.ceil(
//                        proposersFactory.getProposers(getBlock(block.hashPrevBlock)).size()
//                                * 2.0 / 3
//                )
//        );
        List<Block> ancestors =
                chainCache.getAncestors(block.getHash())
                .stream().map(BlockWrapper::get).collect(toList());

        for (Block b : ancestors) {
            if (!confirms.containsKey(b.getHash())) {
                confirms.put(b.getHash(), new ByteArraySet());
            }
            // 区块不能确认自己
            if (Arrays.equals(b.getHash(), block.getHash())) {
                continue;
            }
            confirms.get(b.getHash()).add(block.body.get(0).to);
        }
        Collections.reverse(ancestors);

        // 试图查找被确认的区块，找到则更新到 db
        List<Block> confirmedAncestors = new ArrayList<>();
        for (int i = 0; i < ancestors.size(); i++) {
            Block b = ancestors.get(i);
            if (confirms.get(b.getHash()).size() < leastConfirms.get(b.getHash())) {
                continue;
            }
            if (block.nHeight - b.nHeight < 3) {
                continue;
            }
            // 发现有可以确认的区块
            confirmedAncestors = ancestors.subList(i, ancestors.size());
            Collections.reverse(confirmedAncestors);
            break;
        }

        if (confirmedAncestors.size() == 0) {
            return;
        }

        // 更新到 db
        for (int i = 0; i < confirmedAncestors.size(); ) {
            Block b = confirmedAncestors.get(i);
            // CAS 锁，等待上一个区块状态更新成功
            boolean writeResult = bc.writeBlock(b);
            if (!writeResult) {
                // 数据库 写入失败 重试写入
                log.error("write block to database failed, retrying...");
                continue;
            }
            log.info("write block at height " + b.nHeight + " to db success");
            i++;
        }
    }
}
