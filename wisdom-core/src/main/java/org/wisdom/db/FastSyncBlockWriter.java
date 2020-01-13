package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.validate.CompositeBlockRule;
import org.wisdom.core.validate.Result;

@Component
public class FastSyncBlockWriter {
    private TriesSyncManager triesSyncManager;
    private WisdomRepository wisdomRepository;
    private CompositeBlockRule compositeBlockRule;
    private WisdomBlockChain bc;

    public FastSyncBlockWriter(
            TriesSyncManager triesSyncManager,
            WisdomRepository wisdomRepository,
            CompositeBlockRule compositeBlockRule,
            WisdomBlockChain bc
    ) {
        this.triesSyncManager = triesSyncManager;
        this.wisdomRepository = wisdomRepository;
        this.compositeBlockRule = compositeBlockRule;
        this.bc = bc;
        sync();
    }

    private void sync() {
        long bestHeight = bc.getTopHeight();
        triesSyncManager
                .readBlocks(bestHeight + 1)
                .forEach(b -> {
                    Result res = compositeBlockRule.validateBlock(b);
                    if(!res.isSuccess())
                        throw new RuntimeException(res.getMessage());
                    wisdomRepository.writeBlock(b);
                });
    }
}
