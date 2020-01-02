package org.wisdom.db;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;

import org.wisdom.account.PublicKeyHash;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class CandidateUpdater {
    @Value("${wisdom.wip-1217.height}")
    @Setter
    private long WIP_12_17_HEIGHT;

    private static final long MINIMUM_PROPOSER_MORTGAGE = 100000 * EconomicModel.WDC;
    private static final int MAXIMUM_PROPOSERS = getenv("MAXIMUM_PROPOSERS", 15);
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

    public CandidateUpdater(@Value("${wisdom.allow-miner-joins-era}") int allowMinersJoinEra,
                            @Value("${wisdom.consensus.block-interval}") int blockInterval) {
        this.allowMinersJoinEra = allowMinersJoinEra;
        this.blockInterval = blockInterval;
    }


    public Map<byte[], Candidate> updateAll(Map<byte[], Candidate> beforeUpdates, List<Block> blocks) {
        Map<byte[], Candidate> res = copy(beforeUpdates);
        Map<byte[], Long> proposals = new ByteArrayMap<>();

        candidateStateTrie
                .getProposers(repository.getBlock(blocks.get(0).hashPrevBlock))
                .forEach(h -> proposals.put(h, 0L));


        boolean enableMultiMiners = allowMinersJoinEra >= 0 && EraLinkedStateFactory.getEraAtBlockNumber(
                blocks.stream().findFirst().get().nHeight, blockInterval
        ) >= allowMinersJoinEra;
        // 统计出块数量

        blocks.forEach(block -> {
            byte[] proposer = block.body.get(0).to;
            if(!proposals.containsKey(proposer)) throw new RuntimeException("invalid proposal");

            proposals.put(proposer, proposals.get(proposer) + 1);
            for (Transaction tx : block.body) {

            }
        });

        // 拉黑不出块的节点
        List<byte[]> toBlock = proposals.keySet().stream()
                .filter(k -> proposals.get(k) == 0).collect(Collectors.toList());

        // delete all block list after community miner joins
        if (blocks.stream().anyMatch(b -> b.getnHeight() >= COMMUNITY_MINER_JOINS_HEIGHT
                && blocks.stream().anyMatch(x -> x.getnHeight() < COMMUNITY_MINER_JOINS_HEIGHT))
        ) {
            blockList.clear();
        }

        // 重新生成 proposers
        Stream<Candidate> proposers = beforeUpdates.values().stream()
                // 过滤掉黑名单中节点
                .filter(p -> !blockList.contains(p.getPublicKeyHash()))
                // 过滤掉抵押数量不足和投票为零的账户
                .filter(p -> p.getMortgage() >= MINIMUM_PROPOSER_MORTGAGE);
        if (blocks.stream().findFirst().get().getnHeight() > WIP_12_17_HEIGHT) {
            proposers = proposers
                    .filter(x -> x.getAccumulated() > 0);
        }
        // 按照 投票，抵押，字典从大到小排序
        this.proposers = proposers.sorted((x, y) -> -compareProposer(x, y))
                .limit(MAXIMUM_PROPOSERS)
                .map(Candidate::getPublicKeyHash).collect(Collectors.toList());
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
        Map<byte[], Long> erasCounter = candidate.getErasCounter();
        Map<byte[], Vote> receivedVotes = candidate.getReceivedVotes();
        long mortgage = candidate.getMortgage();
        switch (Transaction.TYPES_TABLE[tx.type]) {
            case VOTE:
                receivedVotes.put(tx.getHash(), new Vote(
                        PublicKeyHash.fromPublicKey(tx.from).getPublicKeyHash(), tx.amount, tx.amount));
                erasCounter.put(tx.getHash(), 0L);
                candidate.clearVotesCache();
                break;
            // 撤回投票
            case EXIT_VOTE:
                receivedVotes.remove(tx.payload);
                erasCounter.remove(tx.payload);
                break;
            case MORTGAGE:
                mortgage += tx.amount;
                candidate.setMortgage(mortgage);
                break;
            case EXIT_MORTGAGE:
                mortgage -= tx.amount;
                candidate.setMortgage(mortgage);
                if (mortgage < 0) {
                    log.error("mortgage < 0");
                }
                break;
        }
    }

    public ProposersCache generateGenesisStates(List<String> initialProposers) {
        ProposersCache state = new ProposersCache();
        Map<byte[], Candidate> map = new HashMap<>();
        for (String proposer : initialProposers) {
            Candidate p = new Candidate();
            URI uri;
            try {
                uri = new URI(proposer);
                byte[] pubKeyHashes = KeystoreAction.addressToPubkeyHash(uri.getRawUserInfo());
                p.setPublicKeyHash(pubKeyHashes);
                map.put(pubKeyHashes, p);
            } catch (URISyntaxException e) {
                System.out.println("uri cannot be resolved");
            }
        }
//        state.setProposers(map);
        return state;
    }

    public Set<byte[]> getRelatedCandidates(Collection<Block> blocks) {
        Set<byte[]> ret = new ByteArraySet();
        for (Block block : blocks) {
            block.body.stream().filter(this::isCandidateRelated).map(this::getRelatedCandidates)
                    .forEach(ret::addAll);
        }
        return ret;
    }

    private boolean isCandidateRelated(Transaction tx) {
        switch (Transaction.TYPES_TABLE[tx.type]) {
            case VOTE:
            case EXIT_VOTE:
            case MORTGAGE:
            case EXIT_MORTGAGE:
                return true;
            default:
                return false;
        }
    }

    public Set<byte[]> getRelatedCandidates(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        switch (Transaction.TYPES_TABLE[tx.type]) {
            case VOTE:
            case EXIT_VOTE:
                byte[] fromHash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from));
                bytes.add(fromHash);
                if (!Arrays.equals(fromHash, tx.to)) {
                    bytes.add(tx.to);
                }
                break;
            case MORTGAGE:
            case EXIT_MORTGAGE:
                bytes.add(tx.to);
                break;
        }
        return bytes;
    }
}
