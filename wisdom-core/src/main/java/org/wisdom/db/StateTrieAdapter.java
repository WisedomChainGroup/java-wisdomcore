package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.CachedStore;
import org.tdf.common.store.NoDeleteBatchStore;
import org.tdf.common.store.NoDeleteStore;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.core.Block;
import org.wisdom.crypto.HashUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class StateTrieAdapter<T> implements StateTrie<T> {
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
        byte[] root = getRootStore().get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        return getTrie().revert(root).get(publicKeyHash);
    }

    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys) {
        byte[] root = getRootStore().get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        Trie<byte[], T> trie = getTrie().revert(root);
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
        getRootStore().put(blockHash, newRoot);
        return trie;
    }

    public Trie<byte[], T> getTrie(byte[] blockHash) {
        return getTrie().revert(
                getRootStore().get(blockHash).orElseThrow(() -> new RuntimeException("unexpected"))
        );
    }

    @Override
    public void commit(Map<byte[], T> states, byte[] blockHash) {
        if(getRootStore().containsKey(blockHash)) return;
        Trie<byte[], T> empty = getTrie().revert();
        states.forEach(empty::put);
        getRootStore().put(blockHash, empty.commit());
        empty.flush();
    }

    public boolean contain(Block block){
        return getRootStore().asMap().containsKey(block.getHash());
    }

}
