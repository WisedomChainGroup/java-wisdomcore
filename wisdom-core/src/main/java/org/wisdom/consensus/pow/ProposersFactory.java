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

    private long allowMinersJoinEra;

    public void setPowWait(int powWait) {
        this.powWait = powWait;
    }

    private int powWait;

    public void setInitialProposers(List<String> initialProposers) {
        this.initialProposers = initialProposers;
    }

    public void setAllowMinerJoinEra(long allowMinersJoinEra) {
        this.allowMinersJoinEra = allowMinersJoinEra;
    }

    public List<String> getProposers(Block parentBlock) {
        boolean enableMultiMiners = allowMinersJoinEra >= 0 &&
                getEraAtBlockNumber(parentBlock.nHeight + 1, this.getBlocksPerEra()) >= allowMinersJoinEra;

        if (!enableMultiMiners && parentBlock.nHeight >= 9235) {
            return initialProposers.subList(0, 1);
        }

        if (!enableMultiMiners) {
            return initialProposers;
        }

        List<String> res;
        if (parentBlock.nHeight % getBlocksPerEra() == 0) {
            ProposersState state = (ProposersState) getFromCache(parentBlock);
            res = state.getProposers().stream().map(p -> p.publicKeyHash).collect(Collectors.toList());
        } else {
            ProposersState state = (ProposersState) getInstance(parentBlock);
            res = state.getProposers().stream().map(p -> p.publicKeyHash).collect(Collectors.toList());
        }
        if (res.size() > 0) {
            return res;
        }
        return initialProposers;

    }

    public Optional<Proposer> getProposer(Block parentBlock, long timeStamp) {
        List<String> proposers = getProposers(parentBlock);

        if (timeStamp <= parentBlock.nTime) {
            return Optional.empty();
        }

        if (parentBlock.nHeight == 0) {
            return Optional.of(new Proposer(proposers.get(0), 0, Long.MAX_VALUE));
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