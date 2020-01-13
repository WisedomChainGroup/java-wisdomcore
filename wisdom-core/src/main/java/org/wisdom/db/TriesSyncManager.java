package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.common.store.Store;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AssetCodeInfo;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.validate.CheckPointRule;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

// helper to keep all state trie synced
@Component
@Slf4j
public class TriesSyncManager {
    @Getter(AccessLevel.PACKAGE)
    private static final int BLOCKS_PER_UPDATE_LOWER_BOUNDS = 2048;

    private static final String DB_STATUS = "status";

    private AccountStateTrie accountStateTrie;

    private ValidatorStateTrie validatorStateTrie;

    private CandidateStateTrie candidateStateTrie;

    private AssetCodeTrie assetCodeTrie;

    private String fastSyncDirectory;

    private WisdomBlockChain bc;

    private CheckPointRule checkPointRule;

    @Setter
    private WisdomRepository repository;

    private int blocksPerEra;

    public TriesSyncManager(
            AccountStateTrie accountStateTrie,
            ValidatorStateTrie validatorStateTrie,
            DatabaseStoreFactory factory,
            CandidateStateTrie candidateStateTrie,
            AssetCodeTrie assetCodeTrie,
            @Value("${wisdom.consensus.fast-sync.directory}") String fastSyncDirectory,
            WisdomBlockChain bc,
            CheckPointRule checkPointRule,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) {
        this.accountStateTrie = accountStateTrie;
        this.validatorStateTrie = validatorStateTrie;
        this.candidateStateTrie = candidateStateTrie;
        this.assetCodeTrie = assetCodeTrie;
        this.bc = bc;
        this.fastSyncDirectory = fastSyncDirectory;
        this.checkPointRule = checkPointRule;
        this.blocksPerEra = blocksPerEra;
    }

    @Getter
    public static class PreBuiltGenesis {
        private Block block;
        private List<AccountState> accountStates;
        private Map<byte[], Long> validators;
        private Map<byte[], Candidate> candidateStates;
        private Map<byte[], AssetCodeInfo> assetCodeInfos;

    }

    public void setRepository(WisdomRepository repository) {
        this.repository = repository;
    }

