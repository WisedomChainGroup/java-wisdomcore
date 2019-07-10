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
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.wisdom.crypto.PublicKey;
import org.wisdom.crypto.PublicKey;

public class Ed25519PublicKey implements PublicKey {
    private Ed25519PublicKeyParameters publicKey;

    public Ed25519PublicKey(byte[] encoded){
        this.publicKey = new Ed25519PublicKeyParameters(encoded, 0);
    }

    Ed25519PublicKey(Ed25519PublicKeyParameters publicKey){
        this.publicKey = publicKey;
    }

    /**
     *
     * @param msg plain text
     * @param signature from author
     * @return whether the signature is signed by author
     */
    public boolean verify(byte[] msg, byte[] signature){
        Signer verifier = new Ed25519Signer();
        verifier.init(false, publicKey);
        verifier.update(msg, 0, msg.length);
        return verifier.verifySignature(signature);
    }

    // TODO: check format
    public String getFormat(){
        return "";
    }

    public String getAlgorithm(){
        return Ed25519.ALGORITHM;
    }

    public byte[] getEncoded(){
        return this.publicKey.getEncoded();
    }

    public void decodeFrom(byte[] encoded){
        this.publicKey = new Ed25519PublicKeyParameters(encoded, 0);
    }
}