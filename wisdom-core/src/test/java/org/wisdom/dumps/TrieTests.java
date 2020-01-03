package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.util.HexBytes;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.db.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
// set SPRING_CONFIG_LOCATION=classpath:application-test.yml to run dump tasks
public class TrieTests {

    @Autowired
    protected ValidatorStateTrie validatorStateTrie;

    @Autowired
    protected CandidateStateTrie candidateStateTrie;

    @Autowired
    private Block genesis;



    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;

    @Autowired
    private ProposersState genesisProposersState;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;



    @Test
    public void testValidatorNonceTrie() {
        blockStreamBuilder.getBlocks().forEach(b -> {
            if (b.nHeight == 0) return;
            validatorStateTrie.commit(b);
            assertEquals(Long.valueOf(b.body.get(0).nonce - 1), validatorStateTrie.get(b.hashPrevBlock, b.body.get(0).to).orElse(0L));
        });
    }

    @Test
    public void testProposers() {
        List<Block> era = new ArrayList<>(blocksPerEra);
        blockStreamBuilder.getBlocks()
                .forEach(b -> {
                    if (b.nHeight == 0) return;
                    era.add(b);
                    if (era.size() < blocksPerEra) return;
                    System.out.println(b.nHeight);
                    candidateStateTrie.commit(era);
                    genesisProposersState.updateBlocks(era);

                    final long nextEra = (b.nHeight - 1) / blocksPerEra + 1;
                    candidateStateTrie.getTrie()
                            .revert(candidateStateTrie.getRootStore().get(b.getHash()).get())
                            .values()
                            .forEach(c -> {
                                if(c.getAccumulated(nextEra) !=
                                        genesisProposersState.getAll()
                                                .get(c.getPublicKeyHash().toHex()).getAccumulated()){

                                    Map<HexBytes, Vote> received = new HashMap<>();
                                    c.getReceivedVotes().forEach((k, v) -> {
                                        received.put(HexBytes.fromBytes(k), v);
                                    });
                                    throw new RuntimeException("assertion failed");
                                }
                            });

                    List<Candidate> proposers = candidateStateTrie.getCache()
                            .asMap().get(HexBytes.fromBytes(b.getHash()));

                    List<ProposersState.Proposer> proposersExpected = genesisProposersState
                            .getProposers();

                    assertEquals(proposersExpected.size(), proposers.size());

                    for(int i = 0; i < proposersExpected.size(); i++){
                        assertEquals(proposersExpected.get(i).publicKeyHash, proposers.get(i).getPublicKeyHash().toHex());
                    }

                    era.clear();
                });
    }
}
