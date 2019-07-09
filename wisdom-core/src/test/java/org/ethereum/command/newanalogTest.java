package org.ethereum.command;

import com.google.protobuf.ByteString;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519KeyPair;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.encoding.BigEndian;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;
import org.junit.Test;

public class newanalogTest {

    public static byte[] test1(){
        Ed25519KeyPair pripubkey= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();

        Ed25519KeyPair pripubkey1= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey1=pripubkey1.getPrivateKey();
        Ed25519PublicKey publickey1=pripubkey1.getPublicKey();
        byte[] privkey1=privatekey1.getEncoded();
        byte[] pubkey1=publickey1.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey1));

        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：WDC转账
        byte[] type=new byte[1];
        type[0]=0x01;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(1);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = pubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64((long)(Math.random()*1000000));
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(100000000);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topub160;
        byte[] datelength=ByteUtil.intToBytes(0);
        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength);
        byte[] tranhash=SHA3Utility.sha3256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength);

        return tranfull;
    }


    public static byte[] test2(){
        Ed25519KeyPair pripubkey= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey));

        Ed25519KeyPair pripubkey1= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey1=pripubkey1.getPrivateKey();
        Ed25519PublicKey publickey1=pripubkey1.getPublicKey();
        byte[] privkey1=privatekey1.getEncoded();
        byte[] pubkey1=publickey1.getEncoded();
        byte[] sharpub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey1));

        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：孵化事务
        byte[] type=new byte[1];
        type[0]=0x09;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(1);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = pubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64((long)(Math.random()*1000));
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(30000000000L);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topub160;

        byte[] nullhash=new byte[32];
        HatchModel.Payload.Builder Payloads=HatchModel.Payload.newBuilder();
        Payloads.setTxId(ByteString.copyFrom(nullhash));
        Payloads.setSharePubkeyHash(Hex.encodeHexString(sharpub160));
        Payloads.setType(120);
        byte[] Payload=Payloads.build().toByteArray();
        byte[] datelength=ByteUtil.intToBytes(Payload.length);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,Payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);
        byte[] tranhash=SHA3Utility.sha3256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }


    public static byte[] test3(){
        Ed25519KeyPair pripubkey= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey));

        Ed25519KeyPair pripubkey1= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey1=pripubkey1.getPrivateKey();
        Ed25519PublicKey publickey1=pripubkey1.getPublicKey();
        byte[] privkey1=privatekey1.getEncoded();
        byte[] pubkey1=publickey1.getEncoded();
        byte[] sharpub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey1));

        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：提取利息
        byte[] type=new byte[1];
        type[0]=0x0a;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(1);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = pubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64((long)(Math.random()*1000));
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(12813000);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topub160;

        byte[] Payload=new byte[32];
        byte[] datelength=ByteUtil.intToBytes(Payload.length);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,Payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);
        byte[] tranhash=SHA3Utility.sha3256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }

    public static byte[] test4(){
        Ed25519KeyPair pripubkey= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey));

        Ed25519KeyPair pripubkey1= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey1=pripubkey1.getPrivateKey();
        Ed25519PublicKey publickey1=pripubkey1.getPublicKey();
        byte[] privkey1=privatekey1.getEncoded();
        byte[] pubkey1=publickey1.getEncoded();
        byte[] sharpub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey1));

        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：提取分享收益不
        byte[] type=new byte[1];
        type[0]=0x0b;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(1);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = pubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64((long)(Math.random()*1000));
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(1281300);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topub160;

        byte[] Payload=new byte[32];
        byte[] datelength=ByteUtil.intToBytes(Payload.length);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,Payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);
        byte[] tranhash=SHA3Utility.sha3256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }

    public static byte[] test5(){
        Ed25519KeyPair pripubkey= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();

        Ed25519KeyPair pripubkey1= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey1=pripubkey1.getPrivateKey();
        Ed25519PublicKey publickey1=pripubkey1.getPublicKey();
        byte[] privkey1=privatekey1.getEncoded();
        byte[] pubkey1=publickey1.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey1));

        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：存证
        byte[] type=new byte[1];
        type[0]=0x03;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(1);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = pubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64((long)(Math.random()*10000));
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(0);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=new byte[20];
        byte[] datelength=ByteUtil.intToBytes(0);
        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength);
        byte[] tranhash=SHA3Utility.sha3256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength);

        return tranfull;
    }

    public static byte[] test6(){
        Ed25519KeyPair pripubkey= Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey));

        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：提取利息
        byte[] type=new byte[1];
        type[0]=0x0c;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(1);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = pubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64((long)(Math.random()*1000));
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(30000000000L);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topub160;

        byte[] Payload=new byte[32];
        byte[] datelength=ByteUtil.intToBytes(Payload.length);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,Payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);
        byte[] tranhash=SHA3Utility.sha3256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }
}
