package org.wisdom.candidate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.util.HexBytes;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.core.Block;
import org.wisdom.db.CandidateStateTrie;
import org.wisdom.dumps.TargetCacheTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CandidateContext.class)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class CandidateTest {
    @Autowired
    protected CandidateStateTrie candidateStateTrie;


    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;


    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    protected TargetCacheTest.MockRepository mockRepository;



    @Before
    public void before() {
        this.mockRepository = new TargetCacheTest.MockRepository();
        this.candidateStateTrie.setRepository(mockRepository);
    }

    @Test
    public void test() {
        List<Block> era = new ArrayList<>(blocksPerEra);
        Block[] parent = new Block[1];

        blockStreamBuilder.getBlocks()
                .forEach(b -> {
                    if (b.nHeight == 0) {
                        parent[0] = b;
                        return;
                    }
                    era.add(b);

                    List<HexBytes> proposers = candidateStateTrie
                            .getProposersByParent(parent[0])
                            .stream().map(HexBytes::fromBytes).collect(Collectors.toList());

                    HexBytes expected = candidateStateTrie
                            .getProposer(parent[0], b.nTime)
                            .map(p -> p.pubkeyHash)
                            .map(HexBytes::fromBytes)
                            .get();

                    HexBytes actual = HexBytes.fromBytes(b.body.get(0).to);

                    assert expected.equals(actual);

                    parent[0] = b;
                    if (era.size() < blocksPerEra) return;
                    mockRepository.setAncestors(era);
                    if (b.nHeight % 10000 == 0) {
                        System.out.println(b.nHeight);
                    }
                    candidateStateTrie.commit(era);

                    era.clear();
                });
    }
}
