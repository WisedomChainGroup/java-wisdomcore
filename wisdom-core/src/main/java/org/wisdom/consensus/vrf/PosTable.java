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

    // TODO: fresh state
    public State updateBlock(Block block) {
        this.counter.incrementAndGet();
        System.out.println(block.nHeight);
        return this;
    }

    // TODO: deep copy
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
