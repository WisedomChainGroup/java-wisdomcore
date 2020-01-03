package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import org.tdf.common.util.ByteArraySet;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.context.TestContext;
import org.wisdom.core.account.AccountDB;
import org.wisdom.db.AccountState;


import java.util.Arrays;
import java.util.List;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
public class AccountStateTrieTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BlockStreamBuilder blockStreamBuilder;

    @Autowired
    private AccountDB accountDB;

    @Test
    public void testAccountsEquals() throws Exception {
        ByteArraySet all = new ByteArraySet();
        int height = 800040;


        byte[] zeroPublicKey = new byte[32];
        byte[] zeroPublicKeyHash = new byte[20];
        blockStreamBuilder.getBlocks()
                .flatMap(b -> b.body.stream())
                .forEach(tx -> {
                    if (!Arrays.equals(zeroPublicKey, tx.from)) {
                        assert tx.from.length == 32 || tx.from.length == 20;
                        all.add(tx.from.length == 32 ? PublicKeyHash.fromPublicKey(tx.from).getPublicKeyHash() : tx.from);
                    }
                    if (!Arrays.equals(zeroPublicKeyHash, tx.to)) {
                        assert tx.to.length == 20;
                        all.add(tx.to);
                    }
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
//        assertEquals(expected.size(), all.size());
        for (byte[] k : expected) {
            if(!all.contains(k)){
                   AccountState accountState = accountDB.getAccounstate(k, height);
                   System.out.println("===");
            }
        }
    }
}
