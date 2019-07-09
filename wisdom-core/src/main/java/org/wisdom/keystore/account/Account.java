package org.wisdom.keystore.account;


import org.wisdom.keystore.crypto.KeyPair;
import org.wisdom.keystore.crypto.KeyPair;

public class Account {
    private  final KeyPair keyPair;
    private  final Address address;

    public Account(KeyPair keyPair,Address address){
        this.keyPair=keyPair;
        this.address=address;
    }


}
