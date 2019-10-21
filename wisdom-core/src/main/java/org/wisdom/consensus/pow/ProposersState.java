package org.wisdom.consensus.pow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.math3.fraction.BigFraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.wisdom.Start;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.core.state.State;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 投票事务
 * 抵押事务
 * 撤回投票事务
 * 撤回抵押事务
 * 2019-10-11 增加投票衰减功能
 */
@Component
public class ProposersState implements State {
    public static Logger logger = LoggerFactory.getLogger(ProposersState.class);
    private static final long MINIMUM_PROPOSER_MORTGAGE = 100000 * EconomicModel.WDC;
    private static final int MAXIMUM_PROPOSERS = 2;

    // 投票数每次衰减 10%
    private static final BigFraction ATTENUATION_COEFFICIENT = new BigFraction(9, 10);
    private static final long ATTENUATION_ERAS = 2160;

    public static class Proposer {
        public long mortgage;

        // transaction hash -> votes
        @JsonIgnore
        private Map<String, Long> receivedVotes;

        @JsonIgnore
        // transaction hash -> count
        private Map<String, Long> erasCounter;

        @JsonIgnore
        private long votesCache;

        public String publicKeyHash;

        Proposer() {
            receivedVotes = new HashMap<>();
            erasCounter = new HashMap<>();
        }

        Proposer(long mortgage, String publicKeyHash, Map<String, Long> receivedVotes, Map<String, Long> erasCounter) {
            this.mortgage = mortgage;
            this.publicKeyHash = publicKeyHash;
            this.receivedVotes = receivedVotes;
            this.erasCounter = erasCounter;
        }

        public Proposer copy() {
            return new Proposer(mortgage, publicKeyHash, new HashMap<>(receivedVotes), new HashMap<>(erasCounter));
        }

        public long getVotes() {
            if(votesCache != 0){
                return votesCache;
            }
            this.votesCache = receivedVotes.values().stream().reduce(Long::sum).orElse(0L);
            return this.votesCache;
        }

        void increaseEraCounters(){
            erasCounter.replaceAll((k, v) -> v + 1);
        }

        void attenuation(){
            for(String k: erasCounter.keySet()){
                if(erasCounter.get(k) < ATTENUATION_ERAS){
                    continue;
                }
                erasCounter.put(k, 0L);
                receivedVotes.put(k,
                        new BigFraction(receivedVotes.get(k), 1L)
                        .multiply(ATTENUATION_COEFFICIENT)
                        .longValue()
                );
            }
        }

        void updateTransaction(Transaction tx){
            switch (Transaction.TYPES_TABLE[tx.type]) {
                // 投票
                case VOTE: {
                    receivedVotes.put(tx.getHashHexString(), tx.amount);
                    erasCounter.put(tx.getHashHexString(), 0L);
                    return;
                }
                // 撤回投票
                case EXIT_VOTE: {
                    receivedVotes.remove(tx.getHashHexString());
                    erasCounter.remove(tx.getHashHexString());
                    return;
                }
                // 抵押
                case MORTGAGE: {
                    mortgage += tx.amount;
                    return;
                }
                // 抵押撤回
                case EXIT_MORTGAGE: {
                    mortgage -= tx.amount;
                    if (mortgage < 0) {
                        logger.error("mortgage < 0");
                    }
                }
            }
        }
    }

    public Map<String, Proposer> getAll() {
        return all;
    }

    public Set<String> getBlockList() {
        return blockList;
    }

    private Map<String, Proposer> all;
    private List<String> proposers;
    private Set<String> blockList;
    private int allowMinersJoinEra;
    private int blockInterval;
    private List<Proposer> candidatesCache;

    @Autowired
    public ProposersState(
            @Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra,
            @Value("${wisdom.consensus.block-interval}") int blockInterval
    ) {
        all = new HashMap<>();
        blockList = new HashSet<>();
        proposers = new ArrayList<>();
        this.allowMinersJoinEra = allowMinersJoinEra;
        this.blockInterval = blockInterval;
    }

    public List<Proposer> getProposers() {
        return proposers.stream()
                .map(k -> all.get(k))
                .collect(Collectors.toList());
    }

