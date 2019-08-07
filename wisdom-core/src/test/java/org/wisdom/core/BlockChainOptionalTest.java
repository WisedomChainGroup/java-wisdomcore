package org.wisdom.core;

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
    private static final byte[] HASH_2 = HashUtil.keccak256("abc".getBytes());

    @Autowired
    private RDBMSBlockChainImpl getChain() {
        return context.getBean(RDBMSBlockChainImpl.class);
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private BlockChainOptional blockChainOptional;

    @Autowired
    private Block genesis;

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
            newBlock.reHash();
            blocks.add(newBlock);
            prev = newBlock;
        }
        return blocks;
    }

    @Before
    public void testNewBigWeightBlockFork() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getTestBlocks();
        for (Block b : blocks) {
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
        Optional<List<Block>> blocks = blockChainOptional.getBlocks(0, 1000);
        assert blocks.map(List::size).map(s -> s == 31).orElse(false);
        assert blocks.map(ls -> ls.get(1).body)
                .map(b -> b.size() > 0 ? b.get(0) : null)
                .map(tx -> Arrays.areEqual(tx.to, HASH_1)).orElse(false);
    }

    @Test
    public void testGetHeaders() {
        Optional<List<Block>> blocks = blockChainOptional.getHeaders(0, 1000);
        assert blocks.map(List::size).map(s -> s == 31).orElse(false);
    }

    @Test
    public void testGeCanonicalHeader() {
        assert blockChainOptional
                .getCanonicalHeader(0)
                .map(b -> b.nHeight == 0)
                .orElse(false);
        assert blockChainOptional
                .getCanonicalHeader(10)
                .map(b -> Arrays.areEqual(b.hashMerkleRoot, HASH_2))
                .orElse(false);
    }

    @Test
    public void testGetCanonicalHeaders() {
        Optional<List<Block>> headers = blockChainOptional.getCanonicalHeaders(0, 11);
        assert headers.map(hs -> hs.size() == 11).orElse(false);
        assert headers.map(hs -> hs.get(0))
                .map(Block::getHash)
                .map(h -> Arrays.areEqual(h, genesis.getHash())).orElse(false);
        assert headers.map(hs -> hs.get(hs.size() - 1))
                .map(Block::getHash)
                .flatMap(h ->
                        blockChainOptional.currentHeader().map(y -> Arrays.areEqual(h, y.getHash())))
                .orElse(false);
    }

    @Test
    public void testGetCanonicalBlock() {
        assert blockChainOptional
                .getCanonicalBlock(9)
                .map(b -> b.nHeight == 9)
                .orElse(false);
    }

    @Test
    public void testGetCanonicalBlocks() {
        Optional<List<Block>> canonicalBlocks = blockChainOptional.getCanonicalBlocks(0, 10);
        assert canonicalBlocks
                .map(bs -> bs.size() > 0 ? bs : null)
                .map(bs -> bs.get(0))
                .map(b -> Arrays.areEqual(b.getHash(), genesis.getHash()))
                .orElse(false);
        assert canonicalBlocks
                .map(bs -> bs.size() == 10)
                .orElse(false);
    }

    @Test
    public void testIsCanonical() {
        List<Block> blocks = getTestBlocks();
        Optional<Block> biggest = blocks.stream().max((o1, o2) -> (int) (o1.weight - o2.weight));
        assert biggest
                .flatMap(b -> blockChainOptional.isCanonical(b.getHash()))
                .orElse(false);
    }

    @Test
    public void testFindAncestorHeader() {
        Optional<Block> currentHeader = blockChainOptional.currentHeader();
        Optional<Block> foundHeader = currentHeader
                .flatMap(c -> blockChainOptional.findAncestorHeader(c.getHash(), 0));
        assert foundHeader.map(h -> h.nHeight == 0).orElse(false);
    }

    @Test
    public void testgetAncestorHeaders() {
        Optional<Block> currentHeader = blockChainOptional.currentHeader();
        Optional<List<Block>> foundHeaders = currentHeader.flatMap(h -> blockChainOptional.getAncestorHeaders(h.getHash(), 0));
        assert foundHeaders
                .flatMap(hs -> currentHeader.map(h -> hs.size() == h.nHeight + 1))
                .orElse(false);
    }

    @Test
    public void testFindAncestorBlock() {
        Optional<Block> currentHeader = blockChainOptional.currentHeader();
        Optional<Block> b = currentHeader.flatMap(h ->
                blockChainOptional.findAncestorBlock(h.getHash(), 0));
        assert b.map(x -> Arrays.areEqual(x.getHash(), genesis.getHash())).orElse(false);
        assert b.map(x -> x.body)
                .map(bd -> bd.size() > 0)
                .orElse(false);
    }

    @Test
    public void testGetAncestorBlocks() {
        Optional<Block> currentHeader = blockChainOptional.currentHeader();
        Optional<List<Block>> foundBlocks = currentHeader.flatMap(h -> blockChainOptional.getAncestorBlocks(h.getHash(), 0));
        assert foundBlocks
                .flatMap(hs -> currentHeader.map(h -> hs.size() == h.nHeight + 1))
                .orElse(false);
        assert foundBlocks.flatMap(
                x -> x.stream()
                        .map(y -> y.body != null && y.size() > 0)
                        .reduce((a, b) -> a && b)
        ).orElse(false);
    }

    @Test
    public void testGetCurrentTotalWeight() {
        assert blockChainOptional.getCurrentTotalWeight()
                .map(w -> w == 1009)
                .orElse(false);
    }

    @Test
    public void testHasTransaction() {
        Optional<Block> currentBlock = blockChainOptional.currentBlock();
        assert currentBlock
                .map(b -> b.body)
                .map(bd -> bd.size() > 0 ? bd.get(0) : null)
                .flatMap(h -> blockChainOptional
                        .hasTransaction(h.getHash())
                )
                .orElse(false);
    }

    @Test
    public void testGetTransaction() {
        Optional<Block> currentBlock = blockChainOptional.currentBlock();
        assert currentBlock
                .map(b -> b.body)
                .map(bd -> bd.size() > 0 ? bd.get(0) : null)
                .flatMap(h -> blockChainOptional
                        .getTransaction(h.getHash())
                )
                .isPresent();
    }
}
