package org.wisdom.db;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.tdf.common.util.*;
import org.wisdom.consensus.pow.Proposer;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.encoding.BigEndian;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
public class WisdomRepositoryImpl implements WisdomRepository {
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

    private TriesSyncManager triesSyncManager;

    private AccountStateTrie accountStateTrie;

    private ValidatorStateTrie validatorStateTrie;

    private TargetCache targetCache;

    private CandidateStateTrie candidateStateTrie;

    private AssetCodeTrie assetCodeTrie;

    private EraLinker eraLinker;

    public WisdomRepositoryImpl(
            WisdomBlockChain bc,
            TriesSyncManager triesSyncManager,
            AccountStateTrie accountStateTrie,
            ValidatorStateTrie validatorStateTrie,
            CandidateStateTrie candidateStateTrie,
            AssetCodeTrie assetCodeTrie,
            TargetCache targetCache,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) throws Exception {
        this.eraLinker = new EraLinker(blocksPerEra);
        this.eraLinker.setRepository(this);
        this.bc = bc;
        this.targetCache = targetCache;
        this.targetCache.setRepository(this);
        chainCache = new ChainCache<>(Integer.MAX_VALUE, this::compareBlockWrapper);
        this.accountStateTrie = accountStateTrie;
        this.validatorStateTrie = validatorStateTrie;
        this.assetCodeTrie = assetCodeTrie;
        this.triesSyncManager = triesSyncManager;
        this.triesSyncManager.setRepository(this);
        this.triesSyncManager.sync();
        this.candidateStateTrie = candidateStateTrie;
        this.candidateStateTrie.setRepository(this);
    }

    private void deleteCache(Block b) {
        chainCache.remove(new BlockWrapper(b));
        confirms.remove(b.getHash());
        leastConfirms.remove(b.getHash());
        transactionIndex.remove(b.getHash());
        b.body.forEach(t -> transactionCache.remove(t.getHash()));
    }

