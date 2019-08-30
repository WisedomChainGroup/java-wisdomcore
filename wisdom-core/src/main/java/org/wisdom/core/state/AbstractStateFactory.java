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
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.db.StateDB;

import java.util.Base64;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractStateFactory<T extends State> {
    static final Base64.Encoder encoder = Base64.getEncoder();
    protected StateDB stateDB;

    protected ConcurrentMap<String, T> cache;

    protected WisdomBlockChain blockChain;

    protected String getLRUCacheKey(byte[] hash) {
        return encoder.encodeToString(hash);
    }


    protected T getFromCache(Block target) {
        if (target == null) {
            return null;
        }
        return cache.get(getLRUCacheKey(target.getHash()));
    }

    public AbstractStateFactory(StateDB stateDB, WisdomBlockChain blockChain, int cacheSize) {
        this.stateDB = stateDB;
        this.blockChain = blockChain;
        this.cache = new ConcurrentLinkedHashMap.Builder<String, T>().maximumWeightedCapacity(cacheSize).build();
    }

    public abstract T getInstance(Block block);
}