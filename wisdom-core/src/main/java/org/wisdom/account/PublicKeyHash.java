package org.wisdom.account;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.account.Transaction;
import org.wisdom.util.Address;

import java.util.Optional;

public class PublicKeyHash {
    private byte[] publicKeyHash;
    private String address;
    private String hex;
    public static PublicKeyHash fromPublicKey(byte[] publicKey){
        return new PublicKeyHash(Address.publicKeyToHash(publicKey));
    }

    public static Optional<PublicKeyHash> fromHex(String hex){
        byte[] publicKeyHash;
        try {
            publicKeyHash = Hex.decodeHex(hex);
            if (publicKeyHash.length == Transaction.PUBLIC_KEY_SIZE) {
                return Optional.of(fromPublicKey(publicKeyHash));
            }
        } catch (Exception e) {
            publicKeyHash = Address.addressToPublicKeyHash(hex);
        }
        if (publicKeyHash == null) {
            return Optional.empty();
        }
        return Optional.of(new PublicKeyHash(publicKeyHash));
    }

    public PublicKeyHash(byte[] publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }

    public String getAddress(){
        if (address == null) address = Address.publicKeyHashToAddress(publicKeyHash);
        return address;
    }

    public byte[] getPublicKeyHash() {
        return publicKeyHash;
    }

    public String getHex(){
        if (hex == null) hex = Hex.encodeHexString(publicKeyHash);
        return hex;
    }
}
