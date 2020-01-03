package org.wisdom.context;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
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
import org.wisdom.consensus.pow.ProposersFactory;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.core.Block;
import org.wisdom.core.RDBMSBlockChainImpl;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.db.CandidateUpdater;
import org.wisdom.db.TargetCache;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.wisdom.context")
public class TestContext {
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
        System.out.println(genesis);
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
    public IncubatorDB incubatorDB(JdbcTemplate jdbcTemplate) {
        IncubatorDB incubatorDB = new IncubatorDB();
        incubatorDB.setTmpl(jdbcTemplate);
        return incubatorDB;
    }

    @Bean
    public AccountDB accountDB(JdbcTemplate jdbcTemplate, IncubatorDB incubatorDB) {
        AccountDB db = new AccountDB();
        db.setTmpl(jdbcTemplate);
        db.setIncubatorDB(incubatorDB);
        return db;
    }

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
    public CandidateUpdater candidateUpdater(
            @Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra,
            @Value("${wisdom.consensus.block-interval}") int blockInterval,
            @Value("${wisdom.wip-1217.height}") long wip1217Height,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) {
        return new CandidateUpdater(allowMinersJoinEra, blockInterval, wip1217Height, blocksPerEra);
    }

    @Bean
    public BlockStreamBuilder blockStreamBuilder(TestConfig testConfig) {
        return new BlockStreamBuilder(testConfig.getBlocksDirectory());
    }

    @Bean
    public TargetCache targetCache(
            Block genesis,
            @Value("${wisdom.consensus.block-interval}") int blockInterval,
            @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra,
            @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra) {
        return new TargetCache(genesis, blockInterval, blockIntervalSwitchEra, blockIntervalSwitchTo, blocksPerEra);
    }

    @Bean
    public TargetState targetState(Block genesis,
                                   @Value("${wisdom.consensus.block-interval}") int blockInterval,
                                   @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra,
                                   @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,
                                   @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra) {
        return new TargetState(genesis, blockInterval, blockIntervalSwitchEra, blockIntervalSwitchTo, blocksPerEra);
    }
}
