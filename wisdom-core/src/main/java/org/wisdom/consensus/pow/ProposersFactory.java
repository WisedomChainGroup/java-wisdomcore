package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.Block;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.core.state.State;
import org.wisdom.db.StateDB;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProposersFactory extends EraLinkedStateFactory {
    public ProposersFactory(StateDB stateDB, int cacheSize, State genesisState, int blocksPerEra) {
        super(stateDB, cacheSize, genesisState, blocksPerEra);
    }

    private List<String> initialProposers;

    private long allowMinerJoinEra;

    public void setPowWait(int powWait) {
        this.powWait = powWait;
    }

    private int powWait;

    public void setInitialProposers(List<String> initialProposers) {
        this.initialProposers = initialProposers;
    }

    public void setAllowMinerJoinEra(long allowMinerJoinEra) {
        this.allowMinerJoinEra = allowMinerJoinEra;
    }

    public Optional<Proposer> getProposer(Block parentBlock, long timeStamp) {
        List<String> proposers = initialProposers;

        if (timeStamp <= parentBlock.nTime) {
            return Optional.empty();
        }
        if (parentBlock.nHeight == 0) {
            return Optional.of(new Proposer(initialProposers.get(0), 0, Long.MAX_VALUE));
        }

        boolean enableMultiMiners = getEraAtBlockNumber(parentBlock.nHeight + 1) >= allowMinerJoinEra;

        // 到了开启多节点挖矿的纪元
        if (enableMultiMiners) {
            if (parentBlock.nHeight % getBlocksPerEra() == 0) {
                ProposersState state = (ProposersState) getFromCache(parentBlock);
                proposers = state.getProposers().stream().map(p -> p.publicKeyHash).collect(Collectors.toList());
            } else {
                ProposersState state = (ProposersState) getInstance(parentBlock);
                proposers = state.getProposers().stream().map(p -> p.publicKeyHash).collect(Collectors.toList());
            }
        }

        // 9236 开始单机挖矿
        if (parentBlock.nHeight >= 9235 && !enableMultiMiners) {
            return Optional.of(new Proposer(initialProposers.get(0), -1, Long.MAX_VALUE));
        }

        long step = (timeStamp - parentBlock.nTime)
                / powWait + 1;
        String lastValidator = Hex
                .encodeHexString(
                        parentBlock.body.get(0).to
                );
        int lastValidatorIndex = proposers
                .indexOf(lastValidator);
        int currentValidatorIndex = (int) (lastValidatorIndex + step) % proposers.size();
        long endTime = parentBlock.nTime + step * powWait;
        long startTime = endTime - powWait;
        String validator = proposers.get(currentValidatorIndex);
        return Optional.of(new Proposer(
                validator,
                startTime,
                endTime
        ));
    }
}
