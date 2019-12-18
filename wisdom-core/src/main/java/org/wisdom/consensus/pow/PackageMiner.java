package org.wisdom.consensus.pow;

import lombok.*;
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
import org.wisdom.pool.WaitCount;

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

    @Autowired
    WaitCount waitCount;


    public List<Transaction> TransferCheck(byte[] parenthash, long height, Block block) throws DecoderException {
        List<Transaction> notWrittern = new ArrayList<>();
        IdentityHashMap<String, Long> removemap = new IdentityHashMap<>();
        Map<String, TreeMap<Long, TransPool>> maps = peningTransPool.getAllMap();
        List<byte[]> pubhashlist = peningTransPool.getAllPubhash();
        Map<String, AccountState> accountStateMap = stateDB.getAccounts(parenthash, pubhashlist);
        if (accountStateMap.size() == 0) {
            return notWrittern;
        }
        int size = block.size();
        boolean exit = false;
        for (Map.Entry<String, TreeMap<Long, TransPool>> entry : maps.entrySet()) {
            String publicKeyHash = entry.getKey();

            TreeMap<Long, TransPool> treeMap = entry.getValue();
            for (Map.Entry<Long, TransPool> entry1 : treeMap.entrySet()) {
                boolean state = false;
                TransPool transPool = entry1.getValue();
                Transaction transaction = transPool.getTransaction();
                if (size > Block.MAX_BLOCK_SIZE || (size + transaction.size()) > Block.MAX_BLOCK_SIZE) {
                    exit = true;
                    break;
                }
                // 防止写入重复事务
                if (stateDB.hasTransaction(parenthash, transaction.getHash())) {
                    continue;
                }
                // 没有获取到 AccountState
                if (!accountStateMap.containsKey(publicKeyHash)) {
                    break;
                }
                AccountState accountState = accountStateMap.get(publicKeyHash);
                Account fromaccount = accountState.getAccount();

                // nonce是否合法
                if (fromaccount.getNonce() >= transaction.nonce) {
                    removemap.put(publicKeyHash,transaction.nonce);
                    continue;
                }
                //nonce是否跳号
                if(fromaccount.getNonce()+1 != transaction.nonce){
                    if(!updateWaitCount(publicKeyHash,transaction.nonce)) break;
                }
                switch (transaction.type) {
                    case 1://转账
                    case 2://投票
                    case 13://撤回投票
                        String tohash = Hex.encodeHexString(transaction.to);
                        Account toaccount;
                        AccountState toaccountState;
                        if (accountStateMap.containsKey(tohash)) {
                            toaccountState = accountStateMap.get(tohash);
                        } else {
                            toaccountState = stateDB.getAccount(parenthash, transaction.to);
                        }
                        toaccount = toaccountState.getAccount();
                        Map<String, Account> accountList = null;
                        if (transaction.type == 1) {
                            accountList = updateTransfer(fromaccount, toaccount, transaction);
                        } else if (transaction.type == 2) {
                            accountList = updateVote(fromaccount, toaccount, transaction);
                        } else {
                            accountList = UpdateCancelVote(fromaccount, toaccount, transaction);
                        }
                        if (accountList == null) {
                            removemap.put(new String(publicKeyHash), transaction.nonce);
                            state = true;
                            break;
                        }
                        if (accountList.containsKey("fromaccount")) {
                            accountState.setAccount(accountList.get("fromaccount"));
                            accountStateMap.put(publicKeyHash, accountState);
                        } else if (accountList.containsKey("toaccount")) {
                            toaccountState.setAccount(accountList.get("toaccount"));
                            accountStateMap.put(tohash, accountState);
                        }
                        break;
                    case 3://存证事务,只需要扣除手续费
                    case 9://孵化事务
                    case 10://提取利息
                    case 11://提取分享
                    case 12://本金
                    case 14://抵押
                    case 15://撤回抵押
                        Account account = UpdateOtherAccount(fromaccount, transaction);
                        if (account == null) {
                            removemap.put(new String(publicKeyHash), transaction.nonce);
                            state = true;
                            break;
                        }
                        accountState.setAccount(account);
                        //校验type 10、11、12事务
                        VerifyHatch verifyHatch=updateHatch(accountState,transaction,block.nHeight);
                        if(!verifyHatch.state){
                            removemap.put(new String(publicKeyHash), transaction.nonce);
                            state = true;
                            break;
                        }
                        accountStateMap.put(publicKeyHash, verifyHatch.getAccountState());
                        break;
                }
                if (state) {
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

    public Map<String, Account> updateTransfer(Account fromaccount, Account toaccount, Transaction transaction) {
        Map<String, Account> map = new HashMap<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.amount;
        balance -= transaction.getFee();
        if (Arrays.equals(fromaccount.getPubkeyHash(), toaccount.getPubkeyHash())) {
            balance += transaction.amount;
        } else {
            long tobalance = toaccount.getBalance();
            tobalance += transaction.amount;
            toaccount.setBalance(tobalance);
            map.put("toaccount", toaccount);
        }
        if (balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        map.put("fromaccount", fromaccount);
        return map;
    }

    public Map<String, Account> updateVote(Account fromaccount, Account toaccount, Transaction transaction) {
        Map<String, Account> map = new HashMap<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.amount;
        balance -= transaction.getFee();
        if (Arrays.equals(fromaccount.getPubkeyHash(), toaccount.getPubkeyHash())) {
            long vote = fromaccount.getVote();
            vote += transaction.amount;
            fromaccount.setVote(vote);
        } else {
            long vote = toaccount.getVote();
            vote += transaction.amount;
            toaccount.setVote(vote);
            map.put("toaccount", toaccount);
        }
        if (balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        map.put("fromaccount", fromaccount);
        return map;
    }

    public Map<String, Account> updateMortgage(Account fromaccount, Transaction transaction) {
        Map<String, Account> map = new HashMap<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.amount;
        balance -= transaction.getFee();
        long mortgage = fromaccount.getMortgage();
        mortgage += transaction.amount;
        fromaccount.setMortgage(mortgage);
        if (balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        map.put("fromaccount", fromaccount);
        return map;
    }

    public Map<String, Account> UpdateCancelMortgage(Account fromaccount, Transaction transaction) {
        Map<String, Account> map = new HashMap<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.getFee();
        if (balance < 0) {
            return null;
        }
        balance += transaction.amount;
        //to-
        long mortgage;
        mortgage = fromaccount.getMortgage();
        mortgage -= transaction.amount;
        fromaccount.setMortgage(mortgage);
        if (mortgage < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        map.put("fromaccount", fromaccount);
        return map;
    }

    public Map<String, Account> UpdateCancelVote(Account fromaccount, Account votetoccount, Transaction transaction) {
        Map<String, Account> map = new HashMap<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.getFee();
        if (balance < 0) {
            return null;
        }
        balance += transaction.amount;
        //to-
        long vote;
        if (Arrays.equals(fromaccount.getPubkeyHash(), votetoccount.getPubkeyHash())) {//撤回自己投给自己的投票
            vote = fromaccount.getVote();
            vote -= transaction.amount;
            fromaccount.setVote(vote);
        } else {
            vote = votetoccount.getVote();
            vote -= transaction.amount;
            votetoccount.setVote(vote);
            map.put("toaccount", votetoccount);
        }
        if (vote < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        map.put("fromaccount", fromaccount);
        return map;
    }


    public Account UpdateOtherAccount(Account fromaccount, Transaction transaction) {
        boolean state = false;
        long balance = fromaccount.getBalance();
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
            if (balance < 0) {
                state = true;
            }
            balance += transaction.amount;
        } else if (transaction.type == 12) {//本金
            balance -= transaction.getFee();
            if (balance < 0) {
                state = true;
            }
            balance += transaction.amount;
            long incubatecost = fromaccount.getIncubatecost();
            incubatecost -= transaction.amount;
            if (incubatecost < 0) {
                state = true;
            }
            fromaccount.setIncubatecost(incubatecost);
        } else if (transaction.type == 14) {//抵押
            balance -= transaction.getFee();
            balance -= transaction.amount;
            long mortgage = fromaccount.getMortgage();
            mortgage += transaction.amount;
            fromaccount.setMortgage(mortgage);
        } else if (transaction.type == 15) {//撤回抵押
            balance -= transaction.getFee();
            if (balance < 0) {
                state = true;
            }
            balance += transaction.amount;
            long mortgage = fromaccount.getMortgage();
            mortgage -= transaction.amount;
            if (mortgage < 0) {
                state = true;
            }
            fromaccount.setMortgage(mortgage);
        }
        if (state || balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        return fromaccount;
    }

    public Incubator UpdateIncubtor(Map<byte[], Incubator> map, Transaction transaction, long hieght) {
        Incubator incubator = map.get(Hex.encodeHexString(transaction.payload));
        if (transaction.type == 10 || transaction.type == 11) {
            incubator = merkleRule.UpdateExtIncuator(transaction, hieght, incubator);
        }
        if (transaction.type == 12) {
            incubator = merkleRule.UpdateCostIncubator(incubator, hieght);
        }
        return incubator;
    }

    public VerifyHatch updateHatch(AccountState accountState,Transaction transaction,long nHeight) {
        VerifyHatch verifyHatch=new VerifyHatch();
        Map<byte[], Incubator> map = null;
        if (transaction.type == 10) {
            map = accountState.getInterestMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            if (incubator.getInterest_amount() < 0 || incubator.getLast_blockheight_interest() > nHeight) {
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        } else if (transaction.type == 11) {
            map = accountState.getShareMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            if (incubator.getShare_amount() < 0 || incubator.getLast_blockheight_share() > nHeight) {
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setShareMap(map);
        } else if (transaction.type == 12) {
            map = accountState.getInterestMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            if(incubator.getInterest_amount() !=0 ){
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        }
        verifyHatch.setAccountState(accountState);
        return verifyHatch;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class VerifyHatch {
        private AccountState accountState;
        private boolean state;
    }
    public boolean updateWaitCount(String publicKeyHash,long nonce){
        if(waitCount.IsExist(publicKeyHash,nonce)){
            if(waitCount.updateNonce(publicKeyHash)){
                return true;//单个节点最长旷工数量的7个区块，可以加入
            }
        }else{
            waitCount.add(publicKeyHash,nonce);
        }
        return false;
    }
}
