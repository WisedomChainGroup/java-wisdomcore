package org.wisdom.p2p;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;

import java.util.concurrent.ConcurrentMap;

/**
 * @author sal 1564319846@qq.com
 * wisdom filter
 */
@Component
public class MessageFilter implements Plugin {

    private ConcurrentMap<String, Boolean> msgs;
    private static final int CACHE_SIZE = PeersCache.MAX_PEERS * 32;

    public MessageFilter() {
        this.msgs = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(CACHE_SIZE).build();
    }

    @Override
    public void onMessage(Context context, PeerServer server) {
        if (context.getPayload().getRemote().peerID.length != 32) {
            context.exit();
            return;
        }
        // 过滤掉签名不合法的包
        if (!new Ed25519PublicKey(context.getPayload().getRemote().peerID).verify(
                Util.getRawForSign(context.getPayload().getMessage()), context.getPayload().getSignature()
        )) {
            context.exit();
            return;
        }
        // 过滤掉自己发的包
        if (context.getPayload().getRemote().equals(server.getSelf())) {
            context.exit();
            return;
        }
        // 过滤掉ttl小于0的包
        if (context.getPayload().getTtl() < 0) {
            context.exit();
            return;
        }
        String k = Hex.encodeHexString(context.getPayload().getSignature());
        // 过滤掉收到过的消息
        if (msgs.containsKey(k)) {
            context.exit();
        }
        msgs.put(k, true);
    }

    @Override
    public void onStart(PeerServer server) {

    }
}
