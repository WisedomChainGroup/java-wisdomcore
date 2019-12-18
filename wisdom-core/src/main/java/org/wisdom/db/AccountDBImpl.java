package org.wisdom.db;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.tdf.common.serialize.Codec;
import org.tdf.common.store.NoDeleteStore;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.tdf.common.trie.TrieImpl;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLPCodec;

import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import org.wisdom.crypto.HashUtil;

import org.wisdom.store.NoDeleteByteArrayStore;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class AccountDBImpl implements AccountDB {
    private static final int BLOCKS_PER_UPDATE_LOWER_BOUNDS = 4096;

    private static final String TRIE = "trie";

    private static final String DELETED = "deleted";

    private static final String STATE_ROOTS = "state-roots";

    private static final String DB_STATUS = "status";

    private static final byte[] LAST_SYNCED_HEIGHT = "last-confirmed".getBytes(StandardCharsets.US_ASCII);

    // mark whether the state of the block had been stored
    // block hash -> long
    private Store<byte[], byte[]> statusStore;

    // block hash -> state root
    private Store<byte[], byte[]> rootStore;

    // removed key in trie store -> dummy, for cleaning up when data replicated too much
    private Store<byte[], byte[]> deleted;

    // store actual data of trie
    private Store<byte[], byte[]> trieStore;

    private NoDeleteStore<byte[], byte[]> noDeleteStore;

    private byte[] nullHash;

    // trie to revert
    private Trie<byte[], AccountState> stateTrie;

    private WisdomBlockChain bc;

    @Autowired
    AccountStateUpdater accountStateUpdater;

    public AccountDBImpl(
            DatabaseStoreFactory factory,
            Block genesis,
            WisdomBlockChain bc
    ) throws InvalidProtocolBufferException, DecoderException {
        trieStore = factory.create(TRIE, false);
        deleted = factory.create(DELETED, false);
        rootStore = factory.create(STATE_ROOTS, false);
        noDeleteStore = new NoDeleteByteArrayStore(trieStore, deleted);
        statusStore = factory.create(DB_STATUS, false);

        stateTrie = TrieImpl.newInstance(
                HashUtil::keccak256,
                noDeleteStore,
                Codec.identity(),
                Codec.newInstance(RLPCodec::encode, (x) -> RLPCodec.decode(x, AccountState.class))
        );

        nullHash = stateTrie.getRootHash();
        // put parent hash of genesis map to null hash
        rootStore.putIfAbsent(genesis.hashPrevBlock, nullHash);

        // query for had been written
        long lastSyncedHeight = statusStore.get(LAST_SYNCED_HEIGHT).map(RLPCodec::decodeLong).orElse(-1L);

        Block last = bc.getCanonicalBlock(lastSyncedHeight);
        int blocksPerUpdate = BLOCKS_PER_UPDATE_LOWER_BOUNDS;
        while (true) {
            List<Block> blocks = bc.getCanonicalBlocks(last.nHeight + 1, blocksPerUpdate);
            int size = blocks.size();
            Map<byte[], AccountState> accounts = new HashMap<>();
            for (Block block : blocks) {
                accounts = accountStateUpdater.updateAll(accounts, block);
                statusStore.put(block.getHash(), putAccounts(block.hashPrevBlock, block.getHash(), accounts.values()));
            }
            // sync trie here
            if (size < blocksPerUpdate) {
                break;
            }
        }
    }

    // sync state trie to best block
    private void sync() {
        long lastSynced = statusStore.get(LAST_SYNCED_HEIGHT)
                .map(bc::getBlock).map(Block::getnHeight).orElse(0L);

    }

    @Override
    public Optional<AccountState> getAccount(byte[] blockHash, byte[] publicKeyHash) {
        byte[] root = rootStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        Trie<byte[], AccountState> trie = stateTrie.revert(root, noDeleteStore);
        return trie.get(publicKeyHash);
    }

    @Override
    public Map<byte[], AccountState> getAccounts(byte[] blockHash, Collection<byte[]> publicKeyHashes) {
        byte[] root = rootStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        Trie<byte[], AccountState> trie = stateTrie.revert(root, noDeleteStore);
        ByteArrayMap<AccountState> m = new ByteArrayMap<>();
        publicKeyHashes.forEach(x -> {
            Optional<AccountState> account = trie.get(x);
            if (!account.isPresent()) return;
            m.put(x, account.get());
        });
        return m;
    }

    @Override
    public byte[] putAccounts(
            // parent hash
            byte[] parentHash,
            byte[] blockHash,
            // updated account
            Collection<AccountState> accounts
    ) {
        if (rootStore.containsKey(blockHash))
            throw new RuntimeException(Hex.encodeHexString(blockHash) + " has exists");
        byte[] root = rootStore.get(parentHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(parentHash) + " not exists"));
        Trie<byte[], AccountState> trie = stateTrie.revert(root, noDeleteStore);
        for (AccountState state : accounts) {
            trie.put(state.getAccount().getPubkeyHash(), state);
        }
        byte[] newRoot = trie.commit();
        trie.flush();
        rootStore.put(blockHash, newRoot);
        return newRoot;
    }
}
