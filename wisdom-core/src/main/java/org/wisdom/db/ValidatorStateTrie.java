package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.core.Block;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ValidatorStateTrie extends AbstractStateTrie<Long>{
    public ValidatorStateTrie(Block genesis, DatabaseStoreFactory factory) {
        super(genesis, Long.class, factory, false, false);
    }

    @Override
    protected String getPrefix() {
        return "validator";
    }

    @Override
    protected Map<byte[], Long> getUpdatedStates(Map<byte[], Long> beforeUpdates, Block block) {
        if(block.nHeight == 0) return Collections.emptyMap();
        Map<byte[], Long> updated = new ByteArrayMap<>(beforeUpdates);
        long after = Optional.ofNullable(updated.get(block.body.get(0).to)).map(x -> x + 1).orElse(1L);
        updated.put(block.body.get(0).to, after);
        return updated;
    }

    @Override
    protected Set<byte[]> getRelatedKeys(Block block) {
        return new ByteArraySet(Collections.singleton(block.body.get(0).to));
    }
}
