package org.wisdom.db;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.wisdom.account.PublicKeyHash;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProposerUpdater {

    public static Logger logger = LoggerFactory.getLogger(ProposerState.class);

    public ProposerState updateAll(ProposerState proposerState, Block block) {
        for (Transaction t : block.body) {
            updateTransaction(proposerState, t);
        }
        return proposerState;
    }

    public ProposerState generateGenesisStates(List<String> initialProposers) {
        ProposerState state = new ProposerState();
        Map<byte[], Proposer> map = new HashMap<>();
        for (String proposer : initialProposers) {
            Proposer p = new Proposer();
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
        state.setProposers(map);
        return state;
    }

    public void updateTransaction(ProposerState state, Transaction transaction) {
        Proposer p = state.getProposers().get(transaction.to);
        if (p == null) {
            p = new Proposer();
            p.setPublicKeyHash(transaction.to);
        }
        updateTransaction(p, transaction);
        state.getProposers().put(p.getPublicKeyHash(), p);
    }

    private void updateTransaction(Proposer proposer, Transaction tx) {
        Map<byte[], Long> erasCounter = proposer.getErasCounter();
        Map<byte[], Vote> receivedVotes = proposer.getReceivedVotes();
        long mortgage = proposer.getMortgage();
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
                proposer.setMortgage(mortgage);
                break;
            case EXIT_MORTGAGE:
                mortgage -= tx.amount;
                proposer.setMortgage(mortgage);
                if (mortgage < 0) {
                    logger.error("mortgage < 0");
                }
                break;
        }
    }
}
