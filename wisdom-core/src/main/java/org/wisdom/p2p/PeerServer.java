package org.wisdom.p2p;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
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

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sal 1564319846@qq.com
 * wisdom protocol implementation
 */
@Component
@ConditionalOnProperty(name = "p2p.mode", havingValue = "grpc")
public class PeerServer extends WisdomGrpc.WisdomImplBase {
    static final int PEER_SCORE = 4;
    static final int HALF_RATE = 60;
    static final int EVIL_SCORE = -(1 << 10);
    static final int MAX_PEERS = 32;

    private static class TestLogger {
        public void info(String msg) {
            System.out.println(msg);
        }

        public void warn(String msg) {
            System.out.println(msg);
        }

        public void error(String msg) {
            System.out.println(msg);
        }
    }

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
    private Map<String, ManagedChannel> chanBuffer;

    @Autowired
    private MessageFilter filter;

    @Autowired
    private PeersManager pmgr;

    @Autowired
    private MessageLogger messageLogger;

    @Autowired
    private SyncManager syncManager;

    public PeerServer(
            @Value("${p2p.address}") String self,
            @Value("${p2p.bootstraps}") String bootstraps,
            @Value("${p2p.trustedpeers}") String trusted
    ) throws Exception {
        nonce = new AtomicLong();
        pluginList = new ArrayList<>();
        this.self = Peer.newPeer(self);
        this.bootstraps = new ConcurrentHashMap<>();
        this.trusted = new ConcurrentHashMap<>();
        this.blocked = new ConcurrentHashMap<>();
        this.peers = new ConcurrentHashMap<>();
        this.pended = new ConcurrentHashMap<>();
        this.chanBuffer = new ConcurrentHashMap<>();
        String[] bs = new String[]{};
        if (bootstraps != null && !bootstraps.equals("")) {
            bs = bootstraps.split(" ");
        }
        String[] ts = new String[]{};
        if (trusted != null && !trusted.equals("")) {
            ts = trusted.split(" ");
        }
        for (String b : bs) {
            Peer p = Peer.parse(b);
            if (p.equals(this.self)) {
                throw new Exception("cannot treat yourself as bootstrap peer");
            }
            this.bootstraps.put(p.key(), p);
        }
        for (String b : ts) {
            Peer p = Peer.parse(b);
            if (p.equals(this.self)) {
                throw new Exception("cannot treat yourself as trusted peer");
            }
            this.trusted.put(p.key(), p);
        }
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
        use(messageLogger).use(filter).use(pmgr).use(syncManager);
        startListening();
    }

    public void startListening() throws Exception {
        logger.info("peer server is listening on " + Peer.PROTOCOL_NAME + "://" + Hex.encodeHexString(self.privateKey.getEncoded()) + Hex.encodeHexString(self.peerID) + "@" + self.hostPort());
        logger.info("provide address to your peers to connect " + Peer.PROTOCOL_NAME + "://" + Hex.encodeHexString(self.peerID) + "@" + self.hostPort());
        for (Plugin p : pluginList) {
            p.onStart(this);
        }
        server = ServerBuilder.forPort(self.port).addService(this).build().start();
    }

    @Scheduled(fixedRate = HALF_RATE * 1000)
    public void startHalf() {
        boolean hasFull = peers.size() + trusted.size() >= MAX_PEERS;
        for (Peer p : pended.values()) {
            pended.remove(p.key());
            if (hasFull || hasPeer(p)) {
                continue;
            }
            dial(p, WisdomOuterClass.Ping.newBuilder().build());
        }
        for (Peer p : blocked.values()) {
            p.score /= 2;
            if (p.score == 0) {
                blocked.remove(p.key());
            }
        }
        for (Peer p : peers.values()) {
            p.score /= 2;
            if (p.score == 0) {
                removePeer(p);
            }
        }

        for (Peer p : getPeers()) {
            dial(p, WisdomOuterClass.Ping.newBuilder().build()); // keep alive
        }
        if (hasFull) {
            return;
        }
        // discover peers when bucket is not full
        Set<Peer> ps = new HashSet<>();
        ps.addAll(peers.values());
        ps.addAll(bootstraps.values());
        for (Peer p : ps) {
            logger.info("peer found, address = " + p.toString() + " score = " + p.score);
            dial(p, WisdomOuterClass.Lookup.newBuilder().build());
        }
    }

    public Peer getSelf() {
        return self;
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
                removePeer(payload.getRemote());
            }
            if (ctx.pending) {
                pendPeer(payload.getRemote());
            }
            if (ctx.keep) {
                keepPeer(payload.getRemote());
            }
            if (ctx.block) {
                blockPeer(payload.getRemote());
            }
            if (ctx.relay) {
                relay(payload);
            }
            if (ctx.response != null) {
                return buildMessage(payload.getRemote(), 1, ctx.response);
            }
            return buildMessage(ctx.payload.getRemote(), 1, WisdomOuterClass.Nothing.newBuilder().build());
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
        String key = peer.key();
        ManagedChannel ch = chanBuffer.get(key);
        if (ch == null) {
            ch = ManagedChannelBuilder.forAddress(peer.host, peer.port
            ).usePlaintext().build(); // without setting up any ssl
            chanBuffer.put(key, ch);
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
                logger.error(t.toString());
                logger.error("cannot connect to peer " + peer.toString() + " half its score");
                int k = self.subTree(peer);
                Peer p = peers.get(k);
                if (p != null && p.equals(peer)) {
                    p.score /= 2;
                    if (p.score == 0) {
                        removePeer(p);
                    }
                }
            }

            @Override
            public void onCompleted() {
//                logger.info("send message " + msg.getCode().name() + " success content = " + msg.toString());
            }
        });
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
        if (payload.getTtl() <= 0) {
            return;
        }
        for (Peer p : getPeers()) {
            if (p.equals(payload.getRemote())) {
                continue;
            }
            try {
                grpcCall(p, buildMessage(p, payload.getTtl() - 1, payload.getBody()));
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
        if (peers.size() + trusted.size() >= MAX_PEERS) {
            return;
        }
        if (hasPeer(peer) || blocked.containsKey(k) || bootstraps.containsKey(k)) {
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
        if (p == null && peers.size() + trusted.size() < MAX_PEERS) {
            peers.put(idx, peer);
            return;
        }
        if (p == null) {
            return;
        }
        if (p.equals(peer)) {
            p.score += 2 * PEER_SCORE;
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
        int idx = self.subTree(peer);
        Peer p = peers.get(idx);
        if (p == null) {
            return;
        }
        if (p.equals(peer)) {
            peers.remove(idx);
        }
        String key = peer.key();
        ManagedChannel ch = chanBuffer.get(key);
        if (ch != null) {
            ch.shutdown();
        }
        chanBuffer.remove(key);
    }

    public boolean hasPeer(Peer peer) {
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
        if (msg instanceof WisdomOuterClass.Transaction) {
            builder.setCode(WisdomOuterClass.Code.TRANSACTION);
            return sign(builder.setBody(((WisdomOuterClass.Transaction) msg).toByteString())).build();
        }
        logger.error("cannot deduce message type " + msg.getClass().toString());
        builder.setCode(WisdomOuterClass.Code.NOTHING).setBody(WisdomOuterClass.Nothing.newBuilder().build().toByteString());
        return sign(builder).build();
    }

    private WisdomOuterClass.Message.Builder sign(WisdomOuterClass.Message.Builder builder) {
        return builder.setSignature(
                ByteString.copyFrom(
                        self.privateKey.sign(Util.getRawForSign(builder.build()))
                )
        );
    }

}
