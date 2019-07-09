package org.ethereum.wisdom_core;

import org.wisdom.core.RDBMSBlockChainImpl;
import org.wisdom.core.WisdomBlockChain;

public class RDBMSImplChainTest extends WisdomChainTest{
    @Override
    public WisdomBlockChain getChain() {
        return ctx.getBean(RDBMSBlockChainImpl.class);
    }
}
