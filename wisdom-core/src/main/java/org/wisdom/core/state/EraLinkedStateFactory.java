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

import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.encoding.BigEndian;

import java.util.List;

/**
 * @author sal 1564319846@qq.com
 * era linked state factory, updates per era
 */
public class EraLinkedStateFactory<T extends State> extends AbstractStateFactory {
    private int blocksPerEra;
    private static final Logger logger = LoggerFactory.getLogger(EraLinkedStateFactory.class);
    private T genesisState;

    public EraLinkedStateFactory(WisdomBlockChain blockChain, int cacheSize, T genesisState, int blocksPerEra) {
        super(blockChain, cacheSize);
        this.genesisState = genesisState;
        this.blocksPerEra = blocksPerEra;
    }

    public int getBlocksPerEra() {
        return blocksPerEra;
    }

    private long getEraAtBlockNumber(long number) {
        return (number - 1) / blocksPerEra;
    }


    private Block prevEralastBlock(Block target) {
        if (target.nHeight == 0) {
            return null;
        }
        long lastHeaderNumber = getEraAtBlockNumber(target.nHeight) * blocksPerEra;
        if (lastHeaderNumber == target.nHeight - 1) {
            return blockChain.getBlock(target.hashPrevBlock);
        }
        return blockChain.findAncestorHeader(target.hashPrevBlock, lastHeaderNumber);
    }

    @Override
    public T getFromCache(Block eraHead) {
        if (eraHead.nHeight == 0) {
            return genesisState;
        }
        T t = (T) cache.get(getLRUCacheKey(eraHead.getHash()));
        if (t != null) {
            return t;
        }
        Block parentEraHead = prevEralastBlock(eraHead);
        t = getFromCache(parentEraHead);
        List<Block> blocks = blockChain.getAncestorBlocks(eraHead.getHash(), parentEraHead.nHeight + 1);
        t = (T) t.copy().updateBlocks(blocks);
        cache.put(getLRUCacheKey(eraHead.getHash()), t);
        return t;
    }

    public T getInstance(Block block) {
        if (block == null) {
            return null;
        }
        if (block.nHeight == 0 || getEraAtBlockNumber(block.nHeight) == 0) {
            return genesisState;
        }
        Block eraHead = prevEralastBlock(block);
        if (eraHead == null) {
            return null;
        }
        State cached = getFromCache(eraHead);
        if (cached != null) {
            return (T) cached;
        }
        T parentEraState = getInstance(eraHead);
        if (parentEraState == null) {
            return null;
        }
        List<Block> blocks = blockChain.getAncestorBlocks(eraHead.getHash(), eraHead.nHeight - (blocksPerEra - 1));
        State newState = parentEraState.copy().updateBlocks(blocks);
        cache.put(getLRUCacheKey(eraHead.getHash()), newState);
        return (T) newState;
    }

    public T getCurrentState() {
        Block target = blockChain.currentHeader();
        return getInstance(target);
    }

    // init cache when restart, avoid stack overflow
    public void initCache() {
        Block latest = blockChain.currentBlock();
        if (latest.nHeight < blocksPerEra) {
            return;
        }
        long latestHeight = latest.nHeight - 6 < 0 ? latest.nHeight : latest.nHeight - 6;
        Block confirmed = blockChain.getCanonicalBlock(latestHeight);
        long era = getEraAtBlockNumber(confirmed.nHeight);
        T state = genesisState;
        for (int i = 0; i < era; i++) {
            List<Block> bks = blockChain.getCanonicalBlocks(i * blocksPerEra + 1, blocksPerEra);
            state = (T) state.copy().updateBlocks(bks);
            cache.put(getLRUCacheKey(bks.get(bks.size() - 1).getHash()), state);
        }
    }
}
