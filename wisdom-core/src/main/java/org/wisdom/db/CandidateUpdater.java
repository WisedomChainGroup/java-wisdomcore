package org.wisdom.db;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;

import org.tdf.common.util.FastByteComparisons;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CandidateUpdater {
    @Getter
    private long WIP_12_17_HEIGHT;

    static final long MINIMUM_PROPOSER_MORTGAGE = 100000 * EconomicModel.WDC;
    static final int MAXIMUM_PROPOSERS = getenv("MAXIMUM_PROPOSERS", 15);
    public static final int COMMUNITY_MINER_JOINS_HEIGHT = getenv("COMMUNITY_MINER_JOINS_HEIGHT", 522215);

    private int allowMinersJoinEra;
    private int blockInterval;

    @Setter
    private CandidateStateTrie candidateStateTrie;

    @Setter
    private WisdomRepository repository;

    private static int getenv(String key, int defaultValue) {
        String v = System.getenv(key);
        if (v == null || v.equals("")) return defaultValue;
        return Integer.parseInt(v);
    }

    private boolean atJoinEra(Collection<Block> blocks) {
        return blocks.stream().anyMatch(b -> b.getnHeight() >= COMMUNITY_MINER_JOINS_HEIGHT
                && blocks.stream().anyMatch(x -> x.getnHeight() < COMMUNITY_MINER_JOINS_HEIGHT));
    }

    public CandidateUpdater(@Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra,
                            @Value("${wisdom.consensus.block-interval}") int blockInterval,
                            @Value("${wisdom.wip-1217.height}") long WIP_12_17_HEIGHT
    ) {
        this.allowMinersJoinEra = allowMinersJoinEra;
        this.blockInterval = blockInterval;
        this.WIP_12_17_HEIGHT = WIP_12_17_HEIGHT;
    }


    public Map<byte[], Candidate> updateAll(Map<byte[], Candidate> beforeUpdates, List<Block> blocks) {
        Map<byte[], Candidate> res = copy(beforeUpdates);
        Map<byte[], Long> proposals = new ByteArrayMap<>();

        candidateStateTrie
                .getProposers(repository.getBlock(blocks.get(0).hashPrevBlock))
                .forEach(h -> proposals.put(h, 0L));

        blocks.forEach(block -> {
            byte[] proposer = block.body.get(0).to;
            if (!proposals.containsKey(proposer)) throw new RuntimeException("invalid proposal");

            proposals.put(proposer, proposals.get(proposer) + 1);
            for (Transaction tx : block.body) {
                res.values().forEach(c -> updateOne(tx, c));
            }
        });

        // 拉黑不出块的节点
        List<byte[]> toBlock = proposals.keySet().stream()
                .filter(k -> proposals.get(k) == 0).collect(Collectors.toList());

        toBlock.forEach(k -> res.get(k).setBlocked(true));

        // delete all block list after community miner joins
        if (atJoinEra(blocks)) {
            res.values().forEach(c -> c.setBlocked(false));
        }
        return res;
    }


    private Map<byte[], Candidate> copy(Map<byte[], Candidate> beforeUpdates) {
        Map<byte[], Candidate> res = new ByteArrayMap<>();
        for (Map.Entry<byte[], Candidate> entry : beforeUpdates.entrySet()) {
            res.put(entry.getKey(), entry.getValue().copy());
        }
        return res;
    }

    private void updateOne(Transaction tx, Candidate candidate) {
        if (!FastByteComparisons.equal(tx.to, candidate.getPublicKeyHash())) return;
        switch (Transaction.TYPES_TABLE[tx.type]) {
            case VOTE:
                candidate.getReceivedVotes()
                        .put(tx.getHash(), new Vote(
                                PublicKeyHash.fromPublicKey(tx.from).getPublicKeyHash(), tx.amount, tx.amount));
                return;
            // 撤回投票
            case EXIT_VOTE:
                candidate.getReceivedVotes()
                        .remove(tx.payload);
                return;
            case MORTGAGE:
                candidate.setMortgage(candidate.getMortgage() + tx.amount);
                return;
            case EXIT_MORTGAGE:
                candidate.setMortgage(candidate.getMortgage() - tx.amount);
                if (candidate.getMortgage() < 0) {
                    log.error("mortgage < 0");
                }
        }
    }

    public Set<byte[]> getRelatedCandidates(List<Block> blocks) {
        if (atJoinEra(blocks)) {
            return candidateStateTrie.getTrie()
                    .revert(candidateStateTrie.getRootStore().get(blocks.get(0).hashPrevBlock).get())
                    .keySet();
        }

        Set<byte[]> ret = new ByteArraySet();
        for (Block block : blocks) {
            block.body.stream().map(this::getRelatedCandidates)
                    .forEach(ret::addAll);
        }
        ret.addAll(candidateStateTrie.getProposers(
                repository.getBlock(blocks.get(0).hashPrevBlock)));
        return ret;
    }

    public Set<byte[]> getRelatedCandidates(Transaction tx) {
        switch (Transaction.TYPES_TABLE[tx.type]) {
            case VOTE:
            case EXIT_VOTE:
            case MORTGAGE:
            case EXIT_MORTGAGE:
                return Collections.singleton(tx.to);
            default:
                return Collections.emptySet();
        }
    }
}
