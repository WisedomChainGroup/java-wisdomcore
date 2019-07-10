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

import org.wisdom.util.Arrays;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.event.NewBestBlockEvent;
import org.wisdom.core.event.NewBlockEvent;
import org.wisdom.core.orm.BlockMapper;
import org.wisdom.core.orm.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

// TODO: use read/write lock, handle null pointer exception

/**
 * @author sal 1564319846@qq.com
 * block query/write based on relational database with concurrently safety
 */
@Component
public class RDBMSBlockChainImpl implements WisdomBlockChain {
    private JdbcTemplate tmpl;
    private TransactionTemplate txTmpl;
    private Block genesis;
    private ApplicationContext ctx;
    private static final Logger logger = LoggerFactory.getLogger(RDBMSBlockChainImpl.class);


    private <T> T getOne(List<T> res) {
        if (res.size() == 0) {
            return null;
        }
        return res.get(0);
    }

    private void clearData() {
        tmpl.batchUpdate("delete  from header where 1 = 1",
                "delete from transaction where 1 = 1",
                "delete from transaction_index where 1 = 1",
                "delete from account where 1 = 1",
                "delete from incubator_state where 1 = 1");
    }

    // get block body
    // TODO: use view
    private List<Transaction> getBlockBody(Block header) {
        return tmpl.query("select tx.*, ti.block_hash, h.height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where ti.block_hash = ? order by ti.tx_index", new Object[]{header.getHash()}, new TransactionMapper());
    }

    private Block getBlockFromHeader(Block block) {
        block.body = getBlockBody(block);
        return block;
    }

    private List<Block> getBlocksFromHeaders(List<Block> headers) {
        List<Block> res = new ArrayList<>();
        for (Block h : headers) {
            res.add(getBlockFromHeader(h));
        }
        return res;
    }

    // TODO: get blocks from headers

