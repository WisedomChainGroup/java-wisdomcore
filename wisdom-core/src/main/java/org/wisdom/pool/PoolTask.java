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

    @Autowired
    TransactionCheck transactionCheck;

    @Scheduled(fixedDelay = 5 * 1000)
    public void AdoptTopendingTask() {
        Map<String, List<TransPool>> map = adoptTransPool.getqueuedtopending();
        IdentityHashMap<String, String> maps = new IdentityHashMap<>();
        List<TransPool> newlist = new ArrayList<>();
        int index=peningTransPool.size();
        boolean state=false;
        for (Map.Entry<String, List<TransPool>> entry : map.entrySet()) {
            //判断pendingnonce是否存在 状态不为2的地址
            PendingNonce pendingNonce = peningTransPool.findptnonce(entry.getKey());
            if (pendingNonce.getState()==2) {
                List<TransPool> list = entry.getValue();
                for (TransPool transPool : list) {
                    Transaction transaction = transPool.getTransaction();
                    if(pendingNonce.getNonce()<transaction.nonce){
                        if (transactionCheck.checkoutPool(transaction)) {
                            //超过pending上限
                            if(index>configuration.getMaxpending()){
                                state=true;
                                break;
                            }
                            newlist.add(transPool);
                            index++;
                        }
                    }
                    maps.put(new String(entry.getKey()), adoptTransPool.getKey(transaction));
                }
                if(state){
                    break;
                }
            }
        }
        adoptTransPool.remove(maps);
        peningTransPool.add(newlist);
    }

    @Scheduled(fixedDelay = 30 * 1000)
    public void clearPool() {
        //adoptTransPool
        List<TransPool> list = adoptTransPool.getAll();
        IdentityHashMap<String, String> maps = new IdentityHashMap<>();
        for (TransPool transPool : list) {
            Transaction t = transPool.getTransaction();
            long nonce = t.nonce;
            byte[] from = t.from;
            byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(from));
            //nonce
            long nownonce = accountDB.getNonce(frompubhash);
            if (nownonce >= nonce) {
                maps.put(new String(Hex.encodeHexString(frompubhash)), adoptTransPool.getKeyTrans(t));
                continue;
            }
            long daysBetween = (new Date().getTime() - transPool.getDatetime() ) / (60 * 60 * 1000);
            if (daysBetween >= configuration.getPoolcleardays()) {
                maps.put(new String(Hex.encodeHexString(frompubhash)), adoptTransPool.getKeyTrans(t));
                continue;
            }
            //db
            Transaction transaction = wisdomBlockChain.getTransaction(t.getHash());
            if (transaction != null) {
                maps.put(new String(Hex.encodeHexString(frompubhash)), adoptTransPool.getKeyTrans(t));
            }
        }
        if (maps.size() > 0) {
            adoptTransPool.remove(maps);
        }
        //peningTransPool
        List<TransPool> pendinglist = peningTransPool.getAll();
        List<Transaction> updatelist = new ArrayList<>();
        IdentityHashMap<String, Long> map = new IdentityHashMap<>();
        for (TransPool transPool : pendinglist) {
            Transaction t = transPool.getTransaction();
            long nonce = t.nonce;
            byte[] from = t.from;
            byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(from));
            //nonce
            long nownonce = accountDB.getNonce(frompubhash);
            if (nownonce >= nonce) {
                map.put(new String(Hex.encodeHexString(frompubhash)), t.nonce);
                continue;
            }
            long daysBetween = (new Date().getTime() - transPool.getDatetime()) / (60 * 60 * 1000);
            if (daysBetween >= configuration.getPoolcleardays()) {
                map.put(new String(Hex.encodeHexString(frompubhash)), t.nonce);
                continue;
            }
            //db
            Transaction transaction = wisdomBlockChain.getTransaction(t.getHash());
            if (transaction != null) {
                map.put(new String(Hex.encodeHexString(frompubhash)), t.nonce);
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
        if (map.size() > 0) {
            peningTransPool.remove(map);
        }
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void updatedbPool() {
        Leveldb leveldb = new Leveldb();
        List<TransPool> list = adoptTransPool.getAllFull();
        List<Transaction> queuedlist = new ArrayList<>();
        for (TransPool transPool : list) {
            queuedlist.add(transPool.getTransaction());
        }
        String queuedjson = JSON.toJSONString(queuedlist, true);
        leveldb.addPoolDb("QueuedPool", queuedjson);

        List<TransPool> transPoolList = peningTransPool.getAllstate();
        String pendingjson = JSON.toJSONString(transPoolList, true);
        leveldb.addPoolDb("PendingPool", pendingjson);
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
