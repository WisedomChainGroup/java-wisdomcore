package org.wisdom.core.event;

import org.wisdom.core.Block;
import org.springframework.context.ApplicationEvent;

public class NewBlockEvent extends ApplicationEvent {

    private Block block;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public NewBlockEvent(Object source, Block b) {
        super(source);
        this.block = b;
    }
}
