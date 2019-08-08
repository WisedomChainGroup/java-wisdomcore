package org.wisdom.sync;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.core.account.Transaction;
import org.wisdom.p2p.Context;
import org.wisdom.p2p.PeerServer;
import org.wisdom.p2p.Plugin;
import org.wisdom.p2p.WisdomOuterClass;
import org.wisdom.service.CommandService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Component
public class TransactionHandler implements Plugin {
    private PeerServer server;
    private static final int CACHE_SIZE = 64;
    private ConcurrentMap<String, Boolean> transactionCache;

    @Autowired
    private CommandService commandService;

    public TransactionHandler() {
        this.transactionCache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(CACHE_SIZE).build();
    }

    @Override
    public void onMessage(Context context, PeerServer server) {
        if (context.getPayload().getCode() != WisdomOuterClass.Code.TRANSACTIONS) {
            return;
        }
        WisdomOuterClass.Transactions txs = (WisdomOuterClass.Transactions) context.getPayload().getBody();
        txs.getTransactionsList()
                .stream()
                .map(Utils::parseTransaction).forEach(t -> {
            byte[] traninfo = t.toRPCBytes();
            commandService.verifyTransfer(traninfo);
        });
        context.relay();
    }

    @Override
    public void onStart(PeerServer server) {
        this.server = server;
    }

    public void broadcastTransactions(List<Transaction> txs) {
        Optional.ofNullable(server)
                .ifPresent(s -> {
                    WisdomOuterClass.Transactions.Builder builder = WisdomOuterClass.Transactions.newBuilder();
                    txs.stream().map(Utils::encodeTransaction).forEach(builder::addTransactions);
                    s.broadcast(builder.build());
                });
    }
}
