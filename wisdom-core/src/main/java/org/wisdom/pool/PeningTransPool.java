package org.wisdom.pool;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.wisdom.core.account.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class PeningTransPool {

    private Map<String,TransPool> ptpool;

    public PeningTransPool() {
        this.ptpool = new ConcurrentHashMap();
    }

    public void add(List<TransPool> pools){
        for(TransPool transPool:pools){
            byte[] from=transPool.getTransaction().from;
            String fromst= Hex.encodeHexString(from);
            long nonce=transPool.getTransaction().nonce;
            String key=fromst+nonce;
            if(hasExist(key)){
                ptpool.put(key,transPool);
            }
        }
    }

    public int size(){
        return ptpool.size();
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

    public boolean hasExist(String key){
        if(ptpool.containsKey(key)){
            return false;
        }else{
            return true;
        }
    }

    public List<TransPool> getAll(){
        List<TransPool> list=new ArrayList<>();
        for(Map.Entry<String,TransPool> entry:ptpool.entrySet()){
            list.add(entry.getValue());
        }
        return list;
    }

    public void remove(List<String> list){
        for(String s:list){
            if(!hasExist(s)){
                ptpool.remove(s);
            }
        }
    }

    public  List<Transaction> compare(){
        List<Map.Entry<String,TransPool>> lists = new ArrayList<>(ptpool.entrySet());
        Collections.sort(lists, new Comparator<Map.Entry<String,TransPool>>() {
            @Override
            public int compare(Map.Entry<String,TransPool> o1, Map.Entry<String,TransPool> o2) {
                Transaction t1=o1.getValue().getTransaction();
                Transaction t2=o2.getValue().getTransaction();
                if(o1.getValue().getState()==o2.getValue().getState()){
                    return (int) (t2.getFee()-t1.getFee());
                }else if(o1.getValue().getState()<o2.getValue().getState()){
                    return -1;
                }else{
                    return 1;
                }
            }
        });
        List<Transaction> t=new ArrayList<>();
//        Map<String,TransPool> t = new LinkedMap<>();
        for(Map.Entry<String,TransPool> entry: lists){
            if(entry.getValue().getState()==0){//返会待确认事务
                t.add(entry.getValue().getTransaction());
            }
        }
        return t;
    }

    public void updatePool(List<Transaction> txs,int type,long height){
        for(Transaction t:txs){
           String key=getKeyTrans(t);
           if(key!=null){
                if(!hasExist(key)){
                    TransPool transPool=ptpool.get(key);
                    transPool.setState(type);
                    transPool.setHeight(height);
                    ptpool.put(key,transPool);
                }
           }
        }
    }

    public static void main(String args[]){
        Transaction t=new Transaction().createEmpty();
        byte[] s=new byte[32];
        s[10]=10;
        t.type=1;
        t.from=s;
        t.nonce=1;
        t.gasPrice=2;
        TransPool tp=new TransPool(t,1,new Date().getTime());

        Transaction t1=new Transaction().createEmpty();
        byte[] s1=new byte[32];
        s1[11]=10;
        t1.type=1;
        t1.from=s1;
        t1.nonce=1;
        t1.gasPrice=3;
        TransPool tp1=new TransPool(t1,1,new Date().getTime());

        Transaction t2=new Transaction().createEmpty();
        byte[] s2=new byte[32];
        s2[12]=10;
        t2.type=1;
        t2.from=s2;
        t2.nonce=3;
        t2.gasPrice=3;
        TransPool tp2=new TransPool(t2,1,new Date().getTime());

        Transaction t3=new Transaction().createEmpty();
        byte[] s3=new byte[32];
        s3[13]=10;
        t3.type=1;
        t3.from=s3;
        t3.nonce=4;
        t3.gasPrice=5;
        TransPool tp3=new TransPool(t3,0,new Date().getTime());

        Transaction t4=new Transaction().createEmpty();
        byte[] s4=new byte[32];
        s4[14]=10;
        t4.type=1;
        t4.from=s4;
        t4.nonce=2;
        t4.gasPrice=3;
        TransPool tp4=new TransPool(t4,0,new Date().getTime());

        Transaction t5=new Transaction().createEmpty();
        byte[] s5=new byte[32];
        s5[15]=10;
        t5.type=1;
        t5.from=s5;
        t5.nonce=5;
        t5.gasPrice=4;
        TransPool tp5=new TransPool(t5,0,new Date().getTime());

        List<TransPool> l=new ArrayList<>();
        l.add(tp);
        l.add(tp1);
        l.add(tp2);
        l.add(tp3);
        l.add(tp4);
        l.add(tp5);
        PeningTransPool peningTransPool=new PeningTransPool();
        peningTransPool.add(l);
        List<Transaction> petpool=peningTransPool.compare();
        for(Transaction ts:petpool){
            System.out.println("key："+ts.gasPrice);
        }
//        for(Map.Entry<String,TransPool> entry:petpool.entrySet()){
//            System.out.println("key:"+entry.getKey()+"  value:"+entry.getValue().getState()+"-->"+entry.getValue().getTransaction().gasPrice);
//        }


    }
}
