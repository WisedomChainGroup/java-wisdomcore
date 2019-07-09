package org.ethereum.consensus;

import org.ethereum.config.TestConfig;
import org.wisdom.consensus.vrf.PosTable;
import org.wisdom.consensus.vrf.PosTableFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisdom.core.Block;
import org.wisdom.core.RDBMSBlockChainImpl;
import org.wisdom.core.WisdomBlockChain;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PosTableFactoryTest {

    @Autowired
    private ApplicationContext ctx;

    private Block getTestGenesis() {
        return ctx.getBean(Block.class);
    }

    @Test
    public void test(){
        PosTableFactory factory = ctx.getBean(PosTableFactory.class);
        assert factory != null;
        assert factory.getInstance(getTestGenesis()) != null;
    }

    @Test
    public void testGetPosTable() {
        WisdomBlockChain bc = ctx.getBean(RDBMSBlockChainImpl.class);
        PosTableFactory factory = new PosTableFactory(bc);
        for (int i = 1; i < 100; i++) {
            Block newBlock = getTestGenesis();
            newBlock.hashPrevBlock = bc.currentHeader().getHash();
            newBlock.nHeight = i;
            newBlock.weight = 1;
            bc.writeBlock(newBlock);
        }

        Block header = bc.getCanonicalHeader(factory.getBlocksPerEra() * 2 + 1);
        assert header != null;
        PosTable tb = factory.getInstance(header);
        assert tb != null;
        assert tb.counter.get() == factory.getBlocksPerEra() * 2 + 1;

        header = bc.getCanonicalHeader(1);
        assert header != null;
        tb = factory.getInstance(header);
        assert tb != null;
        assert tb.counter.get() == 1;

        header = bc.getCanonicalHeader(factory.getBlocksPerEra() * 4);
        assert header != null;
        tb = factory.getInstance(header);
        assert tb != null;
        assert tb.counter.get() == factory.getBlocksPerEra() * 3 + 1;
    }
}
