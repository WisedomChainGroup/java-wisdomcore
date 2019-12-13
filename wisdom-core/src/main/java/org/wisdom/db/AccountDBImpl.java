package org.wisdom.db;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.tdf.rlp.RLPCodec;
import org.wisdom.core.Block;
import org.wisdom.crypto.HashUtil;
import org.wisdom.util.ByteArrayMap;
import org.wisdom.util.trie.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
public class AccountDBImpl implements AccountDB {

    // block hash -> state root
    private Store<byte[], byte[]> rootStore;

    // removed key in trie store -> dummy
    private Store<byte[], byte[]> deleted;

    // trie store
    private Store<byte[], byte[]> trieStore;

    // trie to
    private Trie<byte[], AccountState> stateTrie;

    public AccountDBImpl(){
        stateTrie = TrieImpl.newInstance(
                HashUtil::keccak256,
                new NoDeleteStore<>(trieStore, deleted),
                Codec.identity(),
                Codec.newInstance(RLPCodec::encode, (x) -> RLPCodec.decode(x, AccountState.class))
        );
    }

    @Override
    public Optional<AccountState> getAccount(byte[] blockHash, byte[] publicKeyHash) {
        byte[] root = rootStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        Trie<byte[], AccountState> trie = stateTrie.revert(root, rootStore);
        return trie.get(publicKeyHash);
    }

    @Override
    public Map<byte[], AccountState> getAccounts(byte[] blockHash, Collection<byte[]> publicKeyHashes) {
        byte[] root = rootStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        Trie<byte[], AccountState> trie = stateTrie.revert(root, rootStore);
        ByteArrayMap<AccountState> m = new ByteArrayMap<>();
        publicKeyHashes.forEach(x -> {
            Optional<AccountState> account = trie.get(x);
            if (!account.isPresent()) return;
            m.put(x, account.get());
        });
        return m;
    }

    @Override
    public byte[] putAccounts(byte[] blockHash, Collection<AccountState> accounts) {
        byte[] root = rootStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        if (rootStore.containsKey(blockHash)) return rootStore.get(blockHash).get();
        Trie<byte[], AccountState> trie = stateTrie.revert(root, rootStore);
        for (AccountState state : accounts) {
            trie.put(state.getAccount().getPubkeyHash(), state);
        }
        byte[] newRoot = trie.getRootHash();
        rootStore.put(blockHash, newRoot);
        return newRoot;
    }

    @Override
    public void confirm(Block block, Collection<AccountState> confirmed) {
        byte[] root = rootStore.get(block.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(block.hashPrevBlock) + " not synced"));
        Trie<byte[], AccountState> parentTrie = stateTrie.revert(root, rootStore);
        for(AccountState state: confirmed){
            parentTrie.put(state.getAccount().getPubkeyHash(), state);
        }
        parentTrie.flush();
    }
}
