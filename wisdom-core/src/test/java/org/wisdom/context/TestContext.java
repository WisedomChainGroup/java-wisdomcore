package org.wisdom.context;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wisdom.command.Configuration;
import org.wisdom.consensus.pow.*;
import org.wisdom.contract.AssetCode;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.event.AccountUpdatedEvent;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.core.validate.*;
import org.wisdom.db.*;
import org.wisdom.db.BlocksDump;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.wisdom.context")
public class TestContext {


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
    public BlocksDump blocksDump(TestConfig testConfig, WisdomBlockChain wisdomBlockChain) {
        return new BlocksDump(testConfig.getBlocksDirectory(), wisdomBlockChain);
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
            RateTable rateTable,
            WisdomBlockChain wisdomBlockChain,
            Configuration configuration
    ) {
        MerkleRule merkleRule = new MerkleRule(character, 0);
        merkleRule.setAccountStateTrie(accountDB);
        merkleRule.setConfiguration(configuration);
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

    @Bean
    public CheckPointRule checkPointRule(@Value("${wisdom.open-check-point}") boolean openCheckPoint,
                                         AccountDB accountDB,
                                         WisdomBlockChain wisdomBlockChain) {
        CheckPointRule checkPointRule = new CheckPointRule(openCheckPoint);
//        checkPointRule.setAccountDB(accountDB);
        checkPointRule.setWisdomBlockChain(wisdomBlockChain);
        return checkPointRule;
    }
}
