package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.state.State;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 投票事务
 * 抵押事务
 * 撤回投票事务
 * 撤回抵押事务
 */
@Component
public class ProposersState implements State {
    public static Logger logger = LoggerFactory.getLogger(ProposersState.class);
    public static final long MINIMUM_PROPOSER_MORTGAGE = 100000 * EconomicModel.WDC;
    public static final int MAXIMUM_PROPOSERS = 15;

    public static class Proposer {
        public long mortgage;
        public long votes;
        public String publicKeyHash;

        public Proposer() {
        }

        public Proposer(long mortgage, long votes, String publicKeyHash) {
            this.mortgage = mortgage;
            this.votes = votes;
            this.publicKeyHash = publicKeyHash;
        }

        public Proposer copy(){
            return new Proposer(mortgage, votes, publicKeyHash);
        }
    }

    private Map<String, Proposer> all;
    private List<String> proposers;
    private Set<String> blockList;

    public ProposersState() {
        all = new HashMap<>();
        blockList = new HashSet<>();
        proposers = new ArrayList<>();
    }

    public List<Proposer> getProposers() {
        List<Proposer> res = new ArrayList<>();
        for(int i = 0; i < proposers.size() && i < MAXIMUM_PROPOSERS; i++){
            res.add(all.get(proposers.get(i)));
        }
        return res;
    }

    @Override
    public State updateBlock(Block block) {
        for (Transaction t : block.body) {
            updateTransaction(t);
        }
        return this;
    }

    @Override
    public State updateBlocks(List<Block> blocks) {
        // 统计出块数量
        int[] proposals = new int[proposers.size()];
        for (Block b : blocks) {
            updateBlock(b);
            int idx = proposers.indexOf(Hex.encodeHexString(b.body.get(0).to));
            if (idx < 0 || idx >= proposals.length) {
                logger.error("index overflow invalid proposer");
                continue;
            }
            proposals[idx]++;
        }
        // 拉黑不出块的节点
        for (int i = 0; i < proposals.length; i++) {
            if (proposals[i] > 0) {
                continue;
            }
            blockList.add(proposers.get(i));
        }
        // 重新生成 proposers
        proposers = all.values().stream()
                // 过滤掉黑名单中节点
                .filter(p -> !blockList.contains(p.publicKeyHash))
                // 过滤掉抵押数量不足的节点
                .filter(p -> p.mortgage >= MINIMUM_PROPOSER_MORTGAGE)
                // 按照 投票，抵押，字典依次排序
                .sorted((x, y) -> {
                    if (x.votes != y.votes) {
                        return (int) (y.votes - x.votes);
                    }
                    if (x.mortgage != y.votes) {
                        return (int) (y.mortgage - x.mortgage);
                    }
                    return y.publicKeyHash.compareTo(x.publicKeyHash);
                }).map(p -> p.publicKeyHash).collect(Collectors.toList());
        return this;
    }

    @Override
    public State updateTransaction(Transaction transaction) {
        Proposer p = all.get(Hex.encodeHexString(transaction.to));
        if (p == null) {
            p = new Proposer();
            p.publicKeyHash = Hex.encodeHexString(transaction.to);
        }
        switch (transaction.type) {
            // 投票
            case 0x02: {
                p.votes += transaction.amount;
                all.put(p.publicKeyHash, p);
                return this;
            }
            // 撤回投票
            case 0x0d: {
                p.votes -= transaction.amount;
                if (p.votes < 0) {
                    logger.error("votes < 0");
                }
                all.put(p.publicKeyHash, p);
                return this;
            }
            // 抵押
            case 0x0e: {
                p.mortgage += transaction.amount;
                all.put(p.publicKeyHash, p);
                return this;
            }
            // 抵押撤回
            case 0x0f: {
                p.mortgage -= transaction.amount;
                if (p.mortgage < 0) {
                    logger.error("mortgage < 0");
                }
                return this;
            }
        }
        return this;
    }

    @Override
    public State copy() {
        ProposersState state = new ProposersState();
        state.all = new HashMap<>();
        for(String key: all.keySet()){
            state.all.put(key, all.get(key).copy());
        }
        state.blockList = new HashSet<>(blockList);
        state.proposers = new ArrayList<>();
        if (proposers == null){
            return state;
        }
        state.proposers = new ArrayList<>(proposers);
        return state;
    }
}
