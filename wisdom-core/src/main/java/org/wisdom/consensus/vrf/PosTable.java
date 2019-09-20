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

package org.wisdom.consensus.vrf;

import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.state.State;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sal 1564319846@qq.com
 * pos table
 */
public class PosTable implements State {

    // for testing
    public AtomicLong counter;

    private PosTable() {

    }

    // return a genesis state
    public PosTable(WisdomBlockChain blockChain) {
        this.counter = new AtomicLong();
        this.updateBlock(blockChain.getGenesis());
    }

    public State updateBlock(Block block) {
        this.counter.incrementAndGet();
        System.out.println(block.nHeight);
        return this;
    }

    public State copy() {
        PosTable newPosTable = new PosTable();
        newPosTable.counter = new AtomicLong(this.counter.get());
        return newPosTable;
    }

    @Override
    public State updateTransaction(Transaction tx) {
        return null;
    }

    // update blocks
    public State updateBlocks(List<Block> blocks) {
        for (Block b : blocks) {
            this.updateBlock(b);
        }
        return this;
    }

    public long getWeight(byte[] publicKey) {
        return 0;
    }

    public List<byte[]> getValidators() {
        return null;
    }
}