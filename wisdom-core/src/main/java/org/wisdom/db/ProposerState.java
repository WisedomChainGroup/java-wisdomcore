package org.wisdom.db;


import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDecoding;

import java.util.Map;

public class ProposerState {

    @RLP(0)
    @RLPDecoding(as = ByteArrayMap.class)
    private Map<byte[], Proposer> proposers;

    public ProposerState() {
        this.proposers = new ByteArrayMap<>();
    }

    public ProposerState(Map<byte[], Proposer> proposers) {
        this.proposers = proposers;
    }

    public Map<byte[], Proposer> getProposers() {
        return proposers;
    }

    public void setProposers(Map<byte[], Proposer> proposers) {
        this.proposers = proposers;
    }

    public ProposerState copy() {
        ProposerState state = new ProposerState();
        state.setProposers(new ByteArrayMap<>(proposers));
        return state;
    }

    @Override
    public String toString() {
        return "ProposerState{" +
                "proposers=" + proposers +
                '}';
    }
}
