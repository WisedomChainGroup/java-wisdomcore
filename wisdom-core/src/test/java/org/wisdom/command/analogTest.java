package org.wisdom.command;


import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519KeyPair;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.encoding.BigEndian;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.util.ByteUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class analogTest {

    public static byte[] outscrip;

    public static byte[] analogdate() {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();
//        System.out.println("privkey:"+privkey.length);
//        String address=new Address().pubkeyToAddress(publickey);
//        System.out.println(address);
        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = 0x01;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
//        System.out.println("Date:"+new Date().getTime());
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4

        //输出 1个62长度
        byte[] outacount = new byte[1];
        outacount[0] = 1;
        //转出金额 8长度
        byte[] Amount = ByteUtil.longToBytes(120000000);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf[0] = (byte) 0x76;
        outf[1] = (byte) 0xa9;

        byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
        byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);

//        System.out.println(pubkeysha.length);
        byte[] outl = new byte[2];
        outl[0] = (byte) 0x88;
        outl[1] = (byte) 0xac;
        outscrip = ByteUtil.merge(outf, pubkey160, outl);
/*        for(byte b:outscrip){
            System.out.println("~~"+b);
        }*/

        //输出 1个62长度
        byte[] outacount1 = new byte[1];
        outacount1[0] = 1;
        //转出金额 8长度
        byte[] Amount1 = ByteUtil.longToBytes(100000000);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength1 = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf1 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf1[0] = (byte) 0x76;
        outf1[1] = (byte) 0xa9;

        byte[] pubkeysha1 = SHA3Utility.sha3256(pubkey);
        byte[] pubkey1601 = RipemdUtility.ripemd160(pubkeysha1);

//        System.out.println(pubkeysha.length);
        byte[] outl1 = new byte[2];
        outl1[0] = (byte) 0x88;
        outl1[1] = (byte) 0xac;
        byte[] outscrips = ByteUtil.merge(outf1, pubkey1601, outl1);

        //data
        byte[] datalength = ByteUtil.intToBytes(20);
        byte[] data = new byte[20];

        byte[] outfull = ByteUtil.merge(outacount1, Amount1, outlength1, outscrips, datalength, data);

        //输入
        byte[] incount = new byte[1];
        incount[0] = 1;
        //前置交易哈希值 32长度
        byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
        //索引 4长度
        byte[] indexex = ByteUtil.intToBytes(0);
        //输入脚本长度 4长度
        byte[] inlength = ByteUtil.intToBytes(96);
        //输入脚本
        //本次交易事务数据
        byte[] signull = new byte[64];
        byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
        byte[] localdate = ByteUtil.merge(tran1, nosigin, outfull, date);

        //签名原文数据
        byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrips);

        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

        byte[] sigfull = ByteUtil.merge(sig, pubkey);

        byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

        byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, outfull, date));

        byte[] tranfull = ByteUtil.merge(version, transha, type, infull, outfull, date);

        return tranfull;
    }

    public static byte[] analogdate2() {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();
//        String address=new Address().pubkeyToAddress(publickey);
//        System.out.println(address);
        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = 0x03;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4
//        System.out.println("date:"+ByteUtil.byteArrayToLong(date));
        //输出 转账1个62长度，存证1个不定
        byte[] outacount = new byte[1];
        outacount[0] = 2;
        //转出金额 8长度
        byte[] Amount = ByteUtil.longToBytes(100000000);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf[0] = (byte) 0x76;
        outf[1] = (byte) 0xa9;

        byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
        byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);
//        System.out.println(pubkeysha.length);
        byte[] outl = new byte[2];
        outl[0] = (byte) 0x88;
        outl[1] = (byte) 0xac;
        outscrip = ByteUtil.merge(outf, pubkey160, outl);
/*        for(byte b:outscrip){
            System.out.println("~~"+b);
        }*/
        //data1
        byte[] datalength1 = ByteUtil.intToBytes(20);
        byte[] data1 = new byte[20];

        //存证输出
        byte[] czamount = new byte[8];
        byte[] czone = new byte[1];
        czone[0] = 0x6a;
        byte[] data = new byte[1000];
        int datalength = data.length;
        byte[] len = ByteUtil.intToBytes(datalength);
        byte[] czdata = ByteUtil.merge(czone, len, data);
        byte[] outczlength = ByteUtil.intToBytes(czdata.length);
        byte[] outczfull = ByteUtil.merge(czamount, outczlength, czdata, datalength1, data1);


        byte[] outfull = ByteUtil.merge(outacount, Amount, outlength, outscrip, datalength1, data1, outczfull);

        //输入
        byte[] incount = new byte[1];
        incount[0] = 1;
        //前置交易哈希值 32长度
        byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
        //索引 4长度
        byte[] indexex = ByteUtil.intToBytes(0);
        //输入脚本长度 4长度
        byte[] inlength = ByteUtil.intToBytes(96);
        //输入脚本
        //本次交易事务数据
        byte[] signull = new byte[64];
        byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
        byte[] localdate = ByteUtil.merge(tran1, nosigin, outfull, date);

        //签名原文数据
        byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrip);
