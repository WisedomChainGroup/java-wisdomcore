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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.util.Assert;
import org.wisdom.Start;
import org.wisdom.util.Arrays;
import org.wisdom.core.account.Transaction;
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
import java.util.stream.Collectors;

/**
 * @author sal 1564319846@qq.com
 * block query/write based on relational database with concurrently safety
 */
public class RDBMSBlockChainImpl implements WisdomBlockChain {
    private JdbcTemplate tmpl;
    private TransactionTemplate txTmpl;
    private Block genesis;

    private static final Logger logger = LoggerFactory.getLogger(RDBMSBlockChainImpl.class);

    private <T> T getOne(List<T> res) {
        if (res.size() == 0) {
            return null;
        }
        return res.get(0);
    }

    private void createTableAndIndices(BasicDataSource basicDataSource) throws Exception {
        String ddl = "ddl.sql";
        Resource resource;
        try {
            resource = new ClassPathResource(ddl);
        } catch (Exception e) {
            resource = new FileSystemResource(ddl);
        }
        assert resource.exists();
        ScriptUtils.executeSqlScript(basicDataSource.getConnection(), resource);
    }

    public void clearData() {
        tmpl.batchUpdate("drop table if exists header",
                "drop table if exists transaction",
                "drop table if exists transaction_index",
                "drop table if exists account",
                "drop table if exists incubator_state");
    }

    // get block body
    private List<Transaction> getBlockBody(Block header) {
        if (header == null) {
            return new ArrayList<>();
        }
        return tmpl.query("select tx.*, ti.block_hash, h.height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where ti.block_hash = ? order by ti.tx_index", new Object[]{header.getHash()}, new TransactionMapper());
    }

    private Block getBlockFromHeader(Block block) {
        if (block == null) {
            return null;
        }
        block.body = getBlockBody(block);
        return block;
    }

