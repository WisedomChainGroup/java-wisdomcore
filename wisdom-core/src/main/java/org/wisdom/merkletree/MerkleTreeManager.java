package org.wisdom.merkletree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.p2p.WisdomOuterClass;

import java.util.List;

@Component
public class MerkleTreeManager {

    private MerkleTreeCache cache;

    @Autowired
    private ApplicationContext ctx;

    public MerkleTreeManager() {
        this.cache = new MerkleTreeCache();
    }

    public void writeBlockToCache(Block block) {
        if (!cache.containBlock(block)) {
            cache.addBlock(block);
            ctx.publishEvent(new MerkleMessageEvent(this, block));
        }
    }

    public void removeBlockToCache(String blockHash) {
        if (cache.containBlock(blockHash)) {
            cache.removeBlock(blockHash);
        }
    }

    public Block replaceTransaction(String blockHash, List<WisdomOuterClass.MerkleTransaction> trans) {
        if (cache.containBlock(blockHash)) {
            return cache.replaceTransaction(blockHash, trans);
        }
        return null;
    }

    public Block getCacheBlock(String blockHash) {
        return cache.getCacheBlock(blockHash);
    }

}
