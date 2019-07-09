package org.wisdom.consensus.pow;

import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.state.StateFactory;
import org.springframework.stereotype.Component;

@Component
public class ValidatorStateFactory extends StateFactory<ValidatorState> {
    private static final int cacheSize = 20;

    public ValidatorStateFactory(WisdomBlockChain blockChain, ValidatorState genesisState) {
        super(blockChain, cacheSize, genesisState);
    }
}
