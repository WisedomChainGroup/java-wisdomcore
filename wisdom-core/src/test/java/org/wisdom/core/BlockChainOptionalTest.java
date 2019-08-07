package org.wisdom.core;

import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.config.TestConfig;
import org.wisdom.core.account.Transaction;
import org.wisdom.crypto.HashUtil;
import org.wisdom.util.Arrays;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
public class BlockChainOptionalTest {
    private static final byte[] HASH_1 = HashUtil.keccak256("def".getBytes());
    private static final byte[] HASH_2 = HashUtil.keccak256("def".getBytes());

    @Autowired
    private RDBMSBlockChainImpl getChain() {
        return context.getBean(RDBMSBlockChainImpl.class);
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private BlockChainOptional blockChainOptional;

    private Block getGenesis() {
        return context.getBean(Block.class);
    }

    private List<Block> getTestBlocks() {
        List<Block> fork1 = getHeightN(20, HASH_1, HASH_1);
        List<Block> fork2 = getHeightN(10, HASH_2, HASH_2);
        fork2.get(fork2.size() - 1).weight = 1000;
        fork1.addAll(fork2);
        return fork1;
    }

    private List<Block> getHeightN(long endHeight, byte[] merkleRoot, byte[] coinbase) {
        List<Block> blocks = new ArrayList<>();
        Block prev = getGenesis();
        for (int i = 1; i <= endHeight; i++) {
            Block newBlock = getGenesis();
            newBlock.hashPrevBlock = prev.getHash();
            if (merkleRoot != null) {
                newBlock.hashMerkleRoot = merkleRoot;
            }
            newBlock.nHeight = i;
            newBlock.weight = 1;
            Transaction coinbaseTx = Transaction.createEmpty();
            coinbaseTx.to = coinbase;
            coinbaseTx.nonce = i;
            newBlock.body = Collections.singletonList(coinbaseTx);
            blocks.add(newBlock);
            prev = newBlock;
        }
        return blocks;
    }

    @Before
    public void testNewBigWeightBlockFork() {
        WisdomBlockChain bc = getChain();
        for (Block b : getTestBlocks()) {
            bc.writeBlock(b);
        }
    }

    @Test
    public void testGetGenesis() {
        assert Arrays.areEqual(blockChainOptional
                .getGenesis()
                .getHash(), getGenesis().getHash());
    }

    @Test
    public void testHasBlock() {
        for (Block b : getTestBlocks()) {
            assert blockChainOptional.hasBlock(b.getHash()).orElse(false);
        }
    }

    @Test
    public void testGetCurrentHeader() {
        List<Block> blocks = getTestBlocks();
        Optional<Block> biggest = blocks.stream().max((o1, o2) -> (int) (o1.weight - o2.weight));
        assert blockChainOptional.currentHeader()
                .map(Block::getHash)
                .flatMap(x -> biggest.map(y -> Arrays.areEqual(x, y.getHash())))
                .orElse(false);
    }

    @Test
    public void testGetCurrentBlock() {
        assert blockChainOptional.currentBlock()
                .map(h -> h.body)
                .map(b -> b.size() > 0 ? b.get(0) : null)
                .map(tx -> Arrays.areEqual(tx.to, HASH_2))
                .orElse(false);
    }

    @Test
    public void testGetHeader() {
        Block target = getTestBlocks().get(8);
        assert blockChainOptional.getHeader(target.getHash())
                .map(h -> h.nHeight == target.nHeight)
                .orElse(false);
    }

    @Test
    public void testGetBlock() {
        Block target = getTestBlocks().get(19);
        assert blockChainOptional.getBlock(target.getHash())
                .map(h -> h.body)
                .map(b -> b.size() > 0 ? b.get(0) : null)
                .map(t -> Arrays.areEqual(t.to, HASH_1))
                .orElse(false);
    }

    @Test
    public void testGetBlocks() {
        blockChainOptional
                .getBlocks(0, 1000)
                .ifPresent(x -> System.out.println(x.size()));
    }
}