/*        for(int x=0;x<indexdate.length;x++){
            System.out.println("indexdate:"+indexdate[x]);
        }*/
        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

        byte[] sigfull = ByteUtil.merge(sig, pubkey);

        byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

        byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, outfull, date));

        byte[] tranfull = ByteUtil.merge(version, transha, type, infull, outfull, date);

        return tranfull;
    }

    public static byte[] analogdate3() {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();
//        String address=new Address().pubkeyToAddress(publickey);
//        System.out.println(address);
        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = 0x02;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4
//        System.out.println("date:"+ByteUtil.byteArrayToLong(date));
        //输出 转账1个62长度，投票1个59长度
        byte[] outacount = new byte[1];
        outacount[0] = 2;
        //转出金额 8长度
        byte[] Amount = ByteUtil.longToBytes(1);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf[0] = (byte) 0x76;
        outf[1] = (byte) 0xa9;

        byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
        byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);
//        System.out.println(pubkeysha.length);
        byte[] outl = new byte[2];
        outl[0] = (byte) 0x88;
        outl[1] = (byte) 0xac;
        outscrip = ByteUtil.merge(outf, pubkey160, outl);
/*        for(byte b:outscrip){
            System.out.println("~~"+b);
        }*/
        //data1
        byte[] datalength1 = ByteUtil.intToBytes(20);
        byte[] data1 = new byte[20];

        //投票输出
        byte[] voteamount = new byte[8];
        byte[] votesccriptlength = ByteUtil.intToBytes(21);
        byte[] pubhash = SHA3Utility.sha3256(pubkey);
        byte[] pubhash160 = RipemdUtility.ripemd160(pubhash);
        byte votec = 0x6b;
        byte[] votescript = ByteUtil.prepend(pubhash160, votec);
        byte[] votefull = ByteUtil.merge(voteamount, votesccriptlength, votescript, datalength1, data1);

        byte[] outfull = ByteUtil.merge(outacount, Amount, outlength, outscrip, datalength1, data1, votefull);

        //输入
        byte[] incount = new byte[1];
        incount[0] = 1;
        //前置交易哈希值 32长度
        byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
        //索引 4长度
        byte[] indexex = ByteUtil.intToBytes(0);
        //输入脚本长度 4长度
        byte[] inlength = ByteUtil.intToBytes(96);
        //输入脚本
        //本次交易事务数据
        byte[] signull = new byte[64];
        byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
        byte[] localdate = ByteUtil.merge(tran1, nosigin, outfull, date);

        //签名原文数据
        byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrip);
