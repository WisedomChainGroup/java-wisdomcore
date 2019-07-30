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

import org.springframework.scheduling.annotation.Scheduled;
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
    public static final int ORPHAN_HEIGHT_RANGE = 256;
    private BlocksCache orphans;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private PendingBlocksManager pool;

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
    private void addBlock(Block block) {
        orphans.addBlocks(Collections.singletonList(block));
    }

    // remove orphans return writable blocks，过滤掉孤块
    public BlocksCache removeAndCacheOrphans(List<Block> blocks) {
        BlocksCache cache = new BlocksCache(blocks);
        BlocksCache res = new BlocksCache();
        Block best = bc.currentHeader();
        for (Block init : cache.getInitials()) {
            List<Block> descendantBlocks = new ArrayList<>();
            descendantBlocks.add(init);
            descendantBlocks.addAll(cache.getDescendantBlocks(init));
            if (!isOrphan(init)) {
                res.addBlocks(descendantBlocks);
                continue;
            }
            for (Block b : descendantBlocks) {
                if (Math.abs(best.nHeight - b.nHeight) < ORPHAN_HEIGHT_RANGE) {
                    logger.info("add block at height = " + b.nHeight + " to orphans pool");
                    addBlock(b);
                }
            }
        }
        return res;
    }

    public List<Block> getInitials() {
        return orphans.getInitials();
    }

    private void tryWriteNonOrphans() {
        for (Block ini : orphans.getInitials()) {
            if (!isOrphan(ini)) {
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


    // 定时清理距离当前高度超过 50 的孤块
    @Scheduled(fixedRate = 30 * 1000)
    public void clearOrphans() {
        Block best = bc.currentHeader();
        for (Block init : orphans.getInitials()) {
            List<Block> descendantBlocks = new ArrayList<>();
            descendantBlocks.add(init);
            descendantBlocks.addAll(orphans.getDescendantBlocks(init));
            if (Math.abs(best.nHeight - init.nHeight) < ORPHAN_HEIGHT_RANGE) {
                orphans.deleteBlocks(descendantBlocks);
            }
        }
    }
}
