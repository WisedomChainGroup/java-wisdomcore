package org.wisdom.db;

import org.wisdom.consensus.pow.Proposer;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WisdomRepositoryAdapter implements WisdomRepository{
    @Override
    public Block getLatestConfirmed() {
        return null;
    }

    @Override
    public Block getGenesis() {
        return null;
    }

    @Override
    public Block getBestBlock() {
        return null;
    }

    @Override
    public Block getBlock(byte[] hash) {
        return null;
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        return null;
    }

    @Override
    public Block getHeader(byte[] hash) {
        return null;
    }

    @Override
    public Block findAncestorHeader(byte[] hash, long h) {
        return null;
    }

    @Override
    public List<Block> getAncestorBlocks(byte[] hash, long height) {
        return null;
    }

    @Override
    public boolean isStaged(byte[] hash) {
        return false;
    }

    @Override
    public List<Block> getStaged() {
        return null;
    }

    @Override
    public boolean isConfirmed(byte[] hash) {
        return false;
    }

    @Override
    public boolean hasTransactionAt(byte[] blockHash, byte[] transactionHash) {
        return false;
    }

    @Override
    public byte[] getTargetByParent(Block parent) {
        return new byte[0];
    }

    @Override
    public List<byte[]> getProposersByParent(Block parent) {
        return null;
    }

    @Override
    public Optional<Proposer> getProposerByParentAndEpoch(Block parent, long epochSecond) {
        return Optional.empty();
    }

    @Override
    public List<CandidateStateTrie.CandidateInfo> getCurrentBestCandidates() {
        return null;
    }

    @Override
    public List<CandidateStateTrie.CandidateInfo> getCurrentBlockList() {
        return null;
    }

    @Override
    public long getCurrentEra() {
        return 0;
    }

    @Override
    public Optional<Candidate> getCurrentCandidate(byte[] _) {
        return Optional.empty();
    }

    @Override
    public Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash) {
        return Optional.empty();
    }

    @Override
    public boolean hasPayloadAt(byte[] blockHash, int type, byte[] payload) {
        return false;
    }

    @Override
    public Optional<AccountState> getAccountStateAt(byte[] blockHash, byte[] publicKeyHash) {
        return Optional.empty();
    }

    @Override
    public Map<byte[], AccountState> getAccountStatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes) {
        return null;
    }

    @Override
    public long getValidatorNonceAt(byte[] blockHash, byte[] publicKeyHash) {
        return 0;
    }

    @Override
    public List<Transaction> getTransactionsAtByTo(byte[] blockHash, byte[] publicKeyHash, int offset, int limit) {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsAtByFrom(byte[] blockHash, byte[] publicKey, int offset, int limit) {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsAtByFromAndTo(byte[] blockHash, byte[] from, byte[] to, int offset, int limit) {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeAndTo(byte[] blockHash, int type, byte[] publicKeyHash, int offset, int limit) {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeFromAndTo(byte[] blockHash, int type, byte[] from, byte[] to, int offset, int limit) {
        return null;
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeAndFrom(byte[] blockHash, int type, byte[] from, int offset, int limit) {
        return null;
    }

    @Override
    public void writeBlock(Block block) {

    }
}
