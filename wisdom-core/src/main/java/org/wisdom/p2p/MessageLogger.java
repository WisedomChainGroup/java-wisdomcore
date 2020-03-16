package org.wisdom.p2p;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "net")
public class MessageLogger implements Plugin {

    @Value("${p2p.enable-message-log}")
    private boolean enableMessageLog;

    @Override
    public void onMessage(Context context, PeerServer server) {
        if (!enableMessageLog){
            return;
        }
        if (context.getPayload().getCode().equals(WisdomOuterClass.Code.NOTHING)){
            return;
        }
        log.debug("receive " + context.getPayload().getCode().name() + " message from remote peer " + context.getPayload().getRemote().hostPort());
    }

    @Override
    public void onStart(PeerServer server) {

    }
}
