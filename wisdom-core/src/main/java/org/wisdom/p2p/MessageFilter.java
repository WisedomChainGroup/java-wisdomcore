package org.wisdom.p2p;

import org.springframework.stereotype.Component;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;

/**
 * @author sal 1564319846@qq.com
 * wisdom filter
 */
@Component
public class MessageFilter implements Plugin {
    @Override
    public void onMessage(Context context, PeerServer server) {
        if (context.getPayload().remote.peerID.length != 32) {
            context.exit();
            return;
        }
        // 过滤掉签名不合法的包
        if (!new Ed25519PublicKey(context.getPayload().remote.peerID).verify(
                Util.getRawForSign(context.getPayload().getMessage()), context.getPayload().signature
        )) {
            context.exit();
            return;
        }
        // 过滤掉自己发的包
        if(context.getPayload().remote.equals(server.getSelf())){
            context.exit();
            return;
        }
        // 过滤掉ttl小于0的包
        if (context.getPayload().ttl < 0) {
            context.exit();
            return;
        }
        // 过滤掉不是发给自己的包
        if(!context.getPayload().recipient.equals(server.getSelf())){
            context.exit();
        }
    }

    @Override
    public void onStart(PeerServer server) {

    }
}
