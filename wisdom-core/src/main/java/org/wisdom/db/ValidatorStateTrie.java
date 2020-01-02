package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.core.Block;
import org.wisdom.genesis.Genesis;

import java.util.*;

@Component
public class ValidatorStateTrie extends AbstractStateTrie<Long>{
    public ValidatorStateTrie(Block genesis, Genesis genesisJSON, DatabaseStoreFactory factory) {
        super(genesis, genesisJSON, Long.class, factory, false, false);
    }

    @Override
    protected String getPrefix() {
        return "validator";
    }

    @Override
    protected Map<byte[], Long> getUpdatedStates(Map<byte[], Long> beforeUpdates, Block block) {
        Map<byte[], Long> updated = new ByteArrayMap<>(beforeUpdates);
        long after = updated.get(block.body.get(0).to) + 1;
        updated.put(block.body.get(0).to, after);
        return updated;
    }

    @Override
    protected Set<byte[]> getRelatedKeys(Block block) {
        return new ByteArraySet(Collections.singleton(block.body.get(0).to));
    }

    @Override
    protected Map<byte[], Long> generateGenesisStates(Block genesis, Genesis genesisJSON) {
        return Collections.emptyMap();
    }

    @Override
    protected Long createEmpty(byte[] publicKeyHash) {
        return 0L;
    }
}
