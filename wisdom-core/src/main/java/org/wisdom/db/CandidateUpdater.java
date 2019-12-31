package org.wisdom.db;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CandidateUpdater {
    @Value("${wisdom.wip-1217.height}")
    private long WIP_12_17_HEIGHT;


    private static int getenv(String key, int defaultValue) {
        String v = System.getenv(key);
        if (v == null || v.equals("")) return defaultValue;
        return Integer.parseInt(v);
    }

    private static final long MINIMUM_PROPOSER_MORTGAGE = 100000 * EconomicModel.WDC;
    private static final int MAXIMUM_PROPOSERS = getenv("MAXIMUM_PROPOSERS", 15);
    public static final int COMMUNITY_MINER_JOINS_HEIGHT = getenv("COMMUNITY_MINER_JOINS_HEIGHT", 522215);

    public static Logger logger = LoggerFactory.getLogger(ProposersCache.class);

    public ProposersCache updateAll(ProposersCache proposersCache, Block block) {
        for (Transaction t : block.body) {
            updateTransaction(proposersCache, t);
        }
        return proposersCache;
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

    public void updateTransaction(ProposersCache state, Transaction transaction) {
//        Candidate p = state.getProposers().get(transaction.to);
//        if (p == null) {
//            p = new Candidate();
//            p.setPublicKeyHash(transaction.to);
//        }
//        updateTransaction(p, transaction);
//        state.getProposers().put(p.getPublicKeyHash(), p);
    }

    private void updateTransaction(Candidate candidate, Transaction tx) {
        Map<byte[], Long> erasCounter = candidate.getErasCounter();
        Map<byte[], Vote> receivedVotes = candidate.getReceivedVotes();
        long mortgage = candidate.getMortgage();
        switch (Transaction.TYPES_TABLE[tx.type]) {
            case VOTE:
                receivedVotes.put(tx.getHash(), new Vote(PublicKeyHash.fromPublicKey(tx.from), tx.amount, tx.amount));
                erasCounter.put(tx.getHash(), 0L);
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
                    logger.error("mortgage < 0");
                }
                break;
        }
    }
}
