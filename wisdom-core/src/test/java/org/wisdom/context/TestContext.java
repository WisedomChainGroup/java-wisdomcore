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
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.wisdom.command.Configuration;
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.consensus.pow.*;
import org.wisdom.contract.AssetCode;
import org.wisdom.core.Block;
import org.wisdom.core.RDBMSBlockChainImpl;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.event.AccountUpdatedEvent;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.core.validate.*;
import org.wisdom.db.*;
import org.wisdom.db.BlocksDump;
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
    public DatabaseStoreFactory databaseStoreFactory(@Value("${wisdom.database.type}") String type) {
        return new DatabaseStoreFactory("memory", 512, type);
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
        return new BlocksDump(testConfig.getBlocksDirectory(), wisdomBlockChain);
    }

    @Bean
    @Scope("prototype")
    public ValidatorState validatorState(Block genesis) {
        return new ValidatorState(genesis);
    }

    @Bean
    public GenesisDump genesisDump(
            TestConfig testConfig,
            JdbcTemplate jdbcTemplate,
            CandidateStateTrie candidateStateTrie,
            BlockStreamBuilder blockStreamBuilder,
            AccountDB accountDB,
            WisdomBlockChain wisdomBlockChain,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            ProposersState genesisProposersState
    ) {
        return new GenesisDump(
                testConfig.getGenesisDumpOut(), jdbcTemplate,
                candidateStateTrie, testConfig.getGenesisDumpHeight(), blockStreamBuilder,
                accountDB, wisdomBlockChain, blocksPerEra, genesisProposersState
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
            Genesis genesisJSON,
            Block block,
            WisdomBlockChain wisdomBlockChain) {
        AccountStateUpdater updater = new AccountStateUpdater();
        updater.setGenesis(block);
        updater.setGenesisJSON(genesisJSON);
        updater.setWisdomBlockChain(wisdomBlockChain);
        updater.setRateTable(rateTable);
        return updater;
    }

    @Bean
    public MerkleRule merkleRule(
            @Value("${node-character}") String character,
            AccountStateTrie accountDB,
            IncubatorDB incubatorDB,
            RateTable rateTable,
            WisdomBlockChain wisdomBlockChain,
            Configuration configuration
    ) {
        MerkleRule merkleRule = new MerkleRule(character, 0);
        merkleRule.setAccountStateTrie(accountDB);
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
                                             @Value("${wisdom.consensus.fast-sync.directory}") String fastSyncDirectory) throws Exception {
        return new AccountStateTrie(factory, genesis, bc, genesisJSON, accountStateUpdater, fastSyncDirectory);
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

//    @Bean
//    public TriesSyncManager triesSyncManager(
//            AccountStateTrie accountStateTrie,
//            ValidatorStateTrie validatorStateTrie,
//            DatabaseStoreFactory factory,
//            CandidateStateTrie candidateStateTrie,
//            AssetCodeTrie assetCodeTrie,
//            @Value("${wisdom.consensus.fast-sync.directory}") String fastSyncDirectory,
//            WisdomBlockChain bc,
//            CheckPointRule checkPointRule,
//            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
//    ) {
//        TriesSyncManager triesSyncManager = new TriesSyncManager(accountStateTrie, validatorStateTrie, factory,
//                candidateStateTrie, assetCodeTrie, fastSyncDirectory,
//                bc, checkPointRule, blocksPerEra
//        );
//        return triesSyncManager;
//    }

    @Bean
    public CheckPointRule checkPointRule(@Value("${wisdom.open-check-point}") boolean openCheckPoint,
                                         AccountDB accountDB,
                                         WisdomBlockChain wisdomBlockChain) {
        CheckPointRule checkPointRule = new CheckPointRule(openCheckPoint);
        checkPointRule.setAccountDB(accountDB);
        checkPointRule.setWisdomBlockChain(wisdomBlockChain);
        return checkPointRule;
    }


//    @Bean
//    public WisdomRepository wisdomRepository(
//            WisdomBlockChain bc,
//            TriesSyncManager triesSyncManager,
//            AccountStateTrie accountStateTrie,
//            ValidatorStateTrie validatorStateTrie,
//            CandidateStateTrie candidateStateTrie,
//            AssetCodeTrie assetCodeTrie,
//            TargetCache targetCache,
//            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
//            ApplicationContext applicationContext
//    ) throws Exception {
//        return new WisdomRepositoryWrapper(bc, triesSyncManager, accountStateTrie,
//                validatorStateTrie, candidateStateTrie, assetCodeTrie,
//                targetCache, blocksPerEra, applicationContext
//        );
//    }

//    @Bean
//    public AssetCodeTrie assetCodeTrie(Block genesis, DatabaseStoreFactory factory) {
//        return new AssetCodeTrie(genesis, factory);
//    }
//
//    @Bean
//    public BasicRule basicRule(Block genesis, @Value("${node-character}") String character) {
//        return new BasicRule(genesis, character);
//    }
//
//    @Bean
//    public AddressRule addressRule() {
//        return new AddressRule();
//    }
//
//    @Bean
//    public EconomicModel economicModel(@Value("${wisdom.consensus.block-interval}") int blockInterval,
//                                       @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra,
//                                       @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,
//                                       @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra) {
//        EconomicModel economicModel = new EconomicModel();
//        economicModel.setBlockInterval(blockInterval);
//        economicModel.setBlockIntervalSwitchEra(blockIntervalSwitchEra);
//        economicModel.setBlockIntervalSwitchTo(blockIntervalSwitchTo);
//        economicModel.setBlocksPerEra(blocksPerEra);
//        return economicModel;
//    }

//    @Bean
//    public CoinbaseRule coinbaseRule(WisdomRepository repository, EconomicModel economicModel) {
//        CoinbaseRule coinbaseRule = new CoinbaseRule();
//        coinbaseRule.setEconomicModel(economicModel);
//        coinbaseRule.setRepository(repository);
//        return coinbaseRule;
//    }
//
//    @Bean
//    public ConsensusConfig consensusConfig(JSONEncodeDecoder codec,
//                                           @Value("${miner.coinbase}") String coinbase,
//                                           @Value("${miner.validators}") String validatorsFile,
//                                           @Value("${wisdom.consensus.enable-mining}") boolean enableMining) throws Exception {
//
//        return new ConsensusConfig(codec, coinbase, validatorsFile, enableMining);
//    }
//
//    @Bean
//    public ConsensusRule consensusRule(ConsensusConfig consensusConfig, WisdomRepository repository) {
//        ConsensusRule consensusRule = new ConsensusRule();
//        consensusRule.setConsensusConfig(consensusConfig);
//        consensusRule.setRepository(repository);
//        return consensusRule;
//    }
//
//    @Bean
//    public TraceCeoAddress traceCeoAddress(@Value("${wisdom.ceo.trace}") boolean type, @Value("${wisdom.trace.address}") String CeoFrom) {
//        return new TraceCeoAddress(type, CeoFrom);
//    }
//
//    @Bean
//    public AdoptTransPool adoptTransPool(DatabaseStoreFactory factory,
//                                         Configuration configuration,
//                                         TraceCeoAddress traceCeoAddress,
//                                         @Value("${wisdom.ceo.trace}") boolean type) {
//        AdoptTransPool adoptTransPool = new AdoptTransPool(factory);
//        adoptTransPool.setConfiguration(configuration);
//        adoptTransPool.setTraceCeoAddress(traceCeoAddress);
//        adoptTransPool.setType(type);
//        return adoptTransPool;
//    }
//
//    @Bean
//    public PeningTransPool peningTransPool(DatabaseStoreFactory factory,
//                                           AdoptTransPool adoptTransPool,
//                                           WisdomBlockChain wisdomBlockChain,
//                                           @Value("${wisdom.ceo.trace}") boolean type,
//                                           TraceCeoAddress traceCeoAddress
//    ) {
//        PeningTransPool peningTransPool = new PeningTransPool(factory);
//        peningTransPool.setType(type);
//        peningTransPool.setTraceCeoAddress(traceCeoAddress);
//        peningTransPool.setWisdomBlockChain(wisdomBlockChain);
//        peningTransPool.setAdoptTransPool(adoptTransPool);
//        return peningTransPool;
//    }
//
//    @Bean
//    public TransactionCheck transactionCheck(Configuration configuration,
//                                             AccountDB accountDB,
//                                             WisdomRepository wisdomRepository,
//                                             WisdomBlockChain wisdomBlockChain,
//                                             RateTable rateTable) {
//        TransactionCheck transactionCheck = new TransactionCheck();
//        transactionCheck.setAccountDB(accountDB);
//        transactionCheck.setConfiguration(configuration);
//        transactionCheck.setRateTable(rateTable);
//        transactionCheck.setWisdomBlockChain(wisdomBlockChain);
//        transactionCheck.setWisdomRepository(wisdomRepository);
//        return transactionCheck;
//    }
//
//    @Bean
//    public WhitelistTransaction whitelistTransaction() throws IOException {
//        return new WhitelistTransaction();
//    }
//
//    @Bean
//    public AccountRule accountRule(RateTable rateTable,
//                                   PeningTransPool peningTransPool,
//                                   WisdomRepository wisdomRepository,
//                                   TransactionCheck transactionCheck,
//                                   WhitelistTransaction whitelistTransaction,
//                                   MerkleRule merkleRule,
//                                   @Value("${node-character}") String character) {
//        AccountRule accountRule = new AccountRule(character);
//        accountRule.setMerkleRule(merkleRule);
//        accountRule.setPeningTransPool(peningTransPool);
//        accountRule.setRateTable(rateTable);
//        accountRule.setTransactionCheck(transactionCheck);
//        accountRule.setWisdomRepository(wisdomRepository);
//        accountRule.setWhitelistTransaction(whitelistTransaction);
//        return accountRule;
//    }
//
//    @Bean
//    public SignatureRule signatureRule() {
//        return new SignatureRule();
//    }
//
//    @Bean
//    public CompositeBlockRule compositeBlockRule(BasicRule basicRule,
//                                                 AddressRule addressRule,
//                                                 CoinbaseRule coinbaseRule,
//                                                 ConsensusRule consensusRule,
//                                                 AccountRule accountRule,
//                                                 SignatureRule signatureRule) {
//        CompositeBlockRule rule = new CompositeBlockRule();
//        rule.setAccountRule(accountRule);
//        rule.setAddressRule(addressRule);
//        rule.setBasicRule(basicRule);
//        rule.setConsensusRule(consensusRule);
//        rule.setSignatureRule(signatureRule);
//        rule.setCoinbaseRule(coinbaseRule);
//        return rule;
//    }

}
