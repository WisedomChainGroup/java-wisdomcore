package org.wisdom.core;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.wisdom.dao.HeaderDao;
import org.wisdom.dao.TransactionDao;
import org.wisdom.dao.TransactionIndexDao;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("org.wisdom.dao")
public class TestContext {
    @Bean
    public WisdomBlockChain wisdomBlockChain(
            HeaderDao headerDao,
            TransactionDao transactionDao,
            TransactionIndexDao transactionIndexDao,
            Block genesis,
            @Value("${clear-data}") boolean clearData
    ) throws Exception {
        return new MemoryCachedWisdomBlockChain(headerDao, transactionDao, transactionIndexDao, genesis, clearData);
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
    public Block block(Genesis genesis) throws Exception {
        return new Block(genesis);
    }

}
