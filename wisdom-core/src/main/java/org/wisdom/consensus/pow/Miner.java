package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.crypto.HashUtil;
import org.wisdom.core.TransactionPool;
import org.wisdom.encoding.BigEndian;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.event.NewBlockMinedEvent;
import org.wisdom.core.event.AccountUpdatedEvent;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.core.validate.OfficialIncubateBalanceRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.core.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Miner implements ApplicationListener {

    private static final Logger logger = LoggerFactory.getLogger(Miner.class);
    private static final int MAX_CACHE_SIZE = 1000;

    @Autowired
    private ConsensusConfig consensusConfig;

    private volatile MineThread thread;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private JSONEncodeDecoder jsonEncodeDecoder;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private TargetStateFactory targetStateFactory;

    @Autowired
    private TransactionPool txPool;

    @Autowired
    private PendingBlocksManager pendingBlocksManager;

    private ReentrantLock reentrantLock;

    @Autowired
    private ValidatorStateFactory factory;

    @Autowired
    MerkleRule merkleRule;

    @Autowired
    OfficialIncubateBalanceRule officialIncubateBalanceRule;

    private TreeSet<Long> hasMined;

    private TreeSet<Long> hasUpdated;

    private Block bestBlock;

    public Miner() {
        reentrantLock = new ReentrantLock();
        hasMined = new TreeSet<>();
        hasUpdated = new TreeSet<>();
    }

    private Transaction createCoinBase(long height) throws Exception {
        Transaction tx = Transaction.createEmpty();
        tx.amount = EconomicModel.getConsensusRewardAtHeight(height);
        tx.to = Hex.decodeHex(consensusConfig.getMinerPubKeyHash().toCharArray());
        return tx;
    }

    private Block createBlock() throws Exception {
        Block parent = bc.currentBlock();
        Block block = new Block();
        block.nVersion = parent.nVersion;
        block.hashPrevBlock = parent.getHash();

        // merkle state root
        block.nHeight = parent.nHeight + 1;
        block.nBits = BigEndian.encodeUint256(targetStateFactory.getInstance(block).getTarget());
        block.nNonce = new byte[Block.HASH_SIZE];
        block.body = new ArrayList<>();
        block.body.add(createCoinBase(block.nHeight));

        long nonce = factory.getInstance(parent).
                getNonceFromPublicKeyHash(block.body.get(0).to);

        block.body.get(0).nonce = nonce + 1;

        // 防止 account 重复
        Set<String> hasValidated = new HashSet<>();
        List<Transaction> notWrittern = new ArrayList<>();
        while (txPool.size() > 0 && block.size() < Block.MAX_BLOCK_SIZE) {
            // TODO: 验证事务池里面的事务
            Transaction tx = txPool.poll();
            if(hasValidated.contains(tx.getHashHexString())){
                continue;
            }
            hasValidated.add(Hex.encodeHexString(tx.from));
            // 校验需要事务
            tx.height = block.nHeight;

            // 防止写入重复的事务
            if (bc.hasTransaction(tx.getHash())) {
                continue;
            }

            // nonce 校验
            notWrittern.add(tx);
        }

        // 校验官方孵化余额
        List<Transaction> newTranList = officialIncubateBalanceRule.validateTransaction(notWrittern);
        for(Transaction tx: newTranList){
            block.body.get(0).amount += tx.getFee();
            block.body.add(tx);
        }
        block.body.get(0).setHashCache(
                HashUtil.keccak256(block.body.get(0).getRawForHash())
        );

        Map<String, Object> merklemap = merkleRule.validateMerkle(block.body, block.nHeight);
        List<Account> accountList = (List<Account>) merklemap.get("account");
        List<Incubator> incubatorList = (List<Incubator>) merklemap.get("incubator");
        // hash merkle root
        block.hashMerkleRoot = Block.calculateMerkleRoot(block.body);
        block.hashMerkleState = Block.calculateMerkleState(accountList);
        block.hashMerkleIncubate = Block.calculateMerkleIncubate(incubatorList);
        return block;
    }

    @Scheduled(fixedRate = 1000)
    public void tryMine() {
        reentrantLock.lock();
        try {
            if (!consensusConfig.isEnableMining()) {
                return;
            }
            if (bestBlock == null) {
                bestBlock = bc.currentBlock();
            }
            // avoid mine a block at same height more than once
            if (hasMined.contains(bestBlock.nHeight + 1)) {
                return;
            }
            // 状态更新完成
            if(!hasUpdated.contains(bestBlock.nHeight) && bestBlock.nHeight != 0){
                return;
            }
            // 判断是否轮到自己出块
            long endTime = consensusConfig.getEndTime(bestBlock, System.currentTimeMillis() / 1000, consensusConfig.getMinerPubKeyHash());
            if (endTime < 0){
                return;
            }

            hasMined.add(bestBlock.nHeight + 1);
            if (hasMined.size() > MAX_CACHE_SIZE) {
                hasMined.remove(hasMined.first());
            }

            Block b = createBlock();
            thread = ctx.getBean(MineThread.class);
            thread.mine(b, bestBlock.nTime, endTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    @PostConstruct
    public void init(){
        this.bestBlock = bc.currentBlock();
        addHasUpdated(this.bestBlock.nHeight);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        reentrantLock.lock();
        if (event instanceof NewBlockMinedEvent) {
            Block o = ((NewBlockMinedEvent) event).getBlock();
            pendingBlocksManager.addPendingBlocks(new BlocksCache(Collections.singletonList(o)));
        }
        if(event instanceof NewBestBlockEvent){
            updateBestBlock(((NewBestBlockEvent) event).getBlock());
        }
        if(event instanceof AccountUpdatedEvent){
            addHasUpdated(((AccountUpdatedEvent) event).getBestBlock().nHeight);
        }
        reentrantLock.unlock();
    }

    private void addHasMined(long height){
        hasMined.add(height);
        if(hasMined.size() > MAX_CACHE_SIZE){
            hasMined.remove(hasMined.first());
        }
    }

    private void addHasUpdated(long height){
        hasUpdated.add(height);
        if (hasUpdated.size() > MAX_CACHE_SIZE) {
            hasUpdated.remove(hasUpdated.first());
        }
    }

    private void updateBestBlock(Block bestBlock){
        this.bestBlock = bestBlock;
        addHasMined(bestBlock.nHeight);
    }
}
