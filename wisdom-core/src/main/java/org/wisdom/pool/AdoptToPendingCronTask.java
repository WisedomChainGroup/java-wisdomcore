package org.wisdom.pool;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.ipc.IpcConfig;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@EnableScheduling
public class AdoptToPendingCronTask implements SchedulingConfigurer {

    @Autowired
    IpcConfig ipcConfig;

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

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> {
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
        }, triggerContext -> {
            //任务触发，可修改任务的执行周期
            CronTrigger trigger = new CronTrigger(ipcConfig.getQueuedToPendingCycle());
            return trigger.nextExecutionTime(triggerContext);
        });
    }
}
