package org.wisdom.candidate;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.core.Block;
import org.wisdom.db.CandidateStateTrie;
import org.wisdom.db.CandidateUpdater;
import org.wisdom.db.DatabaseStoreFactory;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.wisdom.candidate")
public class CandidateContext {
    @Bean
    public BlockStreamBuilder blockStreamBuilder(TestConfig testConfig) {
        return new BlockStreamBuilder(testConfig.getBlocksDirectory());
    }

    @Bean
    public CandidateStateTrie candidateStateTrie(
            Block genesis,
            Genesis genesisJSON,
            DatabaseStoreFactory factory,
            CandidateUpdater candidateUpdater,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            @Value("${wisdom.allow-miner-joins-era}") long allowMinersJoinEra,
            @Value("${miner.validators}") String validatorsFile,
            @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra,
            @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,
            @Value("${wisdom.consensus.block-interval}") int initialBlockInterval) throws Exception {
        return new CandidateStateTrie(
                genesis, genesisJSON, factory, candidateUpdater,
                blocksPerEra, allowMinersJoinEra, validatorsFile,
                blockIntervalSwitchEra, blockIntervalSwitchTo, initialBlockInterval
        );
    }

    @Bean
    public DatabaseStoreFactory databaseStoreFactory() {
        return new DatabaseStoreFactory("memory", 512, "memory");
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

    @Bean
    public JSONEncodeDecoder jsonEncodeDecoder() {
        return new JSONEncodeDecoder();
    }

    @Bean
    public CandidateUpdater candidateUpdater(
            @Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra,
            @Value("${wisdom.consensus.block-interval}") int blockInterval,
            @Value("${wisdom.wip-1217.height}") long wip1217Height,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) {
        return new CandidateUpdater(allowMinersJoinEra, blockInterval, wip1217Height, blocksPerEra);
    }
}