/*        for(int x=0;x<indexdate.length;x++){
            System.out.println("indexdate:"+indexdate[x]);
        }*/
        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

        byte[] sigfull = ByteUtil.merge(sig, pubkey);

        byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

        byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, outfull, date));

        byte[] tranfull = ByteUtil.merge(version, transha, type, infull, outfull, date);

        return tranfull;
    }

    public static byte[] analogdate4(byte types) {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();

        Ed25519KeyPair pripubkey2 = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey2 = pripubkey2.getPrivateKey();
        Ed25519PublicKey publickey2 = pripubkey2.getPublicKey();
        byte[] privkey2 = privatekey2.getEncoded();
        byte[] pubkey2 = publickey2.getEncoded();

        Ed25519KeyPair pripubkey3 = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey3 = pripubkey3.getPrivateKey();
        Ed25519PublicKey publickey3 = pripubkey3.getPublicKey();
        byte[] privkey3 = privatekey3.getEncoded();
        byte[] pubkey3 = publickey3.getEncoded();

        //赎回脚本
        byte[] push = new byte[2];
        push[0] = 0x4c;
        push[1] = 32;
        byte[] m = new byte[1];
        m[0] = 0x52;
        byte[] n = new byte[1];
        n[0] = 0x53;
        byte[] comm = new byte[1];
        comm[0] = (byte) 0xaf;
        byte[] shscript = ByteUtil.merge(m, push, pubkey, push, pubkey2, push, pubkey3, n, comm);

//        String address=new Address().pubkeyToAddress(publickey);
//        System.out.println(address);
        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = types;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4
        if (types == 0x04) {//多-多
            //输出数量只能为1
            byte[] outacount = new byte[1];
            outacount[0] = 1;
            //转出金额 8长度
            byte[] Amount = ByteUtil.longToBytes(1);

            //输出多重签名脚本
            byte one = (byte) 0xa9;
            //160哈希，长度20
            byte[] pub160 = RipemdUtility.ripemd160(SHA3Utility.sha3256(shscript));
            byte three = (byte) 0x87;
            byte[] outscript = ByteUtil.prepend(pub160, one);
            outscrip = ByteUtil.appendByte(outscript, three);
            //锁定脚本长度 4长度
            byte[] outlength = ByteUtil.intToBytes(outscrip.length);

            byte[] oufull = ByteUtil.merge(outacount, Amount, outlength, outscrip);

            //输入数量只能为1
            byte[] incount = new byte[1];
            incount[0] = 1;
            //前置交易哈希值 32长度
            byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
            //索引 4长度
            byte[] indexex = ByteUtil.intToBytes(0);
            //输入脚本
            byte[] pushin = new byte[2];
            pushin[0] = 0x4c;
            pushin[1] = 64;
            byte[] nosig = new byte[64];
            byte[] oneonin = ByteUtil.merge(pushin, nosig, pushin, nosig, pushin, nosig, shscript);
            byte[] inlength = ByteUtil.intToBytes(oneonin.length);

            //第一个签名
            byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, oneonin);
            byte[] oneontext = ByteUtil.merge(tran1, infull, oufull, date);
            oneontext = ByteUtil.merge(oneontext, sha32, indexex, outscrip);
            byte[] onesig = new Ed25519PrivateKey(privkey).sign(oneontext);

            //第二个签名
            oneonin = ByteUtil.merge(pushin, onesig, pushin, nosig, pushin, nosig, shscript);
            infull = ByteUtil.merge(incount, sha32, indexex, inlength, oneonin);
            oneontext = ByteUtil.merge(tran1, infull, oufull, date);
            oneontext = ByteUtil.merge(oneontext, sha32, indexex, outscrip);
            byte[] twosig = new Ed25519PrivateKey(privkey2).sign(oneontext);

            //第三个签名
            oneonin = ByteUtil.merge(pushin, onesig, pushin, twosig, pushin, nosig, shscript);
            infull = ByteUtil.merge(incount, sha32, indexex, inlength, oneonin);
            oneontext = ByteUtil.merge(tran1, infull, oufull, date);
            oneontext = ByteUtil.merge(oneontext, sha32, indexex, outscrip);
            byte[] threesig = new Ed25519PrivateKey(privkey3).sign(oneontext);

            byte[] inscript = ByteUtil.merge(pushin, onesig, pushin, twosig, pushin, threesig, shscript);

            //data1
            byte[] datalength1 = ByteUtil.intToBytes(20);
            byte[] data1 = new byte[20];
            byte[] inscriptfull = ByteUtil.merge(incount, sha32, indexex, inlength, inscript, datalength1, data1);
//            System.out.println("inscriptfull:"+inscriptfull.length);

            byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, inscriptfull, oufull, date));

            byte[] tranfull = ByteUtil.merge(version, transha, type, inscriptfull, oufull, date);

            return tranfull;
        } else if (types == 0x05) {//多签-普通
            //上一个多签输出数量只能为1
            byte[] outacount = new byte[1];
            outacount[0] = 1;
            //转出金额 8长度
            byte[] Amount = ByteUtil.longToBytes(1);

            //输出多重签名脚本
            byte one = (byte) 0xa9;
            //160哈希，长度20
            byte[] pub160 = RipemdUtility.ripemd160(SHA3Utility.sha3256(shscript));
            byte three = (byte) 0x87;
            byte[] outscript = ByteUtil.prepend(pub160, one);
            outscrip = ByteUtil.appendByte(outscript, three);
            //锁定脚本长度 4长度
            byte[] outlength = ByteUtil.intToBytes(outscrip.length);
            //data1
            byte[] datalength1 = ByteUtil.intToBytes(20);
            byte[] data1 = new byte[20];
            byte[] oufull = ByteUtil.merge(outacount, Amount, outlength, outscrip, datalength1, data1);


            //当前输出
            //输出 1个62长度
            byte[] outacount1 = new byte[1];
            outacount1[0] = 1;
            //转出金额 8长度
            byte[] Amount1 = ByteUtil.longToBytes(1);
//        System.out.println(Amount.length);
            //锁定脚本长度 4长度
            byte[] outlength1 = ByteUtil.intToBytes(24);
            //输出脚本 50长度
            byte[] outf = new byte[2];

            outf[0] = (byte) 0x76;
            outf[1] = (byte) 0xa9;

            byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
            byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);

