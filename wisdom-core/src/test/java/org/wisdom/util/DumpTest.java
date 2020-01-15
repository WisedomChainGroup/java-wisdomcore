package org.wisdom.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.db.BlocksDump;

import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DumpContext.class)
public class DumpTest {

    @Autowired
    private BlocksDump blocksDump;


    @Test
    public void dump() throws Exception{
        blocksDump.dump();
    }
}
