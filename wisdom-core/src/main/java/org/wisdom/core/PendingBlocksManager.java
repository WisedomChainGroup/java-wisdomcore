package org.wisdom.core;

import org.wisdom.core.validate.CompositeBlockRule;
import org.wisdom.core.validate.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PendingBlocksManager {

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private CompositeBlockRule rule;

    private Logger logger = LoggerFactory.getLogger(PendingBlocksManager.class);

    // 区块的写入全部走这里
    @Async
    public void addPendingBlocks(BlocksCache cache) {
        for(List<Block> chain: cache.popLongestChains()){
            if (chainHasWritten(chain)) {
                continue;
            }
            logger.info("try to write blocks to local storage, size = " + chain.size());
            for (Block b : chain) {
                if(bc.hasBlock(b.getHash())){
                    logger.info("the block has written");
                    continue;
                }
                Result res = rule.validateBlock(b);
                if (!res.isSuccess()) {
                    logger.error("validate the block fail error = " + res.getMessage());
                    return;
                }
                b.weight = 1;
                bc.writeBlock(b);
            }
        }
    }

    private boolean chainHasWritten(List<Block> chain){
        if(chain == null || chain.size() == 0){
            return true;
        }
        return bc.hasBlock(chain.get(chain.size() - 1).getHash());
    }
}
