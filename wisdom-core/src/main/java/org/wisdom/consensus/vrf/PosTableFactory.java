package org.wisdom.consensus.vrf;

import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author sal 1564319846@qq.com
 * pos table factory, lru cached
 */
//@Component
public class PosTableFactory extends EraLinkedStateFactory<PosTable> {
    private static final int cacheSize = 20;

    private static final int blocksPerEra = 20;


    @Autowired
    public PosTableFactory(WisdomBlockChain blockChain) {
        super(blockChain, cacheSize, new PosTable(blockChain), blocksPerEra);
        this.blockChain = blockChain;
    }
}
