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

import org.wisdom.crypto.CryptoException;
import org.wisdom.crypto.HashUtil;
import org.wisdom.crypto.PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519;

public class VRFPrivateKey {
    private PrivateKey signer;

    public VRFPrivateKey(String algorithm) throws CryptoException {
        if (algorithm.equals(Ed25519.getAlgorithm())){
            this.signer = Ed25519.generateKeyPair().getPrivateKey();
            return;
        }
        throw new CryptoException("unsupported signature policy");
    }

    /**
     *
     * @param seed random seed
     * @return verifiable random function result,
     * consists of a random variable, the seed and a proof for verifying
     * @throws CryptoException
     */
    public VRFResult rand(byte[] seed) throws CryptoException {
        if (seed.length > 255){
            throw new CryptoException("seed length overflow");
        }
        byte[] sig = signer.sign(seed);
        byte[] fin = HashUtil.sha256(sig);
        return new VRFResult(fin, sig);
    }

    public VRFPrivateKey(PrivateKey signer) {
        this.signer = signer;
    }
    public byte[] getEncoded(){
        return this.signer.getEncoded();
    }
    public VRFPublicKey generatePublicKey(){
        return new VRFPublicKey(this.signer.generatePublicKey());
    }
}