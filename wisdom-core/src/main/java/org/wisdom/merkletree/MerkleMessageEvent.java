package org.wisdom.merkletree;

import org.springframework.context.ApplicationEvent;
import org.wisdom.core.Block;

public class MerkleMessageEvent extends ApplicationEvent {

    private Block block;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param block  block that failed Merkle check
     */
    public MerkleMessageEvent(Object source, Block block) {
        super(source);
        this.block = block;
    }
}
