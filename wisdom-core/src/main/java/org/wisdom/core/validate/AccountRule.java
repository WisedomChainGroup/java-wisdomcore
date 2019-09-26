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
import org.wisdom.core.WhitelistTransaction;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
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

    @Autowired
    WhitelistTransaction whitelistTransaction;

    private boolean validateIncubator;

    @Override
    public Result validateBlock(Block block) {
        Set<String> froms = new HashSet<>();
        byte[] parenthash=block.hashPrevBlock;
        List<byte[]> pubhashlist=block.getFromhashList(block);
        Map<String,AccountState> map=stateDB.getAccounts(parenthash,pubhashlist);
        List<Transaction> transactionList=new ArrayList<>();
//        boolean result=true;
        if (block.nHeight > 0) {
            for (Transaction tx : block.body) {
                if(whitelistTransaction.IsUnchecked(tx.getHashHexString())){
                    continue;
                }
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
/*                        uncheckedTrans.add(tx.getHashHexString(),apiResult.getMessage());
                        result=false;
                        continue;*/
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
                    }
                    AccountState accountState;
                    if(map.containsKey(publichash)){
                        accountState=map.get(publichash);
                    }else{
//                        uncheckedTrans.add(tx.getHashHexString(),apiResult.getMessage());
//                        result=false;
//                        continue;
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Cannot query the account for the from " );
                    }
                    Account account=accountState.getAccount();
                    Map<String,Incubator> interestMap=null;
                    if(tx.type==0x0a || tx.type==0x0c){
                        interestMap=accountState.getInterestMap();
                    }else if(tx.type==0x0b){
                        interestMap=accountState.getShareMap();
                    }
                    Incubator forkincubator=null;
                    if(interestMap!=null){
                        forkincubator=interestMap.get(Hex.encodeHexString(tx.payload));
//                        if(forkincubator==null){
//                            System.out.println("Transaction failed ,tx:"+Hex.encodeHexString(tx.getHash())+"--->Incubator:"+Hex.encodeHexString(tx.payload));
//                        }
                    }
                    //数据校验
                    apiResult=transactionCheck.TransactionVerify(tx,account, forkincubator);
                    if(apiResult.getCode()==5000){
                        peningTransPool.removeOne(publichash,tx.nonce);
//                        uncheckedTrans.add(tx.getHashHexString(),apiResult.getMessage());
//                        result=false;
//                        continue;
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
                    }
                    //更新Account账户
                    if(tx.type==Transaction.Type.TRANSFER.ordinal()
                            || tx.type==Transaction.Type.VOTE.ordinal()
                            || tx.type==Transaction.Type.EXIT_VOTE.ordinal()){//转账、投票、撤回投票
                        AccountState toaccountState;
                        Account toaccount;
                        String tohash=Hex.encodeHexString(tx.to);
                        if(map.containsKey(tohash)){
                            toaccountState=map.get(tohash);
                            toaccount=toaccountState.getAccount();
                        }else{
                            toaccount=new Account(0,tx.to,0,0,0,0,0);
                        }
                        List<Account> list = null;
                        if(tx.type==1){
                            list=packageMiner.updateTransfer(account,toaccount,tx);
                        }else if(tx.type==2){
                            list=packageMiner.updateVote(account,toaccount,tx);
                        }else if(tx.type==3){
                            list=packageMiner.UpdateCancelVote(account,toaccount,tx);
                        }
                        if(list==null){
//                            uncheckedTrans.add(tx.getHashHexString(),"Update account cannot be null");
//                            result=false;
//                            continue;
                            return Result.Error("Transaction validation failed ,"+Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null" );
                        }
                        list.stream().forEach(a->{
                            accountState.setAccount(a);
                            map.put(publichash,accountState);
                        });
                    }else {//其他事务
                        Account otheraccount=packageMiner.UpdateOtherAccount(account,tx);
                        if(otheraccount==null){
//                            uncheckedTrans.add(tx.getHashHexString(),"Update account cannot be null");
//                            result=false;
//                            continue;
                            return Result.Error("Transaction validation failed ,"+Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null" );
                        }
                        accountState.setAccount(otheraccount);

                        Map<String,Incubator> maps=null;
                        if(tx.type==10){
                            maps=accountState.getInterestMap();
                            Incubator incubator=packageMiner.UpdateIncubtor(maps,tx,block.nHeight);
                            maps.put(Hex.encodeHexString(tx.payload),incubator);
                            accountState.setInterestMap(maps);
                        }else if(tx.type==11){
                            maps=accountState.getShareMap();
                            Incubator incubator=packageMiner.UpdateIncubtor(maps,tx,block.nHeight);
                            maps.put(Hex.encodeHexString(tx.payload),incubator);
                            accountState.setShareMap(maps);
                        }else if(tx.type==12){
                            maps=accountState.getInterestMap();
                            Incubator incubator=packageMiner.UpdateIncubtor(maps,tx,block.nHeight);
                            maps.put(Hex.encodeHexString(tx.payload),incubator);
                            accountState.setInterestMap(maps);
                        }
                        map.put(publichash,accountState);
                    }
                }
                transactionList.add(tx);
            }
//            if(!result){
//                return Result.Error("Transaction validation failed");
//            }
            peningTransPool.updatePool(transactionList,1,block.nHeight);
        }
        return Result.SUCCESS;
    }

    public AccountRule(@Value("${node-character}") String character) {
        this.validateIncubator = !character.equals("exchange");
    }

//    public Account updateAccount(Account account,Transaction tx) {
//        long balance = account.getBalance();
//        if (tx.type == 3) {//存证事务,只需要扣除手续费
//            balance -= tx.getFee();
//        } else if (tx.type == 9) {//孵化事务
//            balance -= tx.getFee();
//            balance -= tx.amount;
//            long incubatecost = account.getIncubatecost();
//            incubatecost += tx.amount;
//            account.setIncubatecost(incubatecost);
//        } else if (tx.type == 10 || tx.type == 11) {//提取利息、分享
//            balance -= tx.getFee();
//            balance += tx.amount;
//        } else if (tx.type == 12) {//本金
//            balance -= tx.getFee();
//            balance += tx.amount;
//            long incubatecost = account.getIncubatecost();
//            incubatecost -= tx.amount;
//            account.setIncubatecost(incubatecost);
//        }
//        account.setBalance(balance);
//        return account;
//    }
}