    private long getTotalWeight(byte[] hash) {
        try {
            tmpl.queryForObject("select total_weight from header where block_hash = ?", new Object[]{
                    hash
            }, Long.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

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

    private void writeTotalWeight(byte[] blockHash, long totalWeight) {
        tmpl.update("update header set total_weight = ? where block_hash = ?", totalWeight, blockHash);
    }

    private void writeTransactions(List<Transaction> txs) {
        if (txs == null || txs.size() == 0) {
            return;
        }
        List<Object[]> args0 = new ArrayList<>();
        for (Transaction tx : txs) {
            args0.add(new Object[]{
                    tx.version,
                    tx.getHash(), tx.type, tx.nonce,
                    tx.from, tx.gasPrice, tx.amount,
                    tx.payload, tx.signature, tx.to
            });
        }
        tmpl.batchUpdate("insert into transaction (" +
                "version, tx_hash, type, nonce, " +
                "\"from\", gas_price, amount, " +
                "payload, signature, \"to\") VALUES (?, ?,?,?,?,?,?,?,?,?) on conflict(tx_hash) do nothing", args0);
    }

    private void writeBody(Block block) {
        if (block.body == null || block.body.size() == 0) {
            return;
        }
        List<Object[]> args = new ArrayList<>();
        for (int i = 0; i < block.body.size(); i++) {
            Transaction tx = block.body.get(i);
            args.add(new Object[]{
                    block.getHash(), tx.getHash(), i
            });
        }
        tmpl.batchUpdate("insert into transaction_index (block_hash, tx_hash, tx_index) values (?,?,?)", args);
        writeTransactions(block.body);
    }

    private void deleteCanonical(long height) {
        tmpl.update("update header set is_canonical = false where height = ?", height);
    }

    private void deleteCanonicals(long start, long end) {
        tmpl.update("update header set is_canonical = false where height >= ? and height <= ?", new Object[]{start, end});
    }

    private void setCanonical(byte[] hash) {
        tmpl.update("update header set is_canonical = true where block_hash = ?", hash);
    }

    private void setCanonicals(List<byte[]> hashes) {
        List<Object[]> args = new ArrayList<>();
        for (byte[] hash : hashes) {
            args.add(new Object[]{hash});
        }
        tmpl.batchUpdate("update header set is_canonical = true where block_hash = ?", args);
    }

    private boolean dbHasGenesis() {
        return tmpl.queryForObject("select count(*) from header where height = 0 limit 1", new Object[]{}, Integer.class) > 0;
    }

    // TODO: use procedure
    private Block findCommonAncestor(Block a, Block b) {
        for (long bn = b.nHeight; a.nHeight > bn; ) {
            a = getHeader(a.hashPrevBlock);
            if (a == null) {
                return null;
            }
        }
        for (long an = a.nHeight; an < b.nHeight; ) {
            b = getHeader(b.hashPrevBlock);
            if (b == null) {
                return null;
            }
        }
        while (!Arrays.areEqual(a.getHash(), b.getHash())) {
            a = getHeader(a.hashPrevBlock);
            if (a == null) {
                return null;
            }
            b = getHeader(b.hashPrevBlock);
            if (b == null) {
                return null;
            }
        }
        return a;
    }

    public void writeGenesis(Block genesis) {
        // null pointer execption
        txTmpl.execute((TransactionStatus status) -> {
            writeHeader(genesis);
            setCanonical(genesis.getHash());
            writeBody(genesis);
            return null;
        });
    }

    public RDBMSBlockChainImpl() {

    }

    public boolean hasBlock(byte[] hash) {
        return tmpl.queryForObject("select count(*) from header where block_hash = ? limit 1", new Object[]{hash}, Integer.class) > 0;
    }

    @Autowired
    public RDBMSBlockChainImpl(JdbcTemplate tmpl, TransactionTemplate txTmpl, Block genesis, ApplicationContext ctx) {
        this.tmpl = tmpl;
        this.txTmpl = txTmpl;
        this.genesis = genesis;
        this.ctx = ctx;
        if (!dbHasGenesis()) {
            clearData();
            writeGenesis(genesis);
            return;
        }
        Block dbGenesis = getCanonicalHeader(0);
        if (!Arrays.areEqual(dbGenesis.getHash(), genesis.getHash())) {
            clearData();
            writeGenesis(genesis);
        }
    }

    @Override
    public Block currentHeader() {
        return getOne(tmpl.query("select * from header where is_canonical = true order by total_weight desc limit 1", new BlockMapper()));
    }

    @Override
    public Block currentBlock() {
        return getBlockFromHeader(currentHeader());
    }

    @Override
    public Block getHeader(byte[] hash) {
        return getOne(tmpl.query("select * from header where block_hash = ?", new Object[]{hash}, new BlockMapper()));
    }

    @Override
    public Block getBlock(byte[] hash) {
        return getBlockFromHeader(getHeader(hash));
    }

    @Override
    public List<Block> getHeaders(long startHeight, int headersCount) {
        return tmpl.query("select * from header where height >= ? and height <= ? order by height limit ?", new Object[]{startHeight, startHeight + headersCount - 1, headersCount}, new BlockMapper());
    }

    @Override
    public List<Block> getBlocks(long startHeight, int headersCount) {
        return getBlocksFromHeaders(getHeaders(startHeight, headersCount));
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight) {
        return getBlocksFromHeaders(tmpl.query("select * from header where height >= ? and height <= ? order by height", new Object[]{startHeight, stopHeight}, new BlockMapper()));
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit) {
        return getBlocksFromHeaders(tmpl.query("select * from header where height >= ? and height <= ? order by height limit ?", new Object[]{startHeight, stopHeight, sizeLimit}, new BlockMapper()));
    }

    @Override
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipFromStop) {
        if (!clipFromStop) {
            return getBlocks(startHeight, stopHeight, sizeLimit);
        }
        List<Block> blocks = getBlocksFromHeaders(tmpl.query("select * from header where height >= ? and height <= ? order by height desc limit ?", new Object[]{startHeight, stopHeight, sizeLimit}, new BlockMapper()));
        if (blocks.size() == 0) {
            return blocks;
        }
        Collections.reverse(blocks);
        return blocks;
    }

    @Override
    public Block getCanonicalHeader(long num) {
        return getOne(tmpl.query("select * from header where height = ? and is_canonical = true", new Object[]{num}, new BlockMapper()));
    }

    @Override
    public List<Block> getCanonicalHeaders(long start, int size) {
        return tmpl.query("select * from header where height < ? and height >= ? and is_canonical = true order by height", new Object[]{start + size, start}, new BlockMapper());
    }

    @Override
    public Block getCanonicalBlock(long num) {
        return getBlockFromHeader(getCanonicalHeader(num));
    }

    @Override
    public Block getGenesis() {
        return genesis;
    }

    @Override
    public List<Block> getCanonicalBlocks(long start, int size) {
        List<Block> headers = getCanonicalHeaders(start, size);
        return getBlocksFromHeaders(headers);
    }

    @Override
    public boolean isCanonical(byte[] hash) {
        return tmpl.queryForObject("select is_canonical from header where block_hash = ?", new Object[]{hash}, Boolean.class);
    }

    @Override
    public synchronized void writeBlock(Block block) {
        Block parentHeader = getHeader(block.hashPrevBlock);
        if (parentHeader == null) {
            // cannot find parent, write fail
            return;
        }
        long ptw = parentHeader.totalWeight;
        Block headHeader = currentHeader();
        long localTW = getTotalWeight(headHeader.getHash());
        long externTW = block.weight + ptw;
        block.totalWeight = ptw + block.weight;

        boolean isNewHeadBlock = externTW > localTW;
        boolean refork = isNewHeadBlock && !Arrays.areEqual(headHeader.getHash(), block.hashPrevBlock);
        Block commonAncestor = refork ? findCommonAncestor(parentHeader, headHeader) : null;
        List<Block> canonicalHeaders = refork ? getAncestorHeaders(parentHeader.getHash(), commonAncestor.nHeight + 1) : null;

        Boolean result = txTmpl.execute((TransactionStatus status) -> {
            try {
                writeHeader(block);
                writeTotalWeight(block.getHash(), block.totalWeight);
                writeBody(block);
                if (!isNewHeadBlock) {
                    return true;
                }
                if (!refork) {
                    setCanonical(block.getHash());
                    return true;
                }
                // delete previous fork's canonical hash
                deleteCanonicals(commonAncestor.nHeight + 1, headHeader.nHeight);

                List<byte[]> hashes = new ArrayList<>();
                // update canonical headers
                for (Block h : canonicalHeaders) {
                    hashes.add(h.getHash());
                }
                hashes.add(block.getHash());
                setCanonicals(hashes);
            } catch (Exception e) {
                status.setRollbackOnly();
                return false;
            }
            return true;
        });
        if (result != null && result) {
            ctx.publishEvent(new NewBlockEvent(this, block));
        }
        if (result != null && isNewHeadBlock && result) {
            ctx.publishEvent(new NewBestBlockEvent(this, block));
        }
    }

    @Override
    public Block findAncestorHeader(byte[] bhash, long anum) {
        Block bHeader = getHeader(bhash);
        while (bHeader.nHeight != anum) {
            bHeader = getHeader(bHeader.hashPrevBlock);
        }
        return bHeader;
    }

    @Override
    public Block findAncestorBlock(byte[] bhash, long anum) {
        return getBlockFromHeader(findAncestorHeader(bhash, anum));
    }

    @Override
    public List<Block> getAncestorHeaders(byte[] bhash, long anum) {
        List<Block> headers = new ArrayList<>();
        for (Block h = getHeader(bhash); h != null && h.nHeight >= anum; ) {
            headers.add(h);
            h = getHeader(h.hashPrevBlock);
        }
        Collections.reverse(headers);
        return headers;
    }

    @Override
    public List<Block> getAncestorBlocks(byte[] bhash, long anum) {
        List<Block> headers = getAncestorHeaders(bhash, anum);
        return getBlocksFromHeaders(headers);
    }

    @Override
    public long getCurrentTotalWeight() {
        return tmpl.queryForObject("select max(total_weight) from header", null, Long.class);
    }

    @Override
    public boolean hasTransaction(byte[] txHash) {
        return tmpl.queryForObject("select count(*) from transaction as tx " +
                "inner join transaction_index as ti on tx.tx_hash = ti.tx_hash " +
                "inner join header as h on ti.block_hash = h.block_hash " +
                "where tx.tx_hash = ? and h.is_canonical = true limit 1", new Object[]{txHash}, Integer.class) > 0;
    }

    @Override
    public Transaction getTransaction(byte[] txHash) {
        return getOne(tmpl.query(
                "select tx.*, h.height as height, ti.block_hash as block_hash from transaction as tx " +
                        "inner join transaction_index as ti on tx.tx_hash = ti.tx_hash " +
                        "inner join header as h on ti.block_hash = h.block_hash" +
                        " where tx.tx_hash = ? and h.is_canonical = true", new Object[]{txHash}, new TransactionMapper()));
    }

    public Transaction getTransactionByTo(byte[] pubKeyHash){
        return getOne(tmpl.query("select tx.*, h.height as height, ti.block_hash as block_hash from transaction as tx " +
                "inner join transaction_index as ti on tx.tx_hash = ti.tx_hash " +
                "inner join header as h on ti.block_hash = h.block_hash" +
                " where tx.to = ? and h.is_canonical = true", new Object[]{pubKeyHash}, new TransactionMapper()));
    }

    @Async
    public void writeBlocksAsync(List<Block> blocks) {
        for (Block b : blocks) {
            writeBlock(b);
        }
    }
}