//        System.out.println(pubkeysha.length);
            byte[] outl = new byte[2];
            outl[0] = (byte) 0x88;
            outl[1] = (byte) 0xac;
            byte[] outscripts = ByteUtil.merge(outf, pubkey160, outl);

            byte[] outfull = ByteUtil.merge(outacount1, Amount1, outlength1, outscripts, datalength1, data1);


            //输入数量只能为1
            byte[] incount = new byte[1];
            incount[0] = 1;
            //前置交易哈希值 32长度
            byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
            //索引 4长度
            byte[] indexex = ByteUtil.intToBytes(0);
            //输入脚本
            byte[] pushin = new byte[2];
            pushin[0] = 0x4c;
            pushin[1] = 64;
            byte[] nosig = new byte[64];
            byte[] oneonin = ByteUtil.merge(pushin, nosig, pushin, nosig, pushin, nosig, shscript);
            byte[] inlength = ByteUtil.intToBytes(oneonin.length);

            //第一个签名
            byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, oneonin);
            byte[] oneontext = ByteUtil.merge(tran1, infull, outfull, date);
            oneontext = ByteUtil.merge(oneontext, sha32, indexex, outscrip);
            byte[] onesig = new Ed25519PrivateKey(privkey).sign(oneontext);

            //第二个签名
            oneonin = ByteUtil.merge(pushin, onesig, pushin, nosig, pushin, nosig, shscript);
            infull = ByteUtil.merge(incount, sha32, indexex, inlength, oneonin);
            oneontext = ByteUtil.merge(tran1, infull, outfull, date);
            oneontext = ByteUtil.merge(oneontext, sha32, indexex, outscrip);
            byte[] twosig = new Ed25519PrivateKey(privkey2).sign(oneontext);

            //第三个签名
            oneonin = ByteUtil.merge(pushin, onesig, pushin, twosig, pushin, nosig, shscript);
            infull = ByteUtil.merge(incount, sha32, indexex, inlength, oneonin);
            oneontext = ByteUtil.merge(tran1, infull, outfull, date);
            oneontext = ByteUtil.merge(oneontext, sha32, indexex, outscrip);
            byte[] threesig = new Ed25519PrivateKey(privkey3).sign(oneontext);

            byte[] inscript = ByteUtil.merge(pushin, onesig, pushin, twosig, pushin, threesig, shscript);

            byte[] inscriptfull = ByteUtil.merge(incount, sha32, indexex, inlength, inscript);

            byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, inscriptfull, outfull, date));

            byte[] tranfull = ByteUtil.merge(version, transha, type, inscriptfull, outfull, date);

            return tranfull;
        } else if (types == 0x06) {//普通-多签
            //上一个输出
            //输出 1个62长度
            byte[] outacount = new byte[1];
            outacount[0] = 1;
            //转出金额 8长度
            byte[] Amount = ByteUtil.longToBytes(1);
//        System.out.println(Amount.length);
            //锁定脚本长度 4长度
            byte[] outlength = ByteUtil.intToBytes(24);
            //输出脚本 50长度
            byte[] outf = new byte[2];

            outf[0] = (byte) 0x76;
            outf[1] = (byte) 0xa9;

            byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
            byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);

//        System.out.println(pubkeysha.length);
            byte[] outl = new byte[2];
            outl[0] = (byte) 0x88;
            outl[1] = (byte) 0xac;
            outscrip = ByteUtil.merge(outf, pubkey160, outl);

            //本次多签输出
            //输出数量只能为1
            byte[] outacount1 = new byte[1];
            outacount1[0] = 1;
            //转出金额 8长度
            byte[] Amount1 = ByteUtil.longToBytes(1);

            //输出多重签名脚本
            byte one = (byte) 0xa9;
            //160哈希，长度20
            byte[] pub160 = RipemdUtility.ripemd160(SHA3Utility.sha3256(shscript));
            byte three = (byte) 0x87;
            byte[] outscript = ByteUtil.prepend(pub160, one);
            byte[] outscrips = ByteUtil.appendByte(outscript, three);
            //锁定脚本长度 4长度
            byte[] outlengths = ByteUtil.intToBytes(outscrips.length);
            //data1
            byte[] datalength1 = ByteUtil.intToBytes(20);
            byte[] data1 = new byte[20];
            byte[] oufull = ByteUtil.merge(outacount1, Amount1, outlengths, outscrips, datalength1, data1);

            //普通输入
            byte[] incount = new byte[1];
            incount[0] = 1;
            //前置交易哈希值 32长度
            byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
            //索引 4长度
            byte[] indexex = ByteUtil.intToBytes(0);
            //输入脚本长度 4长度
            byte[] inlength = ByteUtil.intToBytes(96);
            //输入脚本
            //本次交易事务数据
            byte[] signull = new byte[64];
            byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
            byte[] localdate = ByteUtil.merge(tran1, nosigin, oufull, date);

            //签名原文数据
            byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrip);
