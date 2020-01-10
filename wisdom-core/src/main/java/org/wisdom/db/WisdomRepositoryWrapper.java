package org.wisdom.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.consensus.pow.Proposer;
import org.wisdom.contract.AssetCodeInfo;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.pool.PeningTransPool;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class WisdomRepositoryWrapper implements WisdomRepository {

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private WisdomRepository delegate;

    public WisdomRepositoryWrapper(
            WisdomBlockChain bc,
            TriesSyncManager triesSyncManager,
            AccountStateTrie accountStateTrie,
            ValidatorStateTrie validatorStateTrie,
            CandidateStateTrie candidateStateTrie,
            AssetCodeTrie assetCodeTrie,
            TargetCache targetCache,
            PeningTransPool peningTransPool,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra
    ) throws Exception {
        this.delegate =
                new WisdomRepositoryImpl(bc, triesSyncManager, accountStateTrie,
                        validatorStateTrie, candidateStateTrie, assetCodeTrie, targetCache,peningTransPool,
                        blocksPerEra
                );
    }

    @Override
    public Block getLatestConfirmed() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestConfirmed();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Block getGenesis() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getGenesis();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Block getBestBlock() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getBestBlock();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Block getBlock(byte[] hash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getBlock(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getBlocks(startHeight, stopHeight, sizeLimit, clipInitial);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Block getHeader(byte[] hash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getHeader(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Block getAncestorHeader(byte[] hash, long h) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getAncestorHeader(hash, h);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> getAncestorBlocks(byte[] hash, long height) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getAncestorBlocks(hash, height);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean isStaged(byte[] hash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.isStaged(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> getStaged() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getStaged();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean isConfirmed(byte[] hash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.isConfirmed(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsBlock(byte[] hash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.containsBlock(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsTransactionAt(byte[] blockHash, byte[] transactionHash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.containsTransactionAt(blockHash, transactionHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public byte[] getTargetByParent(Block parent) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTargetByParent(parent);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<byte[]> getProposersByParent(Block parent) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getProposersByParent(parent);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }


    @Override
    public Optional<Proposer> getProposerByParentAndEpoch(Block parent, long epochSecond) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getProposerByParentAndEpoch(parent, epochSecond);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<CandidateInfo> getLatestTopCandidates() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestTopCandidates();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<CandidateInfo> getLatestBlockedCandidates() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestBlockedCandidates();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public long getLatestEra() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestEra();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<Candidate> getLatestCandidate(byte[] publicKeyHash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestCandidate(publicKeyHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<Transaction> getTransactionAt(byte[] blockHash, byte[] txHash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTransactionAt(blockHash, txHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsPayloadAt(byte[] blockHash, int type, byte[] payload) {
        readWriteLock.readLock().lock();
        try {
            return delegate.containsPayloadAt(blockHash, type, payload);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<AccountState> getAccountStateAt(byte[] blockHash, byte[] publicKeyHash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getAccountStateAt(blockHash, publicKeyHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<byte[], AccountState> getAccountStatesAt(byte[] blockHash, Collection<byte[]> publicKeyHashes) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getAccountStatesAt(blockHash, publicKeyHashes);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public long getValidatorNonceAt(byte[] blockHash, byte[] publicKeyHash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getValidatorNonceAt(blockHash, publicKeyHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<AccountState> getConfirmedAccountState(byte[] publicKeyHash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getConfirmedAccountState(publicKeyHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<byte[], AccountState> getConfirmedAccountStates(Collection<byte[]> publicKeyHashes) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getConfirmedAccountStates(publicKeyHashes);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<AccountState> getLatestAccountState(byte[] publicKeyHash) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestAccountState(publicKeyHash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Map<byte[], AccountState> getLatestAccountStates(Collection<byte[]> publicKeyHashes) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestAccountStates(publicKeyHashes);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getTransactionsAtByTo(byte[] blockHash, byte[] publicKeyHash, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTransactionsAtByTo(blockHash, publicKeyHash, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getTransactionsAtByFrom(byte[] blockHash, byte[] publicKey, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTransactionsAtByFrom(blockHash, publicKey, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getTransactionsAtByFromAndTo(byte[] blockHash, byte[] from, byte[] to, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTransactionsAtByFromAndTo(blockHash, from, to, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeAndTo(byte[] blockHash, int type, byte[] publicKeyHash, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTransactionsAtByTypeAndTo(blockHash, type, publicKeyHash, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeFromAndTo(byte[] blockHash, int type, byte[] from, byte[] to, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTransactionsAtByTypeFromAndTo(blockHash, type, from, to, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getTransactionsAtByTypeAndFrom(byte[] blockHash, int type, byte[] from, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getTransactionsAtByTypeAndFrom(blockHash, type, from, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getLatestTransactionsByTo(byte[] publicKeyHash, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestTransactionsByTo(publicKeyHash, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getLatestTransactionsByFrom(byte[] publicKey, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestTransactionsByFrom(publicKey, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getLatestTransactionsByFromAndTo(byte[] blockHash, byte[] from, byte[] to, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestTransactionsByFromAndTo(blockHash, from, to, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getLatestTransactionByTypeAndTo(byte[] blockHash, int type, byte[] publicKeyHash, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestTransactionByTypeAndTo(blockHash, type, publicKeyHash, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getLatestTransactionsByTypeFromAndTo(byte[] blockHash, int type, byte[] from, byte[] to, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestTransactionsByTypeFromAndTo(blockHash, type, from, to, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> getLatestTransactionsByTypeAndFrom(byte[] blockHash, int type, byte[] from, int offset, int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getLatestTransactionsByTypeAndFrom(blockHash, type, from, offset, limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void writeBlock(Block block) {
        readWriteLock.writeLock().lock();
        try {
            delegate.writeBlock(block);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean containsAssetCodeAt(byte[] blockHash, byte[] code) {
        readWriteLock.readLock().lock();
        try {
            return delegate.containsAssetCodeAt(blockHash, code);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<AssetCodeInfo> getAssetCodeAt(byte[] blockHash, byte[] code) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getAssetCodeAt(blockHash, code);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public double getAverageBlocksInterval() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getAverageBlocksInterval();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public long getAverageFee() {
        readWriteLock.readLock().lock();
        try {
            return delegate.getAverageFee();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> getBestChain(int limit) {
        readWriteLock.readLock().lock();
        try {
            return delegate.getBestChain(limit);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public long countBlocksAfter(long timestamp) {
        readWriteLock.readLock().lock();
        try {
            return delegate.countBlocksAfter(timestamp);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

}
