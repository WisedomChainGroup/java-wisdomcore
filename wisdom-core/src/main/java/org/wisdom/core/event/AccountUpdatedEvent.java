package org.wisdom.core.event;

import org.wisdom.core.Block;
import org.springframework.context.ApplicationEvent;

public class AccountUpdatedEvent extends ApplicationEvent {
    private Block bestBlock;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AccountUpdatedEvent(Object source, Block bestBlock) {
        super(source);
        this.bestBlock = bestBlock;
    }

    public Block getBestBlock() {
        return bestBlock;
    }
}
