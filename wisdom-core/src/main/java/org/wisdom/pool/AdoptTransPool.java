package org.wisdom.pool;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.map.LinkedMap;
import org.springframework.stereotype.Component;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AdoptTransPool {

    private ConcurrentHashMap<String, Map<String, TransPool>> atpool;

    public AdoptTransPool() {
        this.atpool = new ConcurrentHashMap<>();
    }

    public void add(List<Transaction> txs) {
        for (Transaction t : txs) {
            String from = Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(t.from)));
            if (hasExist(from)) {
                Map<String, TransPool> map = new HashMap<>();
                TransPool tp = new TransPool(t, 0, new Date().getTime());
                map.put(getKeyTrans(t), tp);
                atpool.put(from, map);
            } else {
                Map<String, TransPool> map = atpool.get(from);
                TransPool tp = new TransPool(t, 0, new Date().getTime());
                map.put(getKeyTrans(t), tp);
                atpool.put(from, map);
            }
        }
    }

    public String getKeyTrans(Transaction t) {
        if (t != null) {
            byte[] from = t.from;
            String fromhash = Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(from)));
            //todo:key=fromhash+nonce
            String key = fromhash + t.nonce;
            return key;
        } else {
            return null;
        }
    }

    public String getKey(TransPool transPool) {
        if (transPool != null) {
            return getKeyTrans(transPool.getTransaction());
        } else {
            return null;
        }
    }

    public boolean hasExistQueued(String key, String key1) {
        if (atpool.containsKey(key)) {
            Map<String, TransPool> map = atpool.get(key);
            if (map.containsKey(key1)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasExist(String key) {
        if (atpool.containsKey(key)) {
            return false;
        } else {
            return true;
        }
    }

    public void remove(Map<String, String> maps) {
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            if (!hasExist(entry.getKey())) {
                Map<String, TransPool> map = atpool.get(entry.getKey());
                if (map.containsKey(entry.getValue())) {
                    map.remove(entry.getValue());
                    if (map.size() == 0) {
                        atpool.remove(entry.getKey());
                    } else {
                        atpool.put(entry.getKey(), map);
                    }
                    break;
                }
            }
        }
    }

    public List<TransPool> getAll() {
        List<TransPool> list = new ArrayList<>();
        for (Map.Entry<String, Map<String, TransPool>> entry : atpool.entrySet()) {
            Map<String, TransPool> map = compare(entry.getValue());
            for (Map.Entry<String, TransPool> entry1 : map.entrySet()) {
                TransPool t = entry1.getValue();
                list.add(t);
                break;
            }
        }
        return list;
    }

    public List<TransPool> getAllFrom(String from) {
        List<TransPool> list = new ArrayList<>();
        if (!hasExist(from)) {
            Map<String, TransPool> map = atpool.get(from);
            for (Map.Entry<String, TransPool> entry : map.entrySet()) {
                TransPool t = entry.getValue();
                list.add(t);
            }
        }
        return list;
    }

    public List<TransPool> getAllFull() {
        List<TransPool> list = new ArrayList<>();
        for (Map.Entry<String, Map<String, TransPool>> entry : atpool.entrySet()) {
            for (Map.Entry<String, TransPool> entrys : entry.getValue().entrySet()) {
                list.add(entrys.getValue());
            }
        }
        return list;
    }

    public TransPool getPoolTranHash(byte[] txhash) {
        for (Map.Entry<String, Map<String, TransPool>> entry : atpool.entrySet()) {
            for (Map.Entry<String, TransPool> entrys : entry.getValue().entrySet()) {
                Transaction t = entrys.getValue().getTransaction();
                if (Arrays.equals(t.getHash(), txhash)) {
                    return entrys.getValue();
                }
            }
        }
        return null;
    }

    public Map<String, TransPool> getfromPool(String from) {
        if (!hasExist(from)) {
            return atpool.get(from);
        }
        return null;
    }

    public Map<String, TransPool> compare(Map<String, TransPool> maps) {
        List<Map.Entry<String, TransPool>> list = new ArrayList<>(maps.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, TransPool>>() {
            @Override
            public int compare(Map.Entry<String, TransPool> o1, Map.Entry<String, TransPool> o2) {
                Transaction t1 = o1.getValue().getTransaction();
                Transaction t2 = o2.getValue().getTransaction();
                return (int) (t1.nonce - t2.nonce);
            }
        });
        Map<String, TransPool> map = new LinkedMap();
        for (Map.Entry<String, TransPool> entry : list) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }


    /*public static void main(String args[]) {
        AdoptTransPool a = new AdoptTransPool();
        Map<String,Map<String, TransPool>> maps=new ConcurrentHashMap<>();
        List<Transaction> ts=new ArrayList<>();
        for(int x=0;x<10000;x++){
            Ed25519KeyPair pripubkey= Ed25519.GenerateKeyPair();
            Ed25519PublicKey publickey=pripubkey.getPublicKey();
            byte[] pubkey=publickey.getEncoded();
            Map<String, TransPool> map=new HashMap<>();
            for(int y=0;y<5;y++){
                Transaction t = new Transaction().createEmpty();
                t.from=pubkey;
                t.nonce=y;
                t.gasPrice = (y+30);
                ts.add(t);
                String fromhash=Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(t.from)));
                TransPool tp=new TransPool(t,0,new Date().getTime());
                map.put(fromhash,tp);
            }
        }
        a.add(ts);
        List<TransPool> lits=a.getAll();
        System.out.println("size:"+lits.size());
        Map<String, TransPool> maps=new HashMap<>();
        Transaction t5 = new Transaction().createEmpty();
        byte[] s5 = new byte[32];
        s5[15] = 10;
        t5.type = 1;
        t5.from = s5;
        t5.nonce = 5;
        t5.gasPrice = 4;
        TransPool tp5 = new TransPool(t5, 0, new Date().getTime());
        maps.put(a.getKey(tp5),tp5);
        JSONObject jsonObject=JSONObject.fromObject(maps);
        System.out.println(jsonObject);
        String s= jsonObject.get("e0f0b332b7e701014e3e110398291f78eac77da65").toString();
        TransPool transPool=JSON.parseObject(s,new TypeReference<TransPool>() {});
        System.out.println(transPool.getTransaction());

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");

        String data=JSON.toJSONString(maps,true);
        Map<String, TransPool> mapss= (Map<String, TransPool>) JSON.parse(data);
        for(Map.Entry<String, TransPool> entry:mapss.entrySet()){
            System.out.println("Key:"+entry.getKey()+"---->Value:"+entry.getValue());
        }
    }*/
}
