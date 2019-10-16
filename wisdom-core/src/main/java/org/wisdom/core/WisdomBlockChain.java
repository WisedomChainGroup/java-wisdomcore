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

    List<Block> getBlocks(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial);

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
    boolean writeBlock(Block block);

    // find b's ancestor header at height of anum
    Block findAncestorHeader(byte[] bhash, long anum);

    // find b's ancestor block at height of anum
    Block findAncestorBlock(byte[] bhash, long anum);

    // find b's ancestor blocks until height of anum, both inclusive
    List<Block> getAncestorHeaders(byte[] bhash, long anum);

    List<Block> getAncestorBlocks(byte[] bhash, long anum);

    long getCurrentTotalWeight();

    boolean hasTransaction(byte[] txHash);

    boolean hasPayload(byte[] payload);

    Transaction getTransaction(byte[] txHash);

    void writeBlocksAsync(List<Block> blocks);

    Transaction getTransactionByTo(byte[] pubKeyHash);

    List<Transaction> getTransactionsByFrom(byte[] publicKey, int offset, int limit);

    List<Transaction> getTransactionsByFromAndType(int type, byte[] publicKey, int offset, int limit);

    List<Transaction> getTransactionsByTo(byte[] publicKeyHash, int offset, int limit);

    List<Transaction> getTransactionsByToAndType(int type, byte[] publicKeyHash, int offset, int limit);

    List<Transaction> getTransactionsByFromAndTo(byte[] from, byte[] to, int offset, int limit);

    List<Transaction> getTransactionsByFromToAndType(int type, byte[] from, byte[] to, int offset, int limit);

    Block getLastConfirmedBlock();
}