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

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
    private String dataname;
    private static final Logger logger = LoggerFactory.getLogger(RDBMSBlockChainImpl.class);
    private BlockChainOptional blockChainOptional;
    private boolean allowFork;

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
                "create index if not exists transaction_payload_index on transaction(payload)"
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

    private List<Block> getBlocksFromHeaders(List<Block> headers) {
        List<Block> res = new ArrayList<>();
        for (Block h : headers) {
            res.add(getBlockFromHeader(h));
        }
        return res;
    }

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
        tmpl.update("update header set is_canonical = false where height >= ? and height <= ?", start, end);
    }

    private void setCanonical(byte[] hash) {
        tmpl.update("update header set is_canonical = true where block_hash = ?", new Object[]{hash});
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
    public RDBMSBlockChainImpl(
            JdbcTemplate tmpl,
            TransactionTemplate txTmpl,
            Block genesis,
            ApplicationContext ctx,
            @Value("${spring.datasource.username}") String dataname,
            @Value("${clear-data}") boolean clearData,
            BlockChainOptional blockChainOptional,
            @Value("${wisdom.consensus.allow-fork}") boolean allowFork
    ) throws Exception {
        this.tmpl = tmpl;
        this.txTmpl = txTmpl;
        this.genesis = genesis;
        this.ctx = ctx;
        this.dataname = dataname;
        this.blockChainOptional = blockChainOptional;
        this.allowFork = allowFork;
        createTableAndIndices();
        if (clearData) {
            clearData();
        }
        //增加account vote字段
        if (this.dataname != null && this.dataname != "" && !this.dataname.equals("")) {
            String sql = "ALTER TABLE account OWNER TO " + dataname;
            tmpl.execute(sql);//更换属主
        }
        tmpl.execute("ALTER TABLE account ADD COLUMN IF NOT EXISTS vote int8 not null DEFAULT 0");
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
        // 清除历史遗留的孤快
        clearOrphans();
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
    public synchronized boolean writeBlock(Block block) {
        Optional<Block> parent = Optional.ofNullable(block)
                .flatMap(b -> blockChainOptional.getHeader(b.hashPrevBlock));
        if (!parent.isPresent()) {
            // cannot find parent, write fail
            return false;
        }
        Block parentHeader = parent.get();

        if (blockChainOptional.hasBlock(block.getnHeight())
                .orElse(true)) {
            return false;
        }
        long ptw = parentHeader.totalWeight;
        block.totalWeight = block.weight + ptw;

        Boolean result = txTmpl.execute((TransactionStatus status) -> {
            try {
                writeHeader(block);
                writeTotalWeight(block.getHash(), block.totalWeight);
                writeBody(block);
                setCanonical(block.getHash());
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

    public Transaction getTransactionByTo(byte[] pubKeyHash) {
        return getOne(tmpl.query("select tx.*, h.height as height, ti.block_hash as block_hash from transaction as tx " +
                "inner join transaction_index as ti on tx.tx_hash = ti.tx_hash " +
                "inner join header as h on ti.block_hash = h.block_hash" +
                " where tx.to = ? and h.is_canonical = true", new Object[]{pubKeyHash}, new TransactionMapper()));
    }

    @Override
    public Block getLastConfirmedBlock() {
        try {
            Long height = tmpl.queryForObject("select a.blockheight from account a order by a.blockheight desc LIMIT 1", Long.class);
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

    public void clearOrphans() {
        // 1. 先删除 transaction_index 中的孤快
        // 2. 在删除 transaction 中不存在于 transaction_index 中的事务
        // 3. 最后删除 header 里面的孤快
        // 4. 在 transaction 中冗余三个字段 block_hash, height, index 分别表示事务所在的区块，事务所在的区块高度，事务所在区块体的位置
        List<byte[]> orphans = tmpl.queryForList("select block_hash from header where is_canonical = false", byte[].class);
        for (byte[] blockHash : orphans) {

            List<byte[]> transactions = tmpl.queryForList("select tx_hash from transaction_index where block_hash = ?", new Object[]{blockHash}, byte[].class);

            // 清理 transaction index
            tmpl.update("delete from transaction_index where block_hash = ?", new Object[]{blockHash});

            // 如果这个孤快的中事务没有被其他不是孤快的区块引用到，则删除这个事务
            for (byte[] tx : transactions) {
                String sql = "select count(*) from header as h inner join transaction_index as ti on h.block_hash = ti.block_hash where h.is_canonical = true and ti.tx_hash = ?";
                if (tmpl.queryForObject(sql, new Object[]{tx}, Integer.class) > 0) {
                    continue;
                }
                tmpl.update("delete from \"transaction\" where tx_hash = ?", new Object[]{tx});
            }
        }
    }

}
