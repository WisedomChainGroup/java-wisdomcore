package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.db.StateDB;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class RestoreTests {
    @Autowired
    private StateDB stateDB;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    @Test
    public void test(){
        stateDB.init();
        blockStreamBuilder.getBlocks()
                .filter(b -> b.nHeight > 0)
                .peek(b -> {
                    if(b.nHeight % 1000 == 0){
                        System.out.println(b.nHeight);
                    }
                })
                .forEach(stateDB::writeBlock);
    }
}
