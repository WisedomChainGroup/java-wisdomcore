package org.wisdom.core;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.crypto.HashUtil;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

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
            newBlock.reHash();
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
        assert Arrays.areEqual(chain.getHeaderByHeight(0).getHash(), getGenesis().getHash());
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
        assert bc.containsBlock(getHeightOne().getHash());
    }

    @Test
    public void testGetCurrentHeader() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert Arrays.areEqual(bc.getTopHeader().getHash(), getHeightOne().getHash());
    }

    @Test
    public void testGetCurrentBlock() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert Arrays.areEqual(bc.getTopBlock().getHash(), getHeightOne().getHash());
    }

    @Test
    public void testGetHeader() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getHeaderByHash(getHeightOne().getHash()) != null;
    }

    @Test
    public void testGetBlock() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getBlockByHash(getHeightOne().getHash()) != null;
    }

    @Test
    public void testGetHeaders() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getHeadersBetween(1, 1000).size() == 1;
    }

    @Test
    public void testGetBlocks() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.getBlocksBetween(1, 1000).size() == 1;
    }

    @Test
    public void testGetCanonicalHeader() {
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert Arrays.areEqual(bc.getHeaderByHeight(1).getHash(), getHeightOne().getHash());
    }

    @Test
    public void testGetCanonicalHeaders() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        List<Block> headers = bc.getHeadersBetween(0, 11);
        assert headers != null;
        assert headers.size() == 11;
        assert headers.get(0) != null;
        assert Arrays.areEqual(headers.get(0).getHash(), getGenesis().getHash());
        assert Arrays.areEqual(headers.get(10).getHash(), bc.getTopHeader().getHash());
    }

    @Test
    public void testGetCanonicalBlock() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block block9 = bc.getBlockByHeight(9);
        assert block9 != null;
    }

    @Test
    public void testGetCanonicalBlocks() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        List<Block> canonicalBlocks = bc.getBlocksBetween(0, 10);
        assert canonicalBlocks != null;
        assert Arrays.areEqual(getGenesis().getHash(), canonicalBlocks.get(0).getHash());
        assert canonicalBlocks.size() == 10;
    }

    @Test
    public void testIsCanonical(){
        WisdomBlockChain bc = getChain();
        bc.writeBlock(getHeightOne());
        assert bc.containsBlock(getHeightOne().getHash());
    }

    @Test
    public void testFindAncestorHeader() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block currentHeader = bc.getTopHeader();
        List<Block> foundHeader = bc.getAncestorBlocks(currentHeader.getHash(), 0);
        assert foundHeader != null;
        assert foundHeader.size() == 0;
        assert foundHeader.get(0).getnHeight() == 0L;
    }

    @Test
    public void testgetAncestorHeaders() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block currentHeader = bc.getTopHeader();
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
        Block currentHeader = bc.getTopHeader();
        List<Block> b = bc.getAncestorBlocks(currentHeader.getHash(), 0);
        assert b != null;
        assert Arrays.areEqual(getGenesis().getHash(), b.get(0).getHash());
    }

    @Test
    public void testGetAncestorBlocks() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(10, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        Block currentHeader = bc.getTopHeader();
        List<Block> b = bc.getAncestorBlocks(currentHeader.getHash(), 0);
        assert b != null;
        assert Arrays.areEqual(getGenesis().getHash(), b.get(0).getHash());
    }

    @Test
    public void testHasTransaction() {
        WisdomBlockChain bc = getChain();
        assert bc.containsTransaction(getGenesis().body.get(0).getHash());
    }

    @Test
    public void testGetTransaction() {
        WisdomBlockChain bc = getChain();
        assert bc.getTransaction(getGenesis().body.get(0).getHash()) != null;
    }

    @Test
    public void testWriteBlocks() {
        WisdomBlockChain bc = getChain();
        List<Block> blocks = getHeightN(20, null);
        for (Block b : blocks) {
            bc.writeBlock(b);
        }
        assert bc.getHeaderByHeight(10) != null;
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
        assert bc.containsBlock(fork2.get(9).getHash());
        assert bc.containsBlock(fork2.get(5).getHash());
    }


    // test fork when encounters a big-weight block
    @Test
    public void testNewBigWeightBlockFork() {
        WisdomBlockChain bc = getChain();
        List<Block> fork1 = getHeightN(20, HashUtil.keccak256("abc".getBytes()));
        List<Block> fork2 = getHeightN(10, HashUtil.keccak256("def".getBytes()));
        fork2.get(fork2.size() - 1).weight = 1000;
        for (Block b : fork1) {
            bc.writeBlock(b);
        }
        for (Block b : fork2) {
            if(b.nHeight == 10){
                System.out.println("============");
            }
            bc.writeBlock(b);
        }
        byte[] h = fork2.get(2).getHash();
        String hx = Hex.encodeHexString(h);
        assert bc.containsBlock(h);
        h = fork2.get(5).getHash();
        assert bc.containsBlock(h);
        assert Arrays.areEqual(bc.getTopHeader().getHash(), fork2.get(fork2.size() - 1).getHash());
    }
}
