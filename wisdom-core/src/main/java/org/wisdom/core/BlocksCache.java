/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.core;

import org.apache.commons.codec.binary.Hex;

import java.util.*;


/**
 * @author sal 1564319846@qq.com
 */
public class BlocksCache {
    private Map<String, Block> blocks;
    private Map<String, Set<String>> childrenHashes;
    private Map<Long, Set<String>> heightIndex;
    private int sizeLimit;

    // lru
    public BlocksCache(int sizeLimit) {
        this();
        this.sizeLimit = sizeLimit;
    }

    public BlocksCache() {
        this.blocks = new HashMap<>();
        this.childrenHashes = new HashMap<>();
        this.heightIndex = new TreeMap<>();
    }

    public BlocksCache(Block b) {
        this();
        addBlock(b);
    }

    public BlocksCache(Collection<Block> blocks) {
        this();
        addBlocks(blocks);
    }

    public Block getBlock(byte[] hash) {
        return blocks.get(Hex.encodeHexString(hash));
    }

    private List<Block> getBlocks(Set<String> hashes) {
        List<Block> res = new ArrayList<>();
        for (String h : hashes) {
            Block b = blocks.get(h);
            if (b != null) {
                res.add(b);
            }
        }
        res.sort(Comparator.comparingLong(Block::getnHeight));
        return res;
    }

    /**
     * deep copy
     *
     * @return
     */
    public BlocksCache copy() {
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
        return copied;
    }

    /**
     * return b's descendant blocks, inclusive b
     *
     * @param b
     * @return
     */
    public List<Block> getDescendantBlocks(Block b) {
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
        List<Block> res = getBlocks(descendantBlocksHash);
        if (res.size() <= 1) {
            return res;
        }
        res.sort(Comparator.comparingLong(Block::getnHeight));
        return res;
    }

    // sort by length descending
    public List<List<Block>> getAllForks() {
        List<List<Block>> res = new ArrayList<>();
        for (String k : getLeavesHash()) {
            List<Block> chain = getAncestors(k);
            chain.add(blocks.get(k));
            res.add(chain);
        }
        if (res.size() > 1) {
            res.sort(Comparator.comparingLong(List::size));
        }
        Collections.reverse(res);
        return res;
    }


    // delete a block
    public void deleteBlock(Block b) {
        String bHash = b.getHashHexString();
        String prevHash = Hex.encodeHexString(b.hashPrevBlock);
        blocks.remove(bHash);
        if (childrenHashes.containsKey(prevHash)) {
            childrenHashes.get(prevHash).remove(bHash);
        }
        if (childrenHashes.containsKey(prevHash) && childrenHashes.get(prevHash).size() == 0) {
            childrenHashes.remove(prevHash);
        }
        if (heightIndex.containsKey(b.nHeight)) {
            heightIndex.get(b.nHeight).remove(bHash);
        }
        if (heightIndex.containsKey(b.nHeight) && heightIndex.get(b.nHeight).size() == 0) {
            heightIndex.remove(b.nHeight);
        }
    }

    public void deleteBlocks(List<Block> bs) {
        for (Block b : bs) {
            deleteBlock(b);
        }
    }

    // leaves not has children
    private Set<String> getLeavesHash() {
        Set<String> res = new HashSet<>();
        for (String key : blocks.keySet()) {
            if (!childrenHashes.containsKey(key) || childrenHashes.get(key).isEmpty()) {
                res.add(key);
            }
        }
        return res;
    }


    public List<Block> getLeaves() {
        return getBlocks(getLeavesHash());
    }

    public List<Block> getInitials() {
        return getBlocks(getInitialsHash());
    }

    // initials not has parent
    private Set<String> getInitialsHash() {
        Set<String> res = new HashSet<>();
        for (String key : blocks.keySet()) {
            Block b = blocks.get(key);
            if (blocks.get(key).nHeight == 0) {
                res.add(key);
                continue;
            }
            if (!blocks.containsKey(Hex.encodeHexString(b.hashPrevBlock))) {
                res.add(key);
            }
        }
        return res;
    }

    public void addBlock(Block block) {
        if (block == null) {
            return;
        }
        while (sizeLimit != 0 && this.blocks.size() > sizeLimit) {
            this.blocks.values()
                    .stream()
                    .min(Comparator.comparingLong(Block::getnHeight))
                    .ifPresent(this::deleteBlock);
        }
        String key = block.getHashHexString();
        if (this.blocks.containsKey(key)) {
            return;
        }
        blocks.put(key, block);
        String prevHash = Hex.encodeHexString(block.hashPrevBlock);
        if (!childrenHashes.containsKey(prevHash)) {
            childrenHashes.put(prevHash, new HashSet<>());
        }
        childrenHashes.get(prevHash).add(key);
        if (!heightIndex.containsKey(block.nHeight)) {
            heightIndex.put(block.nHeight, new HashSet<>());
        }
        heightIndex.get(block.nHeight).add(key);
    }

    public void addBlocks(Collection<Block> blocks) {
        if (blocks == null || blocks.size() == 0) {
            return;
        }
        for (Block b : blocks) {
            addBlock(b);
        }
    }

    public List<Block> getAll() {
        return getBlocks(blocks.keySet());
    }

    public List<Block> popLongestChain() {
        List<List<Block>> res = getAllForks();
        if (res.size() == 0) {
            return new ArrayList<>();
        }
        List<Block> longest = res.get(res.size() - 1);
        for (Block b : longest) {
            deleteBlock(b);
        }
        return longest;
    }


    public List<List<Block>> popLongestChains() {
        List<List<Block>> res = new ArrayList<>();
        while (true) {
            List<Block> longest = popLongestChain();
            if (longest == null || longest.size() == 0) {
                break;
            }
            res.add(longest);
        }
        return res;
    }

    public int size() {
        return blocks.size();
    }

    public boolean isEmpty() {
        return blocks.isEmpty();
    }

    public boolean hasBlock(byte[] hash) {
        return blocks.containsKey(Hex.encodeHexString(hash));
    }


    private List<Block> getAncestors(String key) {
        Block b = blocks.get(key);
        return getAncestors(b);
    }


    public Block getAncestor(Block b, long height) {
        String parentKey = Hex.encodeHexString(b.hashPrevBlock);
        while (b != null && b.nHeight > height) {
            b = blocks.get(parentKey);
        }
        if (b != null && b.nHeight == height) {
            return b;
        }
        return null;
    }

    public List<Block> getAncestors(Block b) {
        List<Block> res = new ArrayList<>();
        while (b != null) {
            res.add(b);
            b = blocks.get(Hex.encodeHexString(b.hashPrevBlock));
        }
        Collections.reverse(res);
        return res;
    }
}