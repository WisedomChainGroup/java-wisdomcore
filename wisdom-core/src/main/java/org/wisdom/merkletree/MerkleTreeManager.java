package org.wisdom.merkletree;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.util.List;
import java.util.Map;

@Component
public class MerkleTreeManager {

    private MerkleTreeCache cache;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private WisdomBlockChain bc;

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

    public Block replaceTransaction(String blockHash, List<MerkleTransaction> trans) {
        if (cache.containBlock(blockHash)) {
            return cache.replaceTransaction(blockHash, trans);
        }
        return null;
    }

    public Block getCacheBlock(String blockHash) {
        return cache.getCacheBlock(blockHash);
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void clearMerkleCache() {
        Map<String, Block> maps = cache.getCacheBlocks();
        for (Map.Entry<String, Block> entry : maps.entrySet()) {
            String blockHash = entry.getKey();
            try {
                boolean exist = bc.containsBlock(Hex.decodeHex(blockHash.toCharArray()));
                if (exist) {
                    cache.removeBlock(blockHash);
                }
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        }
    }

}
