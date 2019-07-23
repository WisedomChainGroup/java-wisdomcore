package org.wisdom.pool;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;

import java.util.*;

@Component
public class PoolTask {

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    Configuration configuration;

    @Autowired
    AccountDB accountDB;

    @Autowired
    IncubatorDB incubatorDB;

    @Autowired
    RateTable rateTable;

    @Scheduled(fixedDelay= 8*1000)
    public void AdoptTopendingTask(){
        List<TransPool> list=adoptTransPool.getAll();
        long nowheight=wisdomBlockChain.currentHeader().nHeight;
        Map<String,String> maps=new HashMap<>();
        List<TransPool> newlist=new ArrayList<>();
        int index=1;
        for(TransPool t:list){
            Transaction tran=t.getTransaction();
            byte[] frompubhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
            long nownonce=accountDB.getNonce(frompubhash);
            long nonce=tran.nonce;
            nownonce++;
            if(nonce==nownonce){
                if(TransactionCheck.checkoutPool(tran,wisdomBlockChain,configuration,accountDB,incubatorDB,rateTable,nowheight)){
                    if(index>5000){
                        break;
                    }else{
                        maps.put(Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from))),adoptTransPool.getKey(t));
                        newlist.add(t);
                        index++;
                    }
                }else{
                    maps.put(Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from))),adoptTransPool.getKey(t));
                }
            }
        }
        if(maps.size()>0){
            adoptTransPool.remove(maps);
        }
        if(newlist.size()>0){
            peningTransPool.add(newlist);
        }
    }

    @Scheduled(fixedDelay= 2*60*1000)
    public void clearPool(){
        //adoptTransPool
        List<TransPool> list=adoptTransPool.getAll();
        Map<String,String> maps=new HashMap<>();
        for(TransPool transPool:list){
            Transaction t=transPool.getTransaction();
            long nonce=t.nonce;
            byte[] from=t.from;
            byte[] frompubhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(from));
            //nonce
            long nownonce=accountDB.getNonce(frompubhash);
            if(nownonce>=nonce){
                maps.put(Hex.encodeHexString(t.from),adoptTransPool.getKeyTrans(t));
                continue;
            }
            long daysBetween=(new Date().getTime()-transPool.getDatetime()+1000000)/(60*60*24*1000);
            if(daysBetween>=configuration.getPoolcleardays()){
                maps.put(Hex.encodeHexString(frompubhash),adoptTransPool.getKeyTrans(t));
                continue;
            }
            //db
            Transaction transaction=wisdomBlockChain.getTransaction(t.getHash());
            if(transaction!=null){
                maps.put(Hex.encodeHexString(frompubhash),adoptTransPool.getKeyTrans(t));
            }
        }
        if(maps.size()>0){
            adoptTransPool.remove(maps);
        }
        //peningTransPool
        List<TransPool> pendinglist=peningTransPool.getAll();
        List<String> pendinglists=new ArrayList<>();
        List<Transaction> updatelist=new ArrayList<>();
        for(TransPool transPool:pendinglist){
            Transaction t=transPool.getTransaction();
            long nonce=t.nonce;
            byte[] from=t.from;
            byte[] frompubhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(from));
            //nonce
            long nownonce=accountDB.getNonce(frompubhash);
            if(nownonce>=nonce){
                pendinglists.add(peningTransPool.getKeyTrans(t));
                continue;
            }
            long daysBetween=(new Date().getTime()-transPool.getDatetime()+1000000)/(60*60*24*1000);
            if(daysBetween>=configuration.getPoolcleardays()){
                pendinglists.add(peningTransPool.getKeyTrans(t));
                continue;
            }
            //db
            Transaction transaction=wisdomBlockChain.getTransaction(t.getHash());
            if(transaction!=null){
                pendinglists.add(peningTransPool.getKeyTrans(t));
                continue;
            }
            //高度
            if(transPool.getState()==1){
                long height=transPool.getHeight();
                long nowheight=wisdomBlockChain.getCurrentTotalWeight();
                if(nowheight>=height){
                    Block b=wisdomBlockChain.getCanonicalBlock(height);
                    if(b!=null){
                        boolean state=true;
                        for(Transaction tx:b.body){
                            if(Arrays.equals(tx.getHash(),t.getHash())){
                                state=false;
                                break;
                            }
                        }
                        if(state==true){
                            updatelist.add(t);
                        }
                    }
                }
            }
        }
        if(updatelist.size()>0){
            peningTransPool.updatePool(updatelist,0,0);
        }
        if(pendinglists.size()>0){
            peningTransPool.remove(pendinglists);
        }
    }
}
