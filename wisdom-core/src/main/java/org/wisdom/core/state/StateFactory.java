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

package org.wisdom.core.state;

import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author sal 1564319846@qq.com
 * state factory, lru cached
 */
public class StateFactory<T extends State> extends AbstractStateFactory {
    private static Logger logger = LoggerFactory.getLogger(StateFactory.class);
    private static final int BLOCKS_PER_UPDATE = 100;

    private T genesisState;

    public StateFactory(WisdomBlockChain blockChain, int cacheSize, T genesisState) {
        super(blockChain, cacheSize);
        this.genesisState = genesisState;
    }

    public T getFromCache(Block block) {
        if (block.nHeight == 0) {
            return genesisState;
        }
        String key = getLRUCacheKey(block.getHash());
        if (cache.containsKey(key)) {
            return (T) cache.get(key);
        }
        Block parent = blockChain.getBlock(block.hashPrevBlock);
        T parentState = getFromCache(parent);
        T newState = (T) (parentState.copy().updateBlock(block));
        cache.put(key, newState);
        return newState;
    }

    public T getInstance(Block block) {
        if (block == null || !blockChain.hasBlock(block.getHash())) {
            return null;
        }
        if (block.nHeight == 0) {
            return genesisState;
        }
        return getFromCache(block);
    }

    public T getCurrentState() {
        Block target = blockChain.currentHeader();
        return getInstance(target);
    }

    // init cache when restart, avoid stack overflow
    public void initCache() {
        Block latest = blockChain.currentBlock();
        long latestHeight = latest.nHeight - 6 < 0 ? latest.nHeight : latest.nHeight - 6;
        Block confirmed = blockChain.getCanonicalBlock(latestHeight / BLOCKS_PER_UPDATE * BLOCKS_PER_UPDATE);
        T state = genesisState;
        for (long i = 0; i < confirmed.nHeight / BLOCKS_PER_UPDATE; i++) {
            List<Block> bks = blockChain.getCanonicalBlocks(i * BLOCKS_PER_UPDATE + 1, BLOCKS_PER_UPDATE);
            state = (T) state.copy().updateBlocks(bks);
            cache.put(getLRUCacheKey(bks.get(bks.size() - 1).getHash()), state);
        }

    }
}
