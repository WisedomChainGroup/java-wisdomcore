package org.wisdom.core;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BlocksCacheTest.BlocksCacheTestConfig.class)
// TODO: more robust test
public class BlocksCacheTest {
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

    private List<Block> getBlocks() {
        return ctx.getBean(RDBMSBlockChainImpl.class).getBlocks(0, 700);
    }

    public BlocksCache blocksCache() {
        List<Block> fork1 = getHeightN(5, null);
        List<Block> fork2 = getHeightN(20, HashUtil.keccak256("abc".getBytes()));
        BlocksCache cache = new BlocksCache();
        cache.addBlocks(fork1);
        cache.addBlocks(fork2);
        return cache;
    }


    public static class BlocksCacheTestConfig extends TestConfig {
        private List<Block> getHeightN(long endHeight, byte[] merkleRoot) throws Exception {
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

        @Bean
        @Scope("prototype")
        public BlocksCache blocksCache(Block genesis) throws Exception {
            List<Block> fork1 = getHeightN(5, null);
            List<Block> fork2 = getHeightN(20, HashUtil.keccak256("abc".getBytes()));
            BlocksCache cache = new BlocksCache(Arrays.asList(genesis));
            cache.addBlocks(fork1);
            cache.addBlocks(fork2);
            return cache;
        }
    }

    @Autowired
    protected ApplicationContext ctx;


    private Block getGenesis() {
        return ctx.getBean(Block.class);
    }

    @Test
    public void testGetLeavesHash() {
        assert ctx.getBean(BlocksCache.class).getLeavesHash().size() == 2;
    }

    @Test
    public void testGetInitials() {
        assert ctx.getBean(BlocksCache.class).getInitials().size() == 1;
    }

    @Test
    public void testGetAllForks() {
        List<List<Block>> forks = ctx.getBean(BlocksCache.class).getAllForks();
        assert forks.size() == 2;
        assert forks.get(0).size() == 6 || forks.get(1).size() == 6;
        assert forks.get(0).size() == 21 || forks.get(1).size() == 21;
    }

    @Test
    public void testFromDB() {
        BlocksCache cache = new BlocksCache(getBlocks());
        for (List<Block> fork : cache.getAllForks()) {
            System.out.println("startListening block height = " + fork.get(0).nHeight);
            System.out.println("end block height = " + fork.get(fork.size() - 1).nHeight);
            System.out.println("========");
            assert isChain(fork);
        }
        System.out.println(cache.popLongestChain().size());
    }

    public boolean isChain(List<Block> blocks) {
        if (blocks.size() <= 1) {
            return true;
        }
        for (int i = 1; i < blocks.size(); i++) {
            Block prev = blocks.get(i - 1);
            Block next = blocks.get(i);
            if (!Arrays.equals(next.hashPrevBlock, prev.getHash())) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testMultiThreadReadWrite() {
        BlocksCache cache = new BlocksCache(getBlocks());

        ScheduledExecutorService service1 = Executors.newScheduledThreadPool(10);
        ScheduledExecutorService service2 = Executors.newScheduledThreadPool(10);
        service1.scheduleAtFixedRate(() -> {
                    cache.popLongestChain();
                    System.out.println("=============");
                }
                , 0, 100, TimeUnit.MILLISECONDS);
        service2.scheduleAtFixedRate(() -> {
                    cache.addBlocks(getHeightN(100, "abc".getBytes()));
                    System.out.println("===============");
                }
                , 0, 500, TimeUnit.MILLISECONDS);
        // blocking here
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testParseInt() {
        System.out.println(Integer.parseInt("-1"));
    }
}
