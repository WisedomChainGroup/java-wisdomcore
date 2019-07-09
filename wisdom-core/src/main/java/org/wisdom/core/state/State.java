package org.wisdom.core.state;

import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;

import java.util.List;

public interface State {
    // transition and return self
    State updateBlock(Block block);

    // transition blocks and return self
    State updateBlocks(List<Block> blocks);

    // transition transaction and return self
    State updateTransaction(Transaction transaction);

    // deep copy
    State copy();
}
