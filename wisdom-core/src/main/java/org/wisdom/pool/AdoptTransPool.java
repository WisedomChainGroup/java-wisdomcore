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

    private Map<String,Map<String,TransPool>> atpool;

    public AdoptTransPool(){
        this.atpool=new ConcurrentHashMap();
    }

    public void add(List<Transaction> txs){
        for(Transaction t:txs){
            String from=Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(t.from)));
            if(hasExist(from)){
                Map<String,TransPool> map=new HashMap<>();
                TransPool tp=new TransPool(t,0,new Date().getTime());
                map.put(getKeyTrans(t),tp);
                atpool.put(from,map);
            }else{
                Map<String,TransPool> map=atpool.get(from);
                TransPool tp=new TransPool(t,0,new Date().getTime());
                map.put(getKeyTrans(t),tp);
                atpool.put(from,map);
            }
        }
    }

    public String getKeyTrans(Transaction t){
        if(t!=null){
            byte[] from=t.from;
            String fromhash= Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(from)));
            //todo:key=fromhash+nonce
            String key=fromhash+t.nonce;
            return key;
        }else{
            return null;
        }
    }

    public String getKey(TransPool transPool){
        if(transPool!=null){
            return getKeyTrans(transPool.getTransaction());
        }else{
            return null;
        }
    }

    public boolean hasExistQueued(String key,String key1){
        if(atpool.containsKey(key)) {
            Map<String, TransPool> map = atpool.get(key);
            if (map.containsKey(key1)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasExist(String key){
        if(atpool.containsKey(key)){
           return false;
        }else{
            return true;
        }
    }

    public void remove(Map<String,String> maps){
        for(Map.Entry<String,String> entry:maps.entrySet()){
            if(!hasExist(entry.getKey())){
                Map<String, TransPool> map=atpool.get(entry.getKey());
                if(map.containsKey(entry.getValue())){
                    map.remove(entry.getValue());
                    if(map.size()==0){
                        atpool.remove(entry.getKey());
                    }else{
                        atpool.put(entry.getKey(),map);
                    }
                    break;
                }
            }
        }
    }

    public List<TransPool> getAll(){
        List<TransPool> list=new ArrayList<>();
        for(Map.Entry<String, Map<String, TransPool>> entry:atpool.entrySet()){
            Map<String,TransPool> map=compare(entry.getValue());
            for(Map.Entry<String,TransPool> entry1:map.entrySet()){
                TransPool t=entry1.getValue();
                list.add(t);
                break;
            }
        }
        return list;
    }

    public List<TransPool> getAllFrom(String from){
        List<TransPool> list=new ArrayList<>();
        if(!hasExist(from)){
            Map<String, TransPool> map=atpool.get(from);
            for(Map.Entry<String,TransPool> entry:map.entrySet()){
                TransPool t=entry.getValue();
                list.add(t);
            }
        }
        return list;
    }

    public List<TransPool> getAllFull(){
        List<TransPool> list=new ArrayList<>();
        for(Map.Entry<String,Map<String, TransPool>> entry:atpool.entrySet()){
            for(Map.Entry<String,TransPool> entrys:entry.getValue().entrySet()){
                list.add(entrys.getValue());
            }
        }
        return list;
    }

    public TransPool getPoolTranHash(byte[] txhash){
        for(Map.Entry<String,Map<String, TransPool>> entry:atpool.entrySet()){
            for(Map.Entry<String,TransPool> entrys:entry.getValue().entrySet()){
                Transaction t=entrys.getValue().getTransaction();
                if(Arrays.equals(t.getHash(),txhash)){
                    return entrys.getValue();
                }
            }
        }
        return null;
    }

    public Map<String,TransPool> getfromPool(String from){
        if(!hasExist(from)){
            return atpool.get(from);
        }
        return null;
    }

    public Map<String,TransPool> compare(Map<String,TransPool> maps){
        List<Map.Entry<String,TransPool>> list = new ArrayList<>(maps.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String,TransPool>>() {
            @Override
            public int compare(Map.Entry<String,TransPool> o1, Map.Entry<String,TransPool> o2) {
                Transaction t1=o1.getValue().getTransaction();
                Transaction t2=o2.getValue().getTransaction();
                return (int) (t1.nonce-t2.nonce);
            }
        });
        Map<String,TransPool> map = new LinkedMap();
        for (Map.Entry<String,TransPool> entry : list) {
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }


    public static void main(String args[]){
        Transaction t=new Transaction().createEmpty();
        byte[] s=new byte[32];
        s[10]=10;
        t.type=1;
        t.from=s;
        t.nonce=1;
        t.gasPrice=2;
        Transaction t1=new Transaction().createEmpty();
        t1.type=1;
        t1.nonce=1;
        t1.gasPrice=3;
        Transaction t2=new Transaction().createEmpty();
        t2.type=1;
        t2.nonce=3;
        t2.gasPrice=3;
        Transaction t3=new Transaction().createEmpty();
        byte[] s3=new byte[32];
        s3[13]=10;
        t3.type=1;
        t3.from=s3;
        t3.nonce=1;
        t3.gasPrice=5;
        Transaction t4=new Transaction().createEmpty();
        t4.type=1;
        t4.nonce=2;
        t4.gasPrice=3;
        Transaction t5=new Transaction().createEmpty();
        t5.type=1;
        t5.nonce=1;
        t5.gasPrice=4;
        List<Transaction> l=new ArrayList<>();
        l.add(t);
        l.add(t1);
        l.add(t2);
        l.add(t3);
        l.add(t4);
        l.add(t5);
        AdoptTransPool a=new AdoptTransPool();
        a.add(l);
        Map<String,TransPool> sss=a.compare(a.getfromPool(Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(new byte[32])))));
        for(Map.Entry<String,TransPool> entry:sss.entrySet()){
            System.out.println("key:"+entry.getKey()+"  value:"+entry.getValue().getTransaction().gasPrice+"-->"+entry.getValue().getTransaction().nonce);
        }


    }
}
