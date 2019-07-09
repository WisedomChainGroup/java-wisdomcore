package org.wisdom.keystore.account;


import org.wisdom.keystore.wallet.KeystoreAction;

import java.security.PublicKey;


public class Address {

    //hex string,not include 0x prefix
    private  String address;
    public static final byte numb = 0x00;


    /*
    1. 地址生成逻辑

    2. 对公钥进行SHA3-256计算，获得结果为s1

    3. 取得s1的后面22字节，并且在前面附加3个字符（WXC，大写字符），共25字节，结果为s2
    */
    public  String pubkeyToAddress(PublicKey publicKey){
        return KeystoreAction.pubkeyHashToAddress(publicKey.getEncoded(),numb);
    }

    public Address(){

    }

    public Address(PublicKey publicKey){
        this.address = pubkeyToAddress(publicKey);
    }

    public String getAddress() {
        return address;
    }


}
