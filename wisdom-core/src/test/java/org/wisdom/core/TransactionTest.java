package org.ethereum.core;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519KeyPair;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.encoding.BigEndian;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.util.ByteUtil;
import org.wisdom.core.account.Transaction;
import org.junit.Test;

public class TransactionTest {

    @Test
    public void test1() {
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();

        Ed25519KeyPair pripubkey1 = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey1 = pripubkey1.getPrivateKey();
        Ed25519PublicKey publickey1 = pripubkey1.getPublicKey();
        byte[] privkey1 = privatekey1.getEncoded();
        byte[] pubkey1 = publickey1.getEncoded();
        byte[] topub160 = RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey1));

        //版本号
        byte[] version = new byte[1];
        version[0] = 0x01;
        //类型：WDC转账
        byte[] type = new byte[1];
        type[0] = 0x01;
        //Nonce 无符号64位
        byte[] nonce = BigEndian.encodeUint64(1);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = pubkey;
        //gas单价
        byte[] gasPrice = BigEndian.encodeUint64(100000);
        //转账金额 无符号64位
        byte[] Amount = BigEndian.encodeUint64(100000000);
        //为签名留白
        byte[] signull = new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash = topub160;
        byte[] datelength = ByteUtil.intToBytes(0);
        byte[] nosig = ByteUtil.merge(version, type, nonce, fromPubkey, gasPrice, Amount, signull, toPubkeyHash, datelength);
        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull = ByteUtil.merge(version, type, nonce, fromPubkey, gasPrice, Amount, sig, toPubkeyHash, datelength);
        byte[] tranhash = SHA3Utility.sha3256(sigfull);

        byte[] tranfull = ByteUtil.merge(version, tranhash, type, nonce, fromPubkey, gasPrice, Amount, sig, toPubkeyHash, datelength);


        Transaction tx = new Transaction();
        tx.version = 1;
        tx.type = 1;
        tx.nonce = 1;
        tx.from = pubkey;
        tx.gasPrice = 100000;
        tx.amount = 100000000;
        tx.signature = new byte[64];
        tx.to = topub160;

        System.out.println(Hex.encodeHexString(nosig));
        System.out.println(Hex.encodeHexString(tx.getRawForSign()));

        System.out.println("================");

        System.out.println(Hex.encodeHexString(sig));
        System.out.println(Hex.encodeHexString(new Ed25519PrivateKey(privkey).sign(tx.getRawForSign())));

        System.out.println("================");

        System.out.println(Hex.encodeHexString(sigfull));
        tx.signature = new Ed25519PrivateKey(privkey).sign(tx.getRawForSign());
        System.out.println(Hex.encodeHexString(tx.getRawForHash()));

        System.out.println("================");
        System.out.println(Hex.encodeHexString(tranhash));
        System.out.println(Hex.encodeHexString(tx.getHash()));


        System.out.println("================");
        System.out.println(Hex.encodeHexString(tranfull));
        System.out.println(Hex.encodeHexString(tx.toRPCBytes()));
    }
}
