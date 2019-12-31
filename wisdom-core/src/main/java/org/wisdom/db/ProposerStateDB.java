package org.wisdom.db;

import java.util.Optional;

public interface ProposerStateDB {

    Optional<ProposersCache> getProposerState(byte[] blockHash);

    void putProposerStates(byte[] blockHash, ProposersCache state);
}
