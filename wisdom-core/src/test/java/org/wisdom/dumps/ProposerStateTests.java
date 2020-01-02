package org.wisdom.dumps;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.rlp.Container;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.consensus.pow.ProposersFactory;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestConfig;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class ProposerStateTests {

    @Autowired
    private ProposersFactory proposersFactory;

    @Autowired
    private ProposersState genesisState;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;

    @Autowired
    private TestConfig testConfig;

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
//            map.put(i, proposersFactory.getSpecificProposers(blocks.get(i + 120)));
        }
    }


    @Test
    public void dump() throws Exception{
        List<Block> blocks = new ArrayList<>(blocksPerEra);

        blockStreamBuilder.getBlocks()
                .forEach(b -> {
                    if(b.nHeight == 0) return;
                    blocks.add(b);
                    if(blocks.size() < blocksPerEra) return;
                    System.out.println(b.nHeight);
                    genesisState.updateBlocks(blocks);
                    blocks.clear();
                });
        Files.write(
                Paths.get(testConfig.getProposersFile()),
                RLPCodec.encode(genesisState.getAll()),
                StandardOpenOption.WRITE, StandardOpenOption.SYNC, StandardOpenOption.CREATE_NEW
        );
    }

    @Test
    public void testGetDumpedProposers() throws Exception{
        assert getDumpedProposers() != null;
    }

    static class Foo extends HashMap<String, ProposersState.Proposer>{}

    Map<String, ProposersState.Proposer> getDumpedProposers() throws Exception{
        byte[] data = Files.readAllBytes(Paths.get(testConfig.getProposersFile()));
        return (Map<String, ProposersState.Proposer>) RLPCodec.
                decodeContainer(data, Container.fromType(Foo.class.getGenericSuperclass()));
    }
}
