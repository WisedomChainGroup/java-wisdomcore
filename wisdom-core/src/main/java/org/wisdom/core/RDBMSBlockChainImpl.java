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
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
@Component
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

    private void createTableAndIndices() throws Exception {
        String ddl = "ddl.sql";
        Resource resource;
        try {
            resource = new ClassPathResource(ddl);
        } catch (Exception e) {
            resource = new FileSystemResource(ddl);
        }
        assert resource.exists();

        String sql = new String(IOUtils.toByteArray(resource.getInputStream()));
        for (String s : sql.split(";")) {
            tmpl.update(s.trim());
        }
        tmpl.batchUpdate(
                "create index if not exists transaction_index_block_hash " +
                        "    on transaction_index (block_hash)",
                "create unique index if not exists transaction_tx_hash_uindex " +
                        "    on transaction (tx_hash)",
                "create index if not exists header_height_index on header (height desc)",
                "create index if not exists header_total_weight_index on header (total_weight desc)",
                "create index if not exists account_blockheight_index on account (blockheight desc)",
                "create index if not exists  account_pubkeyhash_index on account (pubkeyhash)",
                "create index if not exists incubator_state_height_index on incubator_state (height desc)",
                "create index if not exists incubator_state_txid_issue_index on incubator_state (txid_issue)",
                "create index if not exists  account_heightpub_index on account (blockheight,pubkeyhash)",
                "create index if not exists incubator_state_txidheight_index on incubator_state (txid_issue,height)",
                "create index if not exists transaction_index_tx_hash_index on transaction_index (tx_hash)",
                "create index if not exists transaction_type_index on transaction(type)",
                "create index if not exists transaction_payload_index on transaction(payload)",
                "create index if not exists transaction_to_index on transaction(\"to\")"
        );
    }

    public void clearData() {
        tmpl.batchUpdate("delete  from header where 1 = 1",
                "delete from transaction where 1 = 1",
                "delete from transaction_index where 1 = 1",
                "delete from account where 1 = 1",
                "delete from incubator_state where 1 = 1");
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
        if(headers.size() == 0){
            return new ArrayList<>();
        }
        Map<String, Block> cache =  new HashMap<>();
        for(Block b: headers){
            cache.put(b.getHashHexString(), b);
            b.body = new ArrayList<>();
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(tmpl);
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("blocksHash", headers.stream().map(Block::getHash).collect(Collectors.toList()));
        List<Transaction> transactions = namedParameterJdbcTemplate.query("select tx.*, ti.block_hash, h.height from transaction as tx inner join transaction_index as ti on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where ti.block_hash in (:blocksHash) order by ti.tx_index", paramMap, new TransactionMapper());
        for(Transaction tx: transactions){
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
            @Value("${clear-data}") boolean clearData
    ) throws Exception {
        this.tmpl = tmpl;
        this.txTmpl = txTmpl;
        this.genesis = genesis;
        createTableAndIndices();
        //增加account vote字段
        if (databaseUserName != null && !databaseUserName.equals("")) {
            String sql = "ALTER TABLE account OWNER TO " + databaseUserName;
            tmpl.execute(sql);//更换属主
        }

        tmpl.execute("ALTER TABLE account ADD COLUMN IF NOT EXISTS vote int8 not null DEFAULT 0");

        if (clearData) {
            clearData();
        }

        // 重构表
        // refactorTables();


        if (!dbHasGenesis()) {
            clearData();
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

    private List<Block> getHeaders(long startHeight, long stopHeight) {
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
        Block parentHeader = getBlock(block.hashPrevBlock);

        if (parentHeader == null){
            return false;
        }

        long ptw = parentHeader.totalWeight;
        block.totalWeight = block.weight + ptw;

        Boolean result = txTmpl.execute((TransactionStatus status) -> {
            try {
                writeHeader(block);
                writeBody(block);
            } catch (Exception e) {
                status.setRollbackOnly();
                e.printStackTrace();
                return false;
            }
            return true;
        });
        return Optional.ofNullable(result).orElse(false);
    }


    @Override
    public Block findAncestorHeader(byte[] blockHash, long ancestorHeight) {
        Block b = getAncestorHeaders(blockHash, ancestorHeight).get(0);
        // TODO: remove code assertions
        assert b.nHeight == ancestorHeight;
        return b;
    }

    @Override
    public Block findAncestorBlock(byte[] blockHash, long minimumAncestorHeight) {
        return getBlockFromHeader(findAncestorHeader(blockHash, minimumAncestorHeight));
    }

    @Override
    public List<Block> getAncestorHeaders(byte[] blockHash, long minimumAncestorHeight) {
        Block block = getHeader(blockHash);
        if (block == null){
            return new ArrayList<>();
        }
        List<Block> blocks = new BlocksCache(getHeaders(minimumAncestorHeight, block.nHeight)).getAncestors(block);

        // TODO: remove code assertions
        assert blocks.size() == block.nHeight - minimumAncestorHeight + 1;
        assert blocks.get(0).nHeight == minimumAncestorHeight;
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
                "select tx.*, ti.block_hash, h.height from transaction as tx inner join transaction_index as ti " +
                        "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.tx_hash = ?", new Object[]{txHash}, new TransactionMapper()));
    }

    public Transaction getTransactionByTo(byte[] publicKeyHash) {
        return getOne(tmpl.query("select tx.*, ti.block_hash, h.height from transaction as tx inner join transaction_index as ti " +
                "on tx.tx_hash = ti.tx_hash inner join header as h on ti.block_hash = h.block_hash where tx.to = ?", new Object[]{publicKeyHash}, new TransactionMapper()));
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

    @Async
    public void writeBlocksAsync(List<Block> blocks) {
        for (Block b : blocks) {
            writeBlock(b);
        }
    }

    public boolean hasPayload(byte[] payload) {
        return tmpl.queryForObject("select count(*) from transaction as tx where tx.payload = ?  limit 1", new Object[]{payload}, Integer.class) > 0;
    }

    // 删除孤块


    // 重构关系表，删除孤快，冗余 transaction_index 中字段到 transaction 表
    private void refactorTables() {
        // 判断是否已经重构过了
        if (tmpl.queryForObject("SELECT count(*) " +
                "FROM information_schema.columns " +
                "WHERE table_name = 'transaction' and column_name = 'block_hash'", Integer.class) > 0){
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
