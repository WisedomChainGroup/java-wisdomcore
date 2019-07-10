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

import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.event.NewBlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author sal 1564319846@qq.com
 * manage orphan blocks
 */
@Component
public class OrphanBlocksManager implements ApplicationListener<NewBlockEvent> {
    private BlocksCache orphans;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private PendingBlocksManager pool;

    @Autowired
    private JSONEncodeDecoder codec;

    private static final Logger logger = LoggerFactory.getLogger(OrphanBlocksManager.class);



    public OrphanBlocksManager() {
        this.orphans = new BlocksCache();
    }

    private boolean isOrphan(Block block) {
        return !bc.hasBlock(block.hashPrevBlock);
    }

    // TODO: 同步过程中对收到孤块的处理
    // 1. 孤块池大小要作限制
    // 2. 验证不过的区块加入黑名单 以后拒绝将这个区块加入孤块池
    // 3. 在孤块池大小溢出情况下，尽可能保留区块高度小的区块
    // try to add blocks without orphan checking
    private void addBlocks(List<Block> blocks) {
        Block currentHeader = bc.currentHeader();
        BlocksCache cache = new BlocksCache(blocks);
        for (Block b : cache.getLeaves()) {
            if (b.nHeight >= currentHeader.nHeight) {
                orphans.addBlocks(cache.getAncestors(b));
                orphans.addBlocks(Collections.singletonList(b));
                logger.info("add blocks to orphan pool size = " + (cache.getAncestors(b).size()+1));
                continue;
            }
            if (orphans.hasChildrenOf(b.getHash())) {
                orphans.addBlocks(cache.getAncestors(b));
                orphans.addBlocks(Collections.singletonList(b));
                logger.info("add blocks to orphan pool size = " + (cache.getAncestors(b).size()+1));
            }
        }
    }

    // remove orphans return writable blocks
    public BlocksCache removeAndCacheOrphans(List<Block> blocks) {
        BlocksCache cache = new BlocksCache(blocks);
        for (Block init : cache.getInitials()) {
            if (isOrphan(init)) {
                List<Block> descendantBlocks = new ArrayList<>();
                descendantBlocks.add(init);
                descendantBlocks.addAll(cache.getDescendantBlocks(init));
                addBlocks(descendantBlocks);
                cache.deleteBlocks(descendantBlocks);
            }
        }
        logger.info("orphan initials size = " + orphans.getInitials().size());
        tryWriteNonOrphans();
        return cache;
    }

    public List<Block> getInitials() {
        return orphans.getInitials();
    }

    private void tryWriteNonOrphans(){
        for(Block ini: orphans.getInitials()){
            if (!isOrphan(ini)){
                logger.info("writable orphan block found in pool");
                List<Block> descendants = orphans.getDescendantBlocks(ini);
                descendants.add(ini);
                orphans.deleteBlocks(descendants);
                pool.addPendingBlocks(new BlocksCache(descendants));
            }
        }
    }

    @Override
    public void onApplicationEvent(NewBlockEvent event) {
        tryWriteNonOrphans();
    }
}