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

package org.wisdom.crypto.ed25519;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.wisdom.crypto.CryptoException;
import org.wisdom.crypto.PrivateKey;
import org.wisdom.crypto.CryptoException;
import org.wisdom.crypto.PrivateKey;

public class Ed25519PrivateKey implements PrivateKey {
    private Ed25519PrivateKeyParameters privateKey;

    public Ed25519PrivateKey(byte[] encoded){
        this.privateKey = new Ed25519PrivateKeyParameters(encoded, 0);
    }

    Ed25519PrivateKey(Ed25519PrivateKeyParameters privateKey){
        this.privateKey = privateKey;
    }

    /**
     *
     * @param msg plain text
     * @return signature
     * @throws CryptoException
     */
    public byte[] sign(byte[] msg) throws CryptoException {
        try {
            Signer signer = new Ed25519Signer();
            signer.init(true, privateKey);
            signer.update(msg, 0, msg.length);
            return signer.generateSignature();
        }catch (Exception e){
            throw new CryptoException();
        }
    }

    public String getAlgorithm(){
        return Ed25519.ALGORITHM;
    }

    // TODO: check format
    public String getFormat(){
        return "";
    }

    public byte[] getEncoded(){
        return this.privateKey.getEncoded();
    }

    public Ed25519PublicKey generatePublicKey(){
        return new Ed25519PublicKey(this.privateKey.generatePublicKey());
    }
}