package org.wisdom.core;

import org.wisdom.core.account.Transaction;

import java.util.List;

public interface WisdomBlockChain {

    Block getGenesis();

    boolean hasBlock(byte[] hash);

    // currentHeader retrieves the current head header of the canonical chain
    Block currentHeader();

    // currentBlock retrieves the current head Block of the canonical chain
    Block currentBlock();

    // getHeader retrieves a block header from the database by hash
    Block getHeader(byte[] blockHash);

    Block getBlock(byte[] blockHash);

    List<Block> getHeaders(long startHeight, int headersCount);

    List<Block> getBlocks(long startHeight, int headersCount);

    List<Block> getBlocks(long startHeight, long stopHeight);

    List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit);

    List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipFromStop);

    // retrieves the header assigned to a canonical block number
    Block getCanonicalHeader(long height);

    List<Block> getCanonicalHeaders(long startHeight, int headersCount);

    // retrieves the block assigned to a canonical block number
    Block getCanonicalBlock(long height);

    // retrieves canonical blocks starts from the block number
    List<Block> getCanonicalBlocks(long startHeight, int headersCount);

    // lookup the whether the block is canonical
    boolean isCanonical(byte[] hash);

    // write the block to the database
    void writeBlock(Block block);

    // find b's ancestor header at height of anum
    Block findAncestorHeader(byte[] bhash, long anum);

    // find b's ancestor block at height of anum
    Block findAncestorBlock(byte[] bhash, long anum);

    // find b's ancestor blocks until height of anum, both inclusive
    List<Block> getAncestorHeaders(byte[] bhash, long anum);

    List<Block> getAncestorBlocks(byte[] bhash, long anum);

    long getCurrentTotalWeight();

    boolean hasTransaction(byte[] txHash);

    Transaction getTransaction(byte[] txHash);

    void writeBlocksAsync(List<Block> blocks);

    Transaction getTransactionByTo(byte[] pubKeyHash);
}
