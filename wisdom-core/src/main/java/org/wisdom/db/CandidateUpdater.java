package org.wisdom.db;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;

import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CandidateUpdater extends AbstractStateUpdater<Candidate> {
    @Getter
    private long WIP_12_17_HEIGHT;

    static final long MINIMUM_PROPOSER_MORTGAGE = 100000 * EconomicModel.WDC;
    static final int MAXIMUM_PROPOSERS = getenv("MAXIMUM_PROPOSERS", 15);
    public static final int COMMUNITY_MINER_JOINS_HEIGHT = getenv("COMMUNITY_MINER_JOINS_HEIGHT", 522215);

    private int allowMinersJoinEra;
    private int blockInterval;

    @Setter
    private CandidateStateTrie candidateStateTrie;

    private WisdomRepository repository;

    public void setRepository(WisdomRepository repository) {
        this.repository = repository;
        eraLinker.setRepository(repository);
    }

    private EraLinker eraLinker;

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
                            @Value("${wisdom.wip-1217.height}") long WIP_12_17_HEIGHT,
                            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) {
        this.allowMinersJoinEra = allowMinersJoinEra;
        this.blockInterval = blockInterval;
        this.WIP_12_17_HEIGHT = WIP_12_17_HEIGHT;
        this.eraLinker = new EraLinker(blocksPerEra);
    }

    @Override
    public Map<byte[], Candidate> getGenesisStates() {
        return Collections.emptyMap();
    }

    @Override
    public Set<byte[]> getRelatedKeys(Transaction transaction, Map<byte[], Candidate> store) {
        switch (Transaction.TYPES_TABLE[transaction.type]) {
            case VOTE:
            case EXIT_VOTE:
            case MORTGAGE:
            case EXIT_MORTGAGE:
                return Collections.singleton(transaction.to);
            default:
                return Collections.emptySet();
        }
    }

    @Override
    public Set<byte[]> getRelatedKeys(List<Block> blocks, Map<byte[], Candidate> store) {
        Set<byte[]> related = super.getRelatedKeys(blocks, store);

        if (atJoinEra(blocks)) {
            related.addAll(
                    candidateStateTrie.getTrie()
                    .revert(candidateStateTrie.getRootStore().get(blocks.get(0).hashPrevBlock).get())
                    .keySet());
            return related;
        }

        related.addAll(
                candidateStateTrie
                .getBestCandidatesCache()
                .getOrDefault(HexBytes.fromBytes(blocks.get(0).hashPrevBlock), Collections.emptyList())
                .stream().map(c -> c.getPublicKeyHash().getBytes())
                .collect(Collectors.toList())
        );

        return related;
    }

    @Override
    public Candidate update(Map<byte[], Candidate> related, byte[] id, Candidate state, TransactionInfo transaction) {
        throw new RuntimeException("not implemented");
    }

    private Candidate updateInternal(byte[] id, Candidate candidate, List<Block> blocks, Transaction tx) {
        if (!FastByteComparisons.equal(tx.to, candidate.getPublicKeyHash().getBytes()))
            throw new RuntimeException("unreachable");
        candidate = candidate.copy();
        switch (Transaction.TYPES_TABLE[tx.type]) {
            case VOTE:
                candidate.getReceivedVotes()
                        .put(tx.getHash(), new Vote(
                                PublicKeyHash.fromPublicKey(tx.from).getPublicKeyHash(),
                                tx.amount,
                                eraLinker.getEraAtBlockNumber(blocks.get(0).nHeight))
                        );
                return candidate;
            // 撤回投票
            case EXIT_VOTE:
                candidate.getReceivedVotes()
                        .remove(tx.payload);
                return candidate;
            case MORTGAGE:
                candidate.setMortgage(candidate.getMortgage() + tx.amount);
                return candidate;
            case EXIT_MORTGAGE:
                candidate.setMortgage(candidate.getMortgage() - tx.amount);
                if (candidate.getMortgage() < 0) {
                    log.error("mortgage < 0");
                }
                return candidate;
            default:
                throw new RuntimeException("unreachable");
        }
    }

    Map<byte[], Candidate> updateInternal(Map<byte[], Candidate> beforeUpdate, List<Block> blocks) {
        Map<byte[], Candidate> ret = new ByteArrayMap<>(beforeUpdate);
        blocks.stream().flatMap(b -> b.body.stream())
                .forEach(tx -> {
                    getRelatedKeys(tx, Collections.emptyMap()).forEach(k -> {
                        ret.put(k, updateInternal(k, ret.get(k), blocks, tx));
                    });
                });
        return ret;
    }

    @Override
    public Candidate createEmpty(byte[] id) {
        return Candidate.createEmpty(id);
    }

    @Override
    public Map<byte[], Candidate> update(Map<byte[], Candidate> beforeUpdates, List<Block> blocks) {
        Map<byte[], Candidate> ret = updateInternal(beforeUpdates, blocks);
        Map<byte[], Long> proposals = new ByteArrayMap<>();

        candidateStateTrie
                .getBestCandidatesCache()
                .getOrDefault(HexBytes.fromBytes(blocks.get(0).hashPrevBlock), Collections.emptyList())
                .stream().map(c -> c.getPublicKeyHash().getBytes())
                .forEach(h -> proposals.put(h, 0L));

        blocks.forEach(block -> {
            byte[] proposer = block.body.get(0).to;
            if (!proposals.containsKey(proposer)) {
                return;
            }
            proposals.put(proposer, proposals.get(proposer) + 1);
        });

        // 拉黑不出块的节点
        List<byte[]> toBlock = proposals.keySet().stream()
                .filter(k -> proposals.get(k) == 0).collect(Collectors.toList());

        toBlock.forEach(k -> ret.get(k).setBlocked(true));

        // delete all block list after community miner joins
        if (atJoinEra(blocks)) {
            ret.values().forEach(c -> c.setBlocked(false));
        }
        return ret;
    }
}