    public List<Proposer> getCandidates() {
        if(this.candidatesCache != null){
            return candidatesCache;
        }
        this.candidatesCache = getAll().values()
                .stream()
                .filter(p -> !blockList.contains(p.publicKeyHash))
                .filter(p -> p.mortgage >= MINIMUM_PROPOSER_MORTGAGE)
                .sorted((x, y) -> -compareProposer(x, y))
                .collect(Collectors.toList());
        return this.candidatesCache;
//        for (int i = 0; i < candidates.size() - 1; i++) {
//            Proposer x = candidates.get(i);
//            Proposer y = candidates.get(i + 1);
//            assert compareProposer(x, y) >= 0;
//        }
    }

    @Override
    public State updateBlock(Block block) {
        for (Transaction t : block.body) {
            updateTransaction(t);
        }
        return this;
    }

    private static int compareProposer(Proposer x, Proposer y) {
        if (x.getVotes() != y.getVotes()) {
            return Long.compare(x.getVotes(), y.getVotes());
        }
        if (x.mortgage != y.mortgage) {
            return Long.compare(x.mortgage, y.mortgage);
        }
        return x.publicKeyHash.compareTo(y.publicKeyHash);
    }

    @Override
    public State updateBlocks(List<Block> blocks) {
        for(Proposer p: all.values()){
            p.increaseEraCounters();
            p.attenuation();
        }
        boolean enableMultiMiners = allowMinersJoinEra >= 0 && EraLinkedStateFactory.getEraAtBlockNumber(
                blocks.get(0).nHeight, blockInterval
        ) >= allowMinersJoinEra;

        // 统计出块数量
        int[] proposals = new int[proposers.size()];
        for (Block b : blocks) {
            if (Start.ENABLE_ASSERTION){
                Assert.isTrue(b.body != null && b.body.size() > 0, "empty block body");
            }
            updateBlock(b);
            int idx = proposers.indexOf(Hex.encodeHexString(b.body.get(0).to));
            if (idx < 0 || idx >= proposals.length) {
                continue;
            }
            proposals[idx]++;
        }
        // 拉黑不出块的节点
        for (int i = 0; i < proposals.length && enableMultiMiners; i++) {
            if (proposals[i] > 0) {
                continue;
            }
            logger.info("block the proposer " + proposers.get(i));
            blockList.add(proposers.get(i));
        }
        // 重新生成 proposers
        proposers = all.values().stream()
                // 过滤掉黑名单中节点
                .filter(p -> !blockList.contains(p.publicKeyHash))
                // 过滤掉抵押数量不足的节点
                .filter(p -> p.mortgage >= MINIMUM_PROPOSER_MORTGAGE)
                // 按照 投票，抵押，字典从大到小排序
                .sorted((x, y) -> -compareProposer(x, y))
                .limit(MAXIMUM_PROPOSERS)
                .map(p -> p.publicKeyHash).collect(Collectors.toList());

//        for (int i = 0; i < proposers.size() - 1; i++) {
//            Proposer x = all.get(proposers.get(i));
//            Proposer y = all.get(proposers.get(i + 1));
//            assert compareProposer(x, y) >= 0;
//        }
        return this;
    }

    @Override
    public State updateTransaction(Transaction transaction) {
        Proposer p = all.get(Hex.encodeHexString(transaction.to));
        if (p == null) {
            p = new Proposer();
            p.publicKeyHash = Hex.encodeHexString(transaction.to);
        }
        p.updateTransaction(transaction);
        all.put(p.publicKeyHash, p);
        return this;
    }

    @Override
    public State copy() {
        ProposersState state = new ProposersState(this.allowMinersJoinEra, this.blockInterval);
        state.all = new HashMap<>();
        for (String key : all.keySet()) {
            state.all.put(key, all.get(key).copy());
        }
        state.blockList = new HashSet<>(blockList);
        state.proposers = new ArrayList<>();
        if (proposers == null) {
            return state;
        }
        state.proposers = new ArrayList<>(proposers);
        return state;
    }
}
