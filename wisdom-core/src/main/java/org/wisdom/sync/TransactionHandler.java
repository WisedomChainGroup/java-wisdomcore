package org.wisdom.sync;

import org.springframework.stereotype.Component;
import org.wisdom.core.account.Transaction;
import org.wisdom.p2p.Context;
import org.wisdom.p2p.PeerServer;
import org.wisdom.p2p.Plugin;

import java.util.List;

@Component
public class TransactionHandler implements Plugin {
    private PeerServer server;

    @Override
    public void onMessage(Context context, PeerServer server) {

    }

    @Override
    public void onStart(PeerServer server) {
        this.server = server;
    }

    public void broadcastTransactions(List<Transaction> txs) {
    }
}