/*        for(int x=0;x<indexdate.length;x++){
            System.out.println("indexdate:"+indexdate[x]);
        }*/
            //签名数据
            byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

            byte[] sigfull = ByteUtil.merge(sig, pubkey);

            byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

            byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, oufull, date));

            byte[] tranfull = ByteUtil.merge(version, transha, type, infull, oufull, date);

            return tranfull;

        }


        return null;
    }

    public static byte[] analogdate5() {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();
//        String address=new Address().pubkeyToAddress(publickey);
//        System.out.println(address);
        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = 0x09;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4
//        System.out.println("date:"+ByteUtil.byteArrayToLong(date));


        //输出 0普通转账
        byte[] outacount = new byte[1];
        outacount[0] = 4;
        //转出金额 8长度
        byte[] Amount = ByteUtil.longToBytes(100000000);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf[0] = (byte) 0x76;
        outf[1] = (byte) 0xa9;

        byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
        byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);
//        System.out.println(pubkeysha.length);
        byte[] outl = new byte[2];
        outl[0] = (byte) 0x88;
        outl[1] = (byte) 0xac;
        outscrip = ByteUtil.merge(outf, pubkey160, outl);

        //data1
        byte[] datalength1 = ByteUtil.intToBytes(20);
        byte[] data1 = new byte[20];
        byte[] outindex0 = ByteUtil.merge(Amount, outlength, outscrip, datalength1, data1);

        //输出 1 本金
        byte[] Amount1 = ByteUtil.longToBytes(30000000000L);
        //锁定脚本长度 4长度
        byte[] outlength1 = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf1 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf1[0] = (byte) 0x76;
        outf1[1] = (byte) 0xa9;

        byte[] pubkeysha1 = SHA3Utility.sha3256(pubkey);
        byte[] pubkey1601 = RipemdUtility.ripemd160(pubkeysha1);
        byte[] outl1 = new byte[2];
        outl1[0] = (byte) 0x88;
        outl1[1] = (byte) 0xac;
        byte[] outscript1 = ByteUtil.merge(outf1, pubkey1601, outl1);

        byte[] comm = new byte[3];
        comm[0] = (byte) 0xc8;
        comm[1] = (byte) 0xc9;
        comm[2] = (byte) 0x87;
        byte[] commlength = ByteUtil.intToBytes(3);
        byte[] outindex1 = ByteUtil.merge(Amount1, outlength1, outscript1, commlength, comm);

        //输出 2 利息
        long s = (long) (30000000000L * 0.01 * 120);
        byte[] Amount2 = ByteUtil.longToBytes(s);
        //锁定脚本长度 4长度
        byte[] outlength2 = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf2 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf2[0] = (byte) 0x76;
        outf2[1] = (byte) 0xa9;

        byte[] pubkeysha2 = SHA3Utility.sha3256(pubkey);
        byte[] pubkey1602 = RipemdUtility.ripemd160(pubkeysha2);
        byte[] outl2 = new byte[2];
        outl2[0] = (byte) 0x88;
        outl2[1] = (byte) 0xac;
        byte[] outscript2 = ByteUtil.merge(outf2, pubkey1602, outl2);
        byte[] outindex2 = ByteUtil.merge(Amount2, outlength2, outscript2, datalength1, data1);

        //输出3 分享收益
        byte[] Amount3 = ByteUtil.longToBytes((long) (s * 0.1));
        //锁定脚本长度 4长度
        byte[] outlength3 = ByteUtil.intToBytes(24);
        //输出脚本 50长度3
        byte[] outf3 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf3[0] = (byte) 0x76;
        outf3[1] = (byte) 0xa9;

        String supaddress = "1FTkhtGCkRSW56cuYrw1Jf8G7cNNifkKka";
        byte[] pubkey1603 = KeystoreAction.addressToPubkeyHash(supaddress);
        byte[] outl3 = new byte[2];
        outl3[0] = (byte) 0x88;
        outl3[1] = (byte) 0xac;
        byte[] outscript3 = ByteUtil.merge(outf3, pubkey1603, outl3);
        byte[] outindex3 = ByteUtil.merge(Amount3, outlength3, outscript3, datalength1, data1);

        byte[] outfull = ByteUtil.merge(outacount, outindex0, outindex1, outindex2, outindex3);

        //输入
        byte[] incount = new byte[1];
        incount[0] = 1;
        //前置交易哈希值 32长度
        byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
        //索引 4长度
        byte[] indexex = ByteUtil.intToBytes(0);
        //输入脚本长度 4长度
        byte[] inlength = ByteUtil.intToBytes(96);
        //输入脚本
        //本次交易事务数据
        byte[] signull = new byte[64];
        byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
        byte[] localdate = ByteUtil.merge(tran1, nosigin, outfull, date);

        //签名原文数据
        byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrip);
