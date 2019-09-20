package org.wisdom.consensus;

import org.wisdom.config.TestConfig;
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

}
