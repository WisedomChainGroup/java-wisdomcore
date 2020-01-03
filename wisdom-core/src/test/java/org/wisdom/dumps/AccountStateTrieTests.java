package org.wisdom.dumps;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import org.tdf.common.util.ByteArraySet;
import org.tdf.rlp.RLPElement;
import org.wisdom.context.TestContext;

import org.wisdom.core.Block;

import org.wisdom.db.AccountState;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class AccountStateTrieTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testAccountsEquals() throws Exception {
        String preBuiltGenesis = "E:\\java-wisdomcore\\wisdom-core\\src\\main\\resources\\genesis\\";
        File file = Paths.get(preBuiltGenesis).toFile();
        if (!file.isDirectory()) throw new RuntimeException(preBuiltGenesis + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        File lastGenesis = Arrays.stream(files)
                .filter(f -> f.getName().matches("genesis\\.[0-9]+\\.rlp")).min((x, y) -> (int) (Long.parseLong(y.getName().split("\\.")[1]) - Long.parseLong(x.getName().split("\\.")[1])))
                .orElseThrow(() -> new RuntimeException("unreachable"));
        RLPElement el = RLPElement.fromEncoded(Files.readAllBytes(lastGenesis.toPath()));
        AccountState[] accountStates = el.get(1).as(AccountState[].class);
        Block block = el.get(0).as(Block.class);
        long height = block.nHeight;
        Set<byte[]> all = new ByteArraySet(
                Arrays.stream(accountStates).map(a -> a.getAccount().getPubkeyHash()).collect(Collectors.toList())
        );
        Set<byte[]> expected = new ByteArraySet();
        List<byte[]> list;
        try {
            String sql = "select pubkeyhash from account a where a.blockheight<=?";
            list = jdbcTemplate.queryForList(sql, new Object[]{height}, byte[].class);
            expected.addAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            String sql = "select pubkeyhash from incubator_state  where height<=?";
            list = jdbcTemplate.queryForList(sql, new Object[]{height}, byte[].class);
            expected.addAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            String sql = "select share_pubkeyhash from incubator_state  where height<=? and not share_pubkeyhash is null";
            list = jdbcTemplate.queryForList(sql, new Object[]{height}, byte[].class);
            expected.addAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(expected.size(), all.size());
        for (byte[] k : expected) {
            assertTrue(all.contains(k));

        }
    }
}