    private int compareBlockWrapper(BlockWrapper a, BlockWrapper b) {
        return compareBlock(a.get(), b.get());
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

    @Override
    public Block getGenesis() {
        return bc.getGenesis();
    }

    @Override
    public Block getBestBlock() {
        return chainCache.isEmpty() ?
                latestConfirmed :
                chainCache.last().get();
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

    @Override
    public long getLatestEra() {
        Block best = getBestBlock();
        if (best.nHeight % eraLinker.getBlocksPerEra() == 0) {
            return eraLinker.getEraAtBlockNumber(best.nHeight) + 1;
        } else {
            return eraLinker.getEraAtBlockNumber(best.nHeight);
        }
    }

    // 获取包含未确认的区块
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        if (sizeLimit == 0 || startHeight > stopHeight) {
            return Collections.emptyList();
        }

        ChainCache<BlockWrapper> c = new ChainCache<>(
                Integer.MAX_VALUE,
                this::compareBlockWrapper
        );

        // 从数据库获取一部分
        if (startHeight < latestConfirmed.nHeight) {
            c.addAll(
                    bc.getBlocks(startHeight, stopHeight, sizeLimit, clipInitial)
                            .stream().map(BlockWrapper::new)
                            .collect(toList())
            );
        }

        // 从 forkdb 获取一部分
        chainCache.stream().filter((b) -> b.get().nHeight >= startHeight
                && b.get().nHeight <= stopHeight)
                .forEach(c::add);

        // 按需进行裁剪
        List<Block> all = c.stream()
                .map(BlockWrapper::get)
                .collect(toList());

        if (sizeLimit > all.size() || sizeLimit < 0) {
            sizeLimit = all.size();
        }
        if (clipInitial) {
            return all.subList(all.size() - sizeLimit, all.size());
        }
        return all.subList(0, sizeLimit);
    }

    public Block getAncestorHeader(byte[] hash, long height) {
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
        ChainCache<BlockWrapper> ret =
                new ChainCache<>(Integer.MAX_VALUE, this::compareBlockWrapper);

        List<BlockWrapper> blocks = chainCache.getAncestors(bhash)
                .stream().filter(bl -> bl.get().nHeight >= anum).collect(toList());
        ret.addAll(blocks);
        bc.getAncestorBlocks(ret.first().get().hashPrevBlock, anum)
                .stream().map(BlockWrapper::new)
                .forEach(ret::add);

        return ret.stream().map(BlockWrapper::get).collect(toList());
    }

    public boolean isStaged(byte[] hash) {
        return chainCache.contains(new BlockWrapper(hash));
    }

    @Override
    public List<Block> getStaged() {
        return chainCache.stream()
                .map(BlockWrapper::get)
                .collect(toList());
    }

    public boolean isConfirmed(byte[] hash) {
        return !chainCache.containsHash(hash) && (
                FastByteComparisons.equal(latestConfirmed.getHash(), hash) ||
                        bc.hasBlock(hash)
        );
    }

    @Override
    public byte[] getTargetByParent(Block parent) {
        return targetCache.getTargetByParent(parent);
    }

    // the block or the ancestor has the transaction
    public boolean containsTransactionAt(byte[] blockHash, byte[] transactionHash) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return bc.hasTransaction(transactionHash);
        }
        if (!transactionCache.containsKey(transactionHash))
            return bc.hasTransaction(transactionHash);
        Block b = chainCache
                .get(blockHash).map(BlockWrapper::get)
                .orElseThrow(() -> new RuntimeException("unreachable"));
        if (transactionIndex.containsKey(blockHash)
                && transactionIndex.get(blockHash).contains(transactionHash)) {
            return true;
        }
        return containsTransactionAt(b.hashPrevBlock, transactionHash);
    }

    // get transaction from block or the ancestor
    public Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return Optional.ofNullable(bc.getTransaction(txHash));
        }
        Set<byte[]> txs = transactionIndex.get(blockHash);
        if (txs == null) throw new RuntimeException("unreachable");
        if (txs.contains(txHash)) return Optional.of(transactionCache.get(txHash));
        byte[] parent = chainCache.get(blockHash)
                .map(BlockWrapper::get)
                .map(b -> b.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException("unreachable"));
        return getTransactionAt(parent, txHash);
    }

    public boolean containsPayloadAt(byte[] blockHash, int type, byte[] payload) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return bc.hasPayload(type, payload);
        }
        Block b = chainCache
                .get(blockHash).map(BlockWrapper::get)
                .orElseThrow(() -> new RuntimeException("unreachable"));
        for (Transaction t : b.body) {
            if (t.payload != null && t.type == type && FastByteComparisons.equal(t.payload, payload)) {
                return true;
            }
        }
        return containsPayloadAt(b.hashPrevBlock, type, payload);
    }

    public boolean containsAssetCodeAt(byte[] blockHash, byte[] code) {
        return assetCodeTrie.get(blockHash, code).orElse(false);
    }

    @Override
    public Optional<AccountState> getAccountStateAt(byte[] blockHash, byte[] publicKeyHash) {
        return accountStateTrie.get(blockHash, publicKeyHash);
    }

    @Override
    public Map<byte[], AccountState> getAccountStatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes) {
        return accountStateTrie.batchGet(blockHash, publicKeyHashes);
    }

    @Override
    public long getValidatorNonceAt(byte[] blockHash, byte[] publicKeyHash) {
        return validatorStateTrie.get(blockHash, publicKeyHash).orElse(0L);
    }

    @Override
    public List<byte[]> getProposersByParent(Block parent) {
        return candidateStateTrie.getProposers(parent);
    }

    @Override
    public Optional<Proposer> getProposerByParentAndEpoch(Block parent, long epochSecond) {
        return candidateStateTrie.getProposer(parent, epochSecond);
    }

    @Override
    public List<CandidateInfo> getLatestTopCandidates() {
        Block best = getBestBlock();
        byte[] key;
        if (best.nHeight % eraLinker.getBlocksPerEra() == 0) {
            key = best.getHash();
        } else {
            key = eraLinker.getPrevEraLast(best).getHash();
        }
        return candidateStateTrie
                .getBestCandidatesCache()
                .get(HexBytes.fromBytes(key));
    }

    @Override
    public List<CandidateInfo> getLatestBlockedCandidates() {
        Block best = getBestBlock();
        byte[] key;
        if (best.nHeight % eraLinker.getBlocksPerEra() == 0) {
            key = best.getHash();
        } else {
            key = eraLinker.getPrevEraLast(best).getHash();
        }
        return candidateStateTrie
                .getBlockedCandidatesCache()
                .get(HexBytes.fromBytes(key));
    }

    @Override
    public Optional<Candidate> getLatestCandidate(byte[] publicKeyHash) {
        return candidateStateTrie.get(getBestBlock().getHash(), publicKeyHash);
    }

    // average blocks interval
    public double getAverageBlocksInterval() {
        List<Block> best = getBestChain(10);

        BigDecimal bd = BigDecimal.valueOf((best.get(best.size() - 1).nTime - best.get(0).nTime) / (9.0));
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public long getAverageFee() {
        List<Block> list = getBestChain(10);

        int[] txCount = new int[1];
        long total = list.stream()
                .flatMap(b -> b.body.stream())
                .filter(tx -> tx.type != Transaction.Type.COINBASE.ordinal())
                .peek(tx -> txCount[0]++)
                .map(Transaction::getFee)
                .reduce(0L, Long::sum);
        return txCount[0] == 0 ? 0 : (total / txCount[0]);
    }

    // get the best chain of forkdb
    public List<Block> getBestChain(int limit) {
        List<Block> blocks = chainCache
                .getAncestors(chainCache.last().getHash().getBytes())
                .stream().map(BlockWrapper::get)
                .collect(toList());
        if (blocks.size() >= limit) return blocks.subList(0, limit);
        long toFetch = limit - blocks.size();
        List<Block> fetched = bc.getHeaders(blocks.get(0).nHeight - toFetch, (int) toFetch);
        fetched.addAll(blocks);
        return fetched;
    }

    // count blocks after timestamp
    public long countBlocksAfter(long timestamp) {
        return chainCache
                .stream().filter(x -> x.get().nTime >= timestamp)
                .count() +
                bc.countBlocksAfter(timestamp);
    }


    @Override
    public List<Transaction> getTransactionsAtByTo(byte[] blockHash, byte[] publicKeyHash, int offset, int limit) {
        if (FastByteComparisons.equal(blockHash, latestConfirmed.getHash())) {
            return bc.getTransactionsByTo(publicKeyHash, offset, limit);
        }
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            return Collections.emptyList();
        }
        Block b = o.get();
        if (b.body == null) {
            return getTransactionsAtByTo(b.hashPrevBlock, publicKeyHash, offset, limit);
        }
        List<Transaction> transactions = b.body
                .stream()
                .filter(tx -> FastByteComparisons.equal(tx.to, publicKeyHash))
                .collect(toList());
        List<Transaction> transactionsPrevBlocks =
                getTransactionsAtByTo(b.hashPrevBlock, publicKeyHash, offset, limit);
        transactionsPrevBlocks.addAll(transactions);
        return transactionsPrevBlocks;
    }

    @Override
    public List<Transaction> getTransactionsAtByFrom(byte[] blockHash, byte[] publicKey, int offset, int limit) {
        if (FastByteComparisons.equal(blockHash, latestConfirmed.getHash())) {
            return bc.getTransactionsByFrom(publicKey, offset, limit);
        }
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            return Collections.emptyList();
        }
        Block b = o.get();
        if (b.body == null) {
            return getTransactionsAtByFrom(b.hashPrevBlock, publicKey, offset, limit);
        }
        List<Transaction> transactions = b.body.stream()
                .filter(tx -> FastByteComparisons.equal(tx.from, publicKey))
                .collect(toList());
        List<Transaction> transactionsPrevBlocks
                = getTransactionsAtByFrom(b.hashPrevBlock, publicKey, offset, limit);
        transactionsPrevBlocks.addAll(transactions);
        return transactionsPrevBlocks;
    }

    @Override
    public List<Transaction> getTransactionsAtByFromAndTo(byte[] blockHash, byte[] from, byte[] to, int offset, int limit) {
        if (FastByteComparisons.equal(blockHash, latestConfirmed.getHash())) {
            return bc.getTransactionsByFromAndTo(from, to, offset, limit);
        }
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            return Collections.emptyList();
        }
        Block b = o.get();
        if (b.body == null) {
            return getTransactionsAtByFromAndTo(b.hashPrevBlock, from, to, offset, limit);
        }
        List<Transaction> transactions = b.body.stream().filter(
                tx -> FastByteComparisons.equal(tx.from, from)
                        && FastByteComparisons.equal(tx.to, to)
        ).collect(toList());
        List<Transaction> transactionsPrevBlocks =
                getTransactionsAtByFromAndTo(b.hashPrevBlock, from, to, offset, limit);
        transactionsPrevBlocks.addAll(transactions);
        return transactionsPrevBlocks;
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeAndTo(byte[] blockHash, int type, byte[] publicKeyHash, int offset, int limit) {
        if (FastByteComparisons.equal(blockHash, latestConfirmed.getHash())) {
            return bc.getTransactionsByTypeAndTo(type, publicKeyHash, offset, limit);
        }
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            return Collections.emptyList();
        }
        Block b = o.get();
        if (b.body == null) {
            return getTransactionsAtByTypeAndTo(b.hashPrevBlock, type, publicKeyHash, offset, limit);
        }
        List<Transaction> transactions = b.body.stream().filter(tx -> tx.type == type
                && FastByteComparisons.equal(tx.to, publicKeyHash)
        ).collect(toList());
        List<Transaction> transactionsPrevBlocks =
                getTransactionsAtByTypeAndTo(b.hashPrevBlock, type, publicKeyHash, offset, limit);
        transactionsPrevBlocks.addAll(transactions);
        return transactionsPrevBlocks;
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeAndFrom(byte[] blockHash, int type, byte[] publicKey, int offset, int limit) {
        if (FastByteComparisons.equal(blockHash, latestConfirmed.getHash())) {
            return bc.getTransactionsByTypeAndFrom(type, publicKey, offset, limit);
        }
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            return Collections.emptyList();
        }
        Block b = o.get();
        if (b.body == null) {
            return getTransactionsAtByTypeAndFrom(b.hashPrevBlock, type, publicKey, offset, limit);
        }
        List<Transaction> transactions = b.body.stream().filter(
                tx -> tx.type == type
                        && FastByteComparisons.equal(tx.from, publicKey)
        ).collect(toList());
        List<Transaction> transactionsPrevBlocks = getTransactionsAtByTypeAndFrom(b.hashPrevBlock, type, publicKey, offset, limit);
        transactionsPrevBlocks.addAll(transactions);
        return transactionsPrevBlocks;
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeFromAndTo(byte[] blockHash, int type, byte[] from, byte[] to, int offset, int limit) {
        if (FastByteComparisons.equal(blockHash, latestConfirmed.getHash())) {
            return bc.getTransactionsByTypeFromAndTo(type, from, to, offset, limit);
        }
        Optional<Block> o = chainCache.get(blockHash).map(BlockWrapper::get);
        if (!o.isPresent()) {
            return Collections.emptyList();
        }
        Block b = o.get();
        if (b.body == null) {
            return getTransactionsAtByTypeFromAndTo(b.hashPrevBlock, type, from, to, offset, limit);
        }
        List<Transaction> transactions = b.body.stream().filter(
                tx -> tx.type == type
                        && FastByteComparisons.equal(tx.from, from)
                        && FastByteComparisons.equal(tx.to, to)
        ).collect(toList());
        List<Transaction> transactionsPrevBlocks = getTransactionsAtByTypeFromAndTo(b.hashPrevBlock, type, from, to, offset, limit);
        transactionsPrevBlocks.addAll(transactions);
        return transactionsPrevBlocks;
    }

    // 写入区块
    @Override
    public void writeBlock(Block block) {
        // the block had been confirmed
        if (block.nHeight <= latestConfirmed.nHeight) {
            return;
        }
        // filter orphans
        if (!FastByteComparisons.equal(this.latestConfirmed.getHash(), block.hashPrevBlock)
                && !chainCache.containsHash(block.hashPrevBlock)) {
            return;
        }
        // had written
        if (chainCache.containsHash(block.getHash())) {
            return;
        }

        chainCache.add(new BlockWrapper(block));
        triesSyncManager.commit(block);

        // 写入事务索引

        transactionIndex.put(block.getHash(), new ByteArraySet());

        block.body.forEach(t -> {
            t.height = block.nHeight;
            t.blockHash = block.getHash();
            transactionIndex.get(block.getHash()).add(t.getHash());
            transactionCache.put(t.getHash(), t);
        });

        leastConfirms.put(block.getHash(),
                (int) Math.ceil(
                        getProposersByParent(getBlock(block.hashPrevBlock))
                                .size()
                                * 2.0 / 3
                )
        );

        List<Block> ancestors =
                chainCache.getAncestors(block.getHash())
                        .stream().map(BlockWrapper::get).collect(toList());

        for (Block b : ancestors) {

            confirms.putIfAbsent(b.getHash(), new ByteArraySet());

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
