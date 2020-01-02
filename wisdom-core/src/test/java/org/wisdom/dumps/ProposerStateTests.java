package org.wisdom.dumps;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.rlp.RLPElement;
import org.wisdom.consensus.pow.ProposersFactory;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class ProposerStateTests {

    @Autowired
    private ProposersFactory proposersFactory;


    @Test
    public void test() {
        String blocksDirectory = "D:\\dumps";
        File file = Paths.get(blocksDirectory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(blocksDirectory + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        List<Block> blocks = Arrays.stream(files).sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getName().split("\\.")[1])))
                .flatMap(x -> {
                    try {
                        byte[] bytes = Files.readAllBytes(x.toPath());
                        return Arrays.stream(RLPElement.fromEncoded(bytes).as(Block[].class));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        Map<Integer, Map<String, ProposersState.Proposer>> map = new HashMap<>();
        for (int i = 0; i < blocks.size() - 120; i += 120) {
            proposersFactory.initCache(blocks.get(i), blocks.subList(i + 1, i + 121));
            map.put(i, proposersFactory.getSpecificProposers(blocks.get(i + 120)));
        }
    }


}
