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
import org.wisdom.db.StateDB;
import org.wisdom.encoding.BigEndian;
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
import org.wisdom.encoding.JSONEncodeDecoder;
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
    private JSONEncodeDecoder codec;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private StateDB stateDB;

    @Autowired
    private PendingBlocksManager pendingBlocksManager;

    @Autowired
    MerkleRule merkleRule;

    @Autowired
    OfficialIncubateBalanceRule officialIncubateBalanceRule;

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    PackageMiner packageMiner;


    public Miner() {
    }

    private Transaction createCoinBase(long height) throws Exception {
        Transaction tx = Transaction.createEmpty();
        tx.amount = EconomicModel.getConsensusRewardAtHeight(height);
        tx.to = Hex.decodeHex(consensusConfig.getMinerPubKeyHash().toCharArray());
        return tx;
    }

    private Block createBlock() throws Exception {
        Block parent = stateDB.getBestBlock();
        Block block = new Block();
        block.nVersion = parent.nVersion;
        block.hashPrevBlock = parent.getHash();

        // merkle state root
        block.nHeight = parent.nHeight + 1;
        TargetState targetState = (TargetState) stateDB.getTargetStateFactory().getInstance(block);
        block.nBits = BigEndian.encodeUint256(targetState.getTarget());
        block.nNonce = new byte[Block.HASH_SIZE];
        block.body = new ArrayList<>();
        block.body.add(createCoinBase(block.nHeight));
        ValidatorState validatorState = (ValidatorState) stateDB.getValidatorStateFactory().getInstance(parent);
        long nonce = validatorState.
                getNonceFromPublicKeyHash(block.body.get(0).to);

        block.body.get(0).nonce = nonce + 1;

        //打包事务
        List<Transaction> notWrittern = packageMiner.TransferCheck(parent.getHash(), block.nHeight, block);

        // 校验官方孵化余额
        List<Transaction> newTranList = officialIncubateBalanceRule.validateTransaction(notWrittern);
        Set<String> payloads = new HashSet<>();
        for (Transaction tx : newTranList) {
            boolean isExit = tx.type == Transaction.Type.EXIT_VOTE.ordinal() || tx.type == Transaction.Type.EXIT_MORTGAGE.ordinal();
            if(isExit && payloads.contains(Hex.encodeHexString(tx.payload))){
                continue;
            }
            if(isExit){
                payloads.add(Hex.encodeHexString(tx.payload));
            }
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
        peningTransPool.updatePool(newTranList, 1, block.nHeight);
        return block;
    }

    @Scheduled(fixedRate = 1000)
    public void tryMine() {
        if (thread != null
                && !thread.isTerminated()
        ) {
            return;
        }
        if (!consensusConfig.isEnableMining()) {
            return;
        }
        Block bestBlock = stateDB.getBestBlock();
        // 判断是否轮到自己出块
        Optional<Proposer> p = stateDB.getProposersFactory().getProposer(bestBlock, System.currentTimeMillis() / 1000);
        p.ifPresent(proposer -> {
            if (!proposer.pubkeyHash.equals(consensusConfig.getMinerPubKeyHash())) {
                return;
            }
            try {
                Block b = createBlock();
                thread = ctx.getBean(MineThread.class);
                thread.mine(b, proposer.startTimeStamp, proposer.endTimeStamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof NewBlockMinedEvent) {
            Block o = ((NewBlockMinedEvent) event).getBlock();
            logger.info("new block mined event triggered");
            pendingBlocksManager.addPendingBlocks(new BlocksCache(o));
        }
        if (event instanceof NewBestBlockEvent && thread != null) {
            thread.terminate();
        }
    }

}
