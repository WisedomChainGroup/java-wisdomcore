package org.wisdom.core;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlocksCacheWrapper extends BlocksCache {
    private ReadWriteLock readWriteLock;

    public BlocksCacheWrapper() {
        super();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public BlocksCacheWrapper(int sizeLimit) {
        super(sizeLimit);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public Block getBlock(byte[] hash) {
        try {
            readWriteLock.readLock().lock();
            return super.getBlock(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public BlocksCache copy() {
        try {
            readWriteLock.readLock().lock();
            return super.copy();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> getDescendantBlocks(Block b) {
        try {
            readWriteLock.readLock().lock();
            return super.getDescendantBlocks(b);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<List<Block>> getAllForks() {
        try {
            readWriteLock.readLock().lock();
            return super.getAllForks();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void deleteBlock(Block b) {
        try {
            readWriteLock.writeLock().lock();
            super.deleteBlock(b);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteBlocks(List<Block> bs) {
        try {
            readWriteLock.writeLock().lock();
            super.deleteBlocks(bs);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<Block> getLeaves() {
        try {
            readWriteLock.readLock().lock();
            return super.getLeaves();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> getInitials() {
        try {
            readWriteLock.readLock().lock();
            return super.getInitials();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void addBlock(Block block) {
        try {
            readWriteLock.writeLock().lock();
            super.addBlock(block);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void addBlocks(Collection<Block> blocks) {
        try {
            readWriteLock.writeLock().lock();
            super.addBlocks(blocks);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<Block> getAll() {
        try {
            readWriteLock.readLock().lock();
            return super.getAll();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> popLongestChain() {
        try {
            readWriteLock.writeLock().lock();
            return super.popLongestChain();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<List<Block>> popLongestChains() {
        try {
            readWriteLock.writeLock().lock();
            return super.popLongestChains();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public int size() {
        try {
            readWriteLock.readLock().lock();
            return super.size();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            readWriteLock.readLock().lock();
            return super.isEmpty();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean hasBlock(byte[] hash) {
        try {
            readWriteLock.readLock().lock();
            return super.hasBlock(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Block getAncestor(Block b, long height) {
        try {
            readWriteLock.readLock().lock();
            return super.getAncestor(b, height);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Block> getAncestors(Block b) {
        try {
            readWriteLock.readLock().lock();
            return super.getAncestors(b);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
