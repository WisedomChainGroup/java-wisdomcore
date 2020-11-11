package org.wisdom.consensus.pow;

import lombok.Value;
import org.wisdom.core.Block;

@Value
public class BlockAndTask {
    Block block;
    Runnable task;
}
