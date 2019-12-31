package org.wisdom.dumps;

import net.bytebuddy.asm.Advice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.rlp.RLPElement;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.db.DatabaseStoreFactory;
import org.wisdom.db.ValidatorStateTrie;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
// set SPRING_CONFIG_LOCATION=classpath:application-test.yml to run dump tasks
public class ValidatorStateTests {
    protected ValidatorStateTrie validatorStateTrie;

    protected DatabaseStoreFactory factory;

    @Autowired
    private Block genesis;

    @Before
    public void init(){
        factory = new DatabaseStoreFactory("", 512, "memory");

        validatorStateTrie = new ValidatorStateTrie(genesis, factory);
    }

    @Test
    public void testUpdates(){
        String blocksDirectory = "z:\\dumps\\blocks";
        File file = Paths.get(blocksDirectory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(blocksDirectory + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        Arrays.stream(files)
                .sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getName().split("\\.")[1])))
                .flatMap(x -> {
                    try {
                        byte[] bytes = Files.readAllBytes(x.toPath());
                        return Arrays.stream(RLPElement.fromEncoded(bytes).as(Block[].class));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(b -> {
                    validatorStateTrie.commit(b);
                    if(b.nHeight == 0) return;
                    assertEquals(Long.valueOf(b.body.get(0).nonce), validatorStateTrie.get(b.getHash(), b.body.get(0).to).get());
                });
    }
}
