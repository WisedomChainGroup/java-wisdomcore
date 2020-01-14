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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tdf.common.serialize.Codecs;
import org.tdf.common.store.Store;
import org.tdf.common.store.StoreWrapper;
import org.tdf.common.util.ChainCache;
import org.tdf.common.util.ChainCacheImpl;
import org.wisdom.core.event.NewBlockEvent;
import org.wisdom.db.BlockWrapper;
import org.wisdom.db.DatabaseStoreFactory;
import org.wisdom.db.WisdomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author sal 1564319846@qq.com
 * manage orphan blocks
 */
@Component
public class OrphanBlocksManager implements ApplicationListener<NewBlockEvent> {
    private ChainCache<BlockWrapper> orphans
            = ChainCache.<BlockWrapper>builder()
            .comparator(BlockWrapper.COMPARATOR)
            .build();

    @Autowired
    private WisdomRepository repository;

    @Autowired
    private PendingBlocksManager pool;

    @Value("${p2p.max-blocks-per-transfer}")
    private int orphanHeightsRange;

    private static final Logger logger = LoggerFactory.getLogger(OrphanBlocksManager.class);

    private Store<String, String> leveldb;

    public OrphanBlocksManager(DatabaseStoreFactory factory) {
        leveldb = new StoreWrapper<>(
                factory.create("orphans", false)
                , Codecs.STRING, Codecs.STRING);
    }

    private boolean isOrphan(Block block) {
        return !repository.containsBlock(block.hashPrevBlock);
    }

    public void addBlock(Block block) {
        orphans.add(new BlockWrapper(block));
    }

    // remove orphans return writable blocks，过滤掉孤块
    public ChainCache<BlockWrapper> removeAndCacheOrphans(List<Block> blocks) {
        ChainCache<BlockWrapper> cache =
                ChainCacheImpl.of(blocks.stream().map(BlockWrapper::new).collect(Collectors.toList()));

        ChainCache<BlockWrapper> ret = ChainCache.<BlockWrapper>builder()
                .comparator(BlockWrapper.COMPARATOR).build();

        Block best = repository.getBestBlock();
        for (BlockWrapper init : cache.getInitials()) {
            List<BlockWrapper> descendantBlocks =
                    cache.getDescendants(init.getHash().getBytes());

            if (!isOrphan(init.get())) {
                ret.addAll(descendantBlocks);
                continue;
            }

            for (BlockWrapper b : descendantBlocks) {
                if (Math.abs(best.nHeight - b.get().nHeight) < orphanHeightsRange
                        && !orphans.containsHash(b.getHash().getBytes())
                ) {
                    logger.info("add block at height = " + b.get().nHeight + " to orphans pool");
                    addBlock(b.get());
                }
            }
        }
        return ret;
    }

    public List<Block> getInitials() {
        return orphans.getInitials()
                .stream()
                .map(BlockWrapper::get)
                .collect(Collectors.toList());
    }

    private void tryWriteNonOrphans() {
        for (BlockWrapper ini : orphans.getInitials()) {
            if (!isOrphan(ini.get())) {
                logger.info("writable orphan block found in pool");
                List<BlockWrapper> descendants = orphans.getDescendants(ini.get().getHash());
                orphans.removeAll(descendants);
                pool.addPendingBlocks(
                        ChainCacheImpl.of(
                                descendants
                        )
                );
            }
        }
    }

    @Override
    public void onApplicationEvent(NewBlockEvent event) {
        tryWriteNonOrphans();
    }


    // 定时清理距离当前高度已经被确认的区块
    @Scheduled(fixedRate = 30 * 1000)
    public void clearOrphans() {
        Block lastConfirmed = repository.getLatestConfirmed();
        orphans.stream()
                .map(BlockWrapper::get)
                .filter(b -> b.nHeight <= lastConfirmed.nHeight || repository.containsBlock(b.getHash()))
                .forEach(b -> orphans.removeByHash(b.getHash()));
    }

    public List<Block> getOrphans() {
        return orphans.stream()
                .map(BlockWrapper::get)
                .collect(Collectors.toList()
                );
    }


    public void saveOrphanBlocks() {
        List<Block> list = getOrphans();
        String json = JSON.toJSONString(list, true);
        leveldb.put("OrphanBlocksPool", json);
    }

    public void loadOrphanBlocks() {
        String json = leveldb.get("OrphanBlocksPool").orElse("");
        if (!json.equals("")) {
            List<Block> blocks = JSON.parseObject(json, new TypeReference<ArrayList<Block>>() {
            });
            blocks.forEach(this::addBlock);
        }
    }

}
