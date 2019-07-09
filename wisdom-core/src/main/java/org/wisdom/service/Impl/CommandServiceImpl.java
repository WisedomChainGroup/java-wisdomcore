package org.wisdom.service.Impl;

import org.wisdom.ApiResult.APIResult;
import org.wisdom.Controller.ConsensusClient;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.TransactionPool;

import org.wisdom.protobuf.tcp.ProtocolModel;
import org.wisdom.service.CommandService;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

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

    @Override
    public APIResult verifyTransfer(byte[] transfer) {
        APIResult apiResult=new APIResult();
        try {
            long nowheight=wisdomBlockChain.currentHeader().nHeight;
            apiResult= TransactionCheck.TransactionVerifyResult(transfer,wisdomBlockChain,configuration,accountDB,incubatorDB,rateTable,nowheight,true);
            if(apiResult.getStatusCode()==-1){
                return apiResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.setStatusCode(-1);
            apiResult.setMessage("Data error");
            return apiResult;
        }
        ProtocolModel.Transaction tranproto= Transaction.changeProtobuf(transfer);
        Transaction tran=Transaction.fromProto(tranproto);
        transactionPool.add(tran);
        client.broascastTransactions(Collections.singletonList(tran));
        return apiResult;
    }
}
