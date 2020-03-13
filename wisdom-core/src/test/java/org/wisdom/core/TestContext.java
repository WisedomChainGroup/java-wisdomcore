package org.wisdom.core;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.wisdom.dao.HeaderDao;
import org.wisdom.dao.TransactionDao;
import org.wisdom.dao.TransactionDaoJoined;
import org.wisdom.dao.TransactionIndexDao;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;

import javax.persistence.EntityManager;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("org.wisdom.dao")
@EnableJpaRepositories("org.wisdom.dao")
@EntityScan("org.wisdom.entity")
public class TestContext {
    @Bean
    public WisdomBlockChain wisdomBlockChain(
            HeaderDao headerDao,
            TransactionDao transactionDao,
            TransactionIndexDao transactionIndexDao,
            TransactionDaoJoined transactionDaoJoined,
            Block genesis,
            @Value("${clear-data}") boolean clearData
    ) throws Exception {
        return new MemoryCachedWisdomBlockChain(
                headerDao, transactionDao, transactionIndexDao,
                transactionDaoJoined, genesis, clearData
        );
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
    public Block block(Genesis genesis, EntityManager entityManager) throws Exception {
        return new Block(genesis);
    }

    @Bean
    public JSONEncodeDecoder jsonEncodeDecoder(){
        return new JSONEncodeDecoder();
    }
}
