package org.wisdom.db;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.tdf.common.store.Store;
import org.tdf.rlp.RLPCodec;
import org.wisdom.consensus.pow.ConsensusConfig;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;


@Component
public class ProposerStateDBImpl implements ProposerStateDB {

    private Store<byte[], byte[]> proposerStore;

    private static final String PROPOSER_STORE = "proposer-store";

    private static final byte[] LAST_SYNCED_HEIGHT = "last-confirmed".getBytes(StandardCharsets.US_ASCII);

    private static final int BLOCKS_PER_UPDATE_LOWER_BOUNDS = 4096;

    private WisdomBlockChain bc;


    private CandidateUpdater updater;

    private List<String> initialProposers;

    private Store<byte[], byte[]> statusStore;

    private static final String DB_STATUS = "proposer-status";

    public ProposerStateDBImpl(
            DatabaseStoreFactory factory,
            WisdomBlockChain bc,
            CandidateUpdater updater,
            ConsensusConfig config) {
        this.bc = bc;
        this.updater = updater;
        this.initialProposers = config.getValidators();
        proposerStore = factory.create(PROPOSER_STORE, false);
        statusStore = factory.create(DB_STATUS, false);
        sync();
    }

    private void sync() {
        // query for had been written
        long lastSyncedHeight = statusStore.get(LAST_SYNCED_HEIGHT).map(RLPCodec::decodeLong).orElse(-1L);
        int blocksPerUpdate = BLOCKS_PER_UPDATE_LOWER_BOUNDS;
        ProposersCache updated = null;
        while (true) {
            List<Block> blocks = bc.getCanonicalBlocks(lastSyncedHeight + 1, blocksPerUpdate);
            for (Block block : blocks) {
                if (block.nHeight == 0) {
                    updated = updater.generateGenesisStates(initialProposers);
                } else {
                    updated = updater.updateAll(updated, block);
                }
                putProposerStates(block.getHash(), updated);
            }
            lastSyncedHeight = blocks.get(blocks.size() - 1).nHeight;
            statusStore.put(LAST_SYNCED_HEIGHT, RLPCodec.encode(lastSyncedHeight));
            if (blocks.size() < blocksPerUpdate) break;
        }
    }


    @Override
    public Optional<ProposersCache> getProposerState(byte[] blockHash) {
        byte[] proposer = proposerStore.get(blockHash)
                .orElseThrow(() -> new RuntimeException(Hex.encodeHexString(blockHash) + " not synced"));
        try {
            ProposersCache state = RLPCodec.decode(proposer, ProposersCache.class);
            return Optional.of(state);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void putProposerStates(byte[] blockHash, ProposersCache state) {
        proposerStore.put(blockHash, RLPCodec.encode(state));
    }
}
