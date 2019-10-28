package org.wisdom.p2p;

import com.google.protobuf.AbstractMessage;

/**
 * @author sal 1564319846@qq.com
 * wisdom protocol context for multi-thread communication
 */
public class Context {
    boolean broken;
    boolean remove;
    boolean block;
    boolean keep;
    boolean pending;
    AbstractMessage response;
    boolean relay;
    Payload payload;

    public void exit() {
        broken = true;
    }


    public void remove() {
        remove = true;
    }

    public void block() {
        block = true;
    }

    public void keep() {
        keep = true;
    }

    public void pend() {
        pending = true;
    }

    public void response(AbstractMessage o) {
        response = o;
    }

    public void relay() {
        relay = true;
    }

    public Payload getPayload() {
        return payload;
    }
}
