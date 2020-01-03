package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.core.Block;


@Component
public class ValidatorStateTrie extends AbstractStateTrie<Long> {
    public ValidatorStateTrie(Block genesis, DatabaseStoreFactory factory) {
        super(Long.class, new ValidatorStateUpdater(), genesis, factory, false, false);
    }

    @Override
    protected String getPrefix() {
        return "validator";
    }
}
