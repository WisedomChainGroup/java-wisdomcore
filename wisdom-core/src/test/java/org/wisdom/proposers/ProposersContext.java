package org.wisdom.proposers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.wisdom.consensus.pow.ProposersFactory;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.context.BlockStreamBuilder;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.wisdom.proposers")
public class ProposersContext {
    @Bean
    public ProposersFactory proposersFactory(
            ProposersState genesisState,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            @Value("${miner.validators}") String validatorsFile) throws Exception {
        return new ProposersFactory(genesisState, blocksPerEra, validatorsFile);
    }

    @Bean
    public ProposersState proposersState(
            @Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra,
            @Value("${wisdom.consensus.block-interval}") int blockInterval,
            @Value("${wisdom.wip-1217.height}") long wip1217Height

    ) {
        ProposersState proposersState = new ProposersState(allowMinersJoinEra, blockInterval);
        proposersState.setWIP_12_17_HEIGHT(wip1217Height);
        return proposersState;
    }

    @Bean
    public BlockStreamBuilder blockStreamBuilder(TestConfig testConfig) {
        return new BlockStreamBuilder(testConfig.getBlocksDirectory());
    }
}
