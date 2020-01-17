package org.wisdom.db;

import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.core.Block;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractStateUpdater<T> implements StateUpdater<T> {
    Set<byte[]> getRelatedKeys(Block block) {
        Set<byte[]> set = new ByteArraySet();
        block.body.forEach(tx -> set.addAll(this.getRelatedKeys(tx)));
        return set;
    }

    Set<byte[]> getRelatedKeys(List<Block> block) {
        Set<byte[]> set = new ByteArraySet();
        block.forEach(b -> set.addAll(getRelatedKeys(b)));
        return set;
    }

    @Override
    public Map<byte[], T> update(Map<byte[], T> beforeUpdate, Collection<? extends TransactionInfo> transactionInfos) {
        Map<byte[], T> ret = new ByteArrayMap<>(beforeUpdate);
        transactionInfos.forEach(info -> {
            getRelatedKeys(info.getTransaction()).forEach(k -> {
                ret.put(k, update(k, ret.get(k), info));
            });
        });
        return ret;
    }

    @Override
    public Map<byte[], T> update(Map<byte[], T> beforeUpdate, Block block) {
        return update(
                beforeUpdate,
                block.body.stream()
                        .map(t -> TransactionInfo
                                .builder()
                                .transaction(t)
                                .height(block.nHeight)
                                .build()
                        )
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Map<byte[], T> update(Map<byte[], T> beforeUpdate, List<Block> blocks) {
        Map<byte[], T> ret = new ByteArrayMap<>(beforeUpdate);
        blocks.forEach(b -> ret.putAll(update(ret, b)));
        return ret;
    }
}
