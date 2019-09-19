package org.wisdom.consensus.pow;

import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.state.State;

import java.util.List;
import java.util.Map;

/**
 * 投票事务
 * 抵押事务
 * 撤回投票事务
 * 撤回抵押事务
 */
public class ProposersState implements State {
    public static class Proposer{
        public long mortgage;
        public long votes;
        public String publicKeyHash;
    }

    private Map<String, Proposer> proposers;

    @Override
    public State updateBlock(Block block) {
        return null;
    }

    @Override
    public State updateBlocks(List<Block> blocks) {
        return null;
    }

    @Override
    public State updateTransaction(Transaction transaction) {
        return null;
    }

    @Override
    public State copy() {
        return null;
    }
}
