package org.wisdom.p2p;

public interface MessageHandler {
    void handleMessage(Wisdom.Message msg);

    int getPriority();
}
