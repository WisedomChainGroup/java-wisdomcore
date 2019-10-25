package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.core.state.StateFactory;

@Component
public class ValidatorStateFactory extends StateFactory<ValidatorState> {
    public ValidatorStateFactory(ValidatorState genesisState) {
        super(StateDB.CACHE_SIZE, genesisState);
    }
}
