package org.wisdom.keystore.crypto;

import org.bouncycastle.util.encoders.Hex;

public class Hash {

    /**
     * An empty hash.
     */
    public static final Hash EMPTYHASH = new Hash(new byte[32]);

    private final byte[] data;

    private final String hexHash;

    /**
     * Creates new Uint256 object.
     *
     * @param data The raw hash.
     */
    public Hash(final byte[] data) {
        this.data = data;
        this.hexHash= Hex.toHexString(data);
    }

    public String getHexHash(){
        return this.hexHash;
    }

    public byte[] getByteHash(){
        return this.data;
    }


}
