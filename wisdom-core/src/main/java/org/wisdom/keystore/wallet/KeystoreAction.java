/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.keystore.wallet;

import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import org.apache.commons.codec.DecoderException;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.keystore.crypto.AESManage;
import org.wisdom.keystore.crypto.ArgonManage;
import org.wisdom.keystore.crypto.KeyPair;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.util.Base58Utility;
import org.wisdom.keystore.util.Utils;
import org.wisdom.util.ByteUtil;
import org.json.simple.JSONArray;
import net.sf.json.JSONObject;
import org.wisdom.keystore.account.Address;
import org.wisdom.keystore.crypto.*;
import org.wisdom.keystore.util.Base58Utility;
import org.wisdom.keystore.util.Utils;
import org.wisdom.util.ByteUtil;


import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;


public class KeystoreAction {
    public String address;
    public Crypto crypto;
    private static final int saltLength = 32;
    private static final int ivLength = 16;
    private static final String defaultVersion = "1";
    private SecureRandom random;


    public static Keystore unmarshal(String in) throws com.google.gson.JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(in, Keystore.class);
    }
    public static String marshal(Keystore keystore){
        Gson gson = new Gson();
        return gson.toJson(keystore);
    }
    public static Keystore fromPassword(String password) throws Exception{
        if (password.length()>20 || password.length()<8){
            throw new Exception("请输入8-20位密码");
        }else {
            KeyPair keyPair = KeyPair.generateEd25519KeyPair();
            PublicKey publicKey = keyPair.getPublicKey();
            String s=new String(keyPair.getPrivateKey().getEncoded());
            byte[] salt = new byte[saltLength];
            byte[] iv = new byte[ivLength];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(salt);
            ArgonManage argon2id = new ArgonManage(ArgonManage.Type.ARGON2id, salt);
            AESManage aes = new AESManage(iv);

            byte[] derivedKey = argon2id.hash(password.getBytes());
            byte[] cipherPrivKey = aes.encrypt(derivedKey, keyPair.getPrivateKey().getBytes());
            byte[] mac = SHA3Utility.keccak256(Bytes.concat(
                    derivedKey, cipherPrivKey
                    )
            );

            Crypto crypto = new Crypto(
                    AESManage.cipher, Hex.encodeHexString(cipherPrivKey),
                    new Cipherparams(
                            Hex.encodeHexString(iv)
                    )
            );
            Kdfparams kdfparams = new Kdfparams(ArgonManage.memoryCost, ArgonManage.timeCost, ArgonManage.parallelism, Hex.encodeHexString(salt));

            Address ads = new Address(publicKey);
            ArgonManage params = new ArgonManage(salt);
            Keystore ks = new Keystore(ads.getAddress(), crypto, Utils.generateUUID(),
                    defaultVersion, Hex.encodeHexString(mac), argon2id.kdf(), kdfparams
            );
        return ks;
        }
    }
    public static boolean verifyPassword(Keystore keystore,String password) throws Exception{
        // 验证密码是否正确 计算 mac
        ArgonManage argon2id = new ArgonManage(ArgonManage.Type.ARGON2id, Hex.decodeHex(keystore.kdfparams.salt.toCharArray()));
        byte[] derivedKey = argon2id.hash(password.getBytes());
        byte[] cipherPrivKey = Hex.decodeHex(keystore.crypto.ciphertext.toCharArray());
        byte[] mac = SHA3Utility.keccak256(Bytes.concat(
                derivedKey,cipherPrivKey
                )
        );
        return Hex.encodeHexString(mac).equals(keystore.mac);
    }
    public static byte[] decrypt(Keystore keystore,String password) throws Exception{
        if (!KeystoreAction.verifyPassword(keystore,password)){
            throw new Exception("invalid password");
        }
        ArgonManage argon2id = new ArgonManage(ArgonManage.Type.ARGON2id, Hex.decodeHex(keystore.kdfparams.salt.toCharArray()));
        byte[] derivedKey = argon2id.hash(password.getBytes());
        byte[] iv = Hex.decodeHex(keystore.crypto.cipherparams.iv.toCharArray());
        AESManage aes = new AESManage(iv);
        return aes.decrypt(derivedKey, Hex.decodeHex(keystore.crypto.ciphertext.toCharArray()));
    }
    /**
     * Generate keystore file
     */
    public static void generateKeystore(String password) throws Exception{
        String folderPath = System.getProperty("user.dir")+File.separator+"Keystore";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Keystore ks = fromPassword(password);
        Crypto crypto = ks.crypto;
        Cipherparams cipherparams = crypto.cipherparams;
        String filePath=folderPath+"\\"+ks.address+System.currentTimeMillis()/1000;
        File file = new File(filePath);
        file.createNewFile();
        JSONObject ksjson = JSONObject.fromObject(ks);
        JSONObject cryptojson = JSONObject.fromObject(crypto);
        JSONObject cipherparamsjson = JSONObject.fromObject(cipherparams);
        cryptojson.put("cipherparams",cipherparamsjson.toString());
        ksjson.put("crypto", cryptojson.toString());
        String str = ksjson.toString();
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(str);
        bw.close();
    }

    /**
     * read Keystore By FileName
     */
    public static String readKeystoreByFileName(String password,String FileName) throws Exception{
        Keystore ks = new Keystore();
        String privateKey;
        try{
            String folderPath = System.getProperty("user.dir")+File.separator+"Keystore"+File.separator+FileName;
            FileInputStream  file = null;
            file= new FileInputStream(folderPath);
            byte[] data = new byte[1024]; //数据存储的数组
            int i = file.read(data);//对比上面代码中的 int n = fis.read();读取第一个字节的数据返回到n中

            //解析数据
            String str = new String(data,0,i);

            JSONArray jsonarry = new JSONArray();
             ks = KeystoreAction.unmarshal(str);

            file.close();
            privateKey =  Hex.encodeHexString(KeystoreAction.decrypt(ks,password));
            }catch (FileNotFoundException e){
                e.printStackTrace();
                return "";
            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
            return privateKey;
    }

    /**
     * read Keystore By Path
     */
    public static String readKeystoreByPath(String password,String path){
        Keystore ks = new Keystore();
        String privateKey;
        try {
            String folderPath = path;
            FileInputStream  file = null;
            file= new FileInputStream(folderPath);
            byte[] data = new byte[1024]; //数据存储的数组
            int i = file.read(data);//对比上面代码中的 int n = fis.read();读取第一个字节的数据返回到n中

            //解析数据
            String str = new String(data,0,i);
            ks = KeystoreAction.unmarshal(str);

            file.close();
            privateKey =  Hex.encodeHexString(KeystoreAction.decrypt(ks,password));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return "";
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return privateKey;
    }

    public static boolean isAddress(String address){
        if (address.length() != 46 || !address.startsWith("WX")){
            return false;
        }
        String ad = new String(address);
        address = address.substring(2,address.length()).toLowerCase();
        String standardAddress = "";
        String[] resString = address.split("");
        byte[] check = new byte[address.length()];
        for (int j=0;j<address.length();j++){
            if(Utils.isInteger(resString[j])){
                check[j] = Byte.parseByte(resString[j]);
            }else{
                check[j] = 0;
            }
        }
        String bstr=Hex.encodeHexString(SHA3Utility.keccak256(check));
        char[] b = bstr.toCharArray();
        char[] a = address.toCharArray();
        for(int i=0;i<a.length;i++)
            if (Character.isDigit(a[i])) {
                standardAddress = standardAddress + a[i];
            } else {
                if (Integer.parseInt(String.valueOf(a[i]), 16) - Integer.parseInt(String.valueOf(b[i]), 16) > 8) {
                    standardAddress=standardAddress+String.valueOf(a[i]).toUpperCase();
                }else{
                    standardAddress = standardAddress + a[i];
                }
            }
        standardAddress = new String( "WX"+standardAddress);

        if(standardAddress.equals(ad)){
            return true;
        }else{
            return false;
        }
    }
    public static String prikeyToPubkey(String prikey) throws DecoderException {
        Ed25519PrivateKey eprik = new Ed25519PrivateKey(Hex.decodeHex(prikey.toCharArray()));
        Ed25519PublicKey epuk = eprik.generatePublicKey();
        String pubkey = Hex.encodeHexString(epuk.getEncoded());
        return pubkey;
    }

    public static String keystoreToPubkey(Keystore ks,String password) throws Exception {
        String privateKey =  obtainPrikey(ks,password);
        String pubkey = prikeyToPubkey(privateKey);
        return pubkey;
    }

    public static String obtainPrikey(Keystore ks,String password) throws Exception {
        String privateKey =  Hex.encodeHexString(KeystoreAction.decrypt(ks,password));
        return privateKey;
    }
    /*
        地址生成逻辑
       1.对公钥进行SHA3-256哈希，再进行RIPEMD-160哈希，
           得到哈希值r1
      2.在r1前面附加一个字节的版本号:0x01
           得到结果r2
      3.将r1进行两次SHA3-256计算，得到结果r3，
           获得r3的前面4个字节，称之为b4
      4.将b4附加在r2的后面，得到结果r5
      5.将r5进行base58编码，得到结果r6
      6.r6就是地址

   */
    public static String pubkeyToAddress(byte[] pubkey,byte numb){
        byte[] pub256 = SHA3Utility.keccak256(pubkey);
        byte[] r1 = RipemdUtility.ripemd160(pub256);
        byte[] r2 = ByteUtil.prepend(r1,numb);
        byte[] r3 = SHA3Utility.keccak256(SHA3Utility.keccak256(r1));
        byte[] b4 = ByteUtil.bytearraycopy(r3,0,4);
        byte[] b5 = ByteUtil.byteMerger(r2,b4);
        String s6 = Base58Utility.encode(b5);
        return s6 ;
    }

    /**
     * 公钥哈希转地址
     * @param pubkey
     * @param numb
     * @return
     */
    public static String pubkeyHashToAddress(byte[] pubkey,byte numb){
        byte[] r2 = ByteUtil.prepend(pubkey,numb);
        byte[] r3 = SHA3Utility.keccak256(SHA3Utility.keccak256(pubkey));
        byte[] b4 = ByteUtil.bytearraycopy(r3,0,4);
        byte[] b5 = ByteUtil.byteMerger(r2,b4);
        String s6 = Base58Utility.encode(b5);
        return s6 ;
    }


    /**
     *    1.将地址进行base58解码，得到结果r5
     *    2.将r5移除后后面4个字节得到r2
     *    3.将r2移除第1个字节:0x01得到r1(公钥哈希值)
     * @param address
     * @return
     */
    public static byte[] addressToPubkeyHash(String address){
        byte[] r5 = Base58Utility.decode(address);
        byte[] r2 = ByteUtil.bytearraycopy(r5,0,21);
        byte[] r1 = ByteUtil.bytearraycopy(r2,1,20);
        return r1;
    }
    /**
     * 地址有效性校验
     * @param address
     * @return
     */
    public static int verifyAddress(String address){
        byte[] r5 = Base58Utility.decode(address);
//        ResultSupport ar = new ResultSupport();
        if(!address.startsWith("1")){//地址不是以"1"开头
            return  1;
        }
        byte[] r3 = SHA3Utility.keccak256(SHA3Utility.keccak256(KeystoreAction.addressToPubkeyHash(address)));
        byte[] b4 = ByteUtil.bytearraycopy(r3,0,4);
        byte[] _b4 = ByteUtil.bytearraycopy(r5,r5.length-4,4);
        if(Arrays.equals(b4,_b4)){//正确
            return  0;
        }else {//地址格式错误
            return  -2;
        }
    }
}