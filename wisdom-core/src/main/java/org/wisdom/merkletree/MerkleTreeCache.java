package org.wisdom.merkletree;

import org.wisdom.core.Block;
import org.wisdom.p2p.WisdomOuterClass;
import org.wisdom.sync.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MerkleTreeCache {

    private Map<String, Block> blocks;

    private ReadWriteLock readWriteLock;

    public MerkleTreeCache() {
        this.blocks = new HashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public void addBlock(Block block) {
        this.readWriteLock.writeLock().lock();
        addBlockUnsafe(block);
        this.readWriteLock.writeLock().unlock();
    }

    private void addBlockUnsafe(Block block) {
        blocks.put(block.getHashHexString(), block);
    }

    public boolean containBlock(Block block) {
        this.readWriteLock.readLock().lock();
        boolean isContain = containBlockUnsafe(block);
        this.readWriteLock.readLock().lock();
        return isContain;
    }

    public boolean containBlock(String blockHash) {
        this.readWriteLock.readLock().lock();
        boolean isContain = containBlockUnsafe(blockHash);
        this.readWriteLock.readLock().lock();
        return isContain;
    }

    public boolean containBlockUnsafe(Block block) {
        return blocks.containsKey(block.getHashHexString());
    }

    public boolean containBlockUnsafe(String blockHash) {
        return blocks.containsKey(blockHash);
    }

    public void removeBlock(String blockHash) {
        this.readWriteLock.writeLock().lock();
        removeBlockUnsafe(blockHash);
        this.readWriteLock.writeLock().unlock();
    }

    private void removeBlockUnsafe(String blockHash) {
        this.blocks.remove(blockHash);
    }

    public Block replaceTransaction(String blockHash, List<WisdomOuterClass.MerkleTransaction> trans) {
        this.readWriteLock.writeLock().lock();
        Block block = replaceTransactionUnsafe(blockHash, trans);
        this.readWriteLock.writeLock().unlock();
        return block;
    }

    private Block replaceTransactionUnsafe(String blockHash, List<WisdomOuterClass.MerkleTransaction> trans) {
        Block block = this.blocks.get(blockHash);
        for (WisdomOuterClass.MerkleTransaction wm : trans) {
            block.body.set(wm.getIndex(), Utils.parseTransaction(wm.getTransaction()));
        }
        return block;
    }

    public Block getCacheBlock(String blockHash) {
        this.readWriteLock.readLock().lock();
        Block block = getCacheBlockUnsafe(blockHash);
        this.readWriteLock.readLock().unlock();
        return block;
    }

    private Block getCacheBlockUnsafe(String blockHash) {
        if (containBlockUnsafe(blockHash)) {
            return blocks.get(blockHash);
        }
        return null;
    }

}
