package org.wisdom.p2p;

import org.springframework.stereotype.Component;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;

// 签名校验中间件
@Component
public class MessageFilter implements Plugin {
    @Override
    public void onMessage(Context context, PeerServer server) {
        if (context.getPayload().remote.peerID.length != 32) {
            context.exit();
            return;
        }
        if (!new Ed25519PublicKey(context.getPayload().remote.peerID).verify(
                Util.getRawForSign(context.getPayload().getMessage()), context.getPayload().signature
        )) {
            context.exit();
            return;
        }
        if (context.getPayload().ttl < 0) {
            context.exit();
        }
    }

    @Override
    public void onStart(PeerServer server) {

    }
}
