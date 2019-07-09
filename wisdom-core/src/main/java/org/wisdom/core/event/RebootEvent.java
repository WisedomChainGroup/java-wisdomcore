package org.wisdom.core.event;

import org.wisdom.core.Block;
import org.springframework.context.ApplicationEvent;

public class RebootEvent extends ApplicationEvent {
    private Block bestBlock;

    public RebootEvent(Object source, Block bestBlock) {
        super(source);
        this.bestBlock = bestBlock;
    }

    public Block getBestBlock() {
        return bestBlock;
    }
}
