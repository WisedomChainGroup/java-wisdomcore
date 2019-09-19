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

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.wisdom.core.Block;
import org.wisdom.db.StateDB;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractStateFactory {
    static final Base64.Encoder encoder = Base64.getEncoder();
    protected StateDB stateDB;
    protected State genesisState;
    protected ConcurrentMap<String, State> cache;

    protected String getLRUCacheKey(byte[] hash) {
        return encoder.encodeToString(hash);
    }


    protected State getFromCache(Block target) {
        if (target == null) {
            return null;
        }
        return cache.get(getLRUCacheKey(target.getHash()));
    }

    public AbstractStateFactory(StateDB stateDB, State genesisState, int cacheSize) {
        this.stateDB = stateDB;
        this.genesisState = genesisState;
        this.cache = new ConcurrentLinkedHashMap.Builder<String, State>().maximumWeightedCapacity(cacheSize).build();
    }

    public abstract State getInstance(Block block);

    public void initCache(Block lastUpdated, List<Block> blocks) {
        if (lastUpdated.nHeight == 0) {
            cache.put(
                    getLRUCacheKey(blocks.get(blocks.size() - 1).getHash()),
                    genesisState.copy().updateBlocks(blocks)
            );
            return;
        }
        State state = cache.get(getLRUCacheKey(lastUpdated.getHash()));
        cache.put(
                getLRUCacheKey(blocks.get(blocks.size() - 1).getHash()),
                state.copy().updateBlocks(blocks)
        );
    }
}