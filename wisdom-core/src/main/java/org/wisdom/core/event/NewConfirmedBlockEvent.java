package org.wisdom.core.event;

import org.wisdom.core.Block;
import org.springframework.context.ApplicationEvent;

public class NewConfirmedBlockEvent extends ApplicationEvent {
    private Block block;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public NewConfirmedBlockEvent(Object source, Block block) {
        super(source);
        this.block = block;
    }
}
