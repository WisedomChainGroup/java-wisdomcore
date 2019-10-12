package org.wisdom.p2p;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.codec.binary.Hex;
import org.omg.CORBA.TIMEOUT;
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
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sal 1564319846@qq.com
 * wisdom protocol implementation
 */
@Component
@ConditionalOnProperty(name = "p2p.mode", havingValue = "grpc")
public class PeerServer extends WisdomGrpc.WisdomImplBase {
    private static final int HALF_RATE = 60;

    private static final int MAX_PEERS_PER_PING = 6;

    private static final int RPC_TIMEOUT = 5;

    private static final WisdomOuterClass.Ping PING = WisdomOuterClass.Ping.newBuilder().build();
    private static final WisdomOuterClass.Lookup LOOKUP = WisdomOuterClass.Lookup.newBuilder().build();
    private static final WisdomOuterClass.Nothing NOTHING = WisdomOuterClass.Nothing.newBuilder().build();

    private Server server;
    private static final Logger logger = LoggerFactory.getLogger(PeerServer.class);
    private static final int MAX_TTL = 8;
    private AtomicLong nonce;
    private List<Plugin> pluginList;
    private PeersCacheWrapper peersCache;

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

    @Value("${p2p.enable-discovery}")
    private boolean enableDiscovery;

