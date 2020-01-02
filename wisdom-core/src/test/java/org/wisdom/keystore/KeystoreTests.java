package org.wisdom.keystore;



import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.Keystore;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.junit.Test;



public class KeystoreTests {
    private static final String testPrivKey   = "947d3ad33d2b14856d504c5c2984c1c2bb3a9d6c7b4b6307d40d45347903b33c";
    private static final String password = "yongyang2018";

    @Test
    public void keyStoreLoads()throws Throwable{
        Keystore ks = KeystoreAction.unmarshal(testJson());
        assert ks.crypto != null;
        assert ks.kdf.equals("argon2id");
    }

    @Test
    public void keyStoreSave(){
        Keystore ks = KeystoreAction.unmarshal(testJson());
        String str = KeystoreAction.marshal(ks);
        Keystore ks2 = KeystoreAction.unmarshal(str);
        assert ks.crypto.cipherparams.iv.equals(ks2.crypto.cipherparams.iv);
    }
    @Test
    public void fromPassword() throws Throwable{
        Keystore ks = KeystoreAction.fromPassword(password);
//        assert ks.kdfparams.salt != null;
    }

    @Test
    public void verifyPassword() throws Exception{
        Keystore ks = KeystoreAction.unmarshal(testJson());
        Keystore ks2 = KeystoreAction.fromPassword(password);
        assert KeystoreAction.verifyPassword(ks,password);
        assert KeystoreAction.verifyPassword(ks2,password);
        assert ks.kdf.equals("argon2id");
    }
    @Test
    public void decrypt() throws Exception{
        Keystore ks = KeystoreAction.unmarshal(testJson());
        assert Hex.encodeHexString(KeystoreAction.decrypt(ks,password)).equals(testPrivKey);
    }

    public static String testJson(){
        return "{" +
                "  \"address\": \"WXCf8e2b617210d44ccd232ec081f17be76b3eaa6f0cb41\"," +
                "  \"crypto\": {" +
                "    \"cipher\": \"aes-256-ctr\"," +
                "    \"ciphertext\": \"e58c5cd0f07f3a080859ab69ae261d67af2aaa02347283e766870962c9844e0d\"," +
                "    \"cipherparams\": {" +
                "      \"iv\": \"2d96e310684da9c3ce87db65c5a5606c\"" +
                "    }" +
                "  }," +
                "  \"id\": \"617ff99a-5fbe-4e0c-b39c-e6473a6bfd5e\"," +
                "  \"version\": \"1\"," +
                "  \"kdf\": \"argon2id\"," +
                "    \"timeCost\": 4," +
                "    \"memoryCost\": 20480," +
                "    \"parallelism\": 2," +
                "    \"salt\": \"c5b5aef708139af895a52eef251ef7d747680ee785f30e9bc0f5c897fed2a1d0\"," +
                "  \"mac\": \"b5a1e277c2d4f8947fe7c0f43430ab8c5f2df144d1691ca1fb7335c198932a4d9e269a0e5ff27bd818092bbc2c1b68df9fad4ea5e5e9f1ee6d4507b6390c1a0d\"" +
                "}";
    }

    @Test
    public void test1() throws DecoderException {
        String privates=KeystoreAction.readKeystoreByPath("2x50901q8w","C:\\Users\\Administrator\\IdeaProjects\\java-wisdomcore\\Keystore\\keystore");
        System.out.println(privates);

        byte[] pubkey=Hex.decodeHex("5ac9cc553fe5047cf0b33b983f50afe228c17b050c688a7d22800bfbdef9af66".toCharArray());
        System.out.println("pubkey-->pubkeyhash:"+Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(pubkey))));

//        System.out.println("pubkey-->address:"+KeystoreAction.pubkeyToAddress(pubkey, (byte) 0x00));
//
//        byte[] pubhash=Hex.decodeHex("97a87c356867327bfbc1a600cce02546ba07bf20".toCharArray());
//
//        System.out.println("pubhash-->address:"+KeystoreAction.pubkeyHashToAddress(pubhash, (byte) 0x00));

        String address1="1pQfDX4fvz7uzBQuM9FbuoKWohmhg9TmY";
        String address2="12hk3cWr28BJWjASCy9Diw4bqH8SnWvSpP";
        String address3="18mRFaYHguJyCWtAA9ZV1PZuGAb6UzAijE";
        String address4="19JNq2jAprkxVrpkgBiRaa1m47WcUMXtCb";
        String address5="1317J5fZb8kVrACnfi3PXN1T21573hYata";

        System.out.println("种子节点---> address-->pubhash:"+Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(address1))+"\n"+
                Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(address2))+"\n"+
                Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(address3))+"\n"+
                Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(address4))+"\n"+
                Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(address5))+"\n");

        String address="18mRFaYHguJyCWtAA9ZV1PZuGAb6UzAijE";
        System.out.println("address-->pubhash:"+Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(address)));
    }
}
