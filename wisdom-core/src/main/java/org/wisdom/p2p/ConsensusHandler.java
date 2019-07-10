package org.wisdom.p2p;

import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.p2p.entity.Status;

import java.util.List;

@Component
public class ConsensusHandler {
    public PeerAction onProposal(Block b){
        return PeerAction.KEEP_ALIVE;
    }

    public PeerAction onTransaction(Transaction tx){
        return PeerAction.KEEP_ALIVE;
    }

    public PeerAction onBlocks(List<Block> blocks){
        return PeerAction.KEEP_ALIVE;
    }

    public PeerAction onStatus(Status status){
        return PeerAction.KEEP_ALIVE;
    }
}
