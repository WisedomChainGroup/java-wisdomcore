package org.wisdom.consensus.pow;

import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.core.WisdomBlockChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sal 1564319846@qq.com
 * adjust difficulty per era
 */
@Component
public class TargetStateFactory extends EraLinkedStateFactory<TargetState> {

    private static final int CACHE_SIZE = 20;

    @Autowired
    public TargetStateFactory(WisdomBlockChain blockChain, TargetState genesisState, @Value("${wisdom.consensus.blocks-per-era}") int blocksPerRea) {
        super(blockChain, CACHE_SIZE, genesisState, blocksPerRea);
    }
}
