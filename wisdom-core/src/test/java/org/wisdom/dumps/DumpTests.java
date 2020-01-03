package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.tdf.rlp.RLPElement;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.core.account.AccountDB;
import org.wisdom.db.AccountState;
import org.wisdom.db.AccountStateTrie;
import org.wisdom.db.AccountStateUpdater;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    @Autowired
    private AccountStateUpdater accountStateUpdater;

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
    public void compareKeys() throws Exception{
        File genesisFile = Paths.get("C:\\Users\\Sal\\Desktop\\dumps\\genesis\\genesis.800040.rlp").toFile();
        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(genesisFile.toPath()));
        Block genesis = el.get(0).as(Block.class);
        Map<byte[], AccountState> expectedAccountStates = new ByteArrayMap<>();
        for(AccountState state: el.get(1).as(AccountState[].class)){
            expectedAccountStates.put(state.getAccount().getPubkeyHash(), state);
        }

        Set<byte[]> all = new ByteArraySet();

        blockStreamBuilder.getBlocks()
                .filter(b -> b.nHeight <= genesis.nHeight)
                .forEach(b -> {
                    if (b.nHeight == 0) {
                        all.addAll(accountStateUpdater.getGenesisStates().keySet());
                        return;
                    }
                    all.addAll(accountStateUpdater.getRelatedKeys(b));
                });
        assert all.size() == expectedAccountStates.size();
        for(byte[] k: all){
            assert expectedAccountStates.containsKey(k);
        }
    }

    @Test
    public void compareStates() throws Exception {
        File expectedGenesisFile = Paths
                .get("C:\\Users\\Sal\\Desktop\\dumps\\genesis\\genesis.800040.rlp")
                .toFile();
        File fromGenesisFile = Paths
                .get("C:\\Users\\Sal\\Desktop\\dumps\\genesis\\genesis.480000.rlp")
                .toFile();

        if (!fromGenesisFile.isFile() || !expectedGenesisFile.isFile())
            throw new RuntimeException("not a valid file");


        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(fromGenesisFile.toPath()));

        Block fromGenesis = el.get(0).as(Block.class);
        AccountState[] fromAccountStates = el.get(1).as(AccountState[].class);

        el = RLPElement.fromEncoded(Files.readAllBytes(expectedGenesisFile.toPath()));
        Block expectedGenesis = el.get(0).as(Block.class);

        Map<byte[], AccountState> expectedAccountStates = new ByteArrayMap<>();
        for(AccountState state: el.get(1).as(AccountState[].class)){
            expectedAccountStates.put(state.getAccount().getPubkeyHash(), state);
        }

        Trie<byte[], AccountState> empty = accountStateTrie
                .getTrie().revert();
        Arrays.stream(fromAccountStates)
                .forEach(a -> empty.put(a.getAccount().getPubkeyHash(), a));

        byte[] newRoot = empty.commit();
        empty.flush();
        accountStateTrie
                .getRootStore()
                .put(fromGenesis.getHash(), newRoot);


        Stream<Block> blocks =
                blockStreamBuilder.getBlocks()
                .filter(b -> b.nHeight > fromGenesis.nHeight && b.nHeight <= expectedGenesis.nHeight)
                ;

        blocks.forEach(accountStateTrie::commit);

        Trie<byte[], AccountState> accountStates = accountStateTrie
                .getTrie(expectedGenesis.getHash());

        assertEquals(accountStates.size(), expectedAccountStates.size());

        accountStates.values()
                .forEach(a -> {
                    assert expectedAccountStates.containsKey(a.getAccount().getPubkeyHash());
                });
    }
}
