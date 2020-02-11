package org.wisdom.db;

import org.wisdom.consensus.pow.Proposer;
import org.wisdom.contract.AssetCodeInfo;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.dao.TransactionQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WisdomRepositoryAdapter implements WisdomRepository {
    @Override
    public List<Transaction> getTransactionByQuery(TransactionQuery transactionQuery) {
        return null;
    }

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
    public Block getBlockByHash(byte[] hash) {
        return null;
    }

    @Override
    public List<Block> getBlocksBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        return null;
    }

    @Override
    public List<Block> getHeadersBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        return null;
    }

    @Override
    public Block getHeaderByHash(byte[] hash) {
        return null;
    }

    @Override
    public Block getAncestorHeader(byte[] hash, long h) {
        return null;
    }

    @Override
    public List<Block> getAncestorHeaders(byte[] hash, long height) {
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
    public boolean containsTransactionAt(byte[] blockHash, byte[] transactionHash) {
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
    public List<CandidateInfo> getLatestTopCandidates() {
        return null;
    }

    @Override
    public List<CandidateInfo> getLatestBlockedCandidates() {
        return null;
    }

    @Override
    public long getLatestEra() {
        return 0;
    }

    @Override
    public Optional<Candidate> getLatestCandidate(byte[] _) {
        return Optional.empty();
    }

    @Override
    public Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash) {
        return Optional.empty();
    }

    @Override
    public boolean containsPayloadAt(byte[] blockHash, int type, byte[] payload) {
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

    @Override
    public boolean containsAssetCodeAt(byte[] blockHash, byte[] code) {
        return false;
    }

    @Override
    public Optional<AssetCodeInfo> getAssetCodeAt(byte[] blockHash, byte[] code) {
        return Optional.empty();
    }

    @Override
    public double getAverageBlocksInterval() {
        return 0;
    }

    @Override
    public long getAverageFee() {
        return 0;
    }

    @Override
    public List<Block> getBestChain(int limit) {
        return null;
    }

    @Override
    public long countBlocksAfter(long timestamp) {
        return 0;
    }
}
