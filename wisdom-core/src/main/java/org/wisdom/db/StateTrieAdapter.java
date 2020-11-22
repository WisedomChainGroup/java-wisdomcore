package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.*;
import org.tdf.common.trie.ReadOnlyTrie;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.core.Block;
import org.wisdom.crypto.HashUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j(topic = "StateTrieAdapter")
public abstract class StateTrieAdapter<T> implements StateTrie<T> {
    private static final int MAX_CACHE_SIZE = 8;
    private String TRIE;
    private String DELETED;
    private String ROOTS;

    @Getter
    private NoDeleteStore<byte[], byte[]> trieStore;

    @Getter
    private Store<byte[], byte[]> rootStore;

    @Getter
    private Trie<byte[], T> trie;

    protected abstract String getPrefix();

    // root hash -> Trie
    @Getter(AccessLevel.PROTECTED)
    private AbstractStateUpdater<T> updater;

    @Override
    public Optional<byte[]> getRootHashByBlockHash(byte[] blockHash) {
        return rootStore.get(blockHash);
    }

    public StateTrieAdapter(
            Class<T> clazz, Map<byte[], T> genesisState,
            Block genesis, DatabaseStoreFactory factory,
            boolean logDeletes, boolean reset
    ) {
        this(clazz, genesisState, genesis, factory, logDeletes, reset, null);
    }

    public StateTrieAdapter(
            Class<T> clazz,
            Map<byte[], T> genesisStates,
            Block genesis,
            DatabaseStoreFactory factory,
            boolean logDeletes,
            boolean reset,
            AbstractStateUpdater<T> updater
    ) {
        TRIE = getPrefix() + "-trie";
        DELETED = getPrefix() + "-deleted";
        ROOTS = getPrefix() + "-trie-roots";
        this.updater = updater;
        rootStore = factory.create(ROOTS, reset);

        trieStore = new NoDeleteBatchStore<>(factory.create(TRIE, reset));

        trie = Trie.<byte[], T>builder()
                .hashFunction(HashUtil::keccak256)
                .store(trieStore)
                .keyCodec(Codec.identity())
                .valueCodec(
                        Codec.newInstance(RLPCodec::encode, x -> RLPElement.fromEncoded(x).as(clazz))
                )
                .build();

        rootStore.put(genesis.hashPrevBlock, trie.revert().getRootHash());
        if (rootStore.containsKey(genesis.getHash())) return;

        // sync to genesis
        Trie<byte[], T> tmp =
                trie
                        .revert(trie.getNullHash(), new CachedStore<>(trieStore, ByteArrayMap::new));

        genesisStates.forEach(tmp::put);
        byte[] root = tmp.commit();
        tmp.flush();
        rootStore.put(genesis.getHash(), root);
    }

    public Optional<T> get(byte[] blockHash, byte[] publicKeyHash) {
        return getTrieByBlockHash(blockHash).get(publicKeyHash);
    }

    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys) {
        Trie<byte[], T> trie = getTrieByBlockHash(blockHash);
        if(trie == null){
            log.error("trie not found for block hash " + HexBytes.fromBytes(blockHash));
        }
        ByteArrayMap<T> m = new ByteArrayMap<>();
        keys.forEach(x ->
                m.put(
                        x, trie.get(x).orElseGet(() -> {
                            T t = null;
                            try{
                                t = updater.createEmpty(x);
                            }catch (Exception e){
                                log.error("update.createEmpty failed: x = {} updater class = {}", HexBytes.fromBytes(x), updater.getClass());
                                throw e;
                            }
                            if(t == null){
                                log.error("t is null for x = {} updater class = {}", x, updater.getClass());
                            }
                            return t;
                        })
                )
        );
        return m;
    }

    protected Trie<byte[], T> commitInternal(byte[] parentRoot, byte[] blockHash, Map<byte[], T> data) {
        Trie<byte[], T> trie = getTrie()
                .revert(parentRoot, new CachedStore<>(trieStore, ByteArrayMap::new));
        data.forEach(trie::put);
        byte[] newRoot = trie.commit();
        trie.flush();
        getRootStore().put(blockHash, newRoot);
        return trie;
    }

    // get a read only trie for query
    public Trie<byte[], T> getTrieByBlockHash(byte[] blockHash) {
        byte[] root = getRootStore()
                .get(blockHash)
                .orElseThrow(RuntimeException::new);

        return this.getTrieByRootHash(root);

    }

    public Trie<byte[], T> getTrieByRootHash(byte[] rootHash) {
        Trie<byte[], T> ret = getTrie().revert(rootHash);
        return ReadOnlyTrie.of(ret);
    }

    @Override
    public byte[] commit(Map<byte[], T> states, byte[] blockHash) {
        if (getRootStore().containsKey(blockHash))
            return getRootStore().get(blockHash).orElseThrow(() -> new RuntimeException("unreachable"));
        Trie<byte[], T> empty = getTrie().revert();
        states.forEach(empty::put);
        getRootStore().put(blockHash, empty.commit());
        empty.flush();
        return getRootStore().get(blockHash).orElseThrow(() -> new RuntimeException("unreachable"));
    }

    @Override
    public void gc(Collection<? extends byte[]> blockHash) {
        Map<byte[], byte[]> dumped = new ByteArrayMap<>();
        for (byte[] h : blockHash) {
            dumped.putAll(getTrieByBlockHash(h).dump());
        }
        getTrieStore().clear();
        if (getTrieStore() instanceof BatchStore) {
            ((BatchStore<byte[], byte[]>) getTrieStore()).putAll(dumped.entrySet());
            return;
        }
        dumped.forEach(getTrieStore()::put);
    }
}