    private Stream<File> readFastSyncFiles() {
        File file = Paths.get(fastSyncDirectory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(fastSyncDirectory + " is not a valid directory");
        return Optional.ofNullable(file.listFiles())
                .map(Arrays::stream)
                .orElse(Stream.empty());
    }

    public Optional<PreBuiltGenesis> readPreBuiltGenesis() {
        Optional<PreBuiltGenesis> ret = readFastSyncFiles()
                .filter(f -> f.getName().matches("genesis\\.[0-9]+\\.rlp"))
                .max(Comparator.comparingLong(x -> Long.parseLong(x.getName().split("\\.")[1])))
                .map(f -> {
                    try {
                        return Files.readAllBytes(f.toPath());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(RLPElement::fromEncoded)
                .map(el -> el.as(PreBuiltGenesis.class));

        boolean invalid = ret.map(x -> x.getBlock())
                .map(b -> b.nHeight % blocksPerEra != 0)
                .orElse(false);
        if (invalid)
            throw new RuntimeException("prebuilt genesis must be era last block");
        return ret;
    }

    public Stream<Block> readBlocks(long startHeight /* inclusive */) {
        return readFastSyncFiles()
                .filter(f -> f.getName().matches("blocks-dump\\.+[0-9]+\\.+[0-9\\-]+[0-9]+\\.rlp"))
                .map(f -> new AbstractMap.SimpleImmutableEntry<>(
                        Integer.parseInt(f.getName().split("\\.")[1]),
                        f
                ))
                // entry.getKey() * 100000 ~  (entry.getKey() + 1) * 100000 - 1
                // !( (entry.getKey() + 1) * 100000 - 1 < startHeight )
                // (entry.getKey() + 1) * 100000  >= startHeight + 1
                // (entry.getKey() + 1) * 100000  > startHeight
                .sorted((x, y) -> Integer.compare(x.getKey(), y.getKey()))
                .filter(entry ->
                        (entry.getKey() + 1) * 100000 > startHeight
                )
                .flatMap(x -> {
                    try {
                        byte[] bytes = Files.readAllBytes(x.getValue().toPath());
                        return Arrays.stream(RLPElement.fromEncoded(bytes).as(Block[].class));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(b -> b.nHeight >= startHeight)
                ;
    }

    private void syncBlockDatabase(
            PreBuiltGenesis preBuiltGenesis
    ) {
        long currentHeight = bc.getTopHeight();
        if (currentHeight >= preBuiltGenesis.getBlock().nHeight) {
            if (!FastByteComparisons.equal(
                    bc.getHeaderByHeight(preBuiltGenesis.getBlock().nHeight).getHash(),
                    preBuiltGenesis.block.getHash())) {
                throw new RuntimeException("prebuilt genesis conflicts to block in your database");
            }
            return;
        }

        log.info("current best block height is {}, start fast sync to {}", currentHeight, preBuiltGenesis.getBlock().nHeight);
        Block[] parent = new Block[1];

        readBlocks(currentHeight + 1)
                .peek((b) -> {
                    if (b.nHeight == 1 &&
                            !FastByteComparisons.equal(b.hashPrevBlock, bc.getGenesis().getHash())
                    ) {
                        throw new RuntimeException("genesis conflicts");
                    }
                })
                .filter(b -> b.nHeight <= preBuiltGenesis.getBlock().nHeight)
                .peek(b -> {
                    if (!checkPointRule.validateBlock(b).isSuccess())
                        throw new RuntimeException("invalid block in fast sync directory");
                    if (parent[0] != null && !FastByteComparisons.equal(parent[0].getHash(), b.hashPrevBlock)) {
                        throw new RuntimeException("invalid block in fast sync directory");
                    }
                    if (b.nHeight == preBuiltGenesis.block.nHeight
                            && !FastByteComparisons.equal(b.getHash(), preBuiltGenesis.getBlock().getHash())
                    ) throw new RuntimeException("prebuilt genesis conflicts to block in dumped block");
                    if (b.nHeight % 1000 == 0) {
                        double status = (b.nHeight - currentHeight) * 1.0 / (preBuiltGenesis.getBlock().nHeight - currentHeight);
                        log.info("fast sync status {}%", String.format("%.2f", status * 100));
                    }
                    parent[0] = b;
                })
                .forEach(bc::writeBlock);
    }

    void sync() throws Exception {
        // query for states had been written


        Optional<PreBuiltGenesis> o = readPreBuiltGenesis();

        if (!o.isPresent()) {
            syncStatus(o.orElse(null));
            return;
        }

        PreBuiltGenesis preBuiltGenesis = o.get();
        syncBlockDatabase(preBuiltGenesis);

        long currentHeight = bc.getTopHeight();
        if (currentHeight < preBuiltGenesis.getBlock().nHeight)
            throw new RuntimeException("missing blocks to fast sync, please ensure at least "
                    + preBuiltGenesis.getBlock().nHeight +
                    " blocks in fast sync directory");

        // put pre built states
        // TODO: hard code trie roots

        Map<byte[], AccountState> accountStates = new ByteArrayMap<>();
        preBuiltGenesis.getAccountStates()
                .forEach(a -> accountStates.put(a.getAccount().getPubkeyHash(), a));

        accountStateTrie.commit(accountStates, preBuiltGenesis.block.getHash());
        validatorStateTrie.commit(preBuiltGenesis.getValidators(), preBuiltGenesis.block.getHash());
        candidateStateTrie.commit(preBuiltGenesis.getCandidateStates(), preBuiltGenesis.block.getHash());
        assetCodeTrie.commit(preBuiltGenesis.getAssetCodeInfos(), preBuiltGenesis.block.getHash());

        syncStatus(o.orElse(null));
    }

    private void syncStatus(PreBuiltGenesis preBuiltGenesis) {
        long currentHeight = bc.getTopHeight();

        long lastSync = preBuiltGenesis == null ? 0 : preBuiltGenesis.getBlock().nHeight;

        long accountStateTrieLastSyncHeight =
                getLastSyncedHeight(
                        lastSync, currentHeight, accountStateTrie.getRootStore()
                );

        long validatorStateTrieLastSyncHeight =
                getLastSyncedHeight(
                        lastSync, currentHeight, validatorStateTrie.getRootStore()
                );

        long assetCodeTrieLastSyncHeight =
                getLastSyncedHeight(
                        lastSync, currentHeight, assetCodeTrie.getRootStore()
                );

        long candidateStateTrieLastSyncHeight =
                getLastSyncedEra(
                        lastSync / blocksPerEra,
                        (currentHeight - (currentHeight % blocksPerEra)) / blocksPerEra,
                        candidateStateTrie.getRootStore()
                ) * blocksPerEra;


        Block candidateLastSynced = bc.getBlockByHeight(candidateStateTrieLastSyncHeight);

        candidateStateTrie.generateCache(
                candidateLastSynced,
                candidateStateTrie.getTrie(candidateLastSynced.getHash()).asMap()
        );

        long start = Stream.of(
                accountStateTrieLastSyncHeight,
                validatorStateTrieLastSyncHeight,
                candidateStateTrieLastSyncHeight,
                assetCodeTrieLastSyncHeight
        ).min(Long::compareTo).orElseThrow(() -> new RuntimeException("unexpected"));

        start = start - (start % blocksPerEra);
        final long finalStart = start;
        log.info("start sync status from {} to {}", start, currentHeight);

        int blocksPerUpdate = 0;

        while (blocksPerUpdate < BLOCKS_PER_UPDATE_LOWER_BOUNDS) {
            blocksPerUpdate += blocksPerEra;
        }

        while (true) {
            List<Block> blocks = bc.getBlocksSince(start + 1, blocksPerUpdate);
            boolean cont = blocks.size() == blocksPerUpdate;
            Block last = blocks.isEmpty() ? null : blocks.get(blocks.size() - 1);
            blocks.forEach(b -> {
                if (b.nHeight > accountStateTrieLastSyncHeight) {
                    accountStateTrie.commit(b);
                }
                if (b.nHeight > validatorStateTrieLastSyncHeight) {
                    validatorStateTrie.commit(b);
                }
                if (b.nHeight > assetCodeTrieLastSyncHeight) {
                    assetCodeTrie.commit(b);
                }
            });

            while (blocks.size() >= blocksPerEra) {
                candidateStateTrie.commit(blocks.subList(0, blocksPerEra));
                blocks = blocks.subList(blocksPerEra, blocks.size());
            }
            // sync trie here
            if (!cont) break;
            start += blocksPerUpdate;
            if (last == null) continue;
            double status = (last.nHeight - finalStart) * 1.0 / (currentHeight - finalStart);
            log.info("state sync status {}%", String.format("%.2f", status * 100));
        }

        log.info("sync status finished");
    }

    public void commit(Block block) {
        accountStateTrie.commit(block);
        validatorStateTrie.commit(block);
        candidateStateTrie.commit(block);
        assetCodeTrie.commit(block);
    }

    public long getLastSyncedHeight(long start, long end, Store<byte[], byte[]> rootStore) {
        if (start == end) return end;
        long half = (start + end) / 2;
        Block h = bc.getHeaderByHeight(half);
        if (!rootStore.containsKey(h.getHash())) {
            return getLastSyncedHeight(start, half - 1, rootStore);
        }
        Block next = bc.getHeaderByHeight(half + 1);
        if (next == null || !rootStore.containsKey(next.getHash())) return half;
        return getLastSyncedHeight(half + 1, end, rootStore);
    }

    public long getLastSyncedEra(long startEra, long endEra, Store<byte[], byte[]> rootStore) {
        if (startEra == endEra) return endEra;
        long half = (startEra + endEra) / 2;
        Block h = bc.getHeaderByHeight(half * blocksPerEra);
        if (!rootStore.containsKey(h.getHash())) {
            return getLastSyncedEra(startEra, half - 1, rootStore);
        }
        Block next = bc.getHeaderByHeight((half + 1) * blocksPerEra);
        if (next == null || !rootStore.containsKey(next.getHash())) return half;
        return getLastSyncedEra(half + 1, endEra, rootStore);
    }
}
