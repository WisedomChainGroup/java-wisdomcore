package org.wisdom.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.context.TestContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class FastSyncTest {
    @Autowired
    private WisdomRepository repository;


    @Test
    public void test(){}
}
