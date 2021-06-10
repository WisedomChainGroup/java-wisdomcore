package org.wisdom.db;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tdf.common.store.DatabaseStore;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.controller.WebSocket;
import org.wisdom.core.*;
import org.wisdom.core.account.Transaction;
import org.wisdom.vm.abi.*;
import org.wisdom.vm.hosts.Limit;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AccountStateTrie extends StateTrieAdapter<AccountState> {

    // mark whether the state of the block had been stored
    // block hash -> long
    private Store<byte[], Long> statusStore;

    // block hash -> state root

    private WisdomBlockChain bc;

    private AccountStateUpdater accountStateUpdater;

    @Override
    protected String getPrefix() {
        return "account";
    }

    private DB getDB(byte[] root) {
        return new DBImpl(getTrie().revert(root), storageTrie, contractCodeStore);
    }


    private DatabaseStore contractCodeStore;
    private Trie<byte[], byte[]> storageTrie;
    private EconomicModel economicModel;

    public AccountStateTrie(
            DatabaseStoreFactory factory,
            Block genesis,
            WisdomBlockChain bc,
            AccountStateUpdater accountStateUpdater,
            @Qualifier("contractCodeStore") DatabaseStore databaseStore,
            @Qualifier("storageTrie") Trie<byte[], byte[]> storageTrie,
            EconomicModel economicModel
    ) throws Exception {
        super(AccountState.class, accountStateUpdater.getGenesisStates(), genesis, factory, true, false, new AbstractStateUpdater<AccountState>() {
            @Override
            public Map<byte[], AccountState> getGenesisStates() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set<byte[]> getRelatedKeys(Transaction transaction, Map<byte[], AccountState> store) {
                throw new UnsupportedOperationException();
            }

            @Override
            public AccountState createEmpty(byte[] id) {
                return new AccountState(id);
            }

            @Override
            public AccountState update(Map<byte[], AccountState> beforeUpdate, byte[] id, AccountState state, TransactionInfo info) {
                throw new UnsupportedOperationException();
            }
        });
        this.bc = bc;
        this.accountStateUpdater = accountStateUpdater;
        this.accountStateUpdater.setWisdomBlockChain(bc);
        this.contractCodeStore = databaseStore;
        this.storageTrie = storageTrie;
        this.economicModel = economicModel;
    }

    public WASMResult update(Trie<byte[], AccountState> trie, Header header, Transaction tx) {
        DB db = new DBImpl(trie, storageTrie, contractCodeStore);
        return accountStateUpdater.update(db, header, tx);
    }

    @Override
    public byte[] commit(Block block) {
        if (block.nHeight == 0) throw new RuntimeException("cannot commit genesis block");
        Optional<byte[]> o = getRootStore().get(block.getHash());
        if (o.isPresent())
            return o.get();
        byte[] parentRoot = getRootStore().get(block.hashPrevBlock)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(block.hashPrevBlock) + " not exists"));

        Trie<byte[], AccountState> tmp = getTrie().revert(parentRoot);
        long fee = 0;

        Map<byte[], WASMResult> results = new ByteArrayMap<>();
        Transaction coinbase = block.body.get(0);
        for (Transaction tx : block.body.subList(1, block.body.size())) {
            WASMResult result = update(tmp, block, tx);
            results.put(tx.getHash(), result);
            fee = SafeMath.add(fee, result.getGasUsed() * tx.gasPrice);
            if (tx.type == Transaction.Type.COINBASE.ordinal()) {
                throw new RuntimeException("a block contains at most one coin base ");
            }
        }


        // check amount = consensus amount + fees
        if (coinbase.amount != SafeMath.add(economicModel.getConsensusRewardAtHeight1(block.nHeight), fee)) {
            throw new RuntimeException("amount not equals to consensus reward plus fees");
        }

        // update coinbase
        update(tmp, block, coinbase);
        byte[] newRoot = tmp.commit();

        for (Transaction tx : block.body.subList(1, block.body.size())) {
            WASMResult re = results.get(tx.getHash());
            WebSocket.broadcastIncluded(tx, block.nHeight, block.getHash(), re.getGasUsed(), re.getReturns(), re.getWASMEvents());
        }

        if (block.accountStateTrieRoot == null || block.accountStateTrieRoot.length == 0 || (FastByteComparisons.equal(newRoot, block.accountStateTrieRoot))) {
            tmp.flush();
            getRootStore().put(block.getHash(), newRoot);
            return newRoot;
        }


        throw new RuntimeException(
                String.format(
                        "state root not match at height %d hash %s state root local = %s state root remote = %s",
                        block.getnHeight(),
                        block.getHashHexString(),
                        HexBytes.fromBytes(newRoot),
                        HexBytes.fromBytes(block.accountStateTrieRoot)
                )
        );
    }

    public byte[] call(byte[] root, byte[] pkHash, String method, Parameters parameters) {
        // execute method
        Limit limit = new Limit();

        ContractCall contractCall = new ContractCall(
                getTrieByRootHash(root).asMap(), null,
                null, r -> storageTrie.revert(r),
                contractCodeStore,
                limit, 0, null,
                true, new AtomicInteger()
        );

        return contractCall.call(
                pkHash,
                method,
                parameters, Uint256.ZERO,
                false, null
        ).getReturns().getEncoded();
    }

}