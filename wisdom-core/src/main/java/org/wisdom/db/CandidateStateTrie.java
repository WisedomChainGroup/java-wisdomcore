package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class CandidateStateTrie extends AbstractStateTrie<Candidate> {
    private CandidateUpdater candidateUpdater;

    public CandidateStateTrie(Block genesis, DatabaseStoreFactory factory, CandidateUpdater candidateUpdater) {
        super(genesis, Candidate.class, factory, false, false);
        this.candidateUpdater = candidateUpdater;
    }

    @Override
    protected String getPrefix() {
        return "candidate";
    }

    @Override
    protected Map<byte[], Candidate> getUpdatedStates(Map<byte[], Candidate> beforeUpdates, Block block) {
        return null;
    }

    @Override
    protected Set<byte[]> getRelatedKeys(Block block) {
        Set<byte[]> keys = new ByteArraySet();
        block.body.stream()
                .filter(this::isCandidateRelated)
                .forEach(tx -> {
                    keys.add(PublicKeyHash.fromPublicKey(tx.from).getPublicKeyHash());
                    keys.add(tx.to);
                });
        return keys;
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

    @Override
    protected Map<byte[], Candidate> generateGenesisStates() {
        return Collections.emptyMap();
    }
}
