package org.wisdom.p2p;

import com.google.protobuf.AbstractMessage;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.sync.SyncManager;
import org.wisdom.sync.TransactionHandler;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author sal 1564319846@qq.com
 * wisdom protocol implementation
 */
@Component
@ConditionalOnProperty(name = "p2p.mode", havingValue = "grpc")
public class PeerServer extends WisdomGrpc.WisdomImplBase {

    private static final int HALF_RATE = 60;

    private static final int MAX_PEERS_PER_PING = 6;


    private static final WisdomOuterClass.Ping PING = WisdomOuterClass.Ping.newBuilder().build();
    private static final WisdomOuterClass.Lookup LOOKUP = WisdomOuterClass.Lookup.newBuilder().build();
    private static final WisdomOuterClass.Nothing NOTHING = WisdomOuterClass.Nothing.newBuilder().build();

    private Server server;
    private static final Logger logger = LoggerFactory.getLogger(PeerServer.class);
    private static final int MAX_TTL = 8;
    private List<Plugin> pluginList;

    @Autowired
    private PeersStorage peersCache;

    @Autowired
    private MessageFilter filter;

    @Autowired
    private PeersManager pmgr;

    @Autowired
    private MessageLogger messageLogger;

    @Autowired
    private SyncManager syncManager;

    @Autowired
    private TransactionHandler transactionHandler;

    @Autowired
    private MerkleHandler merkleHandler;

    @Autowired
    private GRPCClient gRPCClient;

    @Value("${p2p.enable-discovery}")
    private boolean enableDiscovery;

    public PeerServer(
    ) throws Exception {
        pluginList = new ArrayList<>();
    }

    public PeerServer use(Plugin plugin) {
        pluginList.add(plugin);
        return this;
    }

    /**
     * 加载插件，启动服务
     */
    @PostConstruct
    public void init() throws Exception {
        this.use(messageLogger)
                .use(filter)
                .use(syncManager)
                .use(transactionHandler)
                .use(pmgr)
                .use(merkleHandler);
        gRPCClient.withSelf(peersCache.getSelf());
        startListening();
    }

    public void startListening() throws Exception {
        logger.info("peer server is listening on " +
                Peer.PROTOCOL_NAME + "://" +
                Hex.encodeHexString(peersCache.getSelf().privateKey.getEncoded()) +
                Hex.encodeHexString(peersCache.getSelf().peerID) + "@" + peersCache.getSelf().hostPort());
        logger.info("provide address to your peers to connect " +
                Peer.PROTOCOL_NAME + "://" +
                Hex.encodeHexString(peersCache.getSelf().peerID) +
                "@" + peersCache.getSelf().hostPort());
        for (Plugin p : pluginList) {
            p.onStart(this);
        }
        this.server = ServerBuilder.forPort(peersCache.getSelf().port).addService(this).build().start();
    }

    @Scheduled(fixedRate = HALF_RATE * 1000)
    public void resolve() {
        peersCache.getUnresolved().forEach(h -> {
            dialWithTTL(h.getHost(), h.getPort(), 1, PING);
        });
    }

    @Scheduled(fixedRate = HALF_RATE * 1000)
    public void startHalf() {
        if (!enableDiscovery) {
            return;
        }

        peersCache.half();

        for (Peer p : peersCache.getPeers(MAX_PEERS_PER_PING)) {
            dial(p, PING); // keep alive
        }

        if (peersCache.isFull() || !enableDiscovery) {
            return;
        }

        // discover peers when bucket is not full
        for (Peer p : peersCache.getPeers(MAX_PEERS_PER_PING)) {
            dial(p, LOOKUP);
        }

        for (Peer p : peersCache.popPended()) {
            dial(p, WisdomOuterClass.Ping.newBuilder().build());
        }
    }

    public List<Peer> getBootstraps() {
        return peersCache.getBootstraps();
    }

    public PeersCache getPeersCache() {
        return peersCache;
    }

    public Peer getSelf() {
        return peersCache.getSelf();
    }

    private WisdomOuterClass.Message onMessage(WisdomOuterClass.Message message) {
        try {
            Payload payload = new Payload(message);
            if (peersCache.getBlocked().contains(payload.getRemote())) {
                logger.error("the remote had been blocked");
                return gRPCClient.buildMessage(1, NOTHING);
            }
            Context ctx = new Context();
            ctx.payload = payload;
            for (Plugin p : pluginList) {
                p.onMessage(ctx, this);
                if (ctx.broken) {
                    break;
                }
            }
            if (ctx.remove) {
                peersCache.removePeer(payload.getRemote());
            }
            if (ctx.pending) {
                peersCache.pend(payload.getRemote());
            }
            if (ctx.keep) {
                peersCache.keepPeer(payload.getRemote());
            }
            if (ctx.block) {
                peersCache.blockPeer(payload.getRemote());
            }
            if (ctx.relay) {
                relay(payload);
            }
            if (ctx.response != null) {
                return gRPCClient.buildMessage(1, ctx.response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("fail to parse message");
        }
        return gRPCClient.buildMessage(1, NOTHING);
    }

    @Override
    public void entry(WisdomOuterClass.Message request, StreamObserver<WisdomOuterClass.Message> responseObserver) {
        WisdomOuterClass.Message resp = onMessage(request);
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    private void dialWithTTL(Peer peer, long ttl, AbstractMessage msg) {
        gRPCClient.dialWithTTL(peer.host, peer.port, ttl, msg).handleAsync((m, e) -> {
            if (e == null) {
                return m;
            }
            logger.error("cannot connect to to peer " + peer.toString());
            if (!enableDiscovery) {
                return m;
            }
            peersCache.half(peer);
            logger.error("half " + peer.toString() + " score");
            return m;
        }).thenApplyAsync((m) -> m == null ? null : onMessage(m));
    }

    private CompletableFuture<WisdomOuterClass.Message> dialWithTTL(String host, int port, long ttl, AbstractMessage msg) {
        return gRPCClient.dialWithTTL(host, port, ttl, msg).thenApplyAsync(this::onMessage);
    }

    public void dial(Peer p, AbstractMessage msg) {
        dialWithTTL(p, 1, msg);
    }

    public void broadcast(AbstractMessage msg) {
        for (Peer p : getPeers()) {
            dialWithTTL(p, MAX_TTL, msg);
        }
    }

    public void relay(Payload payload) {
        if (payload.getTtl() <= 0) {
            return;
        }
        for (Peer p : getPeers()) {
            if (p.equals(payload.getRemote())) {
                continue;
            }
            try {
                dialWithTTL(p, payload.getTtl() - 1, payload.getBody());
            } catch (Exception e) {
                logger.error("parse body fail");
            }
        }
    }

    public List<Peer> getPeers() {
        return peersCache.getPeers();
    }

    public boolean hasPeer(Peer peer) {
        return peersCache.hasPeer(peer);
    }


    public String getNodePubKey() {
        return Peer.PROTOCOL_NAME + "://" +
                Hex.encodeHexString(getSelf().peerID) +
                "@" + getSelf().hostPort();
    }

    public String getIP() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(address).getHostAddress();
    }

    public int getPort() {
        return getSelf().port;
    }

    void pend(Peer peer) {
        this.peersCache.pend(peer);
    }
}
