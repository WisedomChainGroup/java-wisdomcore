package org.wisdom.db;

import org.wisdom.consensus.pow.Proposer;
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

    // blocks has staged and waiting for confirm..
    List<Block> getStaged();

    // block has confirmed
    boolean isConfirmed(byte[] hash);

    // block has staged or confirmed
    default boolean isStagedOrConfirmed(byte[] hash) {
        return isStaged(hash) || isConfirmed(hash);
    }

    // the block or any ancestor of the block has transaction
    boolean hasTransactionAt(byte[] blockHash, byte[] transactionHash);

    byte[] getTargetByParent(Block parent);

    List<byte[]> getProposersByParent(Block parent);

    Optional<Proposer> getProposerByParentAndEpoch(Block parent, long epochSecond);

    List<Candidate> getCurrentCandidates();

    // get transaction from block or the ancestor
    Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash);

    boolean hasPayloadAt(byte[] blockHash, int type, byte[] payload);

    Optional<AccountState> getAccountStateAt(byte[] blockHash, byte[] publicKeyHash);

    Map<byte[], AccountState> getAccountStatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes);

    long getValidatorNonceAt(byte[] blockHash, byte[] publicKeyHash);


    default Optional<AccountState> getConfirmedAccountState(byte[] publicKeyHash) {
        return getAccountStateAt(getLatestConfirmed().getHash(), publicKeyHash);
    }

    default Map<byte[], AccountState> getConfirmedAccountStates(Collection<byte[]> publicKeyHashes) {
        return getAccountStatesAt(getLatestConfirmed().getHash(), publicKeyHashes);
    }

    default Optional<AccountState> getLatestAccountState(byte[] publicKeyHash) {
        return getAccountStateAt(getBestBlock().getHash(), publicKeyHash);
    }

    default Map<byte[], AccountState> getLatestAccountStates(Collection<byte[]> publicKeyHashes) {
        return getAccountStatesAt(getBestBlock().getHash(), publicKeyHashes);
    }

    List<Transaction> getTransactionsAtByTo(byte[] blockHash, byte[] publicKeyHash, int offset, int limit);

    List<Transaction> getTransactionsAtByFrom(byte[] blockHash, byte[] publicKey, int offset, int limit);

    List<Transaction> getTransactionsAtByFromAndTo(byte[] blockHash, byte[] from, byte[] to, int offset, int limit);

    List<Transaction> getTransactionsAtByTypeAndTo(byte[] blockHash, int type, byte[] publicKeyHash, int offset, int limit);

    List<Transaction> getTransactionsAtByTypeFromAndTo(byte[] blockHash, int type, byte[] from, byte[] to, int offset, int limit);

    List<Transaction> getTransactionsAtByTypeAndFrom(byte[] blockHash, int type, byte[] from, int offset, int limit);

    default List<Transaction> getLatestTransactionsByTo(byte[] publicKeyHash, int offset, int limit) {
        return getTransactionsAtByTo(getBestBlock().getHash(), publicKeyHash, offset, limit);
    }

    default List<Transaction> getLatestTransactionsByFrom(byte[] publicKey, int offset, int limit) {
        return getTransactionsAtByFrom(getBestBlock().getHash(), publicKey, offset, limit);
    }

    default List<Transaction> getLatestTransactionsByFromAndTo(byte[] blockHash, byte[] from, byte[] to, int offset, int limit) {
        return getTransactionsAtByFromAndTo(getBestBlock().getHash(), from, to, offset, limit);
    }

    default List<Transaction> getLatestTransactionByTypeAndTo(byte[] blockHash, int type, byte[] publicKeyHash, int offset, int limit) {
        return getTransactionsAtByTypeAndTo(getBestBlock().getHash(), type, publicKeyHash, offset, limit);
    }

    default List<Transaction> getLatestTransactionsByTypeFromAndTo(byte[] blockHash, int type, byte[] from, byte[] to, int offset, int limit) {
        return getTransactionsAtByTypeFromAndTo(getBestBlock().getHash(), type, from, to, offset, limit);
    }

    default List<Transaction> getLatestTransactionsByTypeAndFrom(byte[] blockHash, int type, byte[] from, int offset, int limit){
        return getTransactionsAtByTypeAndFrom(getBestBlock().getHash(), type, from, offset, limit);
    }

    void writeBlock(Block block);
}
