package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.tdf.common.util.FastByteComparisons;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.genesis.Genesis;

import java.util.*;

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
