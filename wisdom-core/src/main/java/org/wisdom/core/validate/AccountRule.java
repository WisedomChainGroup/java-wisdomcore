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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.Block;
import org.wisdom.core.WhitelistTransaction;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.db.AccountState;
import org.wisdom.db.StateDB;
import org.wisdom.pool.PeningTransPool;

import java.util.*;

import static org.wisdom.core.account.Transaction.Type.EXIT_VOTE;

// 账户规则校验
// 1. 一个区块内一个只能有一个 from 的事务
// 2. nonce 校验
@Component
public class AccountRule implements BlockRule {
    static final Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    RateTable rateTable;

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    StateDB stateDB;

    @Autowired
    TransactionCheck transactionCheck;

    @Autowired
    WhitelistTransaction whitelistTransaction;

    @Autowired
    MerkleRule merkleRule;

    private boolean validateIncubator;

    @Override
    public Result validateBlock(Block block) {
        byte[] parenthash = block.hashPrevBlock;
        List<byte[]> pubhashlist = block.getFromsPublicKeyHash();
        Map<String, AccountState> map = stateDB.getAccounts(parenthash, pubhashlist);
        if (map == null) {
            return Result.Error("get accounts from database failed");
        }
        Set<String> payloads = new HashSet<>();
        // 一个区块内同一个投票或者抵押只能被撤回一次
        for (Transaction t : block.body) {
            if (
                    t.type != EXIT_VOTE.ordinal() ||
                            t.type != Transaction.Type.EXIT_MORTGAGE.ordinal() || t.payload == null
            ) {
                continue;
            }
            String k = Hex.encodeHexString(t.payload);
            if (payloads.contains(k)) {
                return Result.Error(k + " exit vote or mortgage more than once");
            }
            payloads.add(k);
        }
        if (!validateIncubator) {//交易所、默认模式
            if (block.nHeight > 0) {
                CheckoutTransactions packageCheckOut = new CheckoutTransactions();
                packageCheckOut.init(block, map, peningTransPool, stateDB, transactionCheck, whitelistTransaction, rateTable, merkleRule);
                return packageCheckOut.CheckoutResult();
            }
        }
        return Result.SUCCESS;
    }

    public AccountRule(@Value("${node-character}") String character) {
        this.validateIncubator = !character.equals("exchange");
    }
}

