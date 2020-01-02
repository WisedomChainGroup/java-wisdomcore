package org.wisdom.db;

import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WisdomRepository {
    Block getLatestConfirmed();

    Block getGenesis();

    Block getBestBlock();

    Block getBlock(byte[] hash);

    Block getHeader(byte[] hash);

    // get ancestor at height h of block
    Block findAncestorHeader(byte[] hash, long h);

    // get ancestors after(inclusive) height h of block(inclusive)
    List<Block> getAncestorBlocks(byte[] hash, long height);

    // block has staged and waiting for confirm..
    boolean isStaged(byte[] hash);

    // block has confirmed
    boolean isConfirmed(byte[] hash);

    // block has staged or confirmed
    default boolean isStagedOrConfirmed(byte[] hash){
        return isStaged(hash) || isConfirmed(hash);
    }

    // the block or any ancestor of the block has transaction
    boolean hasTransactionAt(byte[] blockHash, byte[] transactionHash);

    // get transaction from block or the ancestor
    Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash);

    boolean hasPayloadAt(byte[] blockHash, int type, byte[] payload);

    Optional<AccountState> getAccountStateAt(byte[] blockHash, byte[] publicKeyHash);

    Map<byte[], AccountState> getAccountStatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes);

    long getValidatorNonceAt(byte[] blockHash, byte[] publicKeyHash);

    default Optional<AccountState> getConfirmedAccountState(byte[] publicKeyHash){
        return getAccountStateAt(getLatestConfirmed().getHash(), publicKeyHash);
    }

    default Map<byte[], AccountState> getConfirmedAccountStates(Collection<byte[]> publicKeyHashes){
        return getAccountStatesAt(getLatestConfirmed().getHash(), publicKeyHashes);
    }

    default Optional<AccountState> getLatestAccountState(byte[] publicKeyHash){
        return getAccountStateAt(getBestBlock().getHash(), publicKeyHash);
    }

    default Map<byte[], AccountState> getLatestAccountStates(Collection<byte[]> publicKeyHashes){
        return getAccountStatesAt(getBestBlock().getHash(), publicKeyHashes);
    }
}