    public PeerServer(
            @Value("${p2p.address}") String self,
            @Value("${p2p.bootstraps}") String bootstraps,
            @Value("${p2p.trustedpeers}") String trusted,
            @Value("${p2p.enable-discovery}") boolean enableDiscovery
    ) throws Exception {
        nonce = new AtomicLong();
        pluginList = new ArrayList<>();
        this.peersCache = new PeersCacheWrapper(self, bootstraps, trusted, enableDiscovery);
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
            dial(h.getHost(), h.getPort(), PING);
        });
    }

    @Scheduled(fixedRate = HALF_RATE * 1000)
    public void startHalf() {
        if (!enableDiscovery) {
            return;
        }

        peersCache.half();

        for(Peer p: peersCache.getPeers(MAX_PEERS_PER_PING)){
            dial(p, PING); // keep alive
        }

        if(peersCache.isFull()){
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

    public Set<Peer> getBootstraps(){
        return peersCache.getBootstraps();
    }

    public Peer getSelf() {
        return peersCache.getSelf();
    }

    private WisdomOuterClass.Message onMessage(WisdomOuterClass.Message message) {
        try {
            Payload payload = new Payload(message);
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
                return buildMessage(1, ctx.response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("fail to parse message");
        }
        return buildMessage(1, NOTHING);
    }

    @Override
    public void entry(WisdomOuterClass.Message request, StreamObserver<WisdomOuterClass.Message> responseObserver) {
        WisdomOuterClass.Message resp = onMessage(request);
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    private void grpcCall(String host, int port, WisdomOuterClass.Message msg) {
        ManagedChannel ch = ManagedChannelBuilder.forAddress(host, port
        ).usePlaintext().build();

        try {
            ch.awaitTermination(RPC_TIMEOUT, TimeUnit.SECONDS);
        }catch (Exception e){
            logger.error("http2 timeout");
            return;
        }

        WisdomGrpc.WisdomStub stub = WisdomGrpc.newStub(
                ch);
        stub.entry(msg, new StreamObserver<WisdomOuterClass.Message>() {
            @Override
            public void onNext(WisdomOuterClass.Message value) {
                onMessage(value);
            }

            @Override
            public void onError(Throwable t) {
//                t.printStackTrace();
                ch.shutdown();
            }

            @Override
            public void onCompleted() {
                ch.shutdown();
            }
        });
    }

    private void grpcCall(Peer peer, WisdomOuterClass.Message msg) {
        ManagedChannel ch = ManagedChannelBuilder.forAddress(peer.host, peer.port
            ).usePlaintext().build(); // without setting up any ssl

        try {
            ch.awaitTermination(RPC_TIMEOUT, TimeUnit.SECONDS);
        }catch (Exception e){
            logger.error("http2 timeout");
            return;
        }

        WisdomGrpc.WisdomStub stub = WisdomGrpc.newStub(
                ch);
        stub.entry(msg, new StreamObserver<WisdomOuterClass.Message>() {
            @Override
            public void onNext(WisdomOuterClass.Message value) {
                onMessage(value);
            }

            @Override
            public void onError(Throwable t) {
//                t.printStackTrace();
                ch.shutdown();
                peersCache.half(peer);
            }

            @Override
            public void onCompleted() {
                ch.shutdown();
//                logger.info("send message " + msg.getCode().name() + " success content = " + msg.toString());
            }
        });
    }

    public void dial(String host, int port, Object msg) {
        grpcCall(host, port, buildMessage(MAX_TTL, msg));
    }

    public void dial(Peer p, Object msg) {
        grpcCall(p, buildMessage(1, msg));
    }

    public void broadcast(Object msg) {
        for (Peer p : getPeers()) {
            grpcCall(p, buildMessage(MAX_TTL, msg));
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
                grpcCall(p, buildMessage(payload.getTtl() - 1, payload.getBody()));
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

    private WisdomOuterClass.Message buildMessage(long ttl, Object msg) {
        WisdomOuterClass.Message.Builder builder = WisdomOuterClass.Message.newBuilder();
        builder.setCreatedAt(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build());
        builder.setRemotePeer(peersCache.getSelf().toString());
        builder.setTtl(ttl);
        builder.setNonce(nonce.getAndIncrement());
        if (msg instanceof WisdomOuterClass.Nothing) {
            builder.setCode(WisdomOuterClass.Code.NOTHING);
            return sign(builder.setBody(((WisdomOuterClass.Nothing) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Ping) {
            builder.setCode(WisdomOuterClass.Code.PING);
            return sign(builder.setBody(((WisdomOuterClass.Ping) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Pong) {
            builder.setCode(WisdomOuterClass.Code.PONG);
            return sign(builder.setBody(((WisdomOuterClass.Pong) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Lookup) {
            builder.setCode(WisdomOuterClass.Code.LOOK_UP);
            return sign(builder.setBody(((WisdomOuterClass.Lookup) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Peers) {
            builder.setCode(WisdomOuterClass.Code.PEERS);
            return sign(builder.setBody(((WisdomOuterClass.Peers) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetStatus) {
            builder.setCode(WisdomOuterClass.Code.GET_STATUS);
            return sign(builder.setBody(((WisdomOuterClass.GetStatus) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Status) {
            builder.setCode(WisdomOuterClass.Code.STATUS);
            return sign(builder.setBody(((WisdomOuterClass.Status) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetBlocks) {
            builder.setCode(WisdomOuterClass.Code.GET_BLOCKS);
            return sign(builder.setBody(((WisdomOuterClass.GetBlocks) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Blocks) {
            builder.setCode(WisdomOuterClass.Code.BLOCKS);
            return sign(builder.setBody(((WisdomOuterClass.Blocks) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Proposal) {
            builder.setCode(WisdomOuterClass.Code.PROPOSAL);
            return sign(builder.setBody(((WisdomOuterClass.Proposal) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.Transactions) {
            builder.setCode(WisdomOuterClass.Code.TRANSACTIONS);
            return sign(builder.setBody(((WisdomOuterClass.Transactions) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetTreeNodes) {
            builder.setCode(WisdomOuterClass.Code.GET_TREE_NODES);
            return sign(builder.setBody(((WisdomOuterClass.GetTreeNodes) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.TreeNodes) {
            builder.setCode(WisdomOuterClass.Code.TREE_NODES);
            return sign(builder.setBody(((WisdomOuterClass.TreeNodes) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.GetMerkleTransactions) {
            builder.setCode(WisdomOuterClass.Code.GET_MERKELE_TRANSACTIONS);
            return sign(builder.setBody(((WisdomOuterClass.GetMerkleTransactions) msg).toByteString())).build();
        }
        if (msg instanceof WisdomOuterClass.MerkleTransactions) {
            builder.setCode(WisdomOuterClass.Code.MERKLE_TRANSACTIONS);
            return sign(builder.setBody(((WisdomOuterClass.MerkleTransactions) msg).toByteString())).build();
        }
        logger.error("cannot deduce message type " + msg.getClass().toString());
        builder.setCode(WisdomOuterClass.Code.NOTHING).setBody(WisdomOuterClass.Nothing.newBuilder().build().toByteString());
        return sign(builder).build();
    }

    private WisdomOuterClass.Message.Builder sign(WisdomOuterClass.Message.Builder builder) {
        return builder.setSignature(
                ByteString.copyFrom(
                        peersCache.getSelf().privateKey.sign(Util.getRawForSign(builder.build()))
                )
        );
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

    void pend(Peer peer){
        this.peersCache.pend(peer);
    }
}
