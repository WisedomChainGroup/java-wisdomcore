package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.common.serialize.Codec;
import org.tdf.common.serialize.Codecs;
import org.tdf.common.store.Store;
import org.tdf.common.store.StoreWrapper;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.validate.CheckPointRule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

// helper to keep all state trie synced
@Component
@Slf4j
public class TriesSyncManager {
    @Getter(AccessLevel.PACKAGE)
    private static final int BLOCKS_PER_UPDATE_LOWER_BOUNDS = 4096;

    private static final String DB_STATUS = "status";

    private static final String LAST_SYNCED_HEIGHT = "last-confirmed";

    private AccountStateTrie accountStateTrie;

    private ValidatorStateTrie validatorStateTrie;

    private CandidateStateTrie candidateStateTrie;

    private AssetCodeTrie assetCodeTrie;

    private Store<String, Long> statusStore;

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
        this.statusStore = new StoreWrapper<>(
                factory.create(DB_STATUS, false),
                Codecs.STRING,
                Codec.newInstance(RLPCodec::encode, RLPCodec::decodeLong)
        );
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
    }

    public void setRepository(WisdomRepository repository) {
        this.repository = repository;
    }

    private Stream<File> readFastSyncFiles() {
        File file = Paths.get(fastSyncDirectory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(fastSyncDirectory + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        return Arrays.stream(files);
    }

    public PreBuiltGenesis readPreBuiltGenesis() throws IOException {
        File lastGenesis = readFastSyncFiles()
                .filter(f -> f.getName().matches("genesis\\.[0-9]+\\.rlp"))
                .max(Comparator.comparingLong(x -> Long.parseLong(x.getName().split("\\.")[1])))
                .orElseThrow(() -> new RuntimeException("unreachable"));
        PreBuiltGenesis preBuiltGenesis = RLPElement
                .fromEncoded(Files.readAllBytes(lastGenesis.toPath()))
                .as(PreBuiltGenesis.class);
        if (preBuiltGenesis.getBlock().nHeight % blocksPerEra != 0)
            throw new RuntimeException("prebuilt genesis must be era last block");
        return preBuiltGenesis;
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
        long currentHeight = bc.currentHeader().nHeight;
        if (currentHeight >= preBuiltGenesis.getBlock().nHeight) {
            if (!FastByteComparisons.equal(
                    bc.getCanonicalHeader(preBuiltGenesis.getBlock().nHeight).getHash(),
                    preBuiltGenesis.block.getHash())) {
                throw new RuntimeException("prebuilt genesis conflicts to block in your database");
            }
            return;
        }

        log.info("current best block height is {}, start fast sync to {}", currentHeight, preBuiltGenesis.getBlock().nHeight);
        Block[] parent = new Block[1];

        readBlocks(currentHeight + 1)
                .peek((b) -> {
                    if (b.nHeight == 0 &&
                            !FastByteComparisons.equal(b.getHash(), bc.getGenesis().getHash())
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
                    if(b.nHeight == preBuiltGenesis.block.nHeight
                            && !FastByteComparisons.equal(b.getHash(), preBuiltGenesis.getBlock().getHash())
                    ) throw new RuntimeException("prebuilt genesis conflicts to block in dumped block");
                    if (b.nHeight % 1000 == 0) {
                        double status = (b.nHeight - currentHeight) * 1.0 / (preBuiltGenesis.getBlock().nHeight - currentHeight);
                        log.info("fast sync status {}%", String.format("%.2f", status * 100));
                    }
                    parent[0] = b;
                })
                .forEach(bc::writeBlock);

        ;
    }

    void sync() throws Exception {
        // query for states had been written


        PreBuiltGenesis preBuiltGenesis = readPreBuiltGenesis();

        syncBlockDatabase(preBuiltGenesis);

        long currentHeight = bc.currentHeader().nHeight;
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

        long accountStateTrieLastSyncHeight =
                getLastSyncedHeight(
                        preBuiltGenesis.block.nHeight, currentHeight, accountStateTrie.getRootStore()
                );

        long validatorStateTrieLastSyncHeight =
                getLastSyncedHeight(
                        preBuiltGenesis.block.nHeight, currentHeight, validatorStateTrie.getRootStore()
                );

        long candidateStateTrieLastSyncHeight =
                getLastSyncedHeightWithEra(
                        preBuiltGenesis.block.nHeight, currentHeight - (currentHeight % blocksPerEra),
                        blocksPerEra,
                        candidateStateTrie.getRootStore()
                );

        if (candidateStateTrieLastSyncHeight % blocksPerEra != 0)
            throw new RuntimeException("unexpected");

        Block candidateLastSynced = bc.getCanonicalBlock(candidateStateTrieLastSyncHeight);

        candidateStateTrie.generateCache(
                candidateLastSynced,
                candidateStateTrie.getTrie(candidateLastSynced.getHash()).asMap()
        );

        // 从这个最小值开始同步状态
        int blocksPerUpdate = BLOCKS_PER_UPDATE_LOWER_BOUNDS;

        long start = Stream.of(
                accountStateTrieLastSyncHeight,
                validatorStateTrieLastSyncHeight,
                candidateStateTrieLastSyncHeight)
                .min(Long::compareTo).orElseThrow(() -> new RuntimeException("unexpected"));

        log.info("start sync status from {}", start);

        while (true) {
            List<Block> blocks = bc.getCanonicalBlocks(start + 1, blocksPerUpdate);
            for (Block block : blocks) {
                if (block.nHeight > accountStateTrieLastSyncHeight) {
                    accountStateTrie.commit(block);
                }
                if (block.nHeight > validatorStateTrieLastSyncHeight) {
                    validatorStateTrie.commit(block);
                }
                if (block.nHeight > candidateStateTrieLastSyncHeight) {
                    candidateStateTrie.commit(block);
                }
            }
            // sync trie here
            if (blocks.size() < blocksPerUpdate) break;
            double status = (blocks.get(blocks.size() - 1).nHeight - start) * 1.0 / (currentHeight - start);
            log.info("state sync status {}%", String.format("%.2f", status * 100));
            start += blocksPerUpdate;
        }

        log.info("sync status finished");
    }

    public void commit(Block block) {
        accountStateTrie.commit(block);
        validatorStateTrie.commit(block);
        candidateStateTrie.commit(block);
//        assetCodeTrie.commit(block);
    }

    public long getLastSyncedHeight(long start, long end, Store<byte[], byte[]> rootStore) {
        if (start == end) return end;
        long half = (start + end) / 2;
        Block h = bc.getCanonicalHeader(half);
        if (!rootStore.containsKey(h.getHash())) {
            return getLastSyncedHeight(start, half - 1, rootStore);
        }
        Block next = bc.getCanonicalHeader(half + 1);
        if (next == null || !rootStore.containsKey(next.getHash())) return half;
        return getLastSyncedHeight(half + 1, end, rootStore);
    }

    public long getLastSyncedHeightWithEra(long start, long end, int era, Store<byte[], byte[]> rootStore) {
        if (start == end) return end;
        long half = (start + end) / 2;
        half = half - (half % era);
        Block h = bc.getCanonicalHeader(half);
        if (!rootStore.containsKey(h.getHash())) {
            return getLastSyncedHeightWithEra(start, half - era, era, rootStore);
        }
        Block next = bc.getCanonicalHeader(half + era);
        if (next == null || !rootStore.containsKey(next.getHash())) return half;
        return getLastSyncedHeightWithEra(half + era, end, era, rootStore);
    }
}
