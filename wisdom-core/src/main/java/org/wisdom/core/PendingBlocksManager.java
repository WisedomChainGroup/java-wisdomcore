/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.core;

import org.wisdom.core.validate.CompositeBlockRule;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.core.validate.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.wisdom.db.StateDB;
import org.wisdom.merkletree.MerkleTreeManager;

import java.util.List;

@Component
public class PendingBlocksManager {

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private StateDB stateDB;

    @Autowired
    private CompositeBlockRule rule;

    @Autowired
    private MerkleRule merkleRule;

    private Logger logger = LoggerFactory.getLogger(PendingBlocksManager.class);

    @Autowired
    private MerkleTreeManager merkleTreeManager;

    // 区块的写入全部走这里
    @Async
    public void addPendingBlocks(BlocksCache cache) {
        for (List<Block> chain : cache.popLongestChains()) {
            if (chainHasWritten(chain)) {
                continue;
            }
            logger.info("try to write blocks to local storage, size = " + chain.size());
            for (Block b : chain) {
                if (stateDB.hasBlock(b.getHash())) {
                    logger.info("the block has written");
                    continue;
                }
                Result res = rule.validateBlock(b);
                if (!res.isSuccess()) {
                    logger.error("validate the block fail error = " + res.getMessage());
                    return;
                }
                Result result = merkleRule.validateBlock(b);
                if (!result.isSuccess()) {
                    merkleTreeManager.writeBlockToCache(b);
                    continue;
                }
                b.weight = 1;
                stateDB.writeBlock(b);
            }
        }
    }

    private boolean chainHasWritten(List<Block> chain) {
        if (chain == null || chain.size() == 0) {
            return true;
        }
        byte[] hash = chain.get(chain.size() - 1).getHash();
        return stateDB.hasBlock(hash) || bc.hasBlock(hash);
    }
}
