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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.wisdom.controller.WebSocket;
import org.wisdom.core.Block;
import org.wisdom.core.PendingBlocksManager;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.event.NewBlockMinedEvent;
import org.wisdom.core.validate.CheckPointRule;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.core.validate.OfficialIncubateBalanceRule;
import org.wisdom.core.validate.Result;
import org.wisdom.crypto.HashUtil;
import org.wisdom.db.AccountState;
import org.wisdom.db.AccountStateTrie;
import org.wisdom.db.AccountStateUpdater;
import org.wisdom.db.WisdomRepository;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.pool.AdoptTransPool;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.util.Address;
import org.wisdom.vm.abi.PrevNonceWrapper;
import org.wisdom.vm.abi.WASMResult;
import org.wisdom.vm.abi.WASMTXPool;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "miner")
public class Miner implements ApplicationListener {

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
    private WisdomRepository repository;

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

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    private CheckPointRule checkPointRule;

    @Autowired
    private EconomicModel economicModel;

    @Autowired
    AccountStateUpdater accountStateUpdater;

    @Autowired
    AccountStateTrie accountStateTrie;

    @Autowired
    private WASMTXPool wasmtxPool;

    private boolean allowEmptyBlock;

    // 存储事务 wasm 执行时间，避免 mining timeout
    private Cache<HexBytes, Long> cache = Caffeine.newBuilder()
            .maximumSize(1024)
            .build();

    public Miner(@Value("${miner.allow-empty-block}") String aeb) {
        allowEmptyBlock = aeb == null || aeb.isEmpty() || !"false".equals(aeb.toLowerCase().trim());
    }


    private Transaction createCoinBase(long height) throws Exception {
        Transaction tx = Transaction.createEmpty();
        tx.amount = economicModel.getConsensusRewardAtHeight1(height);
        tx.to = consensusConfig.getMinerPubKeyHash();
        return tx;
    }

    private Trie<byte[], AccountState> getTempTrie(byte[] root) {
        return accountStateTrie
                .getTrie()
                .revert(root);
    }

    private int findBestTransaction(List<Transaction> txs, long endTimestamp, boolean transferFirst){
        // 第一步 去掉相同 from 的事务，如果 from 相同取 nonce 较小的

        // public key hash -> index
        Map<byte[], Integer> indices = new ByteArrayMap<>();
        for(int i = 0; i < txs.size(); i++){
            Transaction tx = txs.get(i);
            // 优先打包普通的事务
            if(transferFirst && tx.type != Transaction.Type.WASM_DEPLOY.ordinal() && tx.type != Transaction.Type.WASM_CALL.ordinal())
                return i;
            Integer prevIndex = indices.get(tx.getFromPKHash());
            if(prevIndex == null){
                indices.put(tx.getFromPKHash(), i);
                continue;
            }
            Transaction prev = txs.get(prevIndex);
            // 如果 nonce 值更小，替换掉
            if(tx.nonce < prev.nonce){
                indices.put(tx.getFromPKHash(), i);
            }
        }

        long now = System.currentTimeMillis();

        // 第二步：找出 gasPrice 最大的事务
        return indices.values().stream()
                // 如果事务的 gas 消耗值过大可能会导致超时则跳过
                .filter(x -> now + cache.asMap().getOrDefault(HexBytes.fromBytes(txs.get(x).getHash()), 0L) < endTimestamp)
                .max((x,y) -> Long.compare(txs.get(x).gasPrice, txs.get(y).gasPrice))
                .orElse(-1);
    }

