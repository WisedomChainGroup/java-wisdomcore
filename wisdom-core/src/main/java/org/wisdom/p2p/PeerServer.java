package org.wisdom.p2p;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PeerServer extends WisdomGrpc.WisdomImplBase {
    static final int PEER_SCORE = 4;
    static final int HALF_RATE = 30;
    static final int EVIL_SCORE = -(1 << 10);

    private Server server;
    private static final Logger logger = LoggerFactory.getLogger(PeerServer.class);
    private static final int MAX_TTL = 64;
    private AtomicLong nonce;
    private Peer self;
    private List<Plugin> pluginList;
    private Map<String, Peer> bootstraps;
    private Map<String, Peer> trusted;
    private Map<String, Peer> blocked;
    private Map<Integer, Peer> peers;
    private Map<String, Peer> pended;

    @Autowired
    public PeerServer(
            MessageFilter filter,
            PeersManager pmgr,
            @Value("${p2p.address}") String self,
            @Value("${p2p.bootstraps}") String[] bootstraps,
            @Value("${p2p.trusted}") String[] trusted
    ) throws Exception {
        nonce = new AtomicLong();
        pluginList = new ArrayList<>();
        this.self = Peer.newPeer(self);
        this.bootstraps = new ConcurrentHashMap<>();
        this.trusted = new ConcurrentHashMap<>();
        this.blocked = new ConcurrentHashMap<>();
        this.peers = new HashMap<>();
        this.pended = new ConcurrentHashMap<>();
        for (String b : bootstraps) {
            Peer p = Peer.parse(b);
            if (p.equals(this.self)) {
                throw new Exception("cannot treat yourself as bootstrap peer");
            }
            this.bootstraps.put(p.key(), p);
        }
        for (String b : trusted) {
            Peer p = Peer.parse(b);
            if (p.equals(this.self)) {
                throw new Exception("cannot treat yourself as trusted peer");
            }
            this.trusted.put(p.key(), p);
        }
        use(filter).use(pmgr);
    }

    public PeerServer use(Plugin plugin) {
        pluginList.add(plugin);
        return this;
    }

    /**
     * 启动服务
     *
     * @param port
     * @throws IOException
     */
    @PostConstruct
    private void start(@Value("${p2p.port}") int port) throws Exception {
        server = ServerBuilder.forPort(port).addService(this).build().start();
        logger.info("peer server is listening on" + port);
        for (Plugin p : pluginList) {
            p.onStart(this);
        }
        blockUnitShutdown();
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    logger.warn("shut down p2p service....");
                                    stop();
                                    logger.warn("p2p service is shutdown");
                                }));

    }

    /**
     * 关闭服务
     */
    private void stop() {
        Optional.of(server).map(Server::shutdown);
    }

    /**
     * 循环运行服务,封锁停止
     *
     * @throws InterruptedException
     */
    public void blockUnitShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
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
                if (ctx.response != null) {
                    return buildMessage(payload.remote, 1, ctx.response);
                }
            }
            return buildMessage(ctx.payload.remote, 1, WisdomOuterClass.Nothing.newBuilder().build());
        } catch (Exception e) {
            logger.error("fail to parse message");
        }
        return null;
    }

    @Override
    public void entry(WisdomOuterClass.Message request, StreamObserver<WisdomOuterClass.Message> responseObserver) {
        WisdomOuterClass.Message resp = onMessage(request);
        if (resp != null) {
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onCompleted();
    }

    private void grpcCall(Peer peer, WisdomOuterClass.Message msg) {
        WisdomOuterClass.Message resp = WisdomGrpc.
                newBlockingStub(
                        ManagedChannelBuilder.forAddress(peer.host, peer.port
                        ).build())
                .entry(msg);
        onMessage(resp);
    }

    public void dial(Peer p, Object msg) {
        grpcCall(p, buildMessage(p, 1, msg));
    }

    public void broadcast(Object msg) {
        for (Peer p : getPeers()) {
            grpcCall(p, buildMessage(p, MAX_TTL, msg));
        }
    }

    public void relay(Payload payload) {
        if (payload.ttl <= 0) {
            return;
        }
        for (Peer p : getPeers()) {
            if (p.equals(payload.remote)) {
                continue;
            }
            try {
                grpcCall(p, buildMessage(p, payload.ttl - 1, payload.getBody()));
            } catch (Exception e) {
                logger.error("parse body fail");
            }
        }
    }
    
    public List<Peer> getPeers() {
        List<Peer> ps = new ArrayList<>();
        ps.addAll(peers.values());
        ps.addAll(trusted.values());
        if (ps.size() == 0) {
            ps.addAll(bootstraps.values());
        }
        return ps;
    }


    private void pendPeer(Peer peer) {
        String k = peer.key();
        if (hasPeer(peer) || blocked.containsKey(k)) {
            return;
        }
        pended.put(k, peer);
    }

    private void keepPeer(Peer peer) {
        String k = peer.key();
        if (trusted.containsKey(k) || blocked.containsKey(k)) {
            return;
        }
        peer.score = PEER_SCORE;
        int idx = self.subTree(peer);
        Peer p = peers.get(idx);
        if (p == null) {
            peers.put(idx, peer);
            return;
        }
        if (p.score < PEER_SCORE) {
            peers.put(idx, peer);
        }
    }


    private void blockPeer(Peer peer) {
        peer.score = EVIL_SCORE;
        removePeer(peer);
        blocked.put(peer.key(), peer);
    }

    private void removePeer(Peer peer) {
        String k = peer.key();
        int idx = self.subTree(peer);
        Peer p = peers.get(idx);
        if (p == null) {
            return;
        }
        if (p.equals(peer)) {
            peers.remove(k);
        }
    }

    private boolean hasPeer(Peer peer) {
        String k = peer.key();
        if (trusted.containsKey(k)) {
            return true;
        }
        int idx = self.subTree(peer);
        return peers.containsKey(idx) && peers.get(idx).equals(peer);
    }

    private WisdomOuterClass.Message buildMessage(Peer recipient, long ttl, Object msg) {
        WisdomOuterClass.Message.Builder builder = WisdomOuterClass.Message.newBuilder();
        builder.setCreatedAt(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build());
        builder.setRemotePeer(self.toString());
        builder.setRecipient(recipient.toString());
        builder.setTtl(ttl);
        builder.setNonce(nonce.getAndIncrement());
        if (msg instanceof WisdomOuterClass.Ping) {
            builder.setCode(WisdomOuterClass.Code.PING);
            builder.setBody(((WisdomOuterClass.Ping) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.Pong) {
            builder.setCode(WisdomOuterClass.Code.PONG);
            builder.setBody(((WisdomOuterClass.Pong) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.Lookup) {
            builder.setCode(WisdomOuterClass.Code.LOOK_UP);
            builder.setBody(((WisdomOuterClass.Lookup) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.Peers) {
            builder.setCode(WisdomOuterClass.Code.PEERS);
            builder.setBody(((WisdomOuterClass.Peers) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.GetStatus) {
            builder.setCode(WisdomOuterClass.Code.GET_STATUS);
            builder.setBody(((WisdomOuterClass.GetStatus) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.Status) {
            builder.setCode(WisdomOuterClass.Code.STATUS);
            builder.setBody(((WisdomOuterClass.Status) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.GetBlocks) {
            builder.setCode(WisdomOuterClass.Code.GET_BLOCKS);
            builder.setBody(((WisdomOuterClass.GetBlocks) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.Blocks) {
            builder.setCode(WisdomOuterClass.Code.BLOCKS);
            builder.setBody(((WisdomOuterClass.Blocks) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.Proposal) {
            builder.setCode(WisdomOuterClass.Code.PROPOSAL);
            builder.setBody(((WisdomOuterClass.Proposal) msg).toByteString());
        }
        if (msg instanceof WisdomOuterClass.Transaction) {
            builder.setCode(WisdomOuterClass.Code.TRANSACTION);
            builder.setBody(((WisdomOuterClass.Transaction) msg).toByteString());
        }
        if (builder.getCode() == WisdomOuterClass.Code.NOTHING) {
            builder.setBody(WisdomOuterClass.Nothing.newBuilder().build().toByteString());
        }
        sign(builder);
        return builder.build();
    }

    private void sign(WisdomOuterClass.Message.Builder builder) {
        builder.setSignature(
                ByteString.copyFrom(
                        self.privateKey.sign(Util.getRawForSign(builder.build()))
                )
        );
    }

}
