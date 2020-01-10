package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.core.validate.CompositeBlockRule;

@Component
public class FastSyncBlockWriter {
    private TriesSyncManager triesSyncManager;
    private WisdomRepository wisdomRepository;
    private CompositeBlockRule compositeBlockRule;

    public FastSyncBlockWriter(
            TriesSyncManager triesSyncManager, WisdomRepository wisdomRepository, CompositeBlockRule compositeBlockRule
    ) {
        this.triesSyncManager = triesSyncManager;
        this.wisdomRepository = wisdomRepository;
        this.compositeBlockRule = compositeBlockRule;
        sync();
    }

    private void sync() {
        long bestHeight = wisdomRepository.getBestBlock().nHeight;
        triesSyncManager
                .readBlocks()
                .filter(b -> b.nHeight > bestHeight)
                .forEach(b -> {
                    if(!compositeBlockRule.validateBlock(b).isSuccess())
                        throw new RuntimeException("validate fast sync block failed, please verify your files");
                    wisdomRepository.writeBlock(b);
                });
    }
}
