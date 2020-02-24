package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.core.Block;

@Component
public class LockgetTransferTrie extends AbstractStateTrie<LockTransferInfo> {

    public LockgetTransferTrie(Block genesis, DatabaseStoreFactory factory) {
        super(LockTransferInfo.class, new LockgetTransferUpdater(), genesis, factory, false, false);
    }

    @Override
    protected String getPrefix() {
        return "lock-gettransfer";
    }
}
