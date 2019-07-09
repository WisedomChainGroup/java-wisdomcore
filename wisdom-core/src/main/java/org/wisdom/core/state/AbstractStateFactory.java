package org.wisdom.core.state;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.util.Base64;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractStateFactory<T extends State> {
    static final Base64.Encoder encoder = Base64.getEncoder();

    protected WisdomBlockChain blockChain;

    protected ConcurrentMap<String, T> cache;

    protected String getLRUCacheKey(byte[] hash) {
        return encoder.encodeToString(hash);
    }


    protected T getFromCache(Block target) {
        if (target == null) {
            return null;
        }
        return cache.get(getLRUCacheKey(target.getHash()));
    }

    public AbstractStateFactory(WisdomBlockChain blockChain, int cacheSize) {
        this.blockChain = blockChain;
        this.cache = new ConcurrentLinkedHashMap.Builder<String, T>().maximumWeightedCapacity(cacheSize).build();
    }

    public abstract T getInstance(Block block);

    public abstract T getCurrentState();
}
