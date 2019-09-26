package org.wisdom.consensus.pow;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.db.AccountState;
import org.wisdom.db.StateDB;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.pool.TransPool;

import java.util.*;

//打包时选择事务
//校验转账事务和其他事务的余额,都更新AccountState
@Component
public class PackageMiner {

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    StateDB stateDB;

    @Autowired
    WisdomBlockChain bc;

    @Autowired
    MerkleRule merkleRule;


    public List<Transaction> TransferCheck(byte[] parenthash, long height, Block block) throws DecoderException {
        List<Transaction> notWrittern = new ArrayList<>();
        IdentityHashMap<String, Long> removemap = new IdentityHashMap<>();
        Map<String, TreeMap<Long, TransPool>> maps = peningTransPool.getAllMap();
        List<byte[]> pubhashlist = peningTransPool.getAllPubhash();
        Map<String, AccountState> accountStateMap = stateDB.getAccounts(parenthash, pubhashlist);
        if(accountStateMap.size()==0){
            return notWrittern;
        }
        int size = block.size();
        boolean exit = false;
        for (Map.Entry<String, TreeMap<Long, TransPool>> entry : maps.entrySet()) {
            byte[] key = Hex.decodeHex(entry.getKey().toCharArray());
            String publicKeyHash = Hex.encodeHexString(key);

            TreeMap<Long, TransPool> treeMap = entry.getValue();
            for (Map.Entry<Long, TransPool> entry1 : treeMap.entrySet()) {
                boolean state=false;
                TransPool transPool = entry1.getValue();
                Transaction transaction = transPool.getTransaction();
                if (size > block.MAX_BLOCK_SIZE) {
                    exit = true;
                    break;
                }
                // DB中防止写入重复的事务
                if (bc.hasTransaction(transaction.getHash())) {
                    continue;
                }
                //forkdb中防止写入重复事务
                if (stateDB.hasTransaction(parenthash, transaction.getHash())) {
                    continue;
                }
                // 没有获取到 AccountState
                if (!accountStateMap.containsKey(publicKeyHash)) {
                    continue;
                }
                AccountState accountState = accountStateMap.get(publicKeyHash);
                Account fromaccount = accountState.getAccount();

                switch (transaction.type){
                    case 1://转账
                    case 2:////投票
                    case 13://撤回投票
                        String tohash = Hex.encodeHexString(transaction.to);
                        Account toaccount;
                        if (accountStateMap.containsKey(tohash)) {
                            AccountState toaccountState = accountStateMap.get(tohash);
                            toaccount = toaccountState.getAccount();
                        } else {
                            toaccount = new Account(0, transaction.to, 0, 0, 0, 0, 0);
                        }
                        List<Account> accountList=null;
                        if(transaction.type==1){
                            accountList=updateTransfer(fromaccount,toaccount,transaction);
                        }else if(transaction.type==2){
                            accountList=updateVote(fromaccount,toaccount,transaction);
                        }else if(transaction.type==13){
                            accountList=UpdateCancelVote(fromaccount,toaccount,transaction);
                        }
                        if(accountList==null){
                            removemap.put(new String(entry.getKey()), transaction.nonce);
                            state=true;
                            break;
                        }
                        accountList.stream().forEach(a-> {
                            accountState.setAccount(a);
                            accountStateMap.put(publicKeyHash, accountState);
                        });
                        break;
                    case 3://存证事务,只需要扣除手续费
                    case 9://孵化事务
                    case 10://提取利息
                    case 11://提取分享
                    case 12://本金
                    case 14://抵押
                    case 15://撤回抵押
                        Account account=UpdateOtherAccount(fromaccount,transaction);
                        if(account==null){
                            removemap.put(new String(entry.getKey()), transaction.nonce);
                            state=true;
                            break;
                        }
                        accountState.setAccount(account);

                        Map<String,Incubator> map=null;
                        if(transaction.type==10){
                            map=accountState.getInterestMap();
                            Incubator incubator=UpdateIncubtor(map,transaction,block.nHeight);
                            if(incubator.getInterest_amount()<0 || incubator.getLast_blockheight_interest()>block.nHeight){
                                removemap.put(new String(entry.getKey()), transaction.nonce);
                                break;
                            }
                            map.put(Hex.encodeHexString(transaction.payload),incubator);
                            accountState.setInterestMap(map);
                        }else if(transaction.type==11){
                            map=accountState.getShareMap();
                            Incubator incubator=UpdateIncubtor(map,transaction,block.nHeight);
                            if(incubator.getShare_amount()<0 || incubator.getLast_blockheight_share()>block.nHeight){
                                removemap.put(new String(entry.getKey()), transaction.nonce);
                                break;
                            }
                            map.put(Hex.encodeHexString(transaction.payload),incubator);
                            accountState.setShareMap(map);
                        }else if(transaction.type==12){
                            map=accountState.getInterestMap();
                            Incubator incubator=UpdateIncubtor(map,transaction,block.nHeight);
                            map.put(Hex.encodeHexString(transaction.payload),incubator);
                            accountState.setInterestMap(map);
                        }
                        accountStateMap.put(publicKeyHash, accountState);
                        break;
                }
                if(state){
                    continue;
                }
                transaction.height = height;
                size += transaction.size();
                notWrittern.add(transaction);
            }
            if (exit) {//退出
                break;
            }
        }
        //删除事务内存池事务
        peningTransPool.remove(removemap);
        return notWrittern;
    }