/*        for(int x=0;x<indexdate.length;x++){
            System.out.println("indexdate:"+indexdate[x]);
        }*/
        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

        byte[] sigfull = ByteUtil.merge(sig, pubkey);

        byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

        byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, outfull, date));

        byte[] tranfull = ByteUtil.merge(version, transha, type, infull, outfull, date);

        return tranfull;
    }

    public static byte[] analogdate6() {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();
//        String address=new Address().pubkeyToAddress(publickey);
//        System.out.println(address);
        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = 0x0a;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4
//        System.out.println("date:"+ByteUtil.byteArrayToLong(date));


        //输出 0普通转账
        byte[] outacount = new byte[1];
        outacount[0] = 4;
        //转出金额 8长度
        byte[] Amount = ByteUtil.longToBytes(100000000);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf[0] = (byte) 0x76;
        outf[1] = (byte) 0xa9;

        byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
        byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);
//        System.out.println(pubkeysha.length);
        byte[] outl = new byte[2];
        outl[0] = (byte) 0x88;
        outl[1] = (byte) 0xac;
        outscrip = ByteUtil.merge(outf, pubkey160, outl);

        //data1
        byte[] datalength1 = ByteUtil.intToBytes(20);
        byte[] data1 = new byte[20];
        byte[] outindex0 = ByteUtil.merge(Amount, outlength, outscrip, datalength1, data1);

        //输出 1 本金
        byte[] Amount1 = ByteUtil.longToBytes(30000000000L);
        //锁定脚本长度 4长度
        byte[] outlength1 = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf1 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf1[0] = (byte) 0x76;
        outf1[1] = (byte) 0xa9;

        byte[] pubkeysha1 = SHA3Utility.sha3256(pubkey);
        byte[] pubkey1601 = RipemdUtility.ripemd160(pubkeysha1);
        byte[] outl1 = new byte[2];
        outl1[0] = (byte) 0x88;
        outl1[1] = (byte) 0xac;
        byte[] outscript1 = ByteUtil.merge(outf1, pubkey1601, outl1);

        byte[] comm = new byte[3];
        comm[0] = (byte) 0xc8;
        comm[1] = (byte) 0xc9;
        comm[2] = (byte) 0x87;
        byte[] commlength = ByteUtil.intToBytes(3);

        byte[] outindex1 = ByteUtil.merge(Amount1, outlength1, outscript1, commlength, comm);

        //输出 2 利息
        long s = (long) (30000000000L * 0.01 * 365);
        byte[] Amount2 = ByteUtil.longToBytes(s);
        //锁定脚本长度 4长度
        byte[] outlength2 = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf2 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf2[0] = (byte) 0x76;
        outf2[1] = (byte) 0xa9;

        byte[] pubkeysha2 = SHA3Utility.sha3256(pubkey);
        byte[] pubkey1602 = RipemdUtility.ripemd160(pubkeysha2);
        byte[] outl2 = new byte[2];
        outl2[0] = (byte) 0x88;
        outl2[1] = (byte) 0xac;
        byte[] outscript2 = ByteUtil.merge(outf2, pubkey1602, outl2);
        byte[] outindex2 = ByteUtil.merge(Amount2, outlength2, outscript2, datalength1, data1);

        //输出3 分享收益
        byte[] Amount3 = ByteUtil.longToBytes((long) (s * 0.1));
        //锁定脚本长度 4长度
        byte[] outlength3 = ByteUtil.intToBytes(24);
        //输出脚本 50长度3
        byte[] outf3 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf3[0] = (byte) 0x76;
        outf3[1] = (byte) 0xa9;

        String supaddress = "1FTkhtGCkRSW56cuYrw1Jf8G7cNNifkKka";
        byte[] pubkey1603 = KeystoreAction.addressToPubkeyHash(supaddress);
        byte[] outl3 = new byte[2];
        outl3[0] = (byte) 0x88;
        outl3[1] = (byte) 0xac;
        byte[] outscript3 = ByteUtil.merge(outf3, pubkey1603, outl3);
        byte[] outindex3 = ByteUtil.merge(Amount3, outlength3, outscript3, datalength1, data1);

        byte[] outfull = ByteUtil.merge(outacount, outindex0, outindex1, outindex2, outindex3);

        //输入
        byte[] incount = new byte[1];
        incount[0] = 1;
        //前置交易哈希值 32长度
        byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
        //索引 4长度
        byte[] indexex = ByteUtil.intToBytes(0);
        //输入脚本长度 4长度
        byte[] inlength = ByteUtil.intToBytes(96);
        //输入脚本
        //本次交易事务数据
        byte[] signull = new byte[64];
        byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
        byte[] localdate = ByteUtil.merge(tran1, nosigin, outfull, date);

        //签名原文数据
        byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrip);
