package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
import org.wisdom.core.validate.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// helper to keep all state trie synced
@Component
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
    private static class PreBuiltGenesis {
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
                .max((x, y) -> Long.compare(
                        Long.parseLong(x.getName().split("\\.")[1]),
                        Long.parseLong(y.getName().split("\\.")[1])
                ))
                .orElseThrow(() -> new RuntimeException("unreachable"));
        PreBuiltGenesis preBuiltGenesis = RLPElement
                .fromEncoded(Files.readAllBytes(lastGenesis.toPath()))
                .as(PreBuiltGenesis.class);
        if (preBuiltGenesis.getBlock().nHeight % blocksPerEra != 0)
            throw new RuntimeException("prebuilt genesis must be era last block");
        return preBuiltGenesis;
    }

    public Stream<Block> readBlocks() {
        return readFastSyncFiles()
                // TODO: use String.matches full name
                .filter(f -> f.getName().contains("blocks-dump"))
                .sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getName().split("\\.")[1])))
                .flatMap(x -> {
                    try {
                        byte[] bytes = Files.readAllBytes(x.toPath());
                        return Arrays.stream(RLPElement.fromEncoded(bytes).as(Block[].class));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
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

        readBlocks().forEach((b) -> {
            if (b.nHeight == 0 &&
                    !FastByteComparisons.equal(b.getHash(), bc.getGenesis().getHash())
            ) {
                throw new RuntimeException("genesis conflicts");
            }
            // TODO: 这里断言一下 如果b的高度和prebuiltGenesis相等，那么两者的哈希也必须相等
            if (b.nHeight > currentHeight && b.nHeight <= preBuiltGenesis.getBlock().nHeight) {
                if (!checkPointRule.validateBlock(b).isSuccess())
                    throw new RuntimeException("invalid block in fast sync directory");
                bc.writeBlock(b);
            }
        });
    }

    void sync() throws Exception {
        // query for states had been written


        PreBuiltGenesis preBuiltGenesis = readPreBuiltGenesis();
        Block genesis = preBuiltGenesis.getBlock();

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

        // TODO: generate cache for candidateStateTrielatestSyncHeight, not for prebuilt genesis
        candidateStateTrie.generateCache(preBuiltGenesis.getBlock(), preBuiltGenesis.getCandidateStates());

      
        long accountStateTrieLastSyncHeight =
                getLastSyncedHeight(
                        preBuiltGenesis.block.nHeight, currentHeight, accountStateTrie.getRootStore()
                );

        long validatorStateTrieLastSyncHeight =
                getLastSyncedHeight(
                        preBuiltGenesis.block.nHeight, currentHeight, validatorStateTrie.getRootStore()
                );

        long candidateStateTrieLastSyncHeight =
                getLastSyncedHeight(
                        preBuiltGenesis.block.nHeight, currentHeight, candidateStateTrie.getRootStore()
                );

        // TODO: 根据最小io原则，找到这三个long的最小值 Arrsys.stream().min
        // 从这个最小值开始同步状态
        int blocksPerUpdate = BLOCKS_PER_UPDATE_LOWER_BOUNDS;
        while (true) {
            List<Block> blocks = bc.getCanonicalBlocks(accountStateTrieLastSyncHeight + 1, blocksPerUpdate);
            for (Block block : blocks) {
                // get all related accounts 
                // TODO: 针对每棵树有不同的最新状态高度，如果可以确定这个区块高度同步过可以跳过
                accountStateTrie.commit(block);
                validatorStateTrie.commit(block);
                candidateStateTrie.commit(block);
            }
            // sync trie here
            if (blocks.size() < blocksPerUpdate) break;
        }
    }

    public void commit(Block block) {
        accountStateTrie.commit(block);
        validatorStateTrie.commit(block);
        candidateStateTrie.commit(block);
//        assetCodeTrie.commit(block);
    }

    public long getLastSyncedHeight(long start, long end, Store<byte[], byte[]> rootStore) {
        if(start == end) return end;
        long half = (start + end) / 2;
        Block h = bc.getCanonicalHeader(half);
        if (!rootStore.containsKey(h.getHash())) {
            return getLastSyncedHeight(start, half, rootStore);
        }
        Block next = bc.getCanonicalHeader(half + 1);
        if(next == null || !rootStore.containsKey(next.getHash())) return half;
        return getLastSyncedHeight(half, end, rootStore);
    }
}
