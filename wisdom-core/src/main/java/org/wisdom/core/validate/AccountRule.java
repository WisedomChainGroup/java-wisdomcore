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
        byte[] parenthash = block.hashPrevBlock;
        List<byte[]> pubhashlist = block.getFromsPublicKeyHash();
        Map<String, AccountState> map = stateDB.getAccounts(parenthash, pubhashlist);
        if (map == null) {
            return Result.Error("get accounts from database failed");
        }
        Map<String, Integer> payloadsCounter = new HashMap<>();

        // 一个区块每个 from 只能有一个撤回事务，避免多次撤回同一个投票
        for (Transaction t : block.body) {
            if (t.type != Transaction.Type.EXIT_VOTE.ordinal()) {
                continue;
            }
            String k = Hex.encodeHexString(t.payload);
            if (!payloadsCounter.containsKey(k)) {
                payloadsCounter.put(k, 0);
            }
            payloadsCounter.put(k, payloadsCounter.get(k) + 1);
        }

        for (String k : payloadsCounter.keySet()) {
            if (payloadsCounter.get(k) > 1) {
                return Result.Error(k + " exit vote more than once");
            }
        }

        List<Transaction> transactionList = new ArrayList<>();
        if (block.nHeight > 0) {
            for (Transaction tx : block.body) {
                if (whitelistTransaction.IsUnchecked(tx.getHashHexString())) {
                    continue;
                }
                if (!validateIncubator) {
                    continue;
                }
                // 校验事务
                if (tx.type != Transaction.Type.COINBASE.ordinal()) {
                    byte[] pubkeyhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from));
                    String publichash = Hex.encodeHexString(pubkeyhash);
                    //校验格式
                    APIResult apiResult = transactionCheck.TransactionFormatCheck(tx.toRPCBytes());
                    if (apiResult.getCode() == 5000) {
                        peningTransPool.removeOne(publichash, tx.nonce);
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
                    }
                    AccountState accountState;
                    if (map.containsKey(publichash)) {
                        accountState = map.get(publichash);
                    } else {
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Cannot query the account for the from ");
                    }
                    Account account = accountState.getAccount();
                    Map<String, Incubator> interestMap = null;
                    if (tx.type == 0x0a || tx.type == 0x0c) {
                        interestMap = accountState.getInterestMap();
                    } else if (tx.type == 0x0b) {
                        interestMap = accountState.getShareMap();
                    }
                    Incubator forkincubator = null;
                    if (interestMap != null) {
                        forkincubator = interestMap.get(Hex.encodeHexString(tx.payload));
                    }
                    //数据校验
                    apiResult = transactionCheck.TransactionVerify(tx, account, forkincubator);
                    if (apiResult.getCode() == 5000) {
                        peningTransPool.removeOne(publichash, tx.nonce);
                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
                    }
                    //更新Account账户
                    if (tx.type == Transaction.Type.TRANSFER.ordinal()
                            || tx.type == Transaction.Type.VOTE.ordinal() || tx.type == Transaction.Type.MORTGAGE.ordinal()) {//转账、投票
                        AccountState toaccountState = null;
                        Account toaccount;
                        String tohash = Hex.encodeHexString(tx.to);
                        if (map.containsKey(tohash)) {
                            toaccountState = map.get(tohash);
                            toaccount = toaccountState.getAccount();
                        } else {
                            toaccount = new Account(0, tx.to, 0, 0, 0, 0, 0);
                        }
                        Map<String, Account> mapaccount = null;
                        if (tx.type == 1) {
                            mapaccount = packageMiner.updateTransfer(account, toaccount, tx);
                        } else if (tx.type == 2) {
                            mapaccount = packageMiner.updateVote(account, toaccount, tx);
                        } else if (tx.type == Transaction.Type.MORTGAGE.ordinal()) {
                            mapaccount = packageMiner.updateMortgage(account, tx);
                        }
                        if (mapaccount == null) {
                            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
                        }
                        if (mapaccount.containsKey("fromaccount")) {
                            accountState.setAccount(mapaccount.get("fromaccount"));
                            map.put(publichash, accountState);
                        } else if (mapaccount.containsKey("toaccount")) {
                            toaccountState.setAccount(mapaccount.get("toaccount"));
                            map.put(tohash, accountState);
                        }
                    } else if (tx.type == Transaction.Type.EXIT_VOTE.ordinal()) {//撤回投票
                        Account votetoaccount;
                        AccountState tovoteaccountState;
                        // 投票没有撤回过
                        if (stateDB.hasPayload(block.hashPrevBlock, tx.payload)){
                            return Result.Error("the vote transaction " + Hex.encodeHexString(tx.payload) + " had been exited");
                        }
                        // 撤回的必须是投票
                        Transaction t = stateDB.getTransaction(block.hashPrevBlock, tx.payload);
                        if (t == null || t.type != Transaction.Type.VOTE.ordinal()){
                            return Result.Error("the transaction " + Hex.encodeHexString(tx.payload) + " is not vote or not exists");
                        }
                        if (t.amount != tx.amount){
                            return Result.Error("the transaction " + Hex.encodeHexString(tx.payload) + "  not equals to vote account");
                        }
                        if (map.containsKey(Hex.encodeHexString(tx.to))) {
                            tovoteaccountState = map.get(Hex.encodeHexString(tx.to));
                            votetoaccount = tovoteaccountState.getAccount();
                        } else {
                            tovoteaccountState = stateDB.getAccount(parenthash, tx.to);
                            votetoaccount = tovoteaccountState.getAccount();
                        }
                        Map<String, Account> cancelaccountList = packageMiner.UpdateCancelVote(account, votetoaccount, tx);
                        if (cancelaccountList == null) {
                            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
                        }
                        if (cancelaccountList.containsKey("fromaccount")) {
                            accountState.setAccount(cancelaccountList.get("fromaccount"));
                            map.put(publichash, accountState);
                        } else if (cancelaccountList.containsKey("toaccount")) {
                            tovoteaccountState.setAccount(cancelaccountList.get("toaccount"));
                            map.put(Hex.encodeHexString(tx.to), accountState);
                        }

                    } else if (tx.type == Transaction.Type.EXIT_MORTGAGE.ordinal()) {//撤回抵押
                        Map<String, Account> cancelAccountList = packageMiner.UpdateCancelMortgage(account, tx);
                        if (cancelAccountList == null) {
                            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
                        }
                        if (cancelAccountList.containsKey("fromaccount")) {
                            accountState.setAccount(cancelAccountList.get("fromaccount"));
                            map.put(publichash, accountState);
                        }
                    } else {//其他事务
                        Account otheraccount = packageMiner.UpdateOtherAccount(account, tx);
                        if (otheraccount == null) {
                            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
                        }
                        accountState.setAccount(otheraccount);

                        Map<String, Incubator> maps = null;
                        if (tx.type == 10) {
                            maps = accountState.getInterestMap();
                            Incubator incubator = packageMiner.UpdateIncubtor(maps, tx, block.nHeight);
                            maps.put(Hex.encodeHexString(tx.payload), incubator);
                            accountState.setInterestMap(maps);
                        } else if (tx.type == 11) {
                            maps = accountState.getShareMap();
                            Incubator incubator = packageMiner.UpdateIncubtor(maps, tx, block.nHeight);
                            maps.put(Hex.encodeHexString(tx.payload), incubator);
                            accountState.setShareMap(maps);
                        } else if (tx.type == 12) {
                            maps = accountState.getInterestMap();
                            Incubator incubator = packageMiner.UpdateIncubtor(maps, tx, block.nHeight);
                            maps.put(Hex.encodeHexString(tx.payload), incubator);
                            accountState.setInterestMap(maps);
                        }
                        map.put(publichash, accountState);
                    }
                }
                transactionList.add(tx);
            }
            peningTransPool.updatePool(transactionList, 1, block.nHeight);
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
