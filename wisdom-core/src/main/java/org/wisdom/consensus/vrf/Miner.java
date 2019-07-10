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

import org.wisdom.crypto.KeyPair;
import org.wisdom.crypto.vrf.VRFPrivateKey;
import org.wisdom.crypto.vrf.VRFPublicKey;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.math.BigInteger;

// TODO: add transaction pool
//@Component
//@Scope("prototype")
public class Miner extends Thread {
    private WisdomBlockChain blockChain;
    private KeyPair keyPair;
    private VRFPrivateKey vrfPrivateKey;
    private VRFPublicKey vrfPublicKey;
    private Block genesis;
    private PosTableFactory factory;
    private static final byte[] emptyMerkleRoot = new byte[32];
    private static final int expected = 26;
    private static final int secondsPerBlock = 15;
    private static final int totalWeights = 1000;
    private static final int weight = 900;
    private JSONEncodeDecoder encodeDecoder;

    public Miner(WisdomBlockChain blockChain, KeyPair keyPair, Block genesis, JSONEncodeDecoder encodeDecoder, PosTableFactory factory) {
        this.blockChain = blockChain;
        this.factory = factory;
        this.keyPair = keyPair;
        this.genesis = genesis;
        this.vrfPrivateKey = new VRFPrivateKey(keyPair.getPrivateKey());
        this.vrfPublicKey = vrfPrivateKey.generatePublicKey();
        this.encodeDecoder = encodeDecoder;
    }

    public Block mine() {
        return null;
    }

    /**
     * @param hash       block hash
     * @param difficulty number of leading zeros
     * @return nonce
     */
    public byte[] pow(byte[] hash, byte difficulty) {
        return null;
    }

    @Override
    public void run() {
        System.out.println("new block mined \n" + new String(encodeDecoder.encodeBlock(mine())));
    }

    public static void main(String[] args) {
        System.out.println(new BigInteger(new byte[]{0x01, 0x00, 0x00, 0x00}).longValue());
        System.out.println(1 << 24);
    }
}