/*        for(int x=0;x<indexdate.length;x++){
            System.out.println("indexdate:"+indexdate[x]);
        }*/
        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

        byte[] sigfull = ByteUtil.merge(sig, pubkey);

        byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

        byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, outfull, date));

        byte[] tranfull = ByteUtil.merge(version, transha, type, infull, outfull, date);

        return tranfull;
    }

    public static byte[] analogdate7() {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();
//        String address=new Address().pubkeyToAddress(publickey);
//        System.out.println(address);
        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = 0x0b;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4
//        System.out.println("date:"+ByteUtil.byteArrayToLong(date));


        //输出
        byte[] outacount = new byte[1];
        outacount[0] = 2;

        //0 利息余额
        byte[] Amount = ByteUtil.longToBytes(29900000000L);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf[0] = (byte) 0x76;
        outf[1] = (byte) 0xa9;

        byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
        byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);
//        System.out.println(pubkeysha.length);
        byte[] outl = new byte[2];
        outl[0] = (byte) 0x88;
        outl[1] = (byte) 0xac;
        outscrip = ByteUtil.merge(outf, pubkey160, outl);

        byte[] initheight = ByteUtil.intToBytes(0);
        byte[] daylx = ByteUtil.longToBytes((long) (30000000000L * 0.01));
        byte[] days = new byte[1];
        days[0] = 3;

        byte[] datascript = ByteUtil.merge(initheight, daylx, days);
        byte[] datalength = ByteUtil.intToBytes(datascript.length);

        byte[] outindex0 = ByteUtil.merge(Amount, outlength, outscrip, datalength, datascript);

        //data1
        byte[] datalength1 = ByteUtil.intToBytes(20);
        byte[] data1 = new byte[20];

        //输出 1 提取利息
        byte[] Amount1 = ByteUtil.longToBytes(100000000);
        //锁定脚本长度 4长度
        byte[] outlength1 = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf1 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf1[0] = (byte) 0x76;
        outf1[1] = (byte) 0xa9;

        byte[] pubkeysha1 = SHA3Utility.sha3256(pubkey);
        byte[] pubkey1601 = RipemdUtility.ripemd160(pubkeysha1);
        byte[] outl1 = new byte[2];
        outl1[0] = (byte) 0x88;
        outl1[1] = (byte) 0xac;
        byte[] outscript1 = ByteUtil.merge(outf1, pubkey1601, outl1);
        byte[] outindex1 = ByteUtil.merge(Amount1, outlength1, outscript1, datalength1, data1);

        byte[] outfull = ByteUtil.merge(outacount, outindex0, outindex1);

        //输入
        byte[] incount = new byte[1];
        incount[0] = 1;
        //前置交易哈希值 32长度
        byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
        //索引 4长度
        byte[] indexex = ByteUtil.intToBytes(0);
        //输入脚本长度 4长度
        byte[] inlength = ByteUtil.intToBytes(96);
        //输入脚本
        //本次交易事务数据
        byte[] signull = new byte[64];
        byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
        byte[] localdate = ByteUtil.merge(tran1, nosigin, outfull, date);

        //签名原文数据
        byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrip);
