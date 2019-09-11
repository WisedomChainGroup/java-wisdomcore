/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.core.validate;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.consensus.pow.PackageMiner;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.db.AccountState;
import org.wisdom.db.StateDB;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.pool.PeningTransPool;

import java.util.*;

// 账户规则校验
// 1. 一个区块内一个只能有一个 from 的事务
// 2. nonce 校验
@Component
public class AccountRule implements BlockRule {
    static final Base64.Encoder encoder = Base64.getEncoder();

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
    PeningTransPool peningTransPool;

    @Autowired
    StateDB stateDB;

    @Autowired
    TransactionCheck transactionCheck;

    @Autowired
    PackageMiner packageMiner;

    private boolean validateIncubator;

    @Override
    public Result validateBlock(Block block) {
        Set<String> froms = new HashSet<>();
        byte[] parenthash=block.hashPrevBlock;
        List<byte[]> pubhashlist=block.getFromhashList(block);
        Map<String,AccountState> map=stateDB.getAccounts(parenthash,pubhashlist);
        if (block.nHeight > 30800) {
            for (Transaction tx : block.body) {
                String key = encoder.encodeToString(tx.from);
                if (froms.contains(key)) {
                    return Result.Error("duplicated account found");
                }
                froms.add(key);
                if (!validateIncubator) {
                    continue;
                }
                // 校验事务
                if (tx.type != Transaction.Type.COINBASE.ordinal()) {
                    byte[] pubkeyhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from));
                    String publichash=Hex.encodeHexString(pubkeyhash);
                    //校验格式
                    APIResult apiResult=transactionCheck.TransactionFormatCheck(tx.toRPCBytes());
                    if(apiResult.getCode()==5000){
                        peningTransPool.removeOne(publichash,tx.nonce);
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
                    }
                    AccountState accountState;
                    if(map.containsKey(publichash)){
                        accountState=map.get(publichash);
                    }else{
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Cannot query the account for the from！" );
                    }
                    Account account=accountState.getAccount();
                    //数据校验
                    apiResult=transactionCheck.TransactionVerify(tx,account);
                    if(apiResult.getCode()==5000){
                        peningTransPool.removeOne(publichash,tx.nonce);
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
                    }
                    //更新Account账户
                    if(tx.type==Transaction.Type.TRANSFER.ordinal()){//转账
                        AccountState toaccountState=new AccountState();
                        Account toaccount;
                        String tohash=Hex.encodeHexString(tx.to);
                        if(map.containsKey(tohash)){
                            toaccountState=map.get(tohash);
                            toaccount=toaccountState.getAccount();
                        }else{
                            toaccount=new Account(0,tx.to,0,0,0,0,0);
                        }
                        List<Account> list=packageMiner.updateTransfer(account,toaccount,tx);

                        /*long balance=account.getBalance();
                        balance-=tx.amount;
                        balance-=tx.getFee();
                        account.setBalance(balance);
                        accountState.setAccount(account);
                        map.put(publichash,accountState);

                        //to

                        long tobalance=toaccount.getBalance();
                        tobalance+=tx.amount;
                        toaccount.setBalance(tobalance);
                        toaccountState.setAccount(toaccount);
                        map.put(tohash,toaccountState);*/
                    }else {//其他事务
                        account=updateAccount(account,tx);
                        accountState.setAccount(account);
                        map.put(publichash,accountState);
                    }
                }
            }
        }
        return Result.SUCCESS;
    }

    public AccountRule(@Value("${node-character}") String character) {
        this.validateIncubator = !character.equals("exchange");
    }

    public Account updateAccount(Account account,Transaction tx) {
        long balance = account.getBalance();
        if (tx.type == 3) {//存证事务,只需要扣除手续费
            balance -= tx.getFee();
        } else if (tx.type == 9) {//孵化事务
            balance -= tx.getFee();
            balance -= tx.amount;
            long incubatecost = account.getIncubatecost();
            incubatecost += tx.amount;
            account.setIncubatecost(incubatecost);
        } else if (tx.type == 10 || tx.type == 11) {//提取利息、分享
            balance -= tx.getFee();
            balance += tx.amount;
        } else if (tx.type == 12) {//本金
            balance -= tx.getFee();
            balance += tx.amount;
            long incubatecost = account.getIncubatecost();
            incubatecost -= tx.amount;
            account.setIncubatecost(incubatecost);
        }
        account.setBalance(balance);
        return account;
    }

    public static void main(String[] args) {
        List<String> s=null;
        s.forEach(s1->System.out.println(s1));
    }
}
