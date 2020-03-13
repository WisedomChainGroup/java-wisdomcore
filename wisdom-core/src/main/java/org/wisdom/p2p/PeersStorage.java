package org.wisdom.p2p;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.tdf.common.store.Store;
import org.wisdom.db.DatabaseStoreFactory;
import org.wisdom.encoding.JSONEncodeDecoder;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Repository
public class PeersStorage extends PeersCacheWrapper {
    private static final byte[] LEVELDB_KEY = "peers".getBytes();

    private static final long FLUSH_RATE = 60 * 1000;

    private Store<byte[], byte[]> db;

    @Autowired
    private JSONEncodeDecoder codec;

    public PeersStorage(
            @Value("${p2p.address}") String self,
            @Value("${p2p.bootstraps}") String bootstraps,
            @Value("${p2p.trustedpeers}") String trusted,
            @Value("${p2p.enable-discovery}") boolean enableDiscovery,
            DatabaseStoreFactory factory
    ) throws Exception {
        super(self, bootstraps, trusted, enableDiscovery);
        this.db = factory.create("peers", false);
    }

    @PostConstruct
    public void init() {
        Optional<byte[]> peers = db.get(LEVELDB_KEY);
        peers.ifPresent(peer->{
            try {
                for (String s : codec.decode(peer, String[].class)) {
                    super.keepPeer(Peer.newPeer(s));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedDelay = FLUSH_RATE)
    public void flush(){
        db.put(LEVELDB_KEY, codec.encode(
                super.getPeers().stream()
                .map(Peer::toString)
                .toArray()
                )
        );
    }
}
