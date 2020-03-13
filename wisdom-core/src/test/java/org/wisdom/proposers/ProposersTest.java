package org.wisdom.proposers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.util.HexBytes;
import org.wisdom.consensus.pow.Proposer;
import org.wisdom.consensus.pow.ProposersFactory;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.db.StateDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProposersContext.class)
public class ProposersTest {
    @Autowired
    private ProposersFactory proposersFactory;

    @Autowired
    private ProposersState genesisState;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;


    @Test
    public void testInitFactory() {

        List<Block> blocks = new ArrayList<>(blocksPerEra);
        Block[] parent = new Block[1];
        Block[] prevEraLast = new Block[1];
        Block[] target = new Block[1];
        Block[] targetParent = new Block[1];
        blockStreamBuilder.getBlocks()
                .filter(b -> b.nHeight <= 522218)
                .forEach(b -> {
                    if(b.nHeight == 522218 - 522218 % blocksPerEra){
                        prevEraLast[0] = b;
                    }
                    if(b.nHeight == 522218){
                        target[0] = b;
                    }
                    if(b.nHeight == 522218 - 1){
                        targetParent[0] = b;
                    }
                    if (b.nHeight == 0) {
                        parent[0] = b;
                        return;
                    }
                    blocks.add(b);
                    if (blocks.size() < blocksPerEra) return;
                    System.out.println(b.nHeight);
                    proposersFactory.initCache(parent[0], blocks);
                    parent[0] = blocks.get(blocks.size() - 1);
                    blocks.clear();
                });
        List<HexBytes> all = proposersFactory.getProposers(prevEraLast[0])
                .stream().map(HexBytes::fromBytes).collect(Collectors.toList());

        HexBytes proposer =  HexBytes.fromBytes(proposersFactory.getProposer(all, targetParent[0], target[0].nTime)
                .get().pubkeyHash);
    }

    private static class MockStateDB extends StateDB {
        public MockStateDB() {
            super(0, 0, 0, 0, 0);
        }


    }
}
