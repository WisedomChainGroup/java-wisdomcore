package org.wisdom.core;

import org.apache.commons.codec.binary.Hex;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author sal 1564319846@qq.com
 * concurrently safe in-memory blocks cache for fast indexing/searching
 * TODO: add size limit to prevent heap overflow
 */
public class BlocksCache {
    private Map<String, Block> blocks;
    private Map<String, Set<String>> childrenHashes;
    private TreeMap<Long, Set<String>> heightIndex;
    private Map<String, String> parentIndex;
    private ReadWriteLock readWriteLock;

    public BlocksCache() {
        this.blocks = new HashMap<>();
        this.childrenHashes = new HashMap<>();
        this.heightIndex = new TreeMap<>();
        this.parentIndex = new HashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public BlocksCache(List<Block> blocks) {
        this();
        addBlocks(blocks);
    }

    private List<Block> getBlocks(Set<String> hashes) {
        List<Block> res = new ArrayList<>();
        for (String h : hashes) {
            Block b = blocks.get(h);
            if (b != null) {
                res.add(b);
            }
        }
        Collections.sort(res, Comparator.comparingLong(Block::getnHeight));
        return res;
    }

    /**
     * deep copy
     *
     * @return
     */
    public BlocksCache copy() {
        this.readWriteLock.readLock().lock();
        BlocksCache copied = new BlocksCache();
        copied.blocks = new HashMap<>(blocks);
        copied.childrenHashes = new HashMap<>();
        for (String key : childrenHashes.keySet()) {
            copied.childrenHashes.put(key, new HashSet<>(childrenHashes.get(key)));
        }
        copied.heightIndex = new TreeMap<>();
        for (Long key : heightIndex.keySet()) {
            copied.heightIndex.put(key, new HashSet<>(heightIndex.get(key)));
        }
        this.readWriteLock.readLock().unlock();
        return copied;
    }

    /**
     * return b's descendant blocks, exclusive b
     *
     * @param b
     * @return
     */
    private List<Block> getDescendantBlocksUnsafe(Block b) {
        Set<String> descendantBlocksHash = new HashSet<>();
        String key = b.getHashHexString();
        descendantBlocksHash.add(key);
        while (true) {
            int size = descendantBlocksHash.size();
            Set<String> tmp = new HashSet<>();
            for (String k : descendantBlocksHash) {
                if (!childrenHashes.containsKey(k)) {
                    continue;
                }
                tmp.addAll(childrenHashes.get(k));
            }
            descendantBlocksHash.addAll(tmp);
            if (descendantBlocksHash.size() == size) {
                break;
            }
        }
        descendantBlocksHash.remove(key);
        List<Block> res = getBlocks(descendantBlocksHash);
        if (res.size() <= 1) {
            return res;
        }
        res.sort(Comparator.comparingLong(Block::getnHeight));
        return res;
    }

    public List<Block> getDescendantBlocks(Block b) {
        this.readWriteLock.readLock().lock();
        List<Block> blocks = getDescendantBlocksUnsafe(b);
        this.readWriteLock.readLock().unlock();
        return blocks;
    }

    // sort by length descending
    private List<List<Block>> getAllForksUnsafe() {
        List<List<Block>> res = new ArrayList<>();
        for (String k : getLeavesHash()) {
            List<Block> chain = getAncestorsUnsafe(k);
            chain.add(blocks.get(k));
            res.add(chain);
        }
        if (res.size() > 1) {
            res.sort(Comparator.comparingLong(List::size));
        }
        Collections.reverse(res);
        return res;
    }

    /**
     * return all forks in the cache
     *
     * @return
     */
    public List<List<Block>> getAllForks() {
        this.readWriteLock.readLock().lock();
        List<List<Block>> res = getAllForksUnsafe();
        this.readWriteLock.readLock().unlock();
        return res;
    }

    // delete a block
    private void deleteBlockUnsafe(Block b) {
        String bHash = b.getHashHexString();
        String prevHash = Hex.encodeHexString(b.hashPrevBlock);
        blocks.remove(bHash);
        if (childrenHashes.containsKey(prevHash)) {
            childrenHashes.get(prevHash).remove(bHash);
        }
        if (heightIndex.containsKey(b.nHeight)) {
            heightIndex.get(b.nHeight).remove(bHash);
        }
        parentIndex.remove(bHash);
    }

    public void deleteBlock(Block b) {
        this.readWriteLock.writeLock().lock();
        deleteBlockUnsafe(b);
        this.readWriteLock.writeLock().unlock();
    }

    public void deleteBlocks(List<Block> bs) {
        this.readWriteLock.writeLock().lock();
        for (Block b : bs) {
            deleteBlockUnsafe(b);
        }
        this.readWriteLock.writeLock().unlock();
    }

    // leaves not has children
    private Set<String> getLeavesHashUnSafe() {
        Set<String> res = new HashSet<>();
        for (String key : blocks.keySet()) {
            if (!childrenHashes.containsKey(key) || childrenHashes.get(key).isEmpty()) {
                res.add(key);
            }
        }
        return res;
    }

    public Set<String> getLeavesHash() {
        this.readWriteLock.readLock().lock();
        Set<String> res = getLeavesHashUnSafe();
        this.readWriteLock.readLock().unlock();
        return res;
    }

    public List<Block> getLeaves() {
        this.readWriteLock.readLock().lock();
        List<Block> res = getBlocks(getLeavesHashUnSafe());
        this.readWriteLock.readLock().unlock();
        return res;
    }

    public List<Block> getInitials() {
        this.readWriteLock.readLock().lock();
        List<Block> blocks = getBlocks(getInitialsHashUnsafe());
        this.readWriteLock.readLock().unlock();
        return blocks;
    }

    // initials not has parent
    private Set<String> getInitialsHashUnsafe() {
        Set<String> res = new HashSet<>();
        for (String key : blocks.keySet()) {
            if (blocks.get(key).nHeight == 0) {
                res.add(key);
                continue;
            }
            if (!blocks.containsKey(parentIndex.get(key))) {
                res.add(key);
            }
        }
        return res;
    }

    public void addBlocks(List<Block> blocks) {
        if (blocks == null || blocks.size() == 0) {
            return;
        }
        this.readWriteLock.writeLock().lock();
        for (Block b : blocks) {
            if (b == null) {
                continue;
            }
            String key = b.getHashHexString();
            if (this.blocks.containsKey(key)) {
                continue;
            }
            this.blocks.put(key, b);
            String prevHash = Hex.encodeHexString(b.hashPrevBlock);
            if (!childrenHashes.containsKey(prevHash)) {
                childrenHashes.put(prevHash, new HashSet<>());
            }
            childrenHashes.get(prevHash).add(key);
            if (!heightIndex.containsKey(b.nHeight)) {
                heightIndex.put(b.nHeight, new HashSet<>());
            }
            heightIndex.get(b.nHeight).add(key);
            parentIndex.put(key, prevHash);
        }
        this.readWriteLock.writeLock().unlock();
    }

    public List<Block> getAll() {
        this.readWriteLock.readLock().lock();
        List<Block> res = Arrays.asList(blocks.values().toArray(new Block[]{}));
        this.readWriteLock.readLock().unlock();
        if (res.size() > 1) {
            Collections.sort(res, Comparator.comparingLong(Block::getnHeight));
        }
        return res;
    }

    private List<Block> popLongestChainUnsafe() {
        List<List<Block>> res = getAllForksUnsafe();
        if (res.size() == 0) {
            return new ArrayList<>();
        }
        List<Block> longest = res.get(res.size() - 1);
        for (Block b : longest) {
            deleteBlockUnsafe(b);
        }
        return longest;
    }

    /**
     * @return null if the cache is emtpy
     */
    public List<Block> popLongestChain() {
        this.readWriteLock.writeLock().lock();
        List<Block> longest = popLongestChainUnsafe();
        this.readWriteLock.writeLock().unlock();
        return longest;
    }

    public List<List<Block>> popLongestChains() {
        this.readWriteLock.writeLock().lock();
        List<List<Block>> res = new ArrayList<>();
        while (true) {
            List<Block> longest = popLongestChainUnsafe();
            if (longest == null || longest.size() == 0) {
                break;
            }
            res.add(longest);
        }
        this.readWriteLock.writeLock().unlock();
        return res;
    }

    public int size() {
        this.readWriteLock.readLock().lock();
        int size = blocks.size();
        this.readWriteLock.readLock().unlock();
        return size;
    }

    public boolean isEmpty() {
        this.readWriteLock.readLock().lock();
        boolean isEmpty = blocks.isEmpty();
        this.readWriteLock.readLock().unlock();
        return isEmpty;
    }

    public boolean hasBlock(byte[] hash) {
        this.readWriteLock.readLock().lock();
        boolean res = blocks.containsKey(Hex.encodeHexString(hash));
        this.readWriteLock.readLock().unlock();
        return res;
    }

    public boolean hasChildrenOf(byte[] hash) {
        String key = Hex.encodeHexString(hash);
        this.readWriteLock.readLock().lock();
        boolean res = new HashSet<>(parentIndex.values()).contains(key);
        this.readWriteLock.readLock().unlock();
        return res;
    }

    private List<Block> getAncestorsUnsafe(String bkey) {
        Set<String> keys = new HashSet<>();
        for (String key = bkey; key != null; key = parentIndex.get(key)) {
            keys.add(key);
        }
        keys.remove(bkey);
        return getBlocks(keys);
    }

    public List<Block> getAncestors(Block b) {
        String bkey = b.getHashHexString();
        String bparentKey = Hex.encodeHexString(b.hashPrevBlock);
        List<Block> res = new ArrayList<>();
        this.readWriteLock.readLock().lock();
        try {
            if (blocks.keySet().contains(bkey)) {
                res = getAncestorsUnsafe(bkey);
                return res;
            }
            if (blocks.keySet().contains(bparentKey)) {
                res = getAncestorsUnsafe(bparentKey);
                res.add(blocks.get(bparentKey));
                return res;
            }
        } finally {
            this.readWriteLock.readLock().unlock();
        }
        return res;
    }
}
