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

import org.springframework.beans.factory.annotation.Value;
import org.wisdom.core.account.InitializeAccount;
import org.wisdom.util.Arrays;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.orm.BlockMapper;
import org.wisdom.core.orm.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * @author sal 1564319846@qq.com
 * block chain query wrappered by optional api
 */
@Component
public class BlockChainOptional {
    private JdbcTemplate tmpl;
    private Block genesis;

    // try to get a element from a list
    private <T> Optional<T> getOne(List<T> res) {
        return Optional.ofNullable(res).map(x -> x.get(0));
    }

    // get block body
    private Optional<List<Transaction>> getBlockBody(Block header) {
        try {
            return Optional.ofNullable(header).flatMap(h -> Optional.of(tmpl.query("select tx.*, ti.block_hash, h.height from transaction as tx inner join transaction_index as ti " +
                    "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where ti.block_hash = ? order by ti.tx_index", new Object[]{h.getHash()}, new TransactionMapper())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Block> getBlockFromHeader(Block block) {
        Optional<Block> o = Optional.ofNullable(block);
        return o.flatMap(this::getBlockBody).flatMap(x -> o.map(y -> {
            y.body = x;
            return y;
        }));
    }

    private Optional<List<Block>> getBlocksFromHeaders(List<Block> headers) {
        final Optional<List<Block>> res = Optional.of(new ArrayList<>());
        return headers.stream().map(this::getBlockFromHeader).reduce(res, (a, b) -> b.flatMap(x -> a.map(y -> {
            y.add(x);
            return y;
        })), null);
    }

    // TODO: get blocks from headers

    // write header with total weight
    private void writeHeader(Block header) {
        tmpl.update("insert into header " +
                        "(block_hash, version, hash_prev_block, " +
                        "hash_merkle_root, hash_merkle_state, hash_merkle_incubate," +
                        "height, created_at, nonce, nBits," +
                        "block_notice, is_canonical) values (?,?,?,?,?,?,?,?,?,?,?,?)",
                header.getHash(), header.nVersion, header.hashPrevBlock,
                header.hashMerkleRoot, header.hashMerkleState, header.hashMerkleIncubate, header.nHeight,
                header.nTime, header.nNonce, header.nBits,
                header.blockNotice, false);
    }


    private Optional<Block> findCommonAncestor(Block a, Block b) {
        Optional<Block> ao = Optional.ofNullable(a);
        Optional<Block> bo = Optional.ofNullable(b);
        while (true) {
            Optional<Long> ah = ao.map(x -> x.nHeight);
            Optional<Long> bh = bo.map(x -> x.nHeight);
            Optional<Integer> cmp = ah.flatMap(x -> bh.map(x::compareTo));
            if (cmp.map(x -> x > 0).orElse(false)) {
                ao = ao.map(x -> x.hashPrevBlock).flatMap(this::getHeader);
                continue;
            }
            if (cmp.map(x -> x < 0).orElse(false)) {
                bo = bo.map(x -> x.hashPrevBlock).flatMap(this::getHeader);
                continue;
            }
            break;
        }
        while (true) {
            Optional<byte[]> ahash = ao.map(Block::getHash);
            Optional<byte[]> bhash = bo.map(Block::getHash);
            if (ahash.flatMap(x -> bhash.map(y -> Arrays.areEqual(x, y))).orElse(false)) {
                break;
            }
            ao = ao.map(x -> x.hashPrevBlock).flatMap(this::getHeader);
            bo = bo.map(x -> x.hashPrevBlock).flatMap(this::getHeader);
        }
        return ao;
    }

    public BlockChainOptional() {

    }

    public Optional<Boolean> hasBlock(byte[] hash) {
        return Optional.ofNullable(hash).flatMap(h -> {
            try {
                return Optional.of(tmpl.queryForObject("select count(*) from header where block_hash = ? limit 1", new Object[]{hash}, Integer.class) > 0);
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    @Autowired
    public BlockChainOptional(JdbcTemplate tmpl, TransactionTemplate txTmpl, Block genesis, ApplicationContext ctx, @Value("${spring.datasource.username}") String dataname, InitializeAccount account) {
        this.tmpl = tmpl;
        this.genesis = genesis;
    }

    public Optional<Block> currentHeader() {
        try {
            return getOne(tmpl.query("select * from header where is_canonical = true order by total_weight desc limit 1", new BlockMapper()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Block> currentBlock() {
        return currentHeader().flatMap(this::getBlockFromHeader);
    }

    public Optional<Block> getHeader(byte[] hash) {
        return Optional.ofNullable(hash).flatMap(h -> getOne(tmpl.query("select * from header where block_hash = ?", new Object[]{h}, new BlockMapper())));
    }

    public Optional<Block> getBlock(byte[] hash) {
        return getHeader(hash).flatMap(this::getBlockFromHeader);
    }

    public Optional<List<Block>> getHeaders(long startHeight, int headersCount) {
        try {
            return Optional.of(tmpl.query("select * from header where height >= ? and height <= ? order by height limit ?", new Object[]{startHeight, startHeight + headersCount - 1, headersCount}, new BlockMapper()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<List<Block>> getBlocks(long startHeight, int headersCount) {
        return getHeaders(startHeight, headersCount).flatMap(this::getBlocksFromHeaders);
    }

    public Optional<List<Block>> getBlocks(long startHeight, long stopHeight) {
        try {
            return Optional.of(tmpl.query("select * from header where height >= ? and height <= ? order by height", new Object[]{startHeight, stopHeight}, new BlockMapper())).flatMap(this::getBlocksFromHeaders);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<List<Block>> getBlocks(long startHeight, long stopHeight, int sizeLimit) {
        try {
            return Optional.of(tmpl.query("select * from header where height >= ? and height <= ? order by height limit ?", new Object[]{startHeight, stopHeight, sizeLimit}, new BlockMapper())).flatMap(this::getBlocksFromHeaders);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<List<Block>> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        if (!clipInitial) {
            return getBlocks(startHeight, stopHeight, sizeLimit);
        }
        try {
            Optional<List<Block>> res = Optional.of(tmpl.query("select * from header where height >= ? and height <= ? order by height desc limit ?", new Object[]{startHeight, stopHeight, sizeLimit}, new BlockMapper())).flatMap(this::getBlocksFromHeaders);
            res.ifPresent(Collections::reverse);
            return res;
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    public Optional<Block> getCanonicalHeader(long num) {
        return getOne(tmpl.query("select * from header where height = ? and is_canonical = true", new Object[]{num}, new BlockMapper()));
    }

    public Optional<List<Block>> getCanonicalHeaders(long start, int size) {
        try {
            return Optional.of(tmpl.query("select * from header where height < ? and height >= ? and is_canonical = true order by height", new Object[]{start + size, start}, new BlockMapper()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Block> getCanonicalBlock(long num) {
        return getCanonicalHeader(num).flatMap(this::getBlockFromHeader);
    }

    public Block getGenesis() {
        return genesis;
    }

    public Optional<List<Block>> getCanonicalBlocks(long start, int size) {
        return getCanonicalHeaders(start, size).flatMap(this::getBlocksFromHeaders);
    }

    public Optional<Boolean> isCanonical(byte[] hash) {
        return Optional.ofNullable(hash).flatMap(h -> {
            try {
                return Optional.of(tmpl.queryForObject("select is_canonical from header where block_hash = ?", new Object[]{hash}, Boolean.class));
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    public Optional<Block> findAncestorHeader(byte[] bhash, long anum) {
        Optional<Block> bHeader = getHeader(bhash);
        while (bHeader.map(b -> b.nHeight > anum).orElse(false)) {
            bHeader = bHeader.flatMap(b -> getHeader(b.hashPrevBlock));
        }
        return bHeader;
    }

    public Optional<Block> findAncestorBlock(byte[] bhash, long anum) {
        return findAncestorHeader(bhash, anum).flatMap(this::getBlockFromHeader);
    }

    public Optional<List<Block>> getAncestorHeaders(byte[] bhash, long anum) {
        List<Block> headers = new ArrayList<>();
        Optional<List<Block>> res = Optional.of(headers);

        Optional<Block> bHeader = getHeader(bhash);
        while (bHeader.map(x -> x.nHeight < anum).orElse(false)) {
            res = bHeader.map(x -> {
                headers.add(x);
                return headers;
            });
            bHeader = bHeader.flatMap(h -> getHeader(h.hashPrevBlock));
        }
        res.ifPresent(Collections::reverse);
        return res;
    }

    public Optional<List<Block>> getAncestorBlocks(byte[] bhash, long anum) {
        return getAncestorHeaders(bhash, anum).flatMap(this::getBlocksFromHeaders);
    }

    public Optional<Long> getCurrentTotalWeight() {
        try {
            return Optional.of(tmpl.queryForObject("select max(total_weight) from header", null, Long.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Boolean> hasTransaction(byte[] txHash) {
        try {
            return Optional.ofNullable(txHash).map(h -> (tmpl.queryForObject("select count(*) from transaction as tx " +
                    "inner join transaction_index as ti on tx.tx_hash = ti.tx_hash " +
                    "inner join header as h on ti.block_hash = h.block_hash " +
                    "where tx.tx_hash = ? and h.is_canonical = true limit 1", new Object[]{h}, Integer.class) > 0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Transaction> getTransaction(byte[] txHash) {
        try {
            return getOne(tmpl.query(
                    "select tx.*, h.height as height, ti.block_hash as block_hash from transaction as tx " +
                            "inner join transaction_index as ti on tx.tx_hash = ti.tx_hash " +
                            "inner join header as h on ti.block_hash = h.block_hash" +
                            " where tx.tx_hash = ? and h.is_canonical = true", new Object[]{txHash}, new TransactionMapper()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
