package org.wisdom.consensus;

import org.wisdom.core.Block;

public interface Miner {

    void mine(Block parentHeader, long difficulty);

    void stop();
}
