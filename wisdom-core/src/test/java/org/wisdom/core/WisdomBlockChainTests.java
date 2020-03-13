package org.wisdom.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class WisdomBlockChainTests {

    @Autowired
    private WisdomBlockChain wisdomBlockChain;

    @Autowired
    private Block genesis;


    @Test
    public void test(){
        assert wisdomBlockChain.getBlockByHeight(0).size() > 0;
    }
}
