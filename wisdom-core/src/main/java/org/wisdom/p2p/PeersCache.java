package org.wisdom.p2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class PeersCache {
    private static final Logger logger = LoggerFactory.getLogger(PeersCache.class);
    static final int MAX_PEERS = 16;
    private static final int PEER_SCORE = 32;
    private static final int EVIL_SCORE = -(1 << 10);

    private Map<Integer, Map<String, Peer>> peers;

    private Set<Peer> trusted;

    private Set<HostPort> unresolved;

    private Set<Peer> bootstraps;

    private Set<Peer> blocked;

    private Set<Peer> pended;

    private Peer self;


    private boolean enableDiscovery;

    public PeersCache(String self, String bootstraps, String trusted, boolean enableDiscovery) throws Exception {
        this.self = Peer.newPeer(self);
        this.bootstraps = new HashSet<>();
        this.trusted = new HashSet<>();
        this.blocked = new HashSet<>();
        this.peers = new HashMap<>();
        this.pended = new HashSet<>();
        this.unresolved = new HashSet<>();
        this.enableDiscovery = enableDiscovery;

        initBootstraps(bootstraps);
        initTrusted(trusted);
    }

    private void initBootstraps(String bootstraps){
        if(bootstraps == null || bootstraps.equals("")){
            return;
        }
        List<String> unparsed = new ArrayList<>();
        for(String addr: bootstraps.split(",")){
            try {
                Peer peer = Peer.parse(addr);
                this.bootstraps.add(peer);
            } catch (Exception e) {
                unparsed.add(addr);
            }
        }
        for(String link: unparsed){
            try {
                URI u = new URI(link);
                this.unresolved.add(new HostPort(u.getHost(), u.getPort()));
            } catch (Exception e) {
                logger.error("invalid url");
            }
        }
    }

    private void initTrusted(String trusted) throws Exception{
        if (trusted == null || trusted.equals("")) {
            return;
        }
        for (String b : trusted.split(",")) {
                Peer p = Peer.parse(b);
                if (p.equals(this.self)) {
                    throw new Exception("cannot treat yourself as trusted peer");
                }
            this.trusted.add(p);
        }
    }

    public int size() {
        return peers.values().stream().map(Map::size).reduce(Integer::sum).orElse(0) + trusted.size();
    }

    public boolean hasPeer(Peer peer) {
        if (trusted.contains(peer)) {
            return true;
        }
        int idx = self.subTree(peer);
        return peers.containsKey(idx) && peers.get(idx).containsKey(peer.key());
    }

    public void pend(Peer peer) {
        if (peer.host.equals("localhost") || peer.host.equals("127.0.0.1")){
            return;
        }
        if (size() >= MAX_PEERS) {
            return;
        }
        if(peer.equals(self)){
            return;
        }
        if (hasPeer(peer) || blocked.contains(peer) || bootstraps.contains(peer)) {
            return;
        }
        pended.add(peer);
    }

    public void keepPeer(Peer peer) {
        // 解析 bootstrap 放到 bootstraps 里面
        if(peer.equals(self)){
            return;
        }
        HostPort hp = new HostPort(peer.host, peer.port);

        // 如果没有开启节点发现，而且收到的节点信息不是种子节点，退出
        if (!enableDiscovery && !unresolved.contains(hp)) {
            return;
        }

        // 收到了种子节点的信息，
        if(unresolved.contains(hp) && !trusted.contains(peer)){
            bootstraps.add(peer);
        }
        unresolved.remove(hp);

        // 如果没有开启节点发现不需要新增邻居节点
        if(!enableDiscovery){
            return;
        }

        // 信任和拉黑的节点不需要更新分数
        if (trusted.contains(peer) || blocked.contains(peer)) {
            return;
        }

        peer.score = PEER_SCORE;
        int idx = self.subTree(peer);
        if (!peers.containsKey(idx)){
            peers.put(idx, new HashMap<>());
        }

        // 收到邻居节点的回复
        if (hasPeer(peer)) {
            peers.get(idx).get(peer.key()).score += PEER_SCORE;
            return;
        }

        // 发现新的邻居节点
        if(size() < MAX_PEERS){
            peers.get(idx).put(peer.key(), peer);
            return;
        }

        // 邻居节点数量已经满了, 查看是否可以删掉某个 k 桶里的邻居节点
        // 条件1 新的邻居节点对应的桶必须是空的
        if (peers.get(idx).size() > 0){
            return;
        }

        // 条件2 存在不止一个邻居节点的 k 桶
        Optional<Map<String, Peer>> bucket = peers.values().stream()
                .max(Comparator.comparingInt(Map::size));

        if(!bucket.isPresent() || bucket.get().size() <= 1){
            return;
        }

        // 满足两个条件，删除旧节点后加入新节点
        String key = (String) bucket.get().keySet().toArray()[0];
        bucket.get().remove(key);

        peers.get(idx).put(peer.key(), peer);
    }

    public void removePeer(Peer peer) {
        if(!enableDiscovery || trusted.contains(peer) || blocked.contains(peer)){
            return;
        }
        int idx = self.subTree(peer);
        if (!peers.containsKey(idx)){
            return;
        }
        peers.get(idx).remove(peer.key());
    }

    public List<Peer> getTrusted() {
        return new ArrayList<>(trusted);
    }

    public List<HostPort> getUnresolved() {
        return new ArrayList<>(unresolved);
    }

    public List<Peer> getBootstraps() {
        return new ArrayList<>(bootstraps);
    }

    public List<Peer> getBlocked() {
        return new ArrayList<>(blocked);
    }

    public List<Peer> getPended() {
        return new ArrayList<>(pended);
    }

    public List<Peer> popPended(){
        List<Peer> res = new ArrayList<>(pended);
        pended.clear();
        return res;
    }

    public Peer getSelf() {
        return self;
    }

    // get limit peers randomly
    public List<Peer> getPeers(int limit){
        List<Peer> res = getPeers();
        Random rand = new Random();
        while(res.size() > 0 && res.size() > limit){
            int idx = Math.abs(rand.nextInt()) % res.size();
            res.remove(idx);
        }
        return res;
    }

    public List<Peer> getPeers(){
        if (!enableDiscovery) {
            Set<Peer> tmp = new HashSet<>(bootstraps);
            tmp.addAll(trusted);
            return new ArrayList<>(tmp);
        }
        List<Peer> res = peers.values().stream().reduce(
                new ArrayList<>(), (id, y) -> {
            ArrayList<Peer> tmp = new ArrayList<>(id);
            tmp.addAll(y.values());
            return tmp;
        }, (x, y) -> {
            ArrayList<Peer> ps = new ArrayList<>(x);
            ps.addAll(y);
            return ps;
        });
        res.addAll(trusted);
        if(res.size() > 0){
            return res;
        }
        return bootstraps.stream().filter(p -> !blocked.contains(p)).collect(Collectors.toList());
    }

    public void blockPeer(Peer peer){
        removePeer(peer);
        peer.score = EVIL_SCORE;
        blocked.add(peer);
    }

    // 衰减 peer 分数，如果发现某个 Peer 分数为 0，则删除
    public void half(Peer peer){
        peer.score /= 2;
        if (peer.score == 0){
            removePeer(peer);
            blocked.remove(peer);
        }
    }

    // 衰减 peer 分数，如果发现某个 Peer 分数为 0，则删除
    public void half(){
        List<Peer> toRemove = new ArrayList<>();
        for(Map<String, Peer> bucket: peers.values()){
            for(Peer peer: bucket.values()){
                peer.score /= 2;
                if (peer.score == 0){
                    toRemove.add(peer);
                }
            }
        }
        List<Peer> toRestore = new ArrayList<>();
        toRemove.forEach(this::removePeer);
        for(Peer p: blocked){
            p.score /= 2;
            if (p.score == 0){
                toRestore.add(p);
            }
        }
        toRestore.forEach(p -> blocked.remove(p));
    }

    public boolean isFull(){
        return size() == MAX_PEERS;
    }
}
