package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import org.tdf.rlp.RLPElement;
import org.wisdom.context.TestContext;

import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.db.AccountState;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
        List<AccountState> accountStatesList = new ArrayList<>(Arrays.asList(accountStates));
        Block block = el.get(0).as(Block.class);
        long height = block.nHeight;
        List<Account> list;
        try {
            String sql = "select * from account a where a.blockheight<=?";
            list = jdbcTemplate.query(sql, new Object[]{height}, new BeanPropertyRowMapper<>(Account.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<Incubator> incubatorList;
        try {
            String sql = "select * from incubator_state  where height<=?";
            incubatorList = jdbcTemplate.query(sql, new Object[]{height}, new BeanPropertyRowMapper<>(Incubator.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Account account : list) {
            AtomicBoolean a = new AtomicBoolean(false);
            accountStatesList.forEach(x -> {
                if (Arrays.equals(x.getAccount().getPubkeyHash(), account.getPubkeyHash())) {
                    a.set(true);
                }
            });
            assert a.get();
        }

        for (Incubator incubator : incubatorList) {
            AtomicBoolean a = new AtomicBoolean(false);
            accountStatesList.forEach(x -> {
                if (x.getInterestMap().containsKey(incubator.getTxid_issue())) {
                    a.set(true);
                }
            });

            assert a.get();
        }
    }
}
