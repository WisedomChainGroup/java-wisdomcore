package org.wisdom.sync;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.account.Transaction;
import org.wisdom.p2p.*;
import org.wisdom.service.CommandService;
import org.wisdom.vm.abi.WASMTXPool;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "sync")
public class TransactionHandler implements Plugin {
    private PeerServer server;
    private static final int CACHE_SIZE = 256;
    private ConcurrentMap<String, Boolean> transactionCache;

    @Autowired
    private CommandService commandService;

    @Autowired
    private TransactionCheck transactionCheck;

    @Autowired
    private WASMTXPool wasmtxPool;

    public TransactionHandler() {
        this.transactionCache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(CACHE_SIZE).build();
    }

    @Override
    public void onMessage(Context context, PeerServer server) {
        if (context.getPayload().getCode() != WisdomOuterClass.Code.TRANSACTIONS) {
            return;
        }
        WisdomOuterClass.Transactions txs = (WisdomOuterClass.Transactions) context.getPayload().getBody();

        String key = Hex.encodeHexString(Utils.getTransactionsHash(txs.getTransactionsList()));
        if (transactionCache.containsKey(key)) {
            return;
        }
        transactionCache.put(key, true);
        txs.getTransactionsList()
                .stream()
                .map(Utils::parseTransaction)
                .forEach(t -> {
                    switch (Transaction.TYPES_TABLE[t.type]){
                        case WASM_CALL:
                        case WASM_DEPLOY: {
                            APIResult res = transactionCheck.TransactionFormatCheck(t.toRPCBytes());
                            if(res.getCode() == APIResult.SUCCESS) {
                                this.wasmtxPool.collect(Collections.singleton(t));
                            }
                            return;
                        }
                    }
                    log.debug("receive transaction {} ", t.getHashHexString());
                    transactionCache.put(t.getHashHexString(), true);
                    byte[] traninfo = t.toRPCBytes();
                    APIResult apiResult = commandService.verifyTransfer(traninfo);
                    if (apiResult.getCode() == 5000) {
                        log.info("transaction Check failure,TxHash=" + Hex.encodeHexString(t.getHash()) + ",message:" + apiResult.getMessage());
                    }
                });
        context.relay();
    }

    @Override
    public void onStart(PeerServer server) {
        this.server = server;
    }

    public void broadcastTransactions(List<Transaction> txs) {
        if (server == null) {
            return;
        }

        List<WisdomOuterClass.Transaction> encoded = txs.stream()
                .map(Utils::encodeTransaction).collect(Collectors.toList());

        WisdomOuterClass.Transactions msg = WisdomOuterClass.Transactions.newBuilder().addAllTransactions(encoded).build();

        List<WisdomOuterClass.Transactions> divided = Util.split(msg);

        divided.stream()
                .filter(o -> {
                    String k = Hex.encodeHexString(
                            Utils.getTransactionsHash(o.getTransactionsList())
                    );
                    if (!transactionCache.containsKey(k)) {
                        transactionCache.put(k, true);
                        return true;
                    }
                    return false;
                })
                .forEach(o ->
                        server.broadcast(o)
                );
    }
}

