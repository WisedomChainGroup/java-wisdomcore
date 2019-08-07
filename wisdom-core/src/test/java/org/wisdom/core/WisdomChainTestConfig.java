package org.wisdom.core;

import org.wisdom.config.TestConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class WisdomChainTestConfig extends TestConfig {
    @Bean
    @Scope("prototype")
    public RDBMSBlockChainImpl getRDBMSBlockChainImpl(JdbcTemplate tpl, TransactionTemplate txtmpl, Block genesis, ApplicationContext ctx, BlockChainOptional blockChainOptional) {
        clearData(tpl);
        return new RDBMSBlockChainImpl(tpl, txtmpl, genesis, ctx, "", true, blockChainOptional, true);
    }

}
