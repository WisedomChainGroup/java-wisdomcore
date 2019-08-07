package org.wisdom.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.wisdom.core.BlockChainOptional;
import org.wisdom.crypto.KeyPair;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.Block;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.wisdom.core.RDBMSBlockChainImpl;

public class TestConfig {
    private static String testDBURL = "jdbc:postgresql://localhost:5432/postgres";


    @Bean
    public BasicDataSource basicDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(testDBURL);
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setInitialSize(1);
        ds.setMaxTotal(100);
        ds.setMaxIdle(5);
        return ds;
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(BasicDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate getJDBCTemplate(BasicDataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    @Bean
    public JSONEncodeDecoder encodeDecoder() {
        return new JSONEncodeDecoder();
    }

    @Bean
    @Scope("prototype")
    public Block getGenesis() throws Exception {
        Resource resource = new ClassPathResource("genesis/wisdom-test-genesis.json");
        return encodeDecoder().decodeBlock(IOUtils.toByteArray(resource.getInputStream()));
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager manager) {
        TransactionTemplate tmpl = new TransactionTemplate();
        tmpl.setTransactionManager(manager);
        return tmpl;
    }

    protected void clearData(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.batchUpdate("delete  from header where 1 = 1",
                "delete from transaction where 1 = 1",
                "delete from transaction_index where 1 = 1",
                "delete from account where 1 = 1",
                "delete from incubator_state where 1 = 1");
    }

    @Bean
    public RDBMSBlockChainImpl getRDBMSBlockChainImpl(JdbcTemplate tpl, TransactionTemplate txtmpl, Block genesis, ApplicationContext ctx, BlockChainOptional blockChainOptional) {
        return new RDBMSBlockChainImpl(tpl, txtmpl, genesis, ctx, "", true, blockChainOptional);
    }

    @Bean
    public BlockChainOptional blockChainOptional(JdbcTemplate template, Block genesis) {
        return new BlockChainOptional(template, genesis);
    }

    @Bean
    @Scope("prototype")
    public KeyPair getKeyPair() {
        return Ed25519.GenerateKeyPair();
    }
}
