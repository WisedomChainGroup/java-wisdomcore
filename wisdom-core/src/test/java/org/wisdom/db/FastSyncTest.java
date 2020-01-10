package org.wisdom.db;

import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.validate.CompositeBlockRule;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class FastSyncTest {
    @Autowired
    private WisdomRepository repository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    @Autowired
    private CompositeBlockRule compositeBlockRule;

    @Test
    public void test(){
        final long best = repository.getBestBlock().nHeight;
        blockStreamBuilder
                .getBlocks()
                .filter(b -> b.nHeight > best)
                .peek(b -> {
                    if(!compositeBlockRule.validateBlock(b).isSuccess()){
                        throw new RuntimeException("validate failed");
                    }
                })
                .forEach(repository::writeBlock);
        ;
    }
}
