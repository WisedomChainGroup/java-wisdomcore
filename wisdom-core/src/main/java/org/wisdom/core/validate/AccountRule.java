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
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.consensus.pow.ValidatorStateFactory;
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
    private ValidatorStateFactory factory;

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

    private boolean validateIncubator;

    @Override
    public Result validateBlock(Block block) {
        Set<String> froms = new HashSet<>();
        byte[] parenthash=block.hashPrevBlock;
        Map<String,AccountState> map=new HashMap<>();
        if (block.nHeight > 30800) {
            long nowheight = wisdomBlockChain.currentHeader().nHeight;
            for (Transaction tx : block.body) {
                String key = encoder.encodeToString(tx.from);
                if (froms.contains(key)) {
                    return Result.Error("duplicated account found");
                }
                froms.add(key);
                if (!validateIncubator) {
                    continue;
                }
                // 校验转账事务
                if (tx.type != Transaction.Type.COINBASE.ordinal()) {
                    byte[] pubkeyhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from));
                    String publichash=Hex.encodeHexString(pubkeyhash);
                    Account account;
//                    if(map.containsKey(publichash)){
////                        AccountState accountState=
////                        account=map.get(publichash);
//                    }else{
//                        AccountState accountState=stateDB.getAccountUnsafe(parenthash,pubkeyhash);
//                        account=accountState.getAccount();
//                        if(account==null){
//                            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": The previous block did not track the account status!" );
//                        }
//                    }
//                    APIResult apiResult=transactionCheck.TransactionVerify(tx,account);
//                    if(apiResult.getCode()==5000){
//                        peningTransPool.removeOne(publichash,tx.nonce);
//                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
//                    }
                    //更新Account状态

//                    byte[] transfer = tx.toRPCBytes();
//                    APIResult apiResult = TransactionCheck.TransactionVerifyResult(transfer, wisdomBlockChain, configuration, accountDB, incubatorDB, rateTable, nowheight, false, false);
//                    if (apiResult.getCode() == 5000) {
//                        String keys = peningTransPool.getKeyTrans(tx);
//                        String fromhex=Hex.encodeHexString(tx.from);
//                        peningTransPool.removeOne(keys,fromhex,tx.nonce);
//                        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
//                    }
                }
            }
        }
        return Result.SUCCESS;
    }

    // validateBlock 内已经包含了对 nonce 的校验
    public Result validateTransaction(ValidatorState state, Transaction transaction) {
        if (transaction.type == Transaction.Type.COINBASE.ordinal()) {
            return Result.SUCCESS;
        }
        // nonce 校验
        if (state.getNonceFromPublicKey(transaction.from) + 1 != transaction.nonce) {
            return Result.Error("wrong nonce = " + transaction.nonce + " required = " + state.getNonceFromPublicKey(transaction.from) + 1);
        }
        return Result.SUCCESS;
    }

    public AccountRule(@Value("${node-character}") String character) {
        this.validateIncubator = !character.equals("exchange");
    }
}
