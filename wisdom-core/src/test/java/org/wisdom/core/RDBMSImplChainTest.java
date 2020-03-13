package org.wisdom.core;


public class RDBMSImplChainTest extends WisdomChainTest{
    @Override
    public WisdomBlockChain getChain() {
        return ctx.getBean(RDBMSBlockChainImpl.class);
    }


}
