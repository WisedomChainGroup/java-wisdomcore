package org.wisdom.dumps;

import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.ByteArrayMapStore;
import org.tdf.common.store.NoDeleteStore;
import org.tdf.common.trie.Trie;
import org.tdf.common.trie.TrieImpl;
import org.tdf.common.util.ByteArraySet;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.core.account.AccountDB;
import org.wisdom.crypto.HashUtil;
import org.wisdom.db.AccountState;
import org.wisdom.db.AccountStateTrie;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
// set SPRING_CONFIG_LOCATION=classpath:application-test.yml to run dump tasks
public class DumpTests {
    @Autowired
    private AccountDB accountDB;

    @Autowired
    private BlocksDump blocksDump;

    @Autowired
    private GenesisDump genesisDump;


    private BlockStreamBuilder blockStreamBuilder;

    @Autowired
    private AccountStateTrie accountStateTrie;

    @Test
    public void dumpBlocks() throws Exception {
        blocksDump.dump();
    }

    @Test
    public void restoreDumps() {

    }

    @Test
    public void createNewGenesis() throws Exception {
        genesisDump.dump();
    }

    @Test
    public void compareStates() throws Exception {
        File expectedGenesisFile = Paths.get("").toFile();
        File fromGenesisFile = Paths.get("E:\\java-wisdomcore\\wisdom-core\\src\\test\\resources\\genesis.480000.rlp").toFile();
        RLPElement element = RLPElement.fromEncoded(Files.readAllBytes(expectedGenesisFile.toPath()));

        if (!fromGenesisFile.isFile() && !expectedGenesisFile.isFile())
            throw new RuntimeException("not a valid file");

        File blocksFile = Paths.get("").toFile();
        if (!blocksFile.isDirectory()) throw new RuntimeException(blocksFile.getPath() + " is not a valid directory");

        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(fromGenesisFile.toPath()));
        Block genesis = el.get(0).as(Block.class);
        Trie<byte[], AccountState> empty = accountStateTrie.getTrie().revert();
        Arrays.stream(el.get(1).as(AccountState[].class))
                .forEach(a -> empty.put(a.getAccount().getPubkeyHash(), a));
        byte[] newRoot = empty.commit();
        empty.flush();

        accountStateTrie.getRootStore().put(genesis.getHash(), newRoot);

        blockStreamBuilder = new BlockStreamBuilder("");
        Stream<Block> blocks = blockStreamBuilder.getBlocks();
        accountStateTrie.commit(blocks.collect(Collectors.toList()));

        Block block = blocks.max(Comparator.comparingLong(Block::getnHeight)).get();
        Trie<byte[], AccountState> accountStates = accountStateTrie.getTrie(block.getHash());
        List<AccountState> list = new ArrayList(accountStates.asMap().values());

        RLPElement newGenesisAccounts = RLPElement.readRLPTree(list);
        RLPElement newGenesisData = RLPList.createEmpty(2);
        newGenesisData.add(RLPElement.readRLPTree(block));
        newGenesisData.add(newGenesisAccounts);
        assert newGenesisData == element;
    }
}
