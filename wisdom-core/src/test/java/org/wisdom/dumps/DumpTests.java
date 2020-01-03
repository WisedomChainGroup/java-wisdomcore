package org.wisdom.dumps;

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
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.core.account.AccountDB;
import org.wisdom.crypto.HashUtil;
import org.wisdom.db.AccountState;

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
    public void parse() throws Exception {
        String genesisDirectory = "z:\\dumps\\accounts";
        File file = Paths.get(genesisDirectory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(genesisDirectory + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        File lastGenesis = Arrays.stream(files)
                .filter(f -> f.getName().matches("genesis\\.[0-9]+\\.rlp"))
                .sorted((x, y) -> (int) (Long.parseLong(y.getName().split("\\.")[1]) - Long.parseLong(x.getName().split("\\.")[1])))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("unreachable"));
        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(lastGenesis.toPath()));
        Block genesis = el.get(0).as(Block.class);
        AccountState[] genesisStates = el.get(1).as(AccountState[].class);
        Trie<byte[], AccountState> trie = TrieImpl.newInstance(
                HashUtil::keccak256, new ByteArrayMapStore<>(),
                Codec.identity(),
                Codec.newInstance(RLPCodec::encode, x -> RLPCodec.decode(x, AccountState.class))
        );
        Arrays.asList(genesisStates)
                .forEach(s -> trie.put(s.getAccount().getPubkeyHash(), s));
        byte[] root = trie.commit();
        System.out.println(HexBytes.encode(root));
    }
}