    private BlockAndTask createBlock(long endTimeStamp) throws Exception {
        Block parent = repository.getBestBlock();
        Block block = new Block();
        block.nVersion = parent.nVersion;
        block.hashPrevBlock = parent.getHash();

        // merkle state root
        block.nHeight = parent.nHeight + 1;
        block.nBits = repository.getTargetByParent(parent);
        block.nNonce = new byte[Block.HASH_SIZE];
        block.body = new ArrayList<>();
        block.body.add(createCoinBase(block.nHeight));

        long nonce = repository.getValidatorNonceAt(block.hashPrevBlock, consensusConfig.getMinerPubKeyHash());

        block.body.get(0).nonce = nonce + 1;

        //打包事务
        List<Transaction> notWrittern = packageMiner.TransferCheck(parent.getHash(), block.nHeight, block);

        // 校验官方孵化余额
        List<Transaction> newTranList = officialIncubateBalanceRule.validateTransaction(notWrittern, parent.getHash());

        byte[] tmpRoot = accountStateTrie.getRootStore().get(parent.getHash()).get();

        List<Transaction> wasmList = wasmtxPool.popPackable(new PrevNonceWrapper(newTranList, getTempTrie(tmpRoot).asMap()), -1);
        if (newTranList == null) {
            newTranList = wasmList;
        } else {
            newTranList.addAll(wasmList);
        }
        if (newTranList.isEmpty() && !allowEmptyBlock)
            return null;
        Set<String> payloads = new HashSet<>();

        // 更新 coinbase
        Map<byte[], WASMResult> results = new ByteArrayMap<>();
        Map<byte[], Transaction> included = new ByteArrayMap<>();

        // 一个区块最多包含224条事务，转账事务数量大于64条时，不再优先打包转账事务
        int includedCnt = 0;
        int includedTransferCnt = 0;
        while (!newTranList.isEmpty() && includedCnt < 224) {


            // 优先级 普通事务 > wasm 事务
            int best = findBestTransaction(newTranList, endTimeStamp, includedTransferCnt < 64);

            // best < 0 可能是存在消耗 gas 消耗很大的事务，被跳过了
            if(best < 0){
                break;
            }

            Transaction tx = newTranList.get(best);
            newTranList.remove(best);

            boolean isExit = tx.type == Transaction.Type.EXIT_VOTE.ordinal() || tx.type == Transaction.Type.EXIT_MORTGAGE.ordinal();
            if (isExit && tx.payload != null && payloads.contains(Hex.encodeHexString(tx.payload))) {
                String from = Hex.encodeHexString(Address.publicKeyToHash(tx.from));
                peningTransPool.removeOne(from, tx.nonce);
                adoptTransPool.removeOne(from, adoptTransPool.getKeyTrans(tx));
                continue;
            }
            if (isExit && tx.payload != null) {
                payloads.add(Hex.encodeHexString(tx.payload));
            }
            // 校验事务，记录事务执行消耗的时间
            try {
                Trie<byte[], AccountState> tmp = getTempTrie(tmpRoot);
                long start = System.currentTimeMillis();
                WASMResult res = accountStateTrie.update(tmp, block, tx);
                cache.asMap().put(HexBytes.fromBytes(tx.getHash()), System.currentTimeMillis() - start);
                tmpRoot = tmp.commit();
                block.body.get(0).amount += res.getGasUsed() * tx.gasPrice;
                results.put(tx.getHash(), res);
                included.put(tx.getHash(), tx);
                block.body.add(tx);

                if( tx.type != Transaction.Type.WASM_DEPLOY.ordinal() && tx.type != Transaction.Type.WASM_CALL.ordinal()){
                    includedTransferCnt++;
                }
                includedCnt++;

            } catch (Exception e) {
                // 某个事务执行报错丢弃掉后续来自该 from 的事务
                Iterator<Transaction> it = newTranList.iterator();
                while (it.hasNext()) {
                    Transaction t = it.next();
                    if (FastByteComparisons.equal(t.from, tx.from) &&
                            (t.type == Transaction.Type.WASM_DEPLOY.ordinal() ||
                                    t.type == Transaction.Type.WASM_CALL.ordinal()
                            )
                    ) {
                        it.remove();
                        wasmtxPool.drop(t, e.getMessage());
                    }
                }
                e.printStackTrace();
                wasmtxPool.drop(tx, e.getMessage());
            }
        }


        for (int i = 0; i < newTranList.size(); i++) {
            Transaction x = newTranList.get(i);
            if(x.type == Transaction.Type.WASM_DEPLOY.ordinal() || x.type == Transaction.Type.WASM_CALL.ordinal()) {
                wasmtxPool.collect(Collections.singletonList(x));
            } else {
                adoptTransPool.add(Collections.singletonList(x));
            }
        }

        // 更新 coinbase
        Trie<byte[], AccountState> trie = getTempTrie(tmpRoot);
        accountStateTrie.update(trie, block, block.body.get(0));
        block.accountStateTrieRoot = trie.commit();
        trie.flush();
        accountStateTrie.getRootStore().put(block.getHash(), block.accountStateTrieRoot);

        block.body.get(0).setHashCache(
                HashUtil.keccak256(block.body.get(0).getRawForHash())
        );

        // hash merkle root
        block.hashMerkleRoot = Block.calculateMerkleRoot(block.body);
        block.hashMerkleState = Block.calculateMerkleState(Collections.emptyList());
        block.hashMerkleIncubate = Block.calculateMerkleIncubate(new ArrayList<>());

        peningTransPool.updatePool(included.values().stream().collect(Collectors.toList()), 1, block.nHeight);

        return new BlockAndTask(block, () -> {
            for (Map.Entry<byte[], WASMResult> entry : results.entrySet()) {
                Transaction tx = included.get(entry.getKey());
                WASMResult re = entry.getValue();
                WebSocket.broadcastIncluded(tx, block.nHeight, block.getHash(), re.getGasUsed(), re.getReturns(), re.getWASMEvents());
            }
        });
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
        // check checkpoint in db
        Result result = checkPointRule.validateDBCheckPoint();
        if (!result.isSuccess()) {
            return;
        }
        Block bestBlock = repository.getBestBlock();
        // 判断是否轮到自己出块
        Optional<Proposer> p;
        try {
            p = repository.getProposerByParentAndEpoch(bestBlock, System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        p.ifPresent(proposer -> {
            if (!FastByteComparisons.equal(proposer.pubkeyHash, consensusConfig.getMinerPubKeyHash())) {
                return;
            }
            try {
                // 预留时间给工作量证明，避免 mining timeout 发生
                long avg = MineThread.powAvg();
                BlockAndTask b = createBlock(p.get().endTimeStamp * 1000 - 1 - avg);
                if (b == null)
                    return;
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
            log.info("new block mined at height {}", o.nHeight);
        }
        if (event instanceof NewBestBlockEvent && thread != null) {
            thread.terminate();
        }
    }

}
