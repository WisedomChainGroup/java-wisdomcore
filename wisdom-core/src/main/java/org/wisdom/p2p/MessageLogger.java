package org.wisdom.p2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageLogger implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(MessageLogger.class);

    @Value("${p2p.enable-message-log}")
    private boolean enableMessageLog;

    @Override
    public void onMessage(Context context, PeerServer server) {
        if (!enableMessageLog){
            return;
        }
        logger.info("receive " + context.getPayload().getCode().name() + " message from remote peer " + context.getPayload().getRemote().hostPort());
    }

    @Override
    public void onStart(PeerServer server) {

    }
}
