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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.db.AccountState;
import org.wisdom.db.WisdomRepository;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.pool.AdoptTransPool;
import org.wisdom.service.CommandService;
import org.wisdom.util.Address;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    WisdomBlockChain bc;

    @Autowired
    RateTable rateTable;

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    TransactionCheck transactionCheck;

    @Autowired
    Configuration configuration;

    @Autowired
    WisdomRepository repository;

    @Autowired
    JSONEncodeDecoder jsonEncodeDecoder;


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
            Optional<AccountState> accountStateOptional = repository.getConfirmedAccountState(Address.publicKeyToHash(tran.from));
            if (!accountStateOptional.isPresent()) {
                apiResult.setCode(5000);
                apiResult.setData("");
                apiResult.setMessage("The from account does not exist");
                return apiResult;
            }
            AccountState accountState = accountStateOptional.get();
            if (tran.type == Transaction.Type.EXIT_MORTGAGE.ordinal()) {
                List<String> list = repository.getLatestTopCandidates()
                        .stream().map(x -> x.getPublicKeyHash().toHex())
                        .collect(Collectors.toList());
                byte[] fromPublicHash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
                if (list.size() > 0 && list.contains(Hex.encodeHexString(fromPublicHash))) {
                    apiResult.setCode(5000);
                    apiResult.setData("");
                    apiResult.setMessage("The miner cannot withdraw the mortgage");
                    return apiResult;
                }
            }
            Incubator incubator = getIncubator(accountState, tran.type, tran.payload);
            apiResult = transactionCheck.TransactionVerify(tran, accountState.getAccount(), incubator);
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
            if (tran.getFee() >= configuration.getMin_procedurefee()) {
                //TODO 判断TO的类型并放进去
                adoptTransPool.add(Collections.singletonList(tran));
            }
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
        List<Transaction> txs = bc.getBlockByHeight(height).body.stream()
                .filter(x -> x.type == type).collect(Collectors.toList());
        return jsonEncodeDecoder.encodeBlockBody(txs);
    }

    @Override
    public Object getTransactionBlock(byte[] blockHash, int type) {
        List<Transaction> txs = bc.getBlockByHash(blockHash).body.stream()
                .filter(x -> x.type == type).collect(Collectors.toList());
        return jsonEncodeDecoder.encodeBlockBody(txs);
    }

    public static Incubator getIncubator(AccountState accountState, int type, byte[] payload) {
        if (type == 0x0a || type == 0x0c) {
            Map<byte[], Incubator> incubatorMap = accountState.getInterestMap();
            if (incubatorMap.containsKey(payload)) {
                return incubatorMap.get(payload);
            }
        } else if (type == 0x0b) {
            Map<byte[], Incubator> ShareMap = accountState.getShareMap();
            if (ShareMap.containsKey(payload)) {
                return ShareMap.get(payload);
            }
        }
        return null;
    }
}
