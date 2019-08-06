package org.wisdom.core;

import org.wisdom.crypto.HashUtil;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WisdomChainTestConfig.class})
public abstract class WisdomChainTest {
    private Block getGenesis() {
        return ctx.getBean(Block.class);
    }

    private Block getHeightOne() {
        Block b = getGenesis();
        b.hashPrevBlock = getGenesis().getHash();
        b.nHeight = 1;
        b.weight = 1;
        return b;
    }

    private List<Block> getHeightN(long endHeight, byte[] merkleRoot) {
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
            blocks.add(newBlock);
            prev = newBlock;
        }
        return blocks;
    }

    @Autowired
    protected ApplicationContext ctx;

    @Autowired
    private JSONEncodeDecoder encodeDecoder;

    public abstract WisdomBlockChain getChain();

    @Test
    public void testGetChain() {
        WisdomBlockChain chain = getChain();
        assert Arrays.areEqual(chain.getCanonicalHeader(0).getHash(), getGenesis().getHash());
    }

    @Test
    public void testGetGenesis() {
        WisdomBlockChain bc = getChain();
        assert Arrays.areEqual(bc.getGenesis().getHash(), getGenesis().getHash());
    }

    @Test
    public void testHasBlock() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.hasBlock(getHeightOne().getHash());
    }

    @Test
    public void testGetCurrentHeader() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert Arrays.areEqual(bc.currentHeader().getHash(), getHeightOne().getHash());
    }

    @Test
    public void testGetCurrentBlock() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert Arrays.areEqual(bc.currentBlock().getHash(), getHeightOne().getHash());
    }

    @Test
    public void testGetHeader() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getHeader(getHeightOne().getHash()) != null;
    }

    @Test
    public void testGetBlock() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getBlock(getHeightOne().getHash()) != null;
    }

    @Test
    public void testGetHeaders() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getHeaders(1, 1000).size() == 1;
    }

    @Test
    public void testGetBlocks() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getBlocks(1, 1000).size() == 1;
    }

    @Test
    public void testGetCanonicalHeader() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert Arrays.areEqual(bc.getCanonicalHeader(1).getHash(), getHeightOne().getHash());
    }

    @Test
    public void testGetCanonicalHeaders() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        List<Block> headers = bc.getCanonicalHeaders(0, 11);
        assert headers != null;
        assert headers.size() == 11;
        assert headers.get(0) != null;
        assert Arrays.areEqual(headers.get(0).getHash(), getGenesis().getHash());
        assert Arrays.areEqual(headers.get(10).getHash(), bc.currentHeader().getHash());
    }

    @Test
    public void testGetCanonicalBlock() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block block9 = bc.getCanonicalBlock(9);
        assert block9 != null;
    }

    @Test
    public void testGetCanonicalBlocks() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        List<Block> canonicalBlocks = bc.getCanonicalBlocks(0, 10);
        assert canonicalBlocks != null;
        assert Arrays.areEqual(getGenesis().getHash(), canonicalBlocks.get(0).getHash());
        assert canonicalBlocks.size() == 10;
    }

    @Test
    public void testIsCanonical(){
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.isCanonical(getHeightOne().getHash());
    }

    @Test
    public void testFindAncestorHeader() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block currentHeader = bc.currentHeader();
        Block foundHeader = bc.findAncestorHeader(currentHeader.getHash(), 0);
        assert foundHeader != null;
        assert foundHeader.nHeight == 0;
    }

    @Test
    public void testgetAncestorHeaders() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block currentHeader = bc.currentHeader();
        List<Block> foundHeaders = bc.getAncestorHeaders(currentHeader.getHash(), 0);
        assert foundHeaders != null;
        assert foundHeaders.size() == currentHeader.nHeight + 1;
    }

    @Test
    public void testFindAncestorBlock() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block currentHeader = bc.currentHeader();
        Block b = bc.findAncestorBlock(currentHeader.getHash(), 0);
        assert b != null;
        assert Arrays.areEqual(getGenesis().getHash(), b.getHash());
    }

    @Test
    public void testGetAncestorBlocks() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block currentHeader = bc.currentHeader();
        Block b = bc.findAncestorBlock(currentHeader.getHash(), 0);
        assert b != null;
        assert Arrays.areEqual(getGenesis().getHash(), b.getHash());
    }

    @Test
    public void testGetCurrentTotalWeight(){
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        assert bc.getCurrentTotalWeight() == 10;
    }

    @Test
    public void testHasTransaction() {
        WisdomBlockChain bc = getChain();
        assert bc.hasTransaction(getGenesis().body.get(0).getHash());
    }

    @Test
    public void testGetTransaction() {
        WisdomBlockChain bc = getChain();
        assert bc.getTransaction(getGenesis().body.get(0).getHash()) != null;
    }

    // TODO: benchmark
    @Test
    public void testWriteBlocks() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(20, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        assert bc.getCanonicalHeader(10) != null;
    }

    // test fork when a longer chain occurs
    @Test
    public void testNewLongChainFork() {
        WisdomBlockChain bc = getChain();
        List<Block> fork1 = getHeightN(5, null);
        List<Block> fork2 = getHeightN(20, HashUtil.keccak256("abc".getBytes()));
        for (Block b : fork1) {
            bc.writeBlock(b);
        }
        for (Block b : fork2) {
            bc.writeBlock(b);
        }
        assert bc.isCanonical(fork2.get(9).getHash());
        assert bc.isCanonical(fork2.get(5).getHash());
    }


    // test fork when encounters a big-weight block
    @Test
    public void testNewBigWeightBlockFork() {
        WisdomBlockChain bc = getChain();
        List<Block> fork1 = getHeightN(20, null);
        List<Block> fork2 = getHeightN(10, HashUtil.keccak256("abc".getBytes()));
        fork2.get(fork2.size() - 1).weight = 1000;
        for (Block b : fork1) {
            bc.writeBlock(b);
        }
        for (Block b : fork2) {
            bc.writeBlock(b);
        }
        assert bc.isCanonical(fork2.get(2).getHash());
        assert bc.isCanonical(fork2.get(5).getHash());
        assert Arrays.areEqual(bc.currentHeader().getHash(), fork2.get(fork2.size() - 1).getHash());

    }
}
