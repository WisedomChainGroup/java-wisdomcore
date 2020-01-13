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

    boolean containsBlock(byte[] hash);

    // currentHeader retrieves the current head header of the canonical chain
    Block getTopHeader();

    // currentBlock retrieves the current head Block of the canonical chain
    Block getTopBlock();

    // getHeader retrieves a block header from the database by hash
    Block getHeaderByHash(byte[] blockHash);

    Block getBlockByHash(byte[] blockHash);

    List<Block> getHeadersSince(long startHeight, int headersCount);

    List<Block> getBlocksSince(long startHeight, int headersCount);

    List<Block> getHeadersBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial);

    default List<Block> getHeadersBetween(long startHeight, long stopHeight, int sizeLimit){
        return getHeadersBetween(startHeight, stopHeight, sizeLimit, false);
    }

    default List<Block> getHeadersBetween(long startHeight, long stopHeight){
        return getHeadersBetween(startHeight, stopHeight, Integer.MAX_VALUE);
    }

    List<Block> getBlocksBetween(long startHeight, long stopHeight, int sizeLimit, boolean clipInitial);

    default List<Block> getBlocksBetween(long startHeight, long stopHeight, int sizeLimit){
        return getBlocksBetween(startHeight, stopHeight, sizeLimit, false);
    }

    default List<Block> getBlocksBetween(long startHeight, long stopHeight){
        return getBlocksBetween(startHeight, stopHeight, Integer.MAX_VALUE);
    }

    // retrieves the header assigned to a canonical block number
    Block getHeaderByHeight(long height);

    // retrieves the block assigned to a canonical block number
    Block getBlockByHeight(long height);

    // write the block to the database
    boolean writeBlock(Block block);

    List<Block> getAncestorHeaders(byte[] hash, long ancestorHeight);

    List<Block> getAncestorBlocks(byte[] hash, long ancestorHeight);

    long getTopHeight();

    boolean containsTransaction(byte[] txHash);

    boolean containsPayload(int type, byte[] payload);

    Transaction getTransaction(byte[] txHash);

    Transaction getTransactionByTo(byte[] pubKeyHash);

    List<Transaction> getTransactionsByFrom(byte[] publicKey, int offset, int limit);

    List<Transaction> getTransactionsByTypeAndFrom(int type, byte[] publicKey, int offset, int limit);

    List<Transaction> getTransactionsByTo(byte[] publicKeyHash, int offset, int limit);

    List<Transaction> getTransactionsByTypeAndTo(int type, byte[] to, int offset, int limit);

    List<Transaction> getTransactionsByFromAndTo(byte[] from, byte[] to, int offset, int limit);

    List<Transaction> getTransactionsByTypeFromAndTo(int type, byte[] from, byte[] to, int offset, int limit);

    long countBlocksAfter(long timestamp);
}