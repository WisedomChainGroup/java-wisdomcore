package org.wisdom.Controller;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.crypto.HashUtil;

import java.util.concurrent.ConcurrentMap;

@Component
public class PacketCache {
    protected ConcurrentMap<String, Boolean> cache;

    public boolean hasReceived(Packet packet) {
        String key = Hex.encodeHexString(HashUtil.keccak256(packet.data));
        boolean has = cache.containsKey(key);
        if (has) {
            return true;
        }
        cache.put(key, true);
        return false;
    }

    public PacketCache(@Value("${p2p.packet-cacheSize}") int cacheSize) {
        this.cache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(cacheSize).build();
    }
}
