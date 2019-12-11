package org.wisdom.pool;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.Leveldb;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class PoolTask {

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    private Leveldb leveldb;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void updatedbPool() {
        List<TransPool> list = adoptTransPool.getAllFull();
        List<Transaction> queuedlist = new ArrayList<>();
        for (TransPool transPool : list) {
            queuedlist.add(transPool.getTransaction());
        }
        String queuedjson = JSON.toJSONString(queuedlist, true);
        leveldb.put("QueuedPool".getBytes(StandardCharsets.UTF_8),queuedjson.getBytes(StandardCharsets.UTF_8));

        List<TransPool> transPoolList = peningTransPool.getAllstate();
        String pendingjson = JSON.toJSONString(transPoolList, true);
        leveldb.put("PendingPool".getBytes(StandardCharsets.UTF_8), pendingjson.getBytes(StandardCharsets.UTF_8));
    }

//    //pendingnonce修正
//    @Scheduled(fixedDelay = 60000 * 1)
//    public void correctionPtNonce(){
//        Map<String, PendingNonce> nowmap=peningTransPool.getPtnonce();
//        List<String> stringList=new ArrayList<>();
//        for(Map.Entry<String, PendingNonce> entry:nowmap.entrySet()){
//            List<TransPool> transPoolList=peningTransPool.getAllFromState(entry.getKey());
//            if(transPoolList.size()==0){
//                stringList.add(entry.getKey());
//                continue;
//            }
//            boolean result=transPoolList.stream().allMatch(t->checkPendingPool(t));
//            if(result){
//                stringList.add(entry.getKey());
//            }
//        }
//        peningTransPool.updatePtNonce(stringList);
//    }

//    public boolean checkPendingPool(TransPool transPool){
//        Transaction transaction=transPool.getTransaction();
//        if(transaction.type==9 || transaction.type==10 || transaction.type==11 || transaction.type==12){
//            return false;
//        }
//        return true;
//    }
}
