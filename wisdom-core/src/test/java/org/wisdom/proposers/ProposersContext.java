package org.wisdom.proposers;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.wisdom.context.BlockStreamBuilder;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.wisdom.proposers")
public class ProposersContext {

    @Bean
    public BlockStreamBuilder blockStreamBuilder(TestConfig testConfig) {
        return new BlockStreamBuilder(testConfig.getBlocksDirectory());
    }
}
