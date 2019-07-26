package org.wisdom.Controller;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.wisdom.crypto.HashUtil;

import java.util.concurrent.ConcurrentMap;

@Component
public class PacketCache {
    protected ConcurrentMap<String, Boolean> cache;
    static final int cacheSize = 32;

    public boolean hasReceived(Packet packet) {
        String key = Hex.encodeHexString(HashUtil.keccak256(packet.data));
        boolean has = cache.containsKey(key);
        if (has) {
            return true;
        }
        cache.put(key, true);
        return false;
    }

    public PacketCache() {
        this.cache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(cacheSize).build();
    }
}
