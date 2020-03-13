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
import org.wisdom.db.StateDB;

/**
 * @author sal 1564319846@qq.com
 * state factory, lru cached
 */
@Deprecated // use state trie instead
public class StateFactory<T extends State<T>> extends AbstractStateFactory<T> {

    public StateFactory(int cacheSize, T genesisState) {
        super(genesisState, cacheSize);
    }

    public T getFromCache(Block block) {
        if (block.nHeight == 0) {
            return genesisState;
        }
        String key = getLRUCacheKey(block.getHash());
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Block parent = stateDB.getBlock(block.hashPrevBlock);
        T parentState = getFromCache(parent);
        T newState = parentState.copy().updateBlock(block);
        cache.put(key, newState);
        return newState.copy();
    }

    public T getInstance(Block block) {
        if (block == null || !stateDB.hasBlock(block.getHash())) {
            return null;
        }
        if (block.nHeight == 0) {
            return genesisState;
        }
        return getFromCache(block);
    }
}