/*        for(int x=0;x<indexdate.length;x++){
            System.out.println("indexdate:"+indexdate[x]);
        }*/
        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

        byte[] sigfull = ByteUtil.merge(sig, pubkey);

        byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

        byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, outfull, date));

        byte[] tranfull = ByteUtil.merge(version, transha, type, infull, outfull, date);

        return tranfull;
    }

    public static byte[] analogdate8() {
        //模拟数据
        Ed25519KeyPair pripubkey = Ed25519.GenerateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();
        byte[] privkey = privatekey.getEncoded();
        byte[] pubkey = publickey.getEncoded();

        //事务
        byte[] version = new byte[1];
        version[0] = 0x01;
        byte[] type = new byte[1];
        type[0] = 0x0c;

        byte[] tran1 = ByteUtil.merge(version, type);

        //锁定时间戳
        byte[] date = BigEndian.encodeUint32(new Date().getTime());//长度为4
//        System.out.println("date:"+ByteUtil.byteArrayToLong(date));


        //输出
        byte[] outacount = new byte[1];
        outacount[0] = 2;

        //0 分享余额
        byte[] Amount = ByteUtil.longToBytes(29900000000L);
//        System.out.println(Amount.length);
        //锁定脚本长度 4长度
        byte[] outlength = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf[0] = (byte) 0x76;
        outf[1] = (byte) 0xa9;

        byte[] pubkeysha = SHA3Utility.sha3256(pubkey);
        byte[] pubkey160 = RipemdUtility.ripemd160(pubkeysha);
//        System.out.println(pubkeysha.length);
        byte[] outl = new byte[2];
        outl[0] = (byte) 0x88;
        outl[1] = (byte) 0xac;
        outscrip = ByteUtil.merge(outf, pubkey160, outl);

        byte[] initheight = ByteUtil.intToBytes(0);
        byte[] daylx = ByteUtil.longToBytes((long) (30000000000L * 0.01));
        byte[] days = new byte[1];
        days[0] = 3;

        byte[] datascript = ByteUtil.merge(initheight, daylx, days);
        byte[] datalength = ByteUtil.intToBytes(datascript.length);

        byte[] outindex0 = ByteUtil.merge(Amount, outlength, outscrip, datalength, datascript);

        //data1
        byte[] datalength1 = ByteUtil.intToBytes(20);
        byte[] data1 = new byte[20];

        //输出 1 提取分享收益
        byte[] Amount1 = ByteUtil.longToBytes(100000000);
        //锁定脚本长度 4长度
        byte[] outlength1 = ByteUtil.intToBytes(24);
        //输出脚本 50长度
        byte[] outf1 = new byte[2];
/*        int s=0xb9;
        System.out.println("int:"+s);*/
        outf1[0] = (byte) 0x76;
        outf1[1] = (byte) 0xa9;

        byte[] pubkeysha1 = SHA3Utility.sha3256(pubkey);
        byte[] pubkey1601 = RipemdUtility.ripemd160(pubkeysha1);
        byte[] outl1 = new byte[2];
        outl1[0] = (byte) 0x88;
        outl1[1] = (byte) 0xac;
        byte[] outscript1 = ByteUtil.merge(outf1, pubkey1601, outl1);
        byte[] outindex1 = ByteUtil.merge(Amount1, outlength1, outscript1, datalength1, data1);

        byte[] outfull = ByteUtil.merge(outacount, outindex0, outindex1);

        //输入
        byte[] incount = new byte[1];
        incount[0] = 1;
        //前置交易哈希值 32长度
        byte[] sha32 = SHA3Utility.sha3256(new byte[32]);
        //索引 4长度
        byte[] indexex = ByteUtil.intToBytes(0);
        //输入脚本长度 4长度
        byte[] inlength = ByteUtil.intToBytes(96);
        //输入脚本
        //本次交易事务数据
        byte[] signull = new byte[64];
        byte[] nosigin = ByteUtil.merge(incount, sha32, indexex, inlength, signull, pubkey);
        byte[] localdate = ByteUtil.merge(tran1, nosigin, outfull, date);

        //签名原文数据
        byte[] indexdate = ByteUtil.merge(localdate, sha32, indexex, outscrip);
/*        for(int x=0;x<indexdate.length;x++){
            System.out.println("indexdate:"+indexdate[x]);
        }*/
        //签名数据
        byte[] sig = new Ed25519PrivateKey(privkey).sign(indexdate);

        byte[] sigfull = ByteUtil.merge(sig, pubkey);

        byte[] infull = ByteUtil.merge(incount, sha32, indexex, inlength, sigfull);

        byte[] transha = SHA3Utility.sha3256(ByteUtil.merge(version, type, infull, outfull, date));

        byte[] tranfull = ByteUtil.merge(version, transha, type, infull, outfull, date);

        return tranfull;
    }


    public static void main(String args[]) {
        int a = 23041;
        int b = 5760;
        int d = a / b;
        System.out.println(d);
        long s = 0xffffffffL;
        System.out.println(s);


        Map<String, byte[]> m = new HashMap<>();
        byte[] ss = {1, 2, 2, 3};
        m.put("111", ss);
        byte[] bb = m.get("111");
        bb = new byte[0];
//        m.put("111",bb);
        for (Map.Entry<String, byte[]> entry : m.entrySet()) {
            byte[] bytes = entry.getValue();
            for (int x = 0; x < bytes.length; x++) {
                System.out.println("x" + bytes[x]);
            }
        }
    }
}
