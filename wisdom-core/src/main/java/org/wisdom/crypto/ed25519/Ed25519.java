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

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

import java.security.SecureRandom;

public class Ed25519 {
    public static final String ALGORITHM = "ed25519";

    public static String getAlgorithm() {
        return ALGORITHM;
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * @return Ed25519 keypair for signature and verifying
     */
    public static Ed25519KeyPair GenerateKeyPair() {
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(RANDOM);
        Ed25519PublicKeyParameters publicKey = privateKey.generatePublicKey();
        Ed25519KeyPair nkp = new Ed25519KeyPair();
        nkp.setPrivateKey(new Ed25519PrivateKey(privateKey));
        nkp.setPublicKey(new Ed25519PublicKey(publicKey));
        return nkp;
    }

    
}
