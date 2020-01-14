package org.wisdom.db;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.Getter;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.CachedStore;
import org.tdf.common.store.NoDeleteBatchStore;
import org.tdf.common.store.NoDeleteStore;
import org.tdf.common.store.Store;
import org.tdf.common.trie.ReadOnlyTrie;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.core.Block;
import org.wisdom.crypto.HashUtil;

import javax.validation.UnexpectedTypeException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class StateTrieAdapter<T> implements StateTrie<T> {
    private static final int MAX_CACHE_SIZE = 8;
    private String TRIE;
    private String DELETED;
    private String ROOTS;

    @Getter(AccessLevel.PROTECTED)
    private NoDeleteStore<byte[], byte[]> trieStore;

    @Getter
    private Store<byte[], byte[]> rootStore;

    @Getter
    private Trie<byte[], T> trie;

    protected abstract String getPrefix();

    // root hash -> Trie
    @Getter
    protected Cache<HexBytes, Trie<byte[], T>> cache = Caffeine
            .newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .recordStats()
            .build();

    @Getter(AccessLevel.PROTECTED)
    private AbstractStateUpdater<T> updater;

    public StateTrieAdapter(Class<T> clazz, AbstractStateUpdater<T> updater, Block genesis, DatabaseStoreFactory factory, boolean logDeletes, boolean reset) {
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

        updater.getGenesisStates().forEach(tmp::put);
        byte[] root = tmp.commit();
        tmp.flush();
        rootStore.put(genesis.getHash(), root);
    }

    public Optional<T> get(byte[] blockHash, byte[] publicKeyHash) {
        return getTrieByBlockHash(blockHash).get(publicKeyHash);
    }

    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys) {
        Trie<byte[], T> trie = getTrieByBlockHash(blockHash);
        ByteArrayMap<T> m = new ByteArrayMap<>();
        keys.forEach(x ->
                m.put(
                        x, trie.get(x).orElse(updater.createEmpty(x))
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
        cache.asMap().putIfAbsent(HexBytes.fromBytes(trie.getRootHash()), ReadOnlyTrie.of(trie));
        getRootStore().put(blockHash, newRoot);
        return trie;
    }

    // get a read only trie for query
    public Trie<byte[], T> getTrieByBlockHash(byte[] blockHash) {
        byte[] root = getRootStore()
                .get(blockHash)
                .orElseThrow(RuntimeException::new);

        return cache.get(HexBytes.fromBytes(root),
                x -> this.getTrieByRootHash(x.getBytes())
        );
    }

    public Trie<byte[], T> getTrieByRootHash(byte[] rootHash) {
        Trie<byte[], T> ret = getTrie().revert(rootHash);
        return ReadOnlyTrie.of(ret);
    }

    @Override
    public void commit(Map<byte[], T> states, byte[] blockHash) {
        if (getRootStore().containsKey(blockHash)) return;
        Trie<byte[], T> empty = getTrie().revert();
        states.forEach(empty::put);
        getRootStore().put(blockHash, empty.commit());
        empty.flush();
        cache.asMap().putIfAbsent(HexBytes.fromBytes(empty.getRootHash()), empty);
    }
}
