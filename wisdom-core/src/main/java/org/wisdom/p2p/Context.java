package org.wisdom.p2p;

public class Context {
    boolean broken;
    boolean remove;
    boolean block;
    boolean keep;
    boolean pending;
    WisdomOuterClass.Message respoonse;
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

    public void response(Object o) {

    }

    public void relay() {
        relay = true;
    }

    public Payload getPayload() {
        return payload;
    }
}
