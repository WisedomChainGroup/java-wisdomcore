package org.wisdom.pool;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.wisdom.core.account.Transaction;

import org.wisdom.db.Leveldb;

import java.util.*;

@Component
public class PoolTask {

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    PeningTransPool peningTransPool;


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

}
