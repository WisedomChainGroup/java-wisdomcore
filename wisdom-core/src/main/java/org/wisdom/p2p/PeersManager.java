package org.wisdom.p2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 邻居节点管理中间件
@Component
public class PeersManager implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(PeersManager.class);

    @Override
    public void onMessage(Context context, PeerServer server) {
        switch (context.getPayload().code){
            case PING:
                onPing(context, server);
                break;
            case PONG:
                onPong(context, server);
            case LOOK_UP:
                onLookup(context, server);
            case PEERS:
                onPeers(context, server);
        }
    }

    @Override
    public void onStart(PeerServer server) {

    }

    private void onPing(Context context, PeerServer server) {
        context.response(WisdomOuterClass.Pong.newBuilder().build());
        server.pendPeer(context.getPayload().remote);
    }

    private void onPong(Context context, PeerServer server) {
        server.keepPeer(context.getPayload().remote);
    }

    private void onLookup(Context context, PeerServer server) {
        List<String> peers = new ArrayList<>();
        for (Peer p : server.getPeers()) {
            peers.add(p.toString());
        }
        context.response(WisdomOuterClass.Peers.newBuilder().addAllPeers(peers).build());
    }

    private void onPeers(Context context, PeerServer server) {
        WisdomOuterClass.Ping ping = WisdomOuterClass.Ping.newBuilder().build();
        try {
            for (String p : context.getPayload().getPeers().getPeersList()) {
                server.dial(new Peer(p), ping);
            }
        } catch (Exception e) {
            logger.error("parse peer fail");
        }
    }
}
