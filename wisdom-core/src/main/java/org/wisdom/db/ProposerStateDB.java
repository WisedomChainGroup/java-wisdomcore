package org.wisdom.db;

import java.util.Optional;

public interface ProposerStateDB {

    Optional<ProposerState> getProposerState(byte[] blockHash);

    void putProposerStates(byte[] blockHash, ProposerState state);
}
