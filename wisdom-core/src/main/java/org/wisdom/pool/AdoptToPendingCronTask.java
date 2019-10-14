package org.wisdom.pool;

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
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.ipc.IpcConfig;

import java.util.*;


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
    Configuration configuration;

    @Autowired
    AccountDB accountDB;

    @Autowired
    IncubatorDB incubatorDB;

    @Autowired
    RateTable rateTable;

    @Autowired
    TransactionCheck transactionCheck;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> {
            Map<String, List<TransPool>> map = adoptTransPool.getqueuedtopending();
            IdentityHashMap<String, String> maps = new IdentityHashMap<>();
            List<TransPool> newlist = new ArrayList<>();
            int index = peningTransPool.size();
            boolean state = false;
            for (Map.Entry<String, List<TransPool>> entry : map.entrySet()) {
                //判断pendingnonce是否存在 状态不为2的地址
                PendingNonce pendingNonce = peningTransPool.findptnonce(entry.getKey());
                if (pendingNonce.getState() == 2) {
                    List<TransPool> list = entry.getValue();
                    for (TransPool transPool : list) {
                        Transaction transaction = transPool.getTransaction();
                        if (pendingNonce.getNonce() < transaction.nonce) {
                            Incubator incubator=null;
                            if(transaction.type==0x0a || transaction.type==0x0b || transaction.type==0x0c){
                                incubator=incubatorDB.selectIncubator(transaction.payload);
                            }
                            if (transactionCheck.checkoutPool(transaction,incubator)) {
                                //超过pending上限
                                if (index > configuration.getMaxpending()) {
                                    state = true;
                                    break;
                                }
                                newlist.add(transPool);
                                index++;
                            }
                        }
                        maps.put(new String(entry.getKey()), adoptTransPool.getKey(transaction));
                    }
                    if (state) {
                        break;
                    }
                }
            }
            adoptTransPool.remove(maps);
            peningTransPool.add(newlist);
        }, triggerContext -> {
            //任务触发，可修改任务的执行周期
            CronTrigger trigger = new CronTrigger(ipcConfig.getQueuedToPendingCycle());
            return trigger.nextExecutionTime(triggerContext);
        });
    }
}

