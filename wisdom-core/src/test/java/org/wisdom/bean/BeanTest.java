package org.wisdom.bean;

import org.junit.Test;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLPCodec;
import org.wisdom.core.account.Account;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519KeyPair;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.db.AccountState;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;

import java.util.HashMap;
import java.util.Map;

public class BeanTest {

    @Test
    public void Test(){
        Ed25519KeyPair pripubkey = Ed25519.generateKeyPair();
        Ed25519PrivateKey privatekey = pripubkey.getPrivateKey();
        Ed25519PublicKey publickey = pripubkey.getPublicKey();

        Account account=new Account();
        account.setPubkeyHash(RipemdUtility.ripemd160(SHA3Utility.keccak256(publickey.getEncoded())));
        account.setBalance(1000);
        account.setNonce(10);

        Incubator incubator=new Incubator(new byte[32],new byte[32],new byte[32],10L,20L,500L,0,0,0);
        incubator.setCost(50000000000L);
        incubator.setDays(120);
        incubator.setHeight(1400);
        byte[] s1={0x00,0x01};
        Map<byte[],Incubator> map=new HashMap<>();
        map.put(s1,incubator);

        int type=1;
        byte[] Contract={0x00,0x01};
        byte[] key={0x00,0x01,0x02};
        Map<byte[],Long> sss= new HashMap<>();
        sss.put(key,1L);
        ByteArrayMap<Long> maps=new ByteArrayMap<>(sss);
        AccountState accountState=new AccountState(account,map,map,type,Contract,maps);

        byte[] bytes= RLPCodec.encode(accountState);

        AccountState accountStates= RLPCodec.decode(bytes,AccountState.class);
        assert accountStates.equals(accountState);

    }
}
