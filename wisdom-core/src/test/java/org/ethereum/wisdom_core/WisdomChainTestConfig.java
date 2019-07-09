package org.ethereum.wisdom_core;

import org.ethereum.config.TestConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.wisdom.core.Block;
import org.wisdom.core.RDBMSBlockChainImpl;

public class WisdomChainTestConfig extends TestConfig {
    @Bean
    @Scope("prototype")
    public RDBMSBlockChainImpl getRDBMSBlockChainImpl(JdbcTemplate tpl, TransactionTemplate txtmpl, Block genesis, ApplicationContext ctx){
        clearData(tpl);
        return new RDBMSBlockChainImpl(tpl, txtmpl, genesis, ctx);
    }
}
