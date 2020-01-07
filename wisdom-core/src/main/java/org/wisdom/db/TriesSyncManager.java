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
import org.tdf.common.trie.Trie;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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

    private Store<String, Long> statusStore;

    private String preBuiltGenesis;

    private WisdomBlockChain bc;

    @Setter
    private WisdomRepository repository;

    public TriesSyncManager(
            AccountStateTrie accountStateTrie,
            ValidatorStateTrie validatorStateTrie,
            DatabaseStoreFactory factory,
            CandidateStateTrie candidateStateTrie,
            @Value("${wisdom.consensus.pre-built-genesis-directory}") String preBuiltGenesis,
            WisdomBlockChain bc
    ) {
        this.accountStateTrie = accountStateTrie;
        this.validatorStateTrie = validatorStateTrie;
        this.statusStore = new StoreWrapper<>(
                factory.create(DB_STATUS, false),
                Codecs.STRING,
                Codec.newInstance(RLPCodec::encode, RLPCodec::decodeLong)
        );
        this.bc = bc;
        this.preBuiltGenesis = preBuiltGenesis;
    }

    public void setRepository(WisdomRepository repository) {
        this.repository = repository;
    }

    void sync() throws Exception{
        // query for had been written
        long lastSyncedHeight = statusStore.get(LAST_SYNCED_HEIGHT).orElse(-1L);

        File file = Paths.get(preBuiltGenesis).toFile();
        if (!file.isDirectory()) throw new RuntimeException(preBuiltGenesis + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        File lastGenesis = Arrays.stream(files)
                .filter(f -> f.getName().matches("genesis\\.[0-9]+\\.rlp"))
                .sorted((x, y) -> (int) (Long.parseLong(y.getName().split("\\.")[1]) - Long.parseLong(x.getName().split("\\.")[1])))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("unreachable"));
        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(lastGenesis.toPath()));
        Block genesis = el.get(0).as(Block.class);

        // put pre built genesis file
        // TODO: hard code trie roots
        if(genesis.nHeight > lastSyncedHeight){
            Trie<byte[], AccountState> empty = accountStateTrie.getTrie().revert();
            Arrays.stream(el.get(1).as(AccountState[].class))
                    .forEach(a -> empty.put(a.getAccount().getPubkeyHash(), a));
            byte[] newRoot = empty.commit();
            empty.flush();
            accountStateTrie.getRootStore().put(genesis.getHash(), newRoot);
            lastSyncedHeight = genesis.nHeight;
        }

        int blocksPerUpdate = BLOCKS_PER_UPDATE_LOWER_BOUNDS;
        while (true) {
            List<Block> blocks = bc.getCanonicalBlocks(lastSyncedHeight + 1, blocksPerUpdate);
            for (Block block : blocks) {
                // get all related accounts
                accountStateTrie.commit(block);
                validatorStateTrie.commit(block);
            }
            // sync trie here
            if (blocks.size() < blocksPerUpdate) break;
            lastSyncedHeight = blocks.get(blocks.size() - 1).nHeight;
        }
        statusStore.put(LAST_SYNCED_HEIGHT, lastSyncedHeight);
    }

    public void commit(Block block){
        accountStateTrie.commit(block);
        validatorStateTrie.commit(block);
    }
}
