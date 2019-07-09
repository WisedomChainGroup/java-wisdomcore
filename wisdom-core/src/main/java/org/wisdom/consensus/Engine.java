package org.wisdom.consensus;

import org.wisdom.crypto.KeyPair;
import org.wisdom.core.Block;

import java.math.BigInteger;

public interface Engine {
    KeyPair getKeyPair();

    boolean verifyBlock(Block block);

    boolean verifyHeader(Block header);

    // non-blocking method
    boolean verifyParent(Block parent, Block newBlock);

    // return zero when encounter invalid block
    BigInteger BlockWeight(Block block);
}
