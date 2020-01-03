package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import org.tdf.common.util.ByteArraySet;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.account.AccountDB;
import org.wisdom.db.AccountStateUpdater;


import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class AccountStateTrieTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    @Autowired
    private AccountDB accountDB;

    @Autowired
    private AccountStateUpdater accountStateUpdater;

    @Test
    public void testAccountsEquals() throws Exception {
        ByteArraySet all = new ByteArraySet();
        int height = 800040;

        blockStreamBuilder.getBlocks()
                .forEach(b -> {
                    if (b.nHeight == 0) {
                        all.addAll(accountStateUpdater.getGenesisStates().keySet());
                        return;
                    }
                    all.addAll(accountStateUpdater.getRelatedKeys(b));
                });

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
            assert all.contains(k);
        }
    }
}
