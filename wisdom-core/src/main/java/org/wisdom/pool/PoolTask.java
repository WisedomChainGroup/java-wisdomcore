package org.wisdom.pool;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.Controller.RPCClient;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.db.Leveldb;
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

    @Autowired
    RPCClient client;

    @Scheduled(fixedDelay = 5 * 1000)
    public void AdoptTopendingTask() {
        List<TransPool> list = adoptTransPool.getAll();
        long nowheight = wisdomBlockChain.currentHeader().nHeight;
        Map<String, String> maps = new HashMap<>();
        List<TransPool> newlist = new ArrayList<>();
        int index = 1;
        for (TransPool t : list) {
            Transaction tran = t.getTransaction();
            String fromhex=Hex.encodeHexString(tran.from);
            byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
            long nownonce = accountDB.getNonce(frompubhash);
            long nonce = tran.nonce;
            if (nonce > nownonce) {
                boolean statue=true;
                //判断pendingnonce是否存在 状态不为2的地址
                Map<String, PendingNonce> noncemap=peningTransPool.getPtnonce();
                if(noncemap.containsKey(fromhex)){
                    PendingNonce pendingNonce=noncemap.get(fromhex);
                    int state=pendingNonce.getState();
                    if(state!=2){
                        statue=false;
                    }
                }
                if(statue){
                    if (TransactionCheck.checkoutPool(tran, wisdomBlockChain, configuration, accountDB, incubatorDB, rateTable, nowheight)) {
                        if (index > 5000) {
                            break;
                        } else {
                            maps.put(Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from))), adoptTransPool.getKey(t));
                            newlist.add(t);
                            index++;
                        }
                    } else {
                        maps.put(Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from))), adoptTransPool.getKey(t));
                    }
                }
            } else if (nonce <= nownonce) {
                if (index > 5000) {
                    break;
                } else {
                    maps.put(Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from))), adoptTransPool.getKey(t));
                }
            }
        }
        if (maps.size() > 0) {
            adoptTransPool.remove(maps);
        }
        if (newlist.size() > 0) {
            peningTransPool.add(newlist);
        }
    }

    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void clearPool() {
        //adoptTransPool
        List<TransPool> list = adoptTransPool.getAll();
        Map<String, String> maps = new HashMap<>();
        for (TransPool transPool : list) {
            Transaction t = transPool.getTransaction();
            long nonce = t.nonce;
            byte[] from = t.from;
            byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(from));
            //nonce
            long nownonce = accountDB.getNonce(frompubhash);
            if (nownonce >= nonce) {
                maps.put(Hex.encodeHexString(frompubhash), adoptTransPool.getKeyTrans(t));
                continue;
            }
            long daysBetween = (new Date().getTime() - transPool.getDatetime() + 1000000) / (60 * 60 * 24 * 1000);
            if (daysBetween >= configuration.getPoolcleardays()) {
                maps.put(Hex.encodeHexString(frompubhash), adoptTransPool.getKeyTrans(t));
                continue;
            }
            //db
            Transaction transaction = wisdomBlockChain.getTransaction(t.getHash());
            if (transaction != null) {
                maps.put(Hex.encodeHexString(frompubhash), adoptTransPool.getKeyTrans(t));
            }
        }
        if (maps.size() > 0) {
            adoptTransPool.remove(maps);
        }
        //peningTransPool
        List<TransPool> pendinglist = peningTransPool.getAll();
        List<String> pendinglists = new ArrayList<>();
        List<Transaction> updatelist = new ArrayList<>();
        Map<String,Long> map=new HashMap<>();
        for (TransPool transPool : pendinglist) {
            Transaction t = transPool.getTransaction();
            long nonce = t.nonce;
            byte[] from = t.from;
            String fromhex=Hex.encodeHexString(from);
            byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(from));
            //nonce
            long nownonce = accountDB.getNonce(frompubhash);
            if (nownonce >= nonce) {
                pendinglists.add(peningTransPool.getKeyTrans(t));
                map.put(fromhex,t.nonce);
                continue;
            }
            long daysBetween = (new Date().getTime() - transPool.getDatetime() + 1000000) / (60 * 60 * 24 * 1000);
            if (daysBetween >= configuration.getPoolcleardays()) {
                pendinglists.add(peningTransPool.getKeyTrans(t));
                map.put(fromhex,t.nonce);
                continue;
            }
            //db
            Transaction transaction = wisdomBlockChain.getTransaction(t.getHash());
            if (transaction != null) {
                pendinglists.add(peningTransPool.getKeyTrans(t));
                map.put(fromhex,t.nonce);
                continue;
            }
            //高度
            if (transPool.getState() == 1) {
                long height = transPool.getHeight();
                long nowheight = wisdomBlockChain.getCurrentTotalWeight();
                if (nowheight >= height) {
                    Block b = wisdomBlockChain.getCanonicalBlock(height);
                    if (b != null) {
                        boolean state = true;
                        for (Transaction tx : b.body) {
                            if (Arrays.equals(tx.getHash(), t.getHash())) {
                                state = false;
                                break;
                            }
                        }
                        if (state == true) {
                            updatelist.add(t);
                        }
                    }
                }
            }
        }
        if (updatelist.size() > 0) {
            peningTransPool.updatePool(updatelist, 0, 0);
        }
        if (pendinglists.size() > 0) {
            peningTransPool.remove(pendinglists,map);
        }
    }

    @Scheduled(cron="0 0 0/1 * * ?")
    public void updatedbPool(){
        Leveldb leveldb=new Leveldb();
        List<TransPool> list=adoptTransPool.getAllFull();
        List<Transaction> queuedlist=new ArrayList<>();
        for(TransPool transPool:list){
            queuedlist.add(transPool.getTransaction());
        }
        if(queuedlist.size()>0){
            String queuedjson = JSON.toJSONString(queuedlist,true);
            leveldb.addPoolDb("QueuedPool",queuedjson);
        }
        List<TransPool> transPoolList=peningTransPool.getAllstate();
        if(transPoolList.size()>0){
            String pendingjson = JSON.toJSONString(transPoolList,true);
            leveldb.addPoolDb("PendingPool",pendingjson);
        }
    }




   /* @Scheduled(fixedDelay = 30 * 1000)
    public void sendTranPool() {
        List<TransPool> queuedlist = adoptTransPool.getAllFull();
        List<Transaction> pengdinglist = peningTransPool.compare();
        List<Transaction> totallist = new ArrayList<>();
        for (TransPool transPool : queuedlist) {
            totallist.add(transPool.getTransaction());
        }
        totallist.addAll(pengdinglist);
        if(totallist.size()>0){
            client.broadcastTransactions(totallist);
        }
    }*/

}
