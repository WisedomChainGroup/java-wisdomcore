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

package org.wisdom.service.Impl;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;

import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.db.StateDB;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.pool.AdoptTransPool;
import org.wisdom.service.CommandService;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.RateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    AccountDB accountDB;

    @Autowired
    IncubatorDB incubatorDB;

    @Autowired
    RateTable rateTable;

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    TransactionCheck transactionCheck;

    @Autowired
    Configuration configuration;

    @Autowired
    StateDB stateDB;


    @Override
    public APIResult verifyTransfer(byte[] transfer) {
        APIResult apiResult = new APIResult();
        Transaction tran;
        try {
            apiResult = transactionCheck.TransactionFormatCheck(transfer);
            if (apiResult.getCode() == 5000) {
                return apiResult;
            }
            tran = (Transaction) apiResult.getData();
            Account account = accountDB.selectaccount(RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from)));
            if (account == null) {
                apiResult.setCode(5000);
                apiResult.setMessage("The from account does not exist");
                return apiResult;
            }
            if (tran.type == Transaction.Type.EXIT_MORTGAGE.ordinal()) {
                Block block = stateDB.getBestBlock();
                List<String> list = stateDB.getProposersFactory().getProposers(block);
                byte[] fromPublicHash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
                if (list.size() > 0 && list.contains(Hex.encodeHexString(fromPublicHash))) {
                    apiResult.setCode(5000);
                    apiResult.setMessage("The miner cannot withdraw the mortgage");
                    return apiResult;
                }
            }
            Incubator incubator = null;
            if (tran.type == 0x0a || tran.type == 0x0b || tran.type == 0x0c) {
                incubator = incubatorDB.selectIncubator(tran.payload);
            }
            apiResult = transactionCheck.TransactionVerify(tran, account, incubator);
            if (apiResult.getCode() == 5000) {
                return apiResult;
            }
            //超过queued上限
            int index = adoptTransPool.size();
            if ((index++) > configuration.getMaxqueued()) {
                apiResult.setCode(5000);
                apiResult.setMessage("The node memory is full, please try again later");
                return apiResult;
            }
            adoptTransPool.add(Collections.singletonList(tran));
            apiResult.setData(tran);
        } catch (Exception e) {
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        }
        return apiResult;
    }

    @Override
    public Object getTransactionList(int height, int type) {
        List<Map<String, Object>> transactionList = accountDB.getTranList(height, type);
        return APIResult.newFailResult(2000, "SUCCESS", transactionList);
    }

    @Override
    public Object getTransactionBlcok(byte[] blockhash, int type) {
        List<Map<String, Object>> transactionList = accountDB.getTranBlockList(blockhash, type);
        return APIResult.newFailResult(2000, "SUCCESS", transactionList);
    }
}
