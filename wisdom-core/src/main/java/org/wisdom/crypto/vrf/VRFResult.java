package org.wisdom.crypto.vrf;

import org.bouncycastle.util.Arrays;

public class VRFResult {
    // 32 byte pseudo random variable
    private byte[] r;

    // 64byte
    private byte[] proof;

    public VRFResult(byte[] encoded){
        this.r = Arrays.copyOfRange(encoded, 1, 1 + encoded[0]);
        this.proof = Arrays.copyOfRange(encoded, 1 + encoded[0], encoded.length);
    }

    public VRFResult(byte[] r, byte[] proof) {
        this.r = r;
        this.proof = proof;
    }

    /**
     *
     * @return the pseudo random value
     */
    public byte[] getR() {
        return r;
    }

    void setR(byte[] r) {
        this.r = r;
    }

    public byte[] getProof() {
        return proof;
    }

    void setProof(byte[] proof) {
        this.proof = proof;
    }

    public byte[] getEncoded(){
        return Arrays.concatenate(new byte[]{(byte) this.r.length}, this.r, this.proof);
    }
}
