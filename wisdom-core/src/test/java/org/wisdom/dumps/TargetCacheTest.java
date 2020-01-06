package org.wisdom.dumps;

import lombok.Setter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.util.BigEndian;
import org.tdf.common.util.FastByteComparisons;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.db.TargetCache;
import org.wisdom.db.WisdomRepositoryAdapter;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class TargetCacheTest {
    @Autowired
    private TargetCache targetCache;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    @Autowired
    private TargetState targetState;


    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;

    protected MockRepository mockRepository;

    public static class MockRepository extends WisdomRepositoryAdapter {
        @Setter
        private List<Block> ancestors;

        @Override
        public List<Block> getAncestorBlocks(byte[] hash, long height) {
            if(ancestors.get(0).nHeight != height) throw new RuntimeException("unreachable");
            if(!FastByteComparisons.equal(ancestors.get(ancestors.size() - 1).getHash(), hash))
                throw new RuntimeException("unreachable");
            return ancestors;
        }
    }

    @Before
    public void before(){
        this.mockRepository = new MockRepository();
        this.targetCache.setRepository(mockRepository);
    }

    @Test
    public void testTargets(){
        Block[] parent = new Block[1];
        List<Block> blocks = new ArrayList<>(blocksPerEra);

        blockStreamBuilder
                .getBlocks()
                .forEach(b -> {
                    if(b.nHeight == 0){
                        parent[0] = b;
                        return;
                    }
                    if((b.nHeight-1) % blocksPerEra != 0){
                        if(!FastByteComparisons.equal(parent[0].nBits, b.nBits)){
                            throw new RuntimeException("unreachable");
                        }
                    }
                    blocks.add(b);
                    assert FastByteComparisons.equal(
                            BigEndian.encodeUint256(targetState.getTarget()), b.nBits);
                    if(blocks.size() == blocksPerEra){
                        System.out.println(b.nHeight);
                        System.out.println(b.nHeight);
                        targetState.updateBlocks(blocks);
                        mockRepository.setAncestors(new ArrayList<>(blocks));
                        blocks.clear();
                    }
                    if(!FastByteComparisons.equal(targetCache.getTargetByParent(parent[0]), b.nBits)){
                        throw new RuntimeException("unreachable");
                    }
                    parent[0] = b;
                });

    }
}
