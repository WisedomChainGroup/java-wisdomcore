package org.wisdom.db;

import org.wisdom.core.Block;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface WisdomRepository {
    Block getLatestConfirmed();

    Block getGenesis();

    Block getBestBlock();

    Block getBlock(byte[] hash);

    Block getHeader(byte[] hash);

    Optional<AccountState> getAccountStateAt(byte[] blockHash, byte[] publicKeyHash);

    Map<byte[], AccountState> getAccountStatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes);

    default Optional<AccountState> getAccountState(byte[] publicKeyHash){
        return getAccountStateAt(getLatestConfirmed().getHash(), publicKeyHash);
    }

    default Map<byte[], AccountState> getAccountStates(byte[] blockHash, Collection<byte[]> publicKeyHashes){
        return getAccountStatesAt(getLatestConfirmed().getHash(), publicKeyHashes);
    }

    long getValidatorNonceAt(byte[] blockHash, byte[] publicKeyHash);

    Candidate getCandidateAt(byte[] blockHash, byte[] publicKeyHash);

    Map<byte[], Candidate> getCandidatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes);
}
