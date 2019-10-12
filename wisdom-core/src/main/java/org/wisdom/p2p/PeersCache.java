package org.wisdom.p2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

public class PeersCache {
    private static final Logger logger = LoggerFactory.getLogger(PeersCache.class);
    private static final int MAX_PEERS = 16;
    private static final int MIN_PEERS = 6;

    private Map<Integer, Set<Peer>> peers;

    private Set<Peer> trusted;

    private Set<HostPort> unresolved;

    private Set<Peer> bootstraps;

    private Set<Peer> blocked;

    private Set<Peer> pend;

    private Peer self;


    private boolean enableDiscovery;

    public PeersCache(String self, String bootstraps, String trusted, boolean enableDiscovery) throws Exception {
        this.self = Peer.newPeer(self);
        this.bootstraps = new HashSet<>();
        this.trusted = new HashSet<>();
        this.blocked = new HashSet<>();
        this.peers = new TreeMap<>();
        this.pend = new HashSet<>();
        this.unresolved = new HashSet<>();
        this.enableDiscovery = enableDiscovery;
        String[] ts = new String[]{};
        if (trusted != null && !trusted.equals("")) {
            ts = trusted.split(",");
        }

        Optional.ofNullable(bootstraps)
                .map(x -> Arrays.asList(x.split(",")))
                .map(ps -> {
                    List<String> unparsed = new ArrayList<>();
                    ps.forEach(p -> {
                        try {
                            Peer peer = Peer.parse(p);
                            this.bootstraps.add(peer);
                        } catch (Exception e) {
                            unparsed.add(p);
                        }
                    });
                    return unparsed;
                })
                .get()
                .forEach(link -> {
                    if (link == null || link.equals("")) {
                        return;
                    }
                    try {
                        URI u = new URI(link);
                        this.unresolved.add(new HostPort(u.getHost(), u.getPort()));
                    } catch (Exception e) {
                        logger.error("invalid url");
                    }
                });


        for (String b : ts) {
            Peer p = Peer.parse(b);
            if (p.equals(this.self)) {
                throw new Exception("cannot treat yourself as trusted peer");
            }
            this.trusted.add(p);
        }
    }

    public int size() {
        return peers.values().stream().map(Set::size).reduce(Integer::sum).orElse(0) + trusted.size();
    }

    public boolean hasPeer(Peer peer) {
        if (trusted.contains(peer)) {
            return true;
        }
        int idx = self.subTree(peer);
        return peers.containsKey(idx) && peers.get(idx).contains(peer);
    }

    public void pend(Peer peer) {
        String k = peer.key();
        if (size() >= MAX_PEERS) {
            return;
        }
        if (hasPeer(peer) || blocked.contains(peer) || bootstraps.contains(peer)) {
            return;
        }
        pend.add(peer);
    }

}
