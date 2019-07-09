package org.ethereum.consensus;

import org.ethereum.config.TestConfig;
import org.wisdom.consensus.pow.TargetStateFactory;
import org.wisdom.core.Block;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TargetStateFactoryTest {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private Block genesis;

    @Test
    public void test(){
        TargetStateFactory targetStateFactory = ctx.getBean(TargetStateFactory.class);
        assert targetStateFactory != null;
        assert targetStateFactory.getInstance(genesis) != null;
    }
}
