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

import org.wisdom.consensus.Engine;
import org.wisdom.crypto.KeyPair;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

//@Component
public class VRFDPos implements Engine {
    private WisdomBlockChain blockChain;
    private KeyPair keyPair;
    private PosTableFactory factory;

    public KeyPair getKeyPair() {
        return keyPair;
    }

    // TODO: verify merkle hash
    public boolean verifyBlock(Block block) {
        if (block == null) {
            return false;
        }

        if (!verifyHeader(block)) {
            return false;
        }

        return true;
    }

    public boolean verifyHeader(Block header) {
        if (header == null) {
            return false;
        }
        Block currentHeader = blockChain.currentHeader();
        return true;
    }


    public boolean verifyParent(Block parent, Block newHeader) {
        return true;
    }

    // TODO: pos table query
    public BigInteger BlockWeight(Block block) {
        if (!verifyBlock(block)) {
            return BigInteger.ZERO;
        }
        return BigInteger.ONE;
    }

    @Autowired
    public VRFDPos(WisdomBlockChain blockChain, KeyPair keyPair, PosTableFactory factory) {
        this.blockChain = blockChain;
        this.keyPair = keyPair;
        this.factory = factory;
    }
}