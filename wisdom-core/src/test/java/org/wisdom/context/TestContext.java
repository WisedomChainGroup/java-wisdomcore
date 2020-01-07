package org.wisdom.context;

import lombok.AllArgsConstructor;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.wisdom.command.Configuration;
import org.wisdom.consensus.pow.ProposersFactory;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.contract.AssetCode;
import org.wisdom.core.Block;
import org.wisdom.core.RDBMSBlockChainImpl;
import org.wisdom.core.StatetreeUpdate;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.event.AccountUpdatedEvent;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.db.*;
import org.wisdom.dumps.BlocksDump;
import org.wisdom.dumps.GenesisDump;
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
    public DatabaseStoreFactory databaseStoreFactory() {
        return new DatabaseStoreFactory("memory", 512, "memory");
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

    @Bean
    public ValidatorStateTrie validatorStateTrie(Block genesis, DatabaseStoreFactory factory) {
        return new ValidatorStateTrie(genesis, factory);
    }

    @Bean
    public BlocksDump blocksDump(TestConfig testConfig, WisdomBlockChain wisdomBlockChain) {
        return new BlocksDump(0.0, testConfig.getBlocksDirectory(), wisdomBlockChain);
    }

    @Bean
    public ValidatorState validatorState(Block genesis) {
        return new ValidatorState(genesis);
    }

    @Bean
    public GenesisDump genesisDump(
            TestConfig testConfig,
            JdbcTemplate jdbcTemplate,
            ValidatorState validatorState,
            CandidateStateTrie candidateStateTrie,
            BlockStreamBuilder blockStreamBuilder,
            AccountDB accountDB,
            WisdomBlockChain wisdomBlockChain,
            @Value("${wisdom.block-interval-switch-era}") int switchEra
    ) {
        return new GenesisDump(
                testConfig.getGenesisDumpOut(), jdbcTemplate, validatorState,
                candidateStateTrie, testConfig.getGenesisDumpHeight(), blockStreamBuilder,
                accountDB, wisdomBlockChain, switchEra
        );
    }


    @Bean
    public RateTable rateTable(@Value("${wisdom.block-interval-switch-era}") int switchEra) {
        RateTable rateTable = new RateTable();
        rateTable.setEra(switchEra);
        return rateTable;
    }

    @Bean
    public AccountStateUpdater accountStateUpdater(
            RateTable rateTable,
            MerkleRule merkleRule,
            Genesis genesisJSON,
            Block block) {
        AccountStateUpdater updater = new AccountStateUpdater();
        updater.setGenesis(block);
        updater.setGenesisJSON(genesisJSON);
        updater.setMerkleRule(merkleRule);
        updater.setRateTable(rateTable);
        return updater;
    }

    @Bean
    public MerkleRule merkleRule(
            @Value("${node-character}") String character,
            AccountDB accountDB,
            IncubatorDB incubatorDB,
            RateTable rateTable,
            WisdomBlockChain wisdomBlockChain,
            Configuration configuration
    ) {
        MerkleRule merkleRule = new MerkleRule(character);
        merkleRule.setAccountDB(accountDB);
        merkleRule.setConfiguration(configuration);
        merkleRule.setIncubatorDB(incubatorDB);
        merkleRule.setRateTable(rateTable);
        merkleRule.setWisdomBlockChain(wisdomBlockChain);
        return merkleRule;
    }

    @Bean
    public Configuration configuration(@Value("${transaction.day.count}") int day_count,
                                       @Value("${min.procedurefee}") long min_procedurefee,
                                       @Value("${pool.clear.days}") long poolcleardays,
                                       @Value("${transaction.nonce}") long maxnonce,
                                       @Value("${pool.queued.maxcount}") long maxqueued,
                                       @Value("${pool.pending.maxcount}") long maxpending,
                                       @Value("${pool.queuedtopending.maxcount}") long maxqpcount,
                                       @Value("${wisdom.block-interval-switch-era}") int era) {
        Configuration configuration = new Configuration();
        configuration.setMaxpending(maxpending);
        configuration.setMaxqueued(maxqueued);
        configuration.setMin_procedurefee(min_procedurefee);
        configuration.setDay_count(day_count);
        configuration.setEra(era);
        configuration.setPoolcleardays(poolcleardays);
        configuration.setMaxnonce(maxnonce);
        configuration.setMaxqpcount(maxqpcount);
        return configuration;
    }

    @Bean
    public AccountStateTrie accountStateTrie(DatabaseStoreFactory factory,
                                             Block genesis,
                                             WisdomBlockChain bc,
                                             Genesis genesisJSON,
                                             AccountStateUpdater accountStateUpdater,
                                             @Value("${wisdom.consensus.pre-built-genesis-directory}") String preBuiltGenesis) throws Exception {
        return new AccountStateTrie(factory, genesis, bc, genesisJSON, accountStateUpdater, preBuiltGenesis);
    }

    @Bean
    public ValidatorStateFactory validatorStateFactory(ValidatorState validatorState) {
        return new ValidatorStateFactory(validatorState);
    }

    @Bean
    public TargetStateFactory targetStateFactory(
            TargetState targetState,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) {
        return new TargetStateFactory(targetState, blocksPerEra);
    }

    @Bean
    public AssetCode assetCode(DatabaseStoreFactory databaseStoreFactory) {
        return new AssetCode(databaseStoreFactory);
    }

    @Bean
    public StateDB stateDB(
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            @Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra,
            @Value("${wisdom.consensus.block-interval}") int blockInterval,
            @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra,
            @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,
            WisdomBlockChain wisdomBlockChain,
            AccountDB accountDB,
            IncubatorDB incubatorDB,
            RateTable rateTable,
            MerkleRule merkleRule,
            ApplicationContext applicationContext,
            Block genesis,
            AssetCode assetCode,
            ValidatorStateFactory validatorStateFactory,
            TargetStateFactory targetStateFactory,
            ProposersFactory proposersFactory
    ) {
        StateDB stateDB = new StateDB(blocksPerEra, allowMinersJoinEra, blockInterval, blockIntervalSwitchEra, blockIntervalSwitchTo);
        stateDB.setBc(wisdomBlockChain);
        stateDB.setAccountDB(accountDB);
        stateDB.setIncubatorDB(incubatorDB);
        stateDB.setRateTable(rateTable);
        stateDB.setMerkleRule(merkleRule);
        stateDB.setCtx(applicationContext);
        stateDB.setGenesis(genesis);
        stateDB.setAssetCode(assetCode);
        stateDB.setValidatorStateFactory(validatorStateFactory);
        stateDB.setTargetStateFactory(targetStateFactory);
        stateDB.setProposersFactory(proposersFactory);
        return stateDB;
    }

    @Bean
    public EventEmitter eventEmitter(ApplicationContext applicationContext) {
        return new EventEmitter(applicationContext);
    }

    @AllArgsConstructor
    public static class EventEmitter implements ApplicationListener<NewBestBlockEvent> {
        private ApplicationContext applicationContext;

        @Override
        public void onApplicationEvent(NewBestBlockEvent event) {
            applicationContext.publishEvent(new AccountUpdatedEvent(this, event.getBlock()));
        }
    }
}
