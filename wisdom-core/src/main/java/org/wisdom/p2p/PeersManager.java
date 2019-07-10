package org.wisdom.p2p;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeersManager implements MessageHandler{
    @Autowired
    private ConsensusHandler consensusHandler;

    @Override
    public void handleMessage(Wisdom.Message msg) {

    }

    @Override
    public int getPriority() {
        return 0;
    }
}
