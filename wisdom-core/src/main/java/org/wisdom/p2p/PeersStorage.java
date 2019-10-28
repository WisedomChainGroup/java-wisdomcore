package org.wisdom.p2p;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.wisdom.db.Leveldb;
import org.wisdom.encoding.JSONEncodeDecoder;

import javax.annotation.PostConstruct;

@Repository
public class PeersStorage extends PeersCacheWrapper {
    private static final byte[] LEVELDB_KEY = "peers".getBytes();

    private static final long FLUSH_RATE = 60 * 1000;

    @Autowired
    private Leveldb leveldb;

    @Autowired
    private JSONEncodeDecoder codec;

    public PeersStorage(
            @Value("${p2p.address}") String self,
            @Value("${p2p.bootstraps}") String bootstraps,
            @Value("${p2p.trustedpeers}") String trusted,
            @Value("${p2p.enable-discovery}") boolean enableDiscovery
    ) throws Exception {
        super(self, bootstraps, trusted, enableDiscovery);
    }

    @PostConstruct
    public void init() throws Exception{
        byte[] peers = leveldb.read(LEVELDB_KEY);
        if (peers == null || peers.length == 0) {
            return;
        }
        for (String s : codec.decode(peers, String[].class)) {
            super.keepPeer(Peer.newPeer(s));
        }
    }

    @Scheduled(fixedDelay = FLUSH_RATE)
    public void flush(){
        leveldb.write(LEVELDB_KEY, codec.encode(
                super.getPeers().stream()
                .map(Peer::toString)
                .toArray()
                )
        );
    }
}
