package org.wisdom.dumps;

import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.ByteArrayMapStore;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
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

    public void compareStates() throws Exception {
        File expectedGenesisFile = Paths.get("").toFile();;
        File fromGenesisFile = Paths.get("").toFile();

        if (!fromGenesisFile.isFile() || !expectedGenesisFile.isFile())
            throw new RuntimeException("not a valid file");

        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(fromGenesisFile.toPath()));
        Block genesis = el.get(0).as(Block.class);
        AccountState[] genesisStates = el.get(1).as(AccountState[].class);
        Trie<byte[], AccountState> trie = TrieImpl.newInstance(
                HashUtil::keccak256, new ByteArrayMapStore<>(),
                Codec.identity(),
                Codec.newInstance(RLPCodec::encode, x -> RLPCodec.decode(x, AccountState.class))
        );


    }
}
