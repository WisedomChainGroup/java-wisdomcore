package org.wisdom.dumps;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.rlp.RLPElement;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.db.CandidateStateTrie;
import org.wisdom.db.CandidateUpdater;
import org.wisdom.db.DatabaseStoreFactory;
import org.wisdom.db.ValidatorStateTrie;
import org.wisdom.genesis.Genesis;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
// set SPRING_CONFIG_LOCATION=classpath:application-test.yml to run dump tasks
public class TrieTests {
    protected ValidatorStateTrie validatorStateTrie;

    protected DatabaseStoreFactory factory;

    protected CandidateStateTrie candidateStateTrie;

    @Autowired
    private Block genesis;

    @Autowired
    private Genesis genesisJSON;

    @Autowired
    private CandidateUpdater candidateUpdater;

    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;

    @Before
    public void init(){
        factory = new DatabaseStoreFactory("", 512, "memory");

        validatorStateTrie = new ValidatorStateTrie(genesis, genesisJSON, factory);
        candidateStateTrie = new CandidateStateTrie(genesis, genesisJSON, factory, candidateUpdater, blocksPerEra);
    }

    private Stream<Block> getBlocks(){
        String blocksDirectory = "z:\\dumps\\blocks";
        File file = Paths.get(blocksDirectory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(blocksDirectory + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        return Arrays.stream(files)
                .sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getName().split("\\.")[1])))
                .flatMap(x -> {
                    try {
                        byte[] bytes = Files.readAllBytes(x.toPath());
                        return Arrays.stream(RLPElement.fromEncoded(bytes).as(Block[].class));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testValidatorNonceTrie(){
        getBlocks().forEach(b -> {
                    if(b.nHeight == 0) return;
                    validatorStateTrie.commit(b);
                    assertEquals(Long.valueOf(b.body.get(0).nonce - 1), validatorStateTrie.get(b.hashPrevBlock, b.body.get(0).to).orElse(0L));
                });
    }

    @Test
    public void testProposers(){
        List<Block> era = new ArrayList<>(blocksPerEra);
        getBlocks()
                .forEach(b -> {
                    if(b.nHeight == 0) return;
                    era.add(b);
                    if(era.size() == blocksPerEra){
                        candidateStateTrie.commit(era);
                        era.clear();
                    }
                });
    }
}
