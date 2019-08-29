package org.wisdom.consensus.pow;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;
import org.wisdom.db.StateDB;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.pool.TransPool;

import java.util.*;

//打包时选择事务
//只校验转账事务,其他事务直接打包
@Component
public class PackageMiner {

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    StateDB stateDB;

    @Autowired
    WisdomBlockChain bc;


    public List<Transaction> TransferCheck(byte[] parenthash, long height, Block block) throws DecoderException {
        List<Transaction> notWrittern = new ArrayList<>();
        IdentityHashMap<String,Long> removemap=new IdentityHashMap<>();
        Map<String, TreeMap<Long, TransPool>> maps=peningTransPool.getAllMap();
        List<byte[]> pubhashlist=peningTransPool.getAllPubhash();
        Map<String, AccountState> accountStateMap=stateDB.getAccounts(parenthash,pubhashlist);
        int size=block.size();
        boolean exit=false;
        for(Map.Entry<String, TreeMap<Long, TransPool>> entry: maps.entrySet()){
            byte[] key=Hex.decodeHex(entry.getKey().toCharArray());
            byte[] pubkeyhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(key));
            String publicKeyHash=Hex.encodeHexString(pubkeyhash);

            TreeMap<Long, TransPool> treeMap=entry.getValue();
            for(Map.Entry<Long, TransPool> entry1:treeMap.entrySet()){
                TransPool transPool=entry1.getValue();
                Transaction transaction=transPool.getTransaction();
                if(size>block.MAX_BLOCK_SIZE){
                    exit=true;
                    break;
                }
                // 防止写入重复的事务
                if (bc.hasTransaction(transaction.getHash())) {
                    continue;
                }
                if(transaction.type==1){//转账
                    if(accountStateMap.containsKey(publicKeyHash)){
                        AccountState accountState=accountStateMap.get(publicKeyHash);
                        Account account=accountState.getAccount();
                        long balance=account.getBalance();
                        balance-=transaction.amount;
                        balance-=transaction.getFee();
                        if(balance<0){
                            removemap.put(new String(entry.getKey()),transaction.nonce);
                            continue;
                        }else{
                            account.setBalance(balance);
                            accountState.setAccount(account);
                            accountStateMap.put(publicKeyHash,accountState);
                            String tohash=Hex.encodeHexString(transaction.to);
                            AccountState toaccountState;
                            Account toaccount;
                            if(accountStateMap.containsKey(tohash)){
                                toaccountState=accountStateMap.get(tohash);
                                toaccount=toaccountState.getAccount();
                            }else{
                                toaccountState=new AccountState();
                                toaccount=new Account(0,transaction.to,0,0,0,0,0);
                            }
                            long tobalance=toaccount.getBalance();
                            tobalance+=transaction.amount;
                            toaccount.setBalance(tobalance);
                            toaccountState.setAccount(toaccount);
                            accountStateMap.put(tohash,toaccountState);
                        }
                    }else{
                        break;
                    }
                }
                transaction.height=height;
                size+=transaction.size();
                notWrittern.add(transaction);
            }
            if(exit){//退出
                break;
            }
        }
        //删除事务内存池事务
        peningTransPool.remove(removemap);
        return notWrittern;
    }
}
