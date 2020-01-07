package org.wisdom.pool;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.wisdom.command.Configuration;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;
import org.wisdom.db.WisdomRepository;
import org.wisdom.ipc.IpcConfig;
import org.wisdom.util.Address;

import java.util.*;

@Component
@EnableScheduling
public class ClearPoolCronTask implements SchedulingConfigurer {

    @Autowired
    IpcConfig ipcConfig;

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    WisdomRepository wisdomRepository;

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    Configuration configuration;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> {
            //adoptTransPool
            List<TransPool> list = adoptTransPool.getAll();
            IdentityHashMap<String, String> maps = new IdentityHashMap<>();
            for (TransPool transPool : list) {
                Transaction t = transPool.getTransaction();
                long nonce = t.nonce;
                byte[] from = t.from;
                byte[] frompubhash = Address.publicKeyToHash(from);
                //nonce
                long nownonce = getAccountNonce(frompubhash);
                if (nownonce >= nonce) {
                    maps.put(new String(Hex.encodeHexString(frompubhash)), adoptTransPool.getKeyTrans(t));
                    continue;
                }
                long daysBetween = (new Date().getTime() - transPool.getDatetime()) / (60 * 60 * 1000);
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
                byte[] frompubhash = Address.publicKeyToHash(from);
                //nonce
                long nownonce = getAccountNonce(frompubhash);
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
                            if (state) {
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
        }, triggerContext -> {
            //任务触发，可修改任务的执行周期
            CronTrigger trigger = new CronTrigger(ipcConfig.getClearCycle());
            return trigger.nextExecutionTime(triggerContext);
        });
    }

    public long getAccountNonce(byte[] pubhash) {
        Optional<AccountState> accountStateOptional = wisdomRepository.getConfirmedAccountState(pubhash);
        if (!accountStateOptional.isPresent()) {
            return 0;
        }
        return accountStateOptional.get().getAccount().getNonce();
    }
}
