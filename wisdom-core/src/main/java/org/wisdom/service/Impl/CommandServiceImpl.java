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

import org.wisdom.ApiResult.APIResult;
import org.wisdom.Controller.ConsensusClient;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.TransactionPool;

import org.wisdom.pool.AdoptTransPool;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.protobuf.tcp.ProtocolModel;
import org.wisdom.service.CommandService;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    Configuration configuration;

    @Autowired
    AccountDB accountDB;

    @Autowired
    IncubatorDB incubatorDB;

    @Autowired
    TransactionPool transactionPool;

    @Autowired
    RateTable rateTable;

    @Autowired
    ConsensusClient client;

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    PeningTransPool peningTransPool;

    @Override
    public APIResult verifyTransfer(byte[] transfer) {
        APIResult apiResult=new APIResult();
        try {
            long nowheight=wisdomBlockChain.currentHeader().nHeight;
            apiResult= TransactionCheck.TransactionVerifyResult(transfer,wisdomBlockChain,configuration,accountDB,incubatorDB,rateTable,nowheight,true,true);
            if(apiResult.getCode()==5000){
                return apiResult;
            }else{//pool校验
                List<String> list= (List<String>) apiResult.getData();
                if(adoptTransPool.hasExistQueued(list.get(0),list.get(1))){
                    if(peningTransPool.hasExist(list.get(2))){
                        apiResult.setData(null);
                    }else{
                        apiResult.setCode(5000);
                        apiResult.setMessage("Error sending same nonce transaction to From address");
                    }
                }else{
                    apiResult.setCode(5000);
                    apiResult.setMessage("Error sending same nonce transaction to From address");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.setCode(5000);
            apiResult.setMessage("Exception error");
            return apiResult;
        }
        ProtocolModel.Transaction tranproto= Transaction.changeProtobuf(transfer);
        Transaction tran=Transaction.fromProto(tranproto);
        adoptTransPool.add(Collections.singletonList(tran));
//        transactionPool.add(tran);
        client.broascastTransactions(Collections.singletonList(tran));
        return apiResult;
    }

    @Override
    public Object getTransactionList(int height,int type) {
        List<Map<String,Object>> transactionList=accountDB.getTranList(height,type);
        return APIResult.newFailResult(2000,"SUCCESS",transactionList);
    }

    @Override
    public Object getTransactionBlcok(byte[] blockhash, int type) {
        List<Map<String,Object>> transactionList=accountDB.getTranBlockList(blockhash,type);
        return APIResult.newFailResult(2000,"SUCCESS",transactionList);
    }
}