package org.wisdom.util;

public class BytesReader {
    private byte[] data;
    private int pc;

    public byte[] read(int size) {
        byte[] res = Arrays.copyOfRange(data, pc, pc + size);
        pc += size;
        return res;
    }

    public byte read() {
        return read(1)[0];
    }

    public BytesReader(byte[] data) {
        this.data = data;
    }
}
