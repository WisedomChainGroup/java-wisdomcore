package org.wisdom.controller;

public class Packet {
    public byte[] data;
    public long ttl;

    public void dec() {
        if (ttl > 0) {
            ttl--;
        }
    }

    public Packet(){

    }

    public Packet(byte[] data, long ttl) {
        this.data = data;
        this.ttl = ttl;
    }
}
