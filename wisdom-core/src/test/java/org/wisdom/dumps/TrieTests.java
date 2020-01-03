package org.wisdom.dumps;

import org.junit.Before;
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
import org.wisdom.genesis.Genesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
// set SPRING_CONFIG_LOCATION=classpath:application-test.yml to run dump tasks
public class TrieTests {
    protected ValidatorStateTrie validatorStateTrie;

    protected DatabaseStoreFactory factory;

    protected CandidateStateTrie candidateStateTrie;

    protected WisdomRepository wisdomRepository;

    @Autowired
    private Block genesis;

    @Autowired
    private Genesis genesisJSON;

    @Autowired
    private CandidateUpdater candidateUpdater;

    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;

    @Autowired
    private ProposersState genesisProposersState;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    private @Value("${wisdom.allow-miner-joins-era}")
    long allowMinersJoinEra;
    private @Value("${miner.validators}")
    String validatorsFile;
    private @Value("${wisdom.block-interval-switch-era}")
    long blockIntervalSwitchEra;
    private @Value("${wisdom.block-interval-switch-to}")
    int blockIntervalSwitchTo;
    private @Value("${wisdom.consensus.block-interval}")
    int initialBlockInterval;

    @Before
    public void init() throws Exception {
        factory = new DatabaseStoreFactory("", 512, "memory");

        validatorStateTrie = new ValidatorStateTrie(genesis, factory);
        candidateStateTrie = new CandidateStateTrie(genesis, genesisJSON, factory,
                candidateUpdater, blocksPerEra, allowMinersJoinEra,
                validatorsFile, blockIntervalSwitchEra, blockIntervalSwitchTo,
                initialBlockInterval);
    }


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
