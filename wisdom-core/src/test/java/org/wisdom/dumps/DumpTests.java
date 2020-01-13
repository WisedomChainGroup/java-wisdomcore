package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.tdf.rlp.RLPElement;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.db.AccountState;
import org.wisdom.db.AccountStateTrie;
import org.wisdom.db.AccountStateUpdater;
import org.wisdom.db.BlocksDump;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    public void compareKeys() throws Exception {
        File genesisFile = Paths.get("C:\\Users\\Sal\\Desktop\\dumps\\genesis\\genesis.800040.rlp").toFile();
        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(genesisFile.toPath()));
        Block genesis = el.get(0).as(Block.class);
        Map<byte[], AccountState> expectedAccountStates = new ByteArrayMap<>();
        for (AccountState state : el.get(1).as(AccountState[].class)) {
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
        for (byte[] k : all) {
            assert expectedAccountStates.containsKey(k);
        }
    }

    @Test
    public void compareStates() throws Exception {
        File expectedGenesisFile = Paths
                .get("F:\\newout20200103\\genesis.800040.rlp")
                .toFile();
        File fromGenesisFile = Paths
                .get("F:\\out\\genesis.480000.rlp")
                .toFile();
        TimeUnit.SECONDS.sleep(5);
        if (!fromGenesisFile.isFile() || !expectedGenesisFile.isFile())
            throw new RuntimeException("not a valid file");


        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(fromGenesisFile.toPath()));

        Block fromGenesis = el.get(0).as(Block.class);
        Map<byte[], AccountState> fromAccountStates = new ByteArrayMap<>();

        for (AccountState state : el.get(1).as(AccountState[].class)) {
            fromAccountStates.put(state.getAccount().getPubkeyHash(), state);
        }

        el = RLPElement.fromEncoded(Files.readAllBytes(expectedGenesisFile.toPath()));
        Block expectedGenesis = el.get(0).as(Block.class);

        Map<byte[], AccountState> expectedAccountStates = new ByteArrayMap<>();
        for (AccountState state : el.get(1).as(AccountState[].class)) {
            expectedAccountStates.put(state.getAccount().getPubkeyHash(), state);
        }

        Stream<Block> blocks =
                blockStreamBuilder.getBlocks()
                        .filter(b -> b.nHeight > fromGenesis.nHeight && b.nHeight <= expectedGenesis.nHeight)
                        .peek(b -> {
                            if (b.nHeight % 10000 == 0) {
                                System.out.println(b.nHeight);
                            }
                        });

        blocks.forEach(b -> {
            Map<byte[], AccountState> tmp = new ByteArrayMap<>();
            accountStateUpdater.getRelatedKeys(b).forEach(k ->
                    tmp.put(
                            k, fromAccountStates.getOrDefault(k, accountStateUpdater.createEmpty(k))
                    )
            );

            accountStateUpdater.update(tmp, b).forEach(fromAccountStates::put);
        });

        assertEquals(fromAccountStates.size(), expectedAccountStates.size());

        fromAccountStates.values()
                .forEach(a -> {
                    assert expectedAccountStates.containsKey(a.getAccount().getPubkeyHash());
                    AccountState expected = expectedAccountStates.get(a.getAccount().getPubkeyHash());
                    if (!equalAccount(a.getAccount(), expected.getAccount())) {
                        System.out.println("======");
                    }
                    if (!equalIncubator(a, expected)) {
                        System.out.println("======");
                    }
                });
    }

    private static boolean equalAccount(Account x, Account y) {
        x.setBlockHeight(y.getBlockHeight());
        return x.equals(y);
    }

    private static boolean equalIncubator(AccountState x, AccountState y) {
        if (x.getInterestMap().size() != y.getInterestMap().size()) return false;
        for (byte[] k : x.getInterestMap().keySet()) {
            Incubator xi = x.getInterestMap().get(k);
            Incubator yi = y.getInterestMap().get(k);
            if(yi == null) {
                System.out.println("Interest new ："+xi.toStringInterest()+" old 不存在");
                return false;
            }
            if(!xi.equalsInterest(yi)) {
                System.out.println("Interest new ："+xi.toStringInterest()+"---> old :"+yi.toStringInterest());
                return false;
            }
        }

        for (byte[] k : x.getShareMap().keySet()) {
            Incubator xi = x.getShareMap().get(k);
            Incubator yi = y.getShareMap().get(k);
            if(yi == null) {
                System.out.println("Share new ："+xi.toStringShare()+" old 不存在");
                return false;
            }
            if(!xi.equalsShare(yi)) {
                System.out.println("Share new ："+xi.toStringShare()+"---> old :"+yi.toStringShare());
                return false;
            }
        }
        return true;
    }
}
