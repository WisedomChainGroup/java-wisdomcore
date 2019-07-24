package org.wisdom.p2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class MessageLogger implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(MessageLogger.class);

    @Override
    public void onMessage(Context context, PeerServer server) {
        logger.info("receive " + context.getPayload().getCode().name() + " message from remote peer " + context.getPayload().getRemote().toString());
    }

    @Override
    public void onStart(PeerServer server) {

    }
}
