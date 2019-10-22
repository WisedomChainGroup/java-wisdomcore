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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.db.StateDB;

import java.util.List;

/**
 * @author sal 1564319846@qq.com
 * era linked state factory, updates per era
 */
public class EraLinkedStateFactory<T extends State<T>> extends AbstractStateFactory<T> {
    private int blocksPerEra;
    private static final Logger logger = LoggerFactory.getLogger(EraLinkedStateFactory.class);

    public EraLinkedStateFactory(StateDB stateDB, int cacheSize, T genesisState, int blocksPerEra) {
        super(stateDB, genesisState, cacheSize);
        this.blocksPerEra = blocksPerEra;
    }

    public int getBlocksPerEra() {
        return blocksPerEra;
    }

    public static long getEraAtBlockNumber(long number, int blocksPerEra) {
        return (number - 1) / blocksPerEra;
    }

    private Block prevEraLast(Block target) {
        if (target.nHeight == 0) {
            return null;
        }
        long lastHeaderNumber = getEraAtBlockNumber(target.nHeight, this.blocksPerEra) * blocksPerEra;
        if (lastHeaderNumber == target.nHeight - 1) {
            return stateDB.getHeader(target.hashPrevBlock);
        }
        return stateDB.findAncestorHeader(target.hashPrevBlock, lastHeaderNumber);
    }

    @Override
    public T getFromCache(Block eraHead) {
        if (eraHead.nHeight == 0) {
            return genesisState;
        }
        T t = cache.get(getLRUCacheKey(eraHead.getHash()));
        if (t != null) {
            return t;
        }
        Block parentEraHead = prevEraLast(eraHead);
        if (parentEraHead == null) {
            return null;
        }
        t = getFromCache(parentEraHead);
        List<Block> blocks = stateDB.getAncestorBlocks(eraHead.getHash(), parentEraHead.nHeight + 1);
        t = t.copy().updateBlocks(blocks);
        cache.put(getLRUCacheKey(eraHead.getHash()), t);
        return t.copy();
    }

    public T getInstance(Block block) {
        if (block == null) {
            return null;
        }
        if (block.nHeight == 0 || getEraAtBlockNumber(block.nHeight, this.blocksPerEra) == 0) {
            return genesisState;
        }
        Block eraHead = prevEraLast(block);
        if (eraHead == null) {
            return null;
        }
        T cached = getFromCache(eraHead);
        if (cached != null) {
            return cached;
        }
        T parentEraState = getInstance(eraHead);
        if (parentEraState == null) {
            return null;
        }
        List<Block> blocks = stateDB.getAncestorBlocks(eraHead.getHash(), eraHead.nHeight - (blocksPerEra - 1));
        T newState = parentEraState.copy().updateBlocks(blocks);
        cache.put(getLRUCacheKey(eraHead.getHash()), newState);
        return newState;
    }
}