    // reduce ios
    private List<Block> getBlocksFromHeaders(List<Block> headers) {
        if (headers.size() == 0) {
            return new ArrayList<>();
        }
        Map<String, Block> cache = new HashMap<>();
        for (Block b : headers) {
            cache.put(b.getHashHexString(), b);
            b.body = new ArrayList<>();
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(tmpl);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("blocksHash", headers.stream().map(Block::getHash).collect(Collectors.toList()));
        List<Transaction> transactions = namedParameterJdbcTemplate.query("select tx.*, ti.block_hash, h.height from transaction as tx inner join transaction_index as ti on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where ti.block_hash in (:blocksHash) order by ti.tx_index", paramMap, new TransactionMapper());
        for (Transaction tx : transactions) {
            cache.get(Hex.encodeHexString(tx.blockHash)).body.add(tx);
        }
        return cache.values().stream().sorted(Comparator.comparingLong(x -> x.nHeight)).collect(Collectors.toList());
    }

    // write header with total weight
    private void writeHeader(Block header) {
        tmpl.update("insert into header " +
                        "(block_hash, version, hash_prev_block, " +
                        "hash_merkle_root, hash_merkle_state, hash_merkle_incubate," +
                        "height, created_at, nonce, nBits," +
                        "block_notice, is_canonical, total_weight) values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                header.getHash(), header.nVersion, header.hashPrevBlock,
                header.hashMerkleRoot, header.hashMerkleState, header.hashMerkleIncubate, header.nHeight,
                header.nTime, header.nNonce, header.nBits,
                header.blockNotice, true, header.nHeight);
    }

    private void writeBody(Block block) {
        if (block.body == null || block.size() == 0) {
            return;
        }
        List<Object[]> args0 = new ArrayList<>();
        for (int i = 0; i < block.body.size(); i++) {
            Transaction tx = block.body.get(i);
            args0.add(new Object[]{
                    tx.version,
                    tx.getHash(), tx.type, tx.nonce,
                    tx.from, tx.gasPrice, tx.amount,
                    tx.payload, tx.signature, tx.to
            });
        }

        // 写入 transaction 表
        tmpl.batchUpdate("insert into transaction (" +
                "version, tx_hash, type, nonce, " +
                "\"from\", gas_price, amount, " +
                "payload, signature, \"to\") VALUES (?, ?,?,?,?,?,?,?,?,?) on conflict(tx_hash) do nothing", args0);

        List<Object[]> args = new ArrayList<>();
        for (int i = 0; i < block.body.size(); i++) {
            Transaction tx = block.body.get(i);
            args.add(new Object[]{
                    block.getHash(), tx.getHash(), i
            });
        }

        // 写入 transaction_index 表
        tmpl.batchUpdate("insert into transaction_index (block_hash, tx_hash, tx_index) values (?,?,?)", args);
    }


    private boolean dbHasGenesis() {
        return tmpl.queryForObject("select count(*) from header where height = 0 limit 1", new Object[]{}, Integer.class) > 0;
    }


    public void writeGenesis(Block genesis) {
        // null pointer execption
        txTmpl.execute((TransactionStatus status) -> {
            writeHeader(genesis);
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
    public RDBMSBlockChainImpl(
            JdbcTemplate tmpl,
            TransactionTemplate txTmpl,
            Block genesis,
            ApplicationContext ctx,
            @Value("${spring.datasource.username}") String databaseUserName,
            @Value("${clear-data}") boolean clearData,
            BasicDataSource basicDataSource
    ) throws Exception {
        this.tmpl = tmpl;
        this.txTmpl = txTmpl;
        this.genesis = genesis;

        if (clearData) {
            clearData();
        }

        createTableAndIndices(basicDataSource);
        //增加account vote字段
        if (databaseUserName != null && !databaseUserName.equals("")) {
            String sql = "ALTER TABLE account OWNER TO " + databaseUserName;
            tmpl.execute(sql);//更换属主
        }

        tmpl.execute("ALTER TABLE account ADD COLUMN IF NOT EXISTS vote int8 not null DEFAULT 0");

        // 重构表
        // refactorTables();


        if (!dbHasGenesis()) {
            writeGenesis(genesis);
            return;
        }

        // 发现数据库的创世区块和配置文件的创世区块不一样
        Block dbGenesis = getCanonicalHeader(0);
        if (!Arrays.areEqual(dbGenesis.getHash(), genesis.getHash())) {
            throw new Exception("the genesis in db and genesis in config is not equal");
        }
    }

    @Override
    public Block currentHeader() {
        return getOne(tmpl.query("select * from header order by total_weight desc limit 1", new BlockMapper()));
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

    private List<Block> getHeadersBetween(long startHeight, long stopHeight) {
        return tmpl.query("select * from header where height >= ? and height <= ? order by height", new Object[]{startHeight, stopHeight}, new BlockMapper());
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
    public List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial) {
        if (!clipInitial) {
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
        return getOne(tmpl.query("select * from header where height = ?", new Object[]{num}, new BlockMapper()));
    }

    @Override
    public List<Block> getCanonicalHeaders(long start, int size) {
        return tmpl.query("select * from header where height < ? and height >= ? order by height", new Object[]{start + size, start}, new BlockMapper());
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
    public synchronized boolean writeBlock(Block block) {
        block.totalWeight = block.nHeight;
        Boolean result = txTmpl.execute((TransactionStatus status) -> {
            try {
                writeHeader(block);
                writeBody(block);
            } catch (Exception e) {
                e.printStackTrace();
                status.setRollbackOnly();
                e.printStackTrace();
                return false;
            }
            return true;
        });
        return Optional.ofNullable(result).orElse(false);
    }


    @Override
    public Block getAncestorHeader(byte[] blockHash, long ancestorHeight) {
        Block b = getAncestorHeaders(blockHash, ancestorHeight).get(0);
        if (Start.ENABLE_ASSERTION) {
            Assert.isTrue(b.nHeight == ancestorHeight, "wrong ancestor height");
        }
        return b;
    }

    @Override
    public Block getAncestorBlock(byte[] blockHash, long minimumAncestorHeight) {
        return getBlockFromHeader(getAncestorHeader(blockHash, minimumAncestorHeight));
    }

    @Override
    public List<Block> getAncestorHeaders(byte[] blockHash, long minimumAncestorHeight) {
        Block block = getHeader(blockHash);
        if (block == null) {
            return new ArrayList<>();
        }
        List<Block> blocks = new BlocksCache(getHeadersBetween(minimumAncestorHeight, block.nHeight)).getAncestors(block);
        if (Start.ENABLE_ASSERTION) {
            Assert.isTrue(blocks.get(0).nHeight == minimumAncestorHeight, "wrong ancestor height");
        }
        return blocks;
    }

    @Override
    public List<Block> getAncestorBlocks(byte[] blockHash, long minimumAncestorHeight) {
        List<Block> headers = getAncestorHeaders(blockHash, minimumAncestorHeight);
        return getBlocksFromHeaders(headers);
    }

    @Override
    public long getCurrentTotalWeight() {
        return tmpl.queryForObject("select max(total_weight) from header", null, Long.class);
    }

    @Override
    public boolean hasTransaction(byte[] txHash) {
        return tmpl.queryForObject("select count(*) from transaction as tx " +
                "where tx.tx_hash = ? limit 1", new Object[]{txHash}, Integer.class) > 0;
    }

    @Override
    public Transaction getTransaction(byte[] txHash) {
        return getOne(tmpl.query(
                "select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                        "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.tx_hash = ?", new Object[]{txHash}, new TransactionMapper()));
    }

    public Transaction getTransactionByTo(byte[] publicKeyHash) {
        return getOne(tmpl.query("select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.to = ? limit 1", new Object[]{publicKeyHash}, new TransactionMapper()));
    }

    @Override
    public Block getLastConfirmedBlock() {
        try {
            Long height = tmpl.queryForObject("select max(height) from header", Long.class);
            if (height == null) {
                return null;
            }
            return getCanonicalBlock(height);
        } catch (Exception e) {
            return genesis;
        }
    }

    public boolean hasPayload(int type, byte[] payload) {
        return tmpl.queryForObject("select count(*) from transaction as tx where tx.type = ? and tx.payload = ?  limit 1", new Object[]{type, payload}, Integer.class) > 0;
    }

    @Override
    public List<Transaction> getTransactionsByFrom(byte[] publicKey, int offset, int limit) {
        return tmpl.query("select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.from = ? order by height, ti.tx_index offset ? limit ?", new Object[]{publicKey, offset, limit}, new TransactionMapper());
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndFrom(int type, byte[] publicKey, int offset, int limit) {
        return tmpl.query("select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.type = ? and  tx.from = ?  order by height, ti.tx_index offset ? limit ?", new Object[]{type, publicKey, offset, limit}, new TransactionMapper());
    }

    @Override
    public List<Transaction> getTransactionsByTo(byte[] publicKeyHash, int offset, int limit) {
        return tmpl.query("select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.to = ? order by height, ti.tx_index offset ? limit ?", new Object[]{publicKeyHash, offset, limit}, new TransactionMapper());
    }

    @Override
    public List<Transaction> getTransactionsByTypeAndTo(int type, byte[] publicKeyHash, int offset, int limit) {
        return tmpl.query("select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.type = ? and tx.to = ? order by height, ti.tx_index offset ? limit ?", new Object[]{type, publicKeyHash, offset, limit}, new TransactionMapper());
    }

    @Override
    public List<Transaction> getTransactionsByFromAndTo(byte[] from, byte[] to, int offset, int limit) {
        return tmpl.query("select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.from = ? and tx.to =? order by height, ti.tx_index offset ? limit ?", new Object[]{from, to, offset, limit}, new TransactionMapper());
    }

    @Override
    public List<Transaction> getTransactionsByTypeFromAndTo(int type, byte[] from, byte[] to, int offset, int limit) {
        return tmpl.query("select tx.*, ti.block_hash as block_hash, h.height as height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.type =? and tx.from = ? and tx.to =? order by height, ti.tx_index offset ? limit ?", new Object[]{type, from, to, offset, limit}, new TransactionMapper());
    }

    @Override
    public long countBlocksAfter(long timestamp) {
        return tmpl.queryForObject("select count(*) from header where created_at > ?", new Object[]{timestamp}, Long.class);
    }

    // 重构关系表，删除孤快，冗余 transaction_index 中字段到 transaction 表
    private void refactorTables() {
        // 判断是否已经重构过了
        if (tmpl.queryForObject("SELECT count(*) " +
                "FROM information_schema.columns " +
                "WHERE table_name = 'transaction' and column_name = 'block_hash'", Integer.class) > 0) {
            return;
        }


        // 增加列
        tmpl.update("ALTER TABLE \"transaction\" ADD COLUMN IF NOT EXISTS block_hash bytea DEFAULT null");
        tmpl.update("ALTER TABLE \"transaction\" ADD COLUMN IF NOT EXISTS height bigint NOT NULL DEFAULT 0");
        tmpl.update("ALTER TABLE \"transaction\" ADD COLUMN IF NOT EXISTS tx_index bigint NOT NULL DEFAULT 0");

        // 增加索引
        tmpl.batchUpdate(
                "create index if not exists transaction_height_index on \"transaction\" (height desc)",
                "create index if not exists transaction_block_hash_index on \"transaction\" (block_hash)",
                "create index if not exists transaction_tx_index_index on \"transaction\" (tx_index)"
        );

        // 对字段进行冗余
        tmpl.update("update \"transaction\" as t set block_hash = (select ti.block_hash from transaction_index as ti where ti.tx_hash = t.tx_hash limit 1)");
        tmpl.update("update \"transaction\" as t set tx_index = (select ti.tx_index from transaction_index as ti where ti.tx_hash = t.tx_hash limit 1)");
        tmpl.update("update \"transaction\" as t set height = (select h.height from header as h where h.block_hash = t.block_hash limit 1)");
    }

}