    public List<Account> updateTransfer(Account fromaccount, Account toaccount, Transaction transaction){
        List<Account> list=new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.amount;
        balance -= transaction.getFee();
        if(Arrays.equals(fromaccount.getPubkeyHash(),toaccount.getPubkeyHash())){
            balance+=transaction.amount;
        }else{
            long tobalance = toaccount.getBalance();
            tobalance += transaction.amount;
            toaccount.setBalance(tobalance);
            list.add(toaccount);
        }
        if(balance < 0){
            return null;
        }
        fromaccount.setBalance(balance);
        list.add(fromaccount);
        return list;
    }

    public List<Account> updateVote(Account fromaccount, Account toaccount, Transaction transaction){
        List<Account> list=new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.amount;
        balance -= transaction.getFee();
        if(Arrays.equals(fromaccount.getPubkeyHash(),toaccount.getPubkeyHash())){
            long vote=fromaccount.getVote();
            vote+=transaction.amount;
            fromaccount.setVote(vote);
        }else{
            long vote=toaccount.getVote();
            vote+=transaction.amount;
            toaccount.setVote(vote);
            list.add(toaccount);
        }
        if(balance < 0){
            return null;
        }
        fromaccount.setBalance(balance);
        list.add(fromaccount);
        return list;
    }

    public List<Account> UpdateCancelVote(Account fromaccount, Account toaccount, Transaction transaction){
        List<Account> list=new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.getFee();
        if(balance<0){
            return null;
        }
        balance+=transaction.amount;
        long vote;
        if(Arrays.equals(fromaccount.getPubkeyHash(),toaccount.getPubkeyHash())){
            vote=fromaccount.getVote();
            vote-=transaction.amount;
            if(vote<0){
                return null;
            }
            fromaccount.setVote(vote);
        }else{
            vote=toaccount.getVote();
            vote-=transaction.amount;
            toaccount.setVote(vote);
            list.add(toaccount);
        }
        fromaccount.setBalance(balance);
        list.add(fromaccount);
        return list;
    }

    public Account UpdateOtherAccount(Account fromaccount,Transaction transaction){
        boolean state=false;
        long balance=fromaccount.getBalance();
        if (transaction.type == 3) {//存证事务,只需要扣除手续费
            balance -= transaction.getFee();
        } else if (transaction.type == 9) {//孵化事务
            balance -= transaction.getFee();
            balance -= transaction.amount;
            long incubatecost = fromaccount.getIncubatecost();
            incubatecost += transaction.amount;
            fromaccount.setIncubatecost(incubatecost);
        } else if (transaction.type == 10 || transaction.type == 11) {//提取利息、分享
            balance -= transaction.getFee();
            if(balance<0){
                state=true;
            }
            balance += transaction.amount;
        } else if (transaction.type == 12) {//本金
            balance -= transaction.getFee();
            if(balance<0){
                state=true;
            }
            balance += transaction.amount;
            long incubatecost = fromaccount.getIncubatecost();
            incubatecost -= transaction.amount;
            if (incubatecost < 0) {
                state=true;
            }
            fromaccount.setIncubatecost(incubatecost);
        } else if(transaction.type == 14){//抵押
            balance -= transaction.getFee();
            balance -= transaction.amount;
            long mortgage=fromaccount.getMortgage();
            mortgage+=transaction.amount;
            fromaccount.setMortgage(mortgage);
        } else if(transaction.type == 15){//撤回抵押
            balance -= transaction.getFee();
            if(balance<0){
                state=true;
            }
            balance+=transaction.amount;
            long mortgage=fromaccount.getMortgage();
            mortgage-=transaction.amount;
            if(mortgage<0){
                state=true;
            }
            fromaccount.setMortgage(mortgage);
        }
        if (state || balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        return fromaccount;
    }

    public Incubator UpdateIncubtor(Map<String, Incubator> map, Transaction transaction, long hieght){
        Incubator incubator=map.get(Hex.encodeHexString(transaction.payload));
        if(transaction.type==10 || transaction.type==11){
            incubator=merkleRule.UpdateExtIncuator(transaction,hieght,incubator);
        }
        if(transaction.type==12){
            incubator=merkleRule.UpdateCostIncubator(incubator,hieght);
        }
        return incubator;
    }
}
