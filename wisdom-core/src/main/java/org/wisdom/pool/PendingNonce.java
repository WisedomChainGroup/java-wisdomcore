package org.wisdom.pool;

public class PendingNonce {
    private long nonce;
    private int state;//0未确认，2已确认

    public PendingNonce() {
    }

    public PendingNonce(long nonce, int state) {
        this.nonce = nonce;
        this.state = state;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
