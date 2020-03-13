package org.wisdom.db;

import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;

import java.util.*;

public class Utils {
    public static List<byte[]> getAllPublicKeyHashes(Block b){
        Set<byte[]> res = new HashSet<>();
        if(b.body == null){
            return new ArrayList<>();
        }
        for(Transaction tx: b.body){
            if(!Arrays.equals(tx.from, new byte[tx.from.length])){
                res.add(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)));
            }
            if(!Arrays.equals(tx.to, new byte[tx.to.length])){
                res.add(tx.to);
            }
        }
        // 添加孵化总地址
        return new ArrayList<>(res);
    }
}
