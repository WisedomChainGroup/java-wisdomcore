package org.wisdom.core.state;

import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sal 1564319846@qq.com
 * state factory, lru cached
 */
public class StateFactory<T extends State> extends AbstractStateFactory {
    private static Logger logger = LoggerFactory.getLogger(StateFactory.class);

    private T genesisState;

    public StateFactory(WisdomBlockChain blockChain, int cacheSize, T genesisState) {
        super(blockChain, cacheSize);
        this.genesisState = genesisState;
    }

    public T getFromCache(Block block) {
        if(block.nHeight == 0){
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
}
