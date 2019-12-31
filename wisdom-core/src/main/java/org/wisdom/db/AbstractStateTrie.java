package org.wisdom.db;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.MemoryCachedStore;
import org.tdf.common.store.NoDeleteStore;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.tdf.common.trie.TrieImpl;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.core.Block;
import org.wisdom.crypto.HashUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractStateTrie<T> implements StateTrie<T>{
    private String TRIE;
    private String DELETED;
    private String ROOTS;

    @Getter(AccessLevel.PROTECTED)
    private NoDeleteStore<byte[], byte[]> trieStore;

    @Getter(AccessLevel.PROTECTED)
    private Store<byte[], byte[]> rootStore;

    @Getter(AccessLevel.PROTECTED)
    private Trie<byte[], T> trie;

    protected abstract String getPrefix();

    protected abstract Map<byte[], T> getUpdatedStates(Map<byte[], T> beforeUpdates, Block block);
    protected abstract Set<byte[]> getRelatedKeys(Block block);

    public AbstractStateTrie(Block genesis, Class<T> clazz, DatabaseStoreFactory factory, boolean logDeletes, boolean reset) {
        TRIE = getPrefix() + "-trie";
        DELETED = getPrefix() + "-deleted";
        ROOTS = getPrefix() + "-trie-roots";

        rootStore = factory.create(ROOTS, reset);
        if(logDeletes){
            trieStore = new NoDeleteStore<>(
                    factory.create(TRIE, reset),
                    factory.create(DELETED, reset)
            );
        }else{
            trieStore = new NoDeleteStore<>(
                    factory.create(TRIE, reset),
                    Store.getNop()
            );
        }
        trie = TrieImpl.newInstance(
                HashUtil::keccak256,
                trieStore,
                Codec.identity(),
                Codec.newInstance(RLPCodec::encode, x -> RLPElement.fromEncoded(x).as(clazz))
                );

        rootStore.put(genesis.hashPrevBlock, trie.getNullHash());
    }

    public Optional<T> get(byte[] blockHash, byte[] publicKeyHash){
        byte[] root = rootStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        return getTrie().revert(root).get(publicKeyHash);
    }

    public Map<byte[], T> batchGet(byte[] blockHash, Collection<byte[]> keys){
        byte[] root = rootStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        Trie<byte[], T> trie = getTrie().revert(root);
        ByteArrayMap<T> m = new ByteArrayMap<>();
        keys.forEach(x -> {
            Optional<T> o = trie.get(x);
            if (!o.isPresent()) return;
            m.put(x, o.get());
        });
        return m;
    }

    private byte[] commitInternal(byte[] root, byte[] blockHash, Map<byte[], T> data){
        Store<byte[], byte[]> cache = new MemoryCachedStore<>(trieStore);
        Trie<byte[], T> trie = getTrie().revert(root, cache);
        for (Map.Entry<byte[], T> entry: data.entrySet()) {
            trie.put(entry.getKey(), entry.getValue());
        }
        byte[] newRoot = trie.commit();
        trie.flush();
        rootStore.put(blockHash, newRoot);
        return newRoot;
    }

    public byte[] commit(byte[] parentHash, byte[] blockHash, Map<byte[], T> data){
        if (rootStore.containsKey(blockHash))
            throw new RuntimeException(Hex.encodeHexString(blockHash) + " has exists");
        byte[] root = rootStore.get(parentHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(parentHash) + " not exists"));
        return commitInternal(root, blockHash, data);
    }

    @Override
    public byte[] commit(Block block) {
        if (rootStore.containsKey(block.getHash()))
            throw new RuntimeException(Hex.encodeHexString(block.getHash()) + " has exists");
        byte[] root = rootStore.get(block.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(block.hashPrevBlock) + " not exists"));
        Map<byte[], T> beforeUpdates = batchGet(block.hashPrevBlock, getRelatedKeys(block));
        return commitInternal(root, block.getHash(), getUpdatedStates(beforeUpdates, block));
    }
}
