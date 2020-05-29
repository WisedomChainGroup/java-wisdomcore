package org.wisdom.db;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.tdf.common.util.*;
import org.wisdom.consensus.pow.Proposer;
import org.wisdom.contract.AssetCodeInfo;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.event.NewBlockEvent;
import org.wisdom.core.event.NewConfirmedBlockEvent;
import org.wisdom.dao.TransactionQuery;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

// TODO: create fast sync manager
@Slf4j(topic = "db")
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
    private Map<byte[], Map<byte[], Transaction>> transactionIndex = new ByteArrayMap<>();

    // block hash -> type -> payload
    private Map<byte[], Map<Integer, Set<byte[]>>> payloadsIndex = new ByteArrayMap<>();

    private WisdomBlockChain bc;

    private TriesSyncManager triesSyncManager;

    private AccountStateTrie accountStateTrie;

    private ValidatorStateTrie validatorStateTrie;

    private TargetCache targetCache;

    private CandidateStateTrie candidateStateTrie;

    private AssetCodeTrie assetCodeTrie;

    private LockgetTransferTrie lockgetTransferTrie;

    private EraLinker eraLinker;

    private ApplicationContext applicationContext;

    public WisdomRepositoryImpl(
            WisdomBlockChain bc,
            TriesSyncManager triesSyncManager,
            AccountStateTrie accountStateTrie,
            ValidatorStateTrie validatorStateTrie,
            CandidateStateTrie candidateStateTrie,
            AssetCodeTrie assetCodeTrie,
            LockgetTransferTrie lockgetTransferTrie,
            TargetCache targetCache,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            ApplicationContext applicationContext
    ) throws Exception {
        this.applicationContext = applicationContext;
        this.eraLinker = new EraLinker(blocksPerEra);
        this.eraLinker.setRepository(this);
        this.bc = bc;
        this.targetCache = targetCache;
        this.targetCache.setRepository(this);
        chainCache = ChainCache.<BlockWrapper>builder()
                .comparator(BlockWrapper.COMPARATOR)
                .build();

        this.accountStateTrie = accountStateTrie;
        this.validatorStateTrie = validatorStateTrie;
        this.assetCodeTrie = assetCodeTrie;
        this.lockgetTransferTrie = lockgetTransferTrie;
        this.triesSyncManager = triesSyncManager;
        this.triesSyncManager.setRepository(this);
        this.candidateStateTrie = candidateStateTrie;
        this.candidateStateTrie.setRepository(this);
        this.triesSyncManager.sync();
        initLatestConfirmed();
    }

    private void initLatestConfirmed() throws Exception {
        this.latestConfirmed = bc.getTopBlock();
    }

    private void deleteCache(Block b) {
        chainCache.remove(new BlockWrapper(b));
        confirms.remove(b.getHash());
        leastConfirms.remove(b.getHash());
        transactionIndex.remove(b.getHash());
        payloadsIndex.remove(b.getHash());
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

    public Block getHeaderByHash(byte[] blockHash) {
        return chainCache.get(blockHash)
                .map(ChainedWrapper::get)
                .orElseGet(() -> bc.getHeaderByHash(blockHash));
    }

    public Block getBlockByHash(byte[] blockHash) {
        return chainCache.get(blockHash)
                .map(ChainedWrapper::get)
                .orElseGet(() -> bc.getBlockByHash(blockHash));
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

    public interface BlocksProvider {
        List<Block> apply(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial);
    }

    private List<Block> getBlocksBetweenInternal(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial, BlocksProvider blocksProvider) {
        if (sizeLimit == 0 || startHeight > stopHeight) {
            return Collections.emptyList();
        }

        ChainCache<BlockWrapper> c = ChainCache.<BlockWrapper>builder()
                .comparator(BlockWrapper.COMPARATOR)
                .build();

        // 从数据库获取一部分
        if (startHeight < latestConfirmed.nHeight) {
            c.addAll(
                    blocksProvider.apply(startHeight, stopHeight, sizeLimit, clipInitial)
                            .stream()
                            .map(BlockWrapper::new)
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

    // 获取包含未确认的区块
    public List<Block> getBlocksBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        return getBlocksBetweenInternal(startHeight, stopHeight, sizeLimit, clipInitial, bc::getBlocksBetween);
    }

    @Override
    public List<Block> getHeadersBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        return getBlocksBetweenInternal(startHeight, stopHeight, sizeLimit, clipInitial, bc::getHeadersBetween);
    }

    public Block getAncestorHeader(byte[] hash, long height) {
        Block bHeader = getHeaderByHash(hash);
        if (bHeader.nHeight < height) {
            return null;
        }
        while (bHeader != null && bHeader.nHeight != height) {
            bHeader = getHeaderByHash(bHeader.hashPrevBlock);
        }
        return bHeader;
    }

    private List<Block> getAncestorsInternal(byte[] bhash, long anum, BiFunction<byte[], Long, List<Block>> provider) {
        if (FastByteComparisons.equal(bhash, latestConfirmed.getHash())) {
            return provider.apply(bhash, anum);
        }
        Optional<Block> o = chainCache.get(bhash).map(ChainedWrapper::get);
        if (!o.isPresent()) return provider.apply(bhash, anum);
        ChainCache<BlockWrapper> ret =
                ChainCache.<BlockWrapper>builder()
                        .comparator(BlockWrapper.COMPARATOR)
                        .build();

        List<BlockWrapper> blocks = chainCache.getAncestors(bhash)
                .stream().filter(bl -> bl.get().nHeight >= anum).collect(toList());
        ret.addAll(blocks);
        provider.apply(ret.first().get().hashPrevBlock, anum)
                .stream().map(BlockWrapper::new)
                .forEach(ret::add);

        return ret.stream().map(BlockWrapper::get).collect(toList());
    }

    public List<Block> getAncestorBlocks(byte[] bhash, long anum) {
        return getAncestorsInternal(bhash, anum, bc::getAncestorBlocks);
    }

    @Override
    public List<Block> getAncestorHeaders(byte[] hash, long height) {
        return getAncestorsInternal(hash, height, bc::getAncestorHeaders);
    }

    public boolean isStaged(byte[] hash) {
        return chainCache.containsHash(hash);
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
                        bc.containsBlock(hash)
        );
    }

    @Override
    public byte[] getTargetByParent(Block parent) {
        return targetCache.getTargetByParent(parent);
    }

    // the block or the ancestor has the transaction
    public boolean containsTransactionAt(byte[] blockHash, byte[] transactionHash) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return bc.containsTransaction(transactionHash);
        }
        Block b = chainCache
                .get(blockHash).map(BlockWrapper::get)
                .orElseThrow(() -> new RuntimeException("unreachable"));
        if (transactionIndex.containsKey(blockHash)
                && transactionIndex.get(blockHash).containsKey(transactionHash)) {
            return true;
        }
        return containsTransactionAt(b.hashPrevBlock, transactionHash);
    }

    // get transaction from block or the ancestor
    public Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return Optional.ofNullable(bc.getTransaction(txHash));
        }
        Map<byte[], Transaction> txs = transactionIndex.get(blockHash);
        if (txs == null) throw new RuntimeException("unreachable");
        Transaction tx = txs.get(txHash);
        if (tx != null) return Optional.of(tx);

        byte[] parent = chainCache.get(blockHash)
                .map(BlockWrapper::get)
                .map(b -> b.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException("unreachable"));
        return getTransactionAt(parent, txHash);
    }

    public boolean containsPayloadAt(byte[] blockHash, int type, byte[] payload) {
        if (FastByteComparisons.equal(latestConfirmed.getHash(), blockHash)) {
            return bc.containsPayload(type, payload);
        }
        Block b = chainCache
                .get(blockHash).map(BlockWrapper::get)
                .orElseThrow(() -> new RuntimeException("block " + HexBytes.fromBytes(blockHash) + " not found in fork db"));
        if (payloadsIndex.get(b.getHash())
                .getOrDefault(type, Collections.emptySet())
                .contains(payload)) {
            return true;
        }
        return containsPayloadAt(b.hashPrevBlock, type, payload);
    }

    public Optional<AssetCodeInfo> getAssetCodeAt(byte[] blockHash, byte[] code) {
        return assetCodeTrie.get(blockHash, code);
    }

    @Override
    public Optional<LockTransferInfo> getLockgetTransferAt(byte[] blockHash, byte[] transhash) {
        return lockgetTransferTrie.get(blockHash, transhash);
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
        return candidateStateTrie.getProposersByParent(parent);
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
    public List<CandidateInfo> getLatestCandidates() {
        Block best = getBestBlock();
        byte[] key;
        if (best.nHeight % eraLinker.getBlocksPerEra() == 0) {
            key = best.getHash();
        } else {
            key = eraLinker.getPrevEraLast(best).getHash();
        }
        return candidateStateTrie
                .getCandidatesCache()
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
                .flatMap(b -> Objects.requireNonNull(b.body).stream())
                .filter(tx -> tx.type != Transaction.Type.COINBASE.ordinal())
                .peek(tx -> txCount[0]++)
                .map(Transaction::getFee)
                .reduce(0L, Long::sum);
        return txCount[0] == 0 ? 0 : (total / txCount[0]);
    }

    // get the best chain of forkdb
    public List<Block> getBestChain(int limit) {
        List<Block> blocks = chainCache.isEmpty()? Collections.emptyList() :  chainCache
                .getAncestors(chainCache.last().getHash().getBytes())
                .stream().map(BlockWrapper::get)
                .collect(toList());
        if (blocks.size() >= limit) return blocks.subList(0, limit);
        long toFetch = limit - blocks.size();
        List<Block> fetched = blocks.isEmpty() ?
                bc.getBlocksBetween(latestConfirmed.nHeight - limit + 1 , latestConfirmed.nHeight) :
                bc.getBlocksSince(blocks.get(0).nHeight - toFetch, (int) toFetch);
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
    public List<Transaction> getTransactionByQuery(TransactionQuery transactionQuery) {
        List<Transaction> ret = bc.getTransactionByQuery(transactionQuery);

        Stream<Transaction> all =
                chainCache.getAncestors(chainCache.last().getHash().getBytes())
                        .stream()
                        .flatMap(x -> x.get().body.stream());
        if (transactionQuery.getType() != null)
            all = all.filter(x -> x.type == transactionQuery.getType());
        if (transactionQuery.getFrom() != null)
            all = all.filter(x -> FastByteComparisons.equal(x.from, transactionQuery.getFrom()));
        if (transactionQuery.getTo() != null)
            all = all.filter(x -> FastByteComparisons.equal(x.to, transactionQuery.getTo()));
        if (transactionQuery.getLimit() != null) {
            long limit = transactionQuery.getLimit() - ret.size();
            all = all.limit(limit < 0 ? 0 : limit);
        }
        ret.addAll(all.collect(toList()));
        return ret;
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
        // 写入状态存储
        triesSyncManager.commit(block);

        // 写入事务索引

        transactionIndex.put(block.getHash(), new ByteArrayMap<>());
        payloadsIndex.put(block.getHash(), new HashMap<>());

        block.body.forEach(t -> {
            t.height = block.nHeight;
            t.blockHash = block.getHash();
            transactionIndex.get(block.getHash()).put(t.getHash(), t);
            if (t.payload != null) {
                payloadsIndex
                        .get(block.getHash())
                        .putIfAbsent(t.type, new ByteArraySet());
                payloadsIndex.get(block.getHash())
                        .get(t.type).add(t.payload);
            }
        });

        leastConfirms.put(block.getHash(),
                (int) Math.ceil(
                        getProposersByParent(getBlockByHash(block.hashPrevBlock))
                                .size()
                                * 2.0 / 3
                )
        );

        applicationContext.publishEvent(new NewBlockEvent(this, block));

        if (chainCache.last().getHash().equals(HexBytes.fromBytes(block.getHash()))) {
            applicationContext.publishEvent(new NewBestBlockEvent(this, block));
        }

        List<Block> ancestors =
                chainCache.getAncestors(block.getHash())
                        .stream().map(BlockWrapper::get).collect(toList());

        for (Block b : ancestors) {

            confirms.putIfAbsent(b.getHash(), new ByteArraySet());

            // 区块不能确认自己
            if (FastByteComparisons.equal(b.getHash(), block.getHash())) {
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
            boolean writeResult;
            writeResult = bc.writeBlock(b);

            if (!writeResult) {
                // 数据库 写入失败 重试写入
                log.error("write block to database failed, retrying...");
                continue;
            }
            applicationContext.publishEvent(new NewConfirmedBlockEvent(this, b));
            log.info("write block at height " + b.nHeight + " to db success");

            // 删除孤块
            chainCache
                    .getChildren(latestConfirmed.getHash())
                    .stream()
                    .filter(x -> !FastByteComparisons.equal(x.get().getHash(), b.getHash()))
                    .flatMap(x -> chainCache.getDescendants(x.getHash().getBytes()).stream())
                    .forEach(w -> deleteCache(w.get()));

            // 回收垃圾
            if (b.nHeight % 100000 == 0) {
                List<byte[]> excluded =
                        chainCache.stream().map(BlockWrapper::getHash).map(HexBytes::getBytes).collect(toList());

//                accountStateTrie.gc(excluded);
            }

            // 确认的区块不需要放在缓存中
            deleteCache(b);
            i++;
            this.latestConfirmed = b;
        }
    }
}
