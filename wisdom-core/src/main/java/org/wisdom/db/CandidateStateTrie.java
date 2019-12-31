package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.genesis.Genesis;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class CandidateStateTrie extends EraLinkedStateTrie<Candidate> {
    private CandidateUpdater candidateUpdater;

    public CandidateStateTrie(Block genesis, Genesis genesisJSON, DatabaseStoreFactory factory, CandidateUpdater candidateUpdater) {
        super(genesis, genesisJSON, Candidate.class, factory, false, false);
        this.candidateUpdater = candidateUpdater;
    }

    @Override
    protected String getPrefix() {
        return "candidate";
    }

    @Override
    protected int getBlocksPerEra() {
        return 0;
    }

    @Override
    protected Map<byte[], Candidate> getUpdatedStates(Map<byte[], Candidate> beforeUpdates, Collection<Block> blocks) {
        return null;
    }

    @Override
    protected Set<byte[]> getRelatedKeys(Collection<Block> blocks) {
        return null;
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
    protected Map<byte[], Candidate> generateGenesisStates(Block genesis, Genesis genesisJSON) {
        return Collections.emptyMap();
    }
}
