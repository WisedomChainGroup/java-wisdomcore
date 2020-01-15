package org.wisdom.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.wisdom.core.Block;
import org.wisdom.core.RDBMSBlockChainImpl;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.db.BlocksDump;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.wisdom.util")
public class DumpContext {
    @Bean
    public WisdomBlockChain wisdomBlockChain(
            JdbcTemplate jdbcTemplate,
            TransactionTemplate transactionTemplate,
            ApplicationContext applicationContext,
            Block genesis,
            @Value("${spring.datasource.username}") String databaseUserName,
            @Value("${clear-data}") boolean clearData,
            BasicDataSource basicDataSource
    ) throws Exception {
        return new RDBMSBlockChainImpl(jdbcTemplate, transactionTemplate, genesis, applicationContext, databaseUserName, clearData, basicDataSource);
    }

    @Bean
    public Genesis genesis(JSONEncodeDecoder codec, @Value("${wisdom.consensus.genesis}") String genesis)
            throws Exception {
        Resource resource = new FileSystemResource(genesis);
        if (!resource.exists()) {
            resource = new ClassPathResource(genesis);
        }
        return codec.decodeGenesis(IOUtils.toByteArray(resource.getInputStream()));
    }

    @Bean
    public JSONEncodeDecoder jsonEncodeDecoder() {
        return new JSONEncodeDecoder();
    }

    @Bean
    public BlocksDump blocksDump(
            @Value("${wisdom.consensus.fast-sync.directory}") String directory,
            WisdomBlockChain wisdomBlockChain) {
        return new BlocksDump(directory, wisdomBlockChain);
    }

    @Bean
    public Block block(Genesis genesis) throws Exception {
        return new Block(genesis);
    }
}
