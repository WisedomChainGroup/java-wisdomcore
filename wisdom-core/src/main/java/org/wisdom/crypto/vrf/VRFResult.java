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

package org.wisdom.crypto.vrf;

import org.bouncycastle.util.Arrays;

public class VRFResult {
    // 32 byte pseudo random variable
    private byte[] r;

    // 64byte
    private byte[] proof;

    public VRFResult(byte[] encoded){
        this.r = Arrays.copyOfRange(encoded, 1, 1 + encoded[0]);
        this.proof = Arrays.copyOfRange(encoded, 1 + encoded[0], encoded.length);
    }

    public VRFResult(byte[] r, byte[] proof) {
        this.r = r;
        this.proof = proof;
    }

    /**
     *
     * @return the pseudo random value
     */
    public byte[] getR() {
        return r;
    }

    void setR(byte[] r) {
        this.r = r;
    }

    public byte[] getProof() {
        return proof;
    }

    void setProof(byte[] proof) {
        this.proof = proof;
    }

    public byte[] getEncoded(){
        return Arrays.concatenate(new byte[]{(byte) this.r.length}, this.r, this.proof);
    }
}