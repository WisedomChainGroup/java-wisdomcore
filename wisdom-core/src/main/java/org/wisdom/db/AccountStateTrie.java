package org.wisdom.db;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.tdf.common.store.Store;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

@Component
public class AccountStateTrie extends AbstractStateTrie<AccountState> {

    // mark whether the state of the block had been stored
    // block hash -> long
    private Store<byte[], Long> statusStore;

    // block hash -> state root

    private WisdomBlockChain bc;

    private AccountStateUpdater accountStateUpdater;

    @Override
    protected String getPrefix() {
        return "account";
    }

    public AccountStateTrie(
            DatabaseStoreFactory factory,
            Block genesis,
            WisdomBlockChain bc,
            AccountStateUpdater accountStateUpdater
    ) throws Exception {
        super(AccountState.class, accountStateUpdater, genesis, factory, true, false);
        this.bc = bc;
        this.accountStateUpdater = accountStateUpdater;
        this.accountStateUpdater.setWisdomBlockChain(bc);
    }

    @Override
    public byte[] commit(Block block) {
        byte[] root = super.commit(block);
        if(block.accountStateTrieRoot == null || block.accountStateTrieRoot.length == 0)
            return root;
        if(!FastByteComparisons.equal(root, block.accountStateTrieRoot))
            throw new RuntimeException(
                    String.format(
                            "state root not match at height %d hash %s state root local = %s state root remote = %s",
                            block.getnHeight(),
                            block.getHashHexString(),
                            HexBytes.fromBytes(root),
                            HexBytes.fromBytes(block.accountStateTrieRoot)
                    )
            );
        return root;
    }
}