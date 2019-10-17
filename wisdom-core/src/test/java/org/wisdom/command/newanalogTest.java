package org.wisdom.command;

import com.google.protobuf.ByteString;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519KeyPair;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.encoding.BigEndian;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class newanalogTest {

    public static byte[] test1(){
        Ed25519KeyPair pripubkey= Ed25519.generateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();

        Ed25519KeyPair pripubkey1= Ed25519.generateKeyPair();
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
        Ed25519KeyPair pripubkey= Ed25519.generateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey));

        Ed25519KeyPair pripubkey1= Ed25519.generateKeyPair();
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
        Payloads.setSharePubkeyHash(Hex.encodeHexString(topub160));
        Payloads.setType(120);
        byte[] Payload=Payloads.build().toByteArray();
        byte[] datelength=ByteUtil.intToBytes(Payload.length);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,Payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(privkey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);
        byte[] tranhash=SHA3Utility.keccak256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }


    public static byte[] test3(){
        Ed25519KeyPair pripubkey= Ed25519.generateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey));

        Ed25519KeyPair pripubkey1= Ed25519.generateKeyPair();
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
        Ed25519KeyPair pripubkey= Ed25519.generateKeyPair();
        Ed25519PrivateKey privatekey=pripubkey.getPrivateKey();
        Ed25519PublicKey publickey=pripubkey.getPublicKey();
        byte[] privkey=privatekey.getEncoded();
        byte[] pubkey=publickey.getEncoded();
        byte[] topub160=RipemdUtility.ripemd160(SHA3Utility.sha3256(pubkey));

        Ed25519KeyPair pripubkey1= Ed25519.generateKeyPair();
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

    public static byte[] test5() throws DecoderException {
        String fromprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] fromprikey=Hex.decodeHex(fromprivatekey.toCharArray());
        String frompublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] frompubkey=Hex.decodeHex(frompublickey.toCharArray());
        byte[] frompubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(frompubkey));

        String toprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] toprikey=Hex.decodeHex(toprivatekey.toCharArray());
        String topublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] topubkey=Hex.decodeHex(topublickey.toCharArray());
        byte[] topubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(topubkey));

        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：存证
        byte[] type=new byte[1];
        type[0]=0x03;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(5);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = frompubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64((long)(Math.random()*10000));
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(0);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=new byte[20];

        byte[] payload=new byte[1000];
        byte[] datelength=ByteUtil.intToBytes(payload.length);

        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(toprikey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,payload);
        byte[] tranhash=SHA3Utility.keccak256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,payload);

        return tranfull;
    }

    public static byte[] test6(){
        Ed25519KeyPair pripubkey= Ed25519.generateKeyPair();
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
        byte[] tranhash=SHA3Utility.keccak256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }

    public static byte[] test7() throws DecoderException {
        String fromprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] fromprikey=Hex.decodeHex(fromprivatekey.toCharArray());
        String frompublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] frompubkey=Hex.decodeHex(frompublickey.toCharArray());
        byte[] frompubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(frompubkey));

        String toprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] toprikey=Hex.decodeHex(toprivatekey.toCharArray());
        String topublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] topubkey=Hex.decodeHex(topublickey.toCharArray());
        byte[] topubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(topubkey));



         //版本号
         byte[] version=new byte[1];
         version[0]=0x01;   
        //类型：投票
        byte[] type=new byte[1];
        type[0]=0x02;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(3);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = frompubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64(10);
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(30000000000L);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topubkeyhash;

/*        byte[] Payload=new byte[32];*/
        byte[] datelength=ByteUtil.intToBytes(0);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(fromprikey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength);
        byte[] tranhash=SHA3Utility.keccak256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength);

        return tranfull;
    }

    public static byte[] test8() throws DecoderException {
        String fromprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] fromprikey=Hex.decodeHex(fromprivatekey.toCharArray());
        String frompublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] frompubkey=Hex.decodeHex(frompublickey.toCharArray());
        byte[] frompubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(frompubkey));

        String toprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] toprikey=Hex.decodeHex(toprivatekey.toCharArray());
        String topublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] topubkey=Hex.decodeHex(topublickey.toCharArray());
        byte[] topubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(topubkey));



        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：撤回投票
        byte[] type=new byte[1];
        type[0]=0x0d;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(4);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = frompubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64(10);
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(30000000000L);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topubkeyhash;

        String payloadhex="3325d2a80b98afdcc8ce1eb4f169b928eff1b53062533573990d392a4225b853";
        byte[] Payload=Hex.decodeHex(payloadhex.toCharArray());
        byte[] datelength=ByteUtil.intToBytes(Payload.length);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,Payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(fromprikey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);
        byte[] tranhash=SHA3Utility.keccak256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }

    public static byte[] test9() throws DecoderException {
        String fromprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] fromprikey=Hex.decodeHex(fromprivatekey.toCharArray());
        String frompublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] frompubkey=Hex.decodeHex(frompublickey.toCharArray());
        byte[] frompubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(frompubkey));

        String toprivatekey="f4b6b5b72dfb8b44241e7ed61e2c61e56e48e8d035650f35b5ebc58981ce009e";
        byte[] toprikey=Hex.decodeHex(toprivatekey.toCharArray());
        String topublickey="5b7514a3d3337022cfaf9619b8d7dc8c5fbbb3c3d942ded3ee240248c0550ad8";
        byte[] topubkey=Hex.decodeHex(topublickey.toCharArray());
        byte[] topubkeyhash=RipemdUtility.ripemd160(SHA3Utility.sha3256(topubkey));



        //版本号
        byte[] version=new byte[1];
        version[0]=0x01;
        //类型：撤回投票
        byte[] type=new byte[1];
        type[0]=0x0d;
        //Nonce 无符号64位
        byte[] nonce=BigEndian.encodeUint64(4);
        //签发者公钥哈希 20字节
        byte[] fromPubkey = frompubkey;
        //gas单价
        byte[] gasPrice =BigEndian.encodeUint64(10);
        //转账金额 无符号64位
        byte[] Amount=BigEndian.encodeUint64(30000000000L);
        //为签名留白
        byte[] signull=new byte[64];
        //接收者公钥哈希
        byte[] toPubkeyHash=topubkeyhash;

        String payloadhex="3325d2a80b98afdcc8ce1eb4f169b928eff1b53062533573990d392a4225b853";
        byte[] Payload=Hex.decodeHex(payloadhex.toCharArray());
        byte[] datelength=ByteUtil.intToBytes(Payload.length);


        byte[] nosig=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,signull,toPubkeyHash,datelength,Payload);
        //签名数据
        byte[] sig=new Ed25519PrivateKey(fromprikey).sign(nosig);

        byte[] sigfull=ByteUtil.merge(version,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);
        byte[] tranhash=SHA3Utility.keccak256(sigfull);

        byte[] tranfull=ByteUtil.merge(version,tranhash,type,nonce,fromPubkey,gasPrice,Amount,sig,toPubkeyHash,datelength,Payload);

        return tranfull;
    }

    private static Map<String,Object> map;
    public static void main(String args[]) throws DecoderException {
        String nowrate=new RateTable().selectrate(0,365);
        BigDecimal aount=new BigDecimal(30012345678L);
        BigDecimal nowratebig=new BigDecimal(nowrate);
        BigDecimal dayrate=aount.multiply(nowratebig);
        long s=dayrate.longValue();
        BigDecimal s1=BigDecimal.valueOf(s);
        BigDecimal lastdaysbig=BigDecimal.valueOf(11);
        long intersets=s1.multiply(lastdaysbig).longValue();
        long interset=(long)(dayrate.longValue()*11);
        System.out.println(intersets+"--->"+interset);


        String rate =new RateTable().selectrate(1220, 0);
    }






    public static class test111{
        private List<String> s;

        public List<String> getS() {
            return s;
        }

        public void setS(List<String> s) {
            this.s = s;
        }
    }
}
