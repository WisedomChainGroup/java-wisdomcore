package org.wisdom.account;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.codec.binary.Hex;
import org.tdf.rlp.*;
import org.wisdom.core.account.Transaction;
import org.wisdom.crypto.PublicKey;
import org.wisdom.util.Address;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RLPEncoding(PublicKeyHash.PublicKeyHashEncoder.class)
@RLPDecoding(PublicKeyHash.PublicKeyHashDecoder.class)
@JsonDeserialize(using = PublicKeyHash.PublicKeyHashDeserializer.class)
public class PublicKeyHash {
    public static class PublicKeyHashEncoder implements RLPEncoder<PublicKeyHash>{
        @Override
        public RLPElement encode(PublicKeyHash publicKeyHash) {
            return RLPItem.fromBytes(publicKeyHash.publicKeyHash);
        }
    }

    public static class PublicKeyHashDecoder implements RLPDecoder<PublicKeyHash>{
        @Override
        public PublicKeyHash decode(RLPElement rlpElement) {
            return new PublicKeyHash(rlpElement.asBytes());
        }
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicKeyHash that = (PublicKeyHash) o;
        return Arrays.equals(publicKeyHash, that.publicKeyHash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(publicKeyHash);
    }

    public static class PublicKeyHashDeserializer extends StdDeserializer<PublicKeyHash>{
        public PublicKeyHashDeserializer(Class<?> vc) {
            super(PublicKeyHash.class);
        }

        @Override
        public PublicKeyHash deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            if(node.isNull()) return null;
            String encoded = node.asText();
            if(encoded == null || encoded.trim().isEmpty()){
                return null;
            }
            return PublicKeyHash.fromHex(encoded).orElseThrow(IllegalArgumentException::new);
        }
    }
}
