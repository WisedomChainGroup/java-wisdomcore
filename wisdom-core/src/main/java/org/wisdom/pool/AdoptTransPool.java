package org.wisdom.pool;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.map.LinkedMap;
import org.springframework.stereotype.Component;
import org.wisdom.core.account.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class AdoptTransPool {

    private Map<String,TransPool> atpool;

    private ReadWriteLock lock;

    public AdoptTransPool(){
        this.atpool=new ConcurrentHashMap();
        this.lock=new ReentrantReadWriteLock();
    }

    public void add(List<Transaction> txs){
        lock.writeLock().lock();
        for(Transaction t:txs){
            String key=getKeyTrans(t);
            if(key!=null){
                if(hasExist(key)){
                    TransPool tp=new TransPool(t,0,new Date().getTime());
                    atpool.put(key,tp);
                }
            }
        }
        lock.writeLock().unlock();
    }

    public int size(){
        return atpool.size();
    }

    public String getKeyTrans(Transaction t){
        if(t!=null){
            byte[] from=t.from;
            String froms= Hex.encodeHexString(from);
            //todo:key=from+nonce
            String key=froms+t.nonce;
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

    public boolean hasExist(String key){
        if(atpool.containsKey(key)){
           return false;
        }else{
            return true;
        }
    }

    public void remove(List<String> list){
        lock.writeLock().lock();
        for(String s:list){
            if(!hasExist(s)){
                atpool.remove(s);
            }
        }
        lock.writeLock().unlock();
    }

    public List<TransPool> getAll(){
        List<TransPool> list=new ArrayList<>();
        for(Map.Entry<String,TransPool> entry:atpool.entrySet()){
            list.add(entry.getValue());
        }
        return list;
    }

    public Map<String,TransPool> compare(){
        List<Map.Entry<String,TransPool>> list = new ArrayList<>(atpool.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String,TransPool>>() {
            @Override
            public int compare(Map.Entry<String,TransPool> o1, Map.Entry<String,TransPool> o2) {
                Transaction t1=o1.getValue().getTransaction();
                Transaction t2=o2.getValue().getTransaction();
                if(t1.getFee()==t2.getFee()){
                    return (int) (t2.nonce-t1.nonce);
                }else if(t1.getFee()>t2.getFee()){
                    return -1;
                }else{
                    return 1;
                }
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
        Map<String,TransPool> sss=a.compare();
        for(Map.Entry<String,TransPool> entry:sss.entrySet()){
            System.out.println("key:"+entry.getKey()+"  value:"+entry.getValue().getTransaction().gasPrice+"-->"+entry.getValue().getTransaction().nonce);
        }


    }
}
