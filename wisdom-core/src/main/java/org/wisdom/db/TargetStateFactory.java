package org.wisdom.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.core.state.EraLinkedStateFactory;

@Component
@Deprecated // use state trie instead
public class TargetStateFactory extends EraLinkedStateFactory<TargetState> {
    public TargetStateFactory(TargetState genesisState, @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra) {
        super(StateDB.CACHE_SIZE, genesisState, blocksPerEra);
    }
}
