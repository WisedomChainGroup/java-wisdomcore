package org.wisdom.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.dao.TransactionIndexDao;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class WisdomBlockChainTests {

    @Autowired
    private WisdomBlockChain wisdomBlockChain;

    @Autowired
    private Block genesis;

    @Autowired
    private TransactionIndexDao transactionIndexDao;

    @Autowired

    @Test
    public void test(){
        int size = transactionIndexDao.findByBlockHash(genesis.getHash()).size();
        assert size > 0;
    }
}
