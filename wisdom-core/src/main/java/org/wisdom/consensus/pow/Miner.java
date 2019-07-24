/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.crypto.HashUtil;
import org.wisdom.core.TransactionPool;
import org.wisdom.encoding.BigEndian;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.event.NewBlockMinedEvent;
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
import org.wisdom.pool.PeningTransPool;

import java.util.*;

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

    @Autowired
    private ValidatorStateFactory factory;

    @Autowired
    MerkleRule merkleRule;

    @Autowired
    OfficialIncubateBalanceRule officialIncubateBalanceRule;

    @Autowired
    PeningTransPool peningTransPool;

    public Miner() {
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
        try{
            List<Transaction> transactionList=peningTransPool.compare();
            int totalpool=transactionList.size();
            int index=0;
            while ( totalpool> 0 && block.size() < Block.MAX_BLOCK_SIZE) {
                // TODO: 验证事务池里面的事务
                logger.info("totalpool:"+totalpool+"--->transactionList.size:"+transactionList.size());
                Transaction tx = transactionList.get(index);
                if(tx!=null){
                    if (hasValidated.contains(tx.getHashHexString())) {
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
                    index++;
                    totalpool--;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // 校验官方孵化余额
        List<Transaction> newTranList = officialIncubateBalanceRule.validateTransaction(notWrittern);
        for (Transaction tx : newTranList) {
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
        peningTransPool.updatePool(newTranList,1,block.nHeight);
        return block;
    }

    @Scheduled(fixedRate = 1000)
    public void tryMine() {
        if (thread != null
                && !thread.isTerminated()
        ) {
            return;
        }
        try {
            if (!consensusConfig.isEnableMining()) {
                return;
            }
            Block bestBlock = bc.currentBlock();
            // 判断是否轮到自己出块
            Proposer p = consensusConfig.getProposer(bestBlock, System.currentTimeMillis() / 1000);
            if (!p.pubkeyHash.equals(consensusConfig.getMinerPubKeyHash())) {
                return;
            }
            Block b = createBlock();
            thread = ctx.getBean(MineThread.class);
            thread.mine(b, p.startTimeStamp, p.endTimeStamp);
        } catch (Exception e) {
            logger.error("mining failed, exception occurred");
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof NewBlockMinedEvent) {
            Block o = ((NewBlockMinedEvent) event).getBlock();
            logger.info("new block mined event triggered");
            pendingBlocksManager.addPendingBlocks(new BlocksCache(Collections.singletonList(o)));
        }
        if (event instanceof NewBestBlockEvent && thread != null){
            thread.terminate();
        }
    }

}
