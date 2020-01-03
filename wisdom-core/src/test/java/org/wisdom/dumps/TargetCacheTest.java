package org.wisdom.dumps;

import lombok.AllArgsConstructor;
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
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;
import org.wisdom.db.TargetCache;
import org.wisdom.db.WisdomRepository;

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

    public static class MockRepository implements WisdomRepository{
        @Setter
        private List<Block> ancestors;

        @Override
        public Block getLatestConfirmed() {
            return null;
        }

        @Override
        public Block getGenesis() {
            return null;
        }

        @Override
        public Block getBestBlock() {
            return null;
        }

        @Override
        public Block getBlock(byte[] hash) {
            return null;
        }

        @Override
        public Block getHeader(byte[] hash) {
            return null;
        }

        @Override
        public Block findAncestorHeader(byte[] hash, long h) {
            return null;
        }

        @Override
        public List<Block> getAncestorBlocks(byte[] hash, long height) {
            if(ancestors.get(0).nHeight != height) throw new RuntimeException("unreachable");
            if(!FastByteComparisons.equal(ancestors.get(ancestors.size() - 1).getHash(), hash))
                throw new RuntimeException("unreachable");
            return ancestors;
        }

        @Override
        public boolean isStaged(byte[] hash) {
            return false;
        }

        @Override
        public boolean isConfirmed(byte[] hash) {
            return false;
        }

        @Override
        public boolean hasTransactionAt(byte[] blockHash, byte[] transactionHash) {
            return false;
        }

        @Override
        public Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash) {
            return Optional.empty();
        }

        @Override
        public boolean hasPayloadAt(byte[] blockHash, int type, byte[] payload) {
            return false;
        }

        @Override
        public Optional<AccountState> getAccountStateAt(byte[] blockHash, byte[] publicKeyHash) {
            return Optional.empty();
        }

        @Override
        public Map<byte[], AccountState> getAccountStatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes) {
            return null;
        }

        @Override
        public long getValidatorNonceAt(byte[] blockHash, byte[] publicKeyHash) {
            return 0;
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
                        targetState.updateBlocks(blocks);
                        mockRepository.setAncestors(new ArrayList<>(blocks));
                        blocks.clear();
                    }
                    if(!FastByteComparisons.equal(targetCache.getTargetAt(parent[0]), b.nBits)){
                        throw new RuntimeException("unreachable");
                    }
                    parent[0] = b;
                });

    }
}
