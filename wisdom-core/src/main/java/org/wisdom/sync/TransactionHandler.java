package org.wisdom.sync;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.springframework.stereotype.Component;
import org.wisdom.core.account.Transaction;
import org.wisdom.p2p.Context;
import org.wisdom.p2p.PeerServer;
import org.wisdom.p2p.Plugin;
import org.wisdom.p2p.WisdomOuterClass;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Component
public class TransactionHandler implements Plugin {
    private PeerServer server;
    private static final int CACHE_SIZE = 64;
    private ConcurrentMap<String, Boolean> transactionCache;

    public TransactionHandler() {
        this.transactionCache = new ConcurrentLinkedHashMap.Builder<String, Boolean>().maximumWeightedCapacity(CACHE_SIZE).build();
    }

    @Override
    public void onMessage(Context context, PeerServer server) {
        if (context.getPayload().getCode() != WisdomOuterClass.Code.TRANSACTIONS){
            return;
        }
        WisdomOuterClass.Transactions txs = (WisdomOuterClass.Transactions) context.getPayload().getBody();
        txs.getTransactionsList()
                .stream()
                .map(Utils::parseTransaction).forEach(y -> {
                    
        });
    }

    @Override
    public void onStart(PeerServer server) {
        this.server = server;
    }

    public void broadcastTransactions(List<Transaction> txs) {
        Optional.ofNullable(server)
                .ifPresent(s -> {

                });
    }

    private void onTransaction(Context context, PeerServer server) {
        WisdomOuterClass.Transaction tx = context.getPayload().getTransaction();
        Transaction t = Utils.parseTransaction(tx);
        if (transactionCache.containsKey(t.getHashHexString())) {
            return;
        }
        transactionCache.put(t.getHashHexString(), true);
        // TODO: 收到广播后的事务要进行处理
        context.relay();
    }
}
