package org.wisdom.core.state;

import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    private Block findEraHead(Block target) {
        if (target.nHeight == 0) {
            return null;
        }
        long lastHeaderNumber = getEraAtBlockNumber(target.nHeight) * blocksPerEra;
        if (lastHeaderNumber == target.nHeight - 1){
            return blockChain.getBlock(target.hashPrevBlock);
        }
        return blockChain.findAncestorHeader(target.hashPrevBlock, lastHeaderNumber);
    }

    @Override
    public T getFromCache(Block eraHead){
        if(eraHead.nHeight == 0){
            return genesisState;
        }
        T t = (T)cache.get(getLRUCacheKey(eraHead.getHash()));
        if(t != null){
            return t;
        }
        Block parentEraHead = findEraHead(eraHead);
        t = getFromCache(parentEraHead);
        List<Block> blocks = blockChain.getAncestorBlocks(eraHead.getHash(), parentEraHead.nHeight + 1);
        t = (T)t.copy().updateBlocks(blocks);
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
        Block eraHead = findEraHead(block);
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
        return (T)newState;
    }

    public T getCurrentState() {
        Block target = blockChain.currentHeader();
        return getInstance(target);
    }
}
