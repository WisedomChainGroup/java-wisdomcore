package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.core.account.AccountDB;
import org.wisdom.db.AccountState;
import org.wisdom.db.AccountStateTrie;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

;import static org.junit.Assert.assertEquals;

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

    @Test
    public void compareStates() throws Exception {
        File expectedGenesisFile = Paths.get("").toFile();
        File fromGenesisFile = Paths.get("E:\\java-wisdomcore\\wisdom-core\\src\\test\\resources\\genesis.480000.rlp").toFile();

        if (!fromGenesisFile.isFile() || !expectedGenesisFile.isFile())
            throw new RuntimeException("not a valid file");


        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(fromGenesisFile.toPath()));
        Block genesis = el.get(0).as(Block.class);
        RLPElement elExpected = RLPElement.fromEncoded(Files.readAllBytes(fromGenesisFile.toPath()));
        Block expected = elExpected.get(0).as(Block.class);

        Map<byte[], AccountState> expectedAccountStates = new ByteArrayMap<>();
        for(AccountState state: elExpected.get(1).as(AccountState[].class)){
            expectedAccountStates.put(state.getAccount().getPubkeyHash(), state);
        }

        Trie<byte[], AccountState> empty = accountStateTrie.getTrie().revert();
        Arrays.stream(el.get(1).as(AccountState[].class))
                .forEach(a -> empty.put(a.getAccount().getPubkeyHash(), a));

        byte[] newRoot = empty.commit();
        empty.flush();
        accountStateTrie.getRootStore().put(genesis.getHash(), newRoot);


        Stream<Block> blocks =
                blockStreamBuilder.getBlocks()
                .filter(b -> b.nHeight > genesis.nHeight && b.nHeight <= expected.nHeight)
                ;

        blocks.forEach(accountStateTrie::commit);

        Trie<byte[], AccountState> accountStates = accountStateTrie
                .getTrie(expected.getHash());

        assertEquals(accountStates.size(), expectedAccountStates.size());
        accountStates.values()
                .forEach(a -> {
                    assert expectedAccountStates.containsKey(a.getAccount().getPubkeyHash());
                    assert a.equals(expectedAccountStates.get(a.getAccount().getPubkeyHash()));
                });
    }
}
