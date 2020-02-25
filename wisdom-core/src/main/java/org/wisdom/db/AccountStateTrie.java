package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.store.Store;
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

    private WisdomRepository repository;

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

    public void setRepository(WisdomRepository repository) {
        this.accountStateUpdater.setRepository(repository);
    }
}