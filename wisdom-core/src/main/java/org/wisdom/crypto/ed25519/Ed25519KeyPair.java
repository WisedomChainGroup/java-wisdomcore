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

import org.wisdom.crypto.KeyPair;
import org.wisdom.crypto.KeyPair;

/**
 * Ed25519 keypair for signature and verifying
 */
public class Ed25519KeyPair implements KeyPair {
    private Ed25519PrivateKey privateKey;
    private Ed25519PublicKey publicKey;

    public Ed25519PrivateKey getPrivateKey() {
        return privateKey;
    }

    void setPrivateKey(Ed25519PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public Ed25519PublicKey getPublicKey() {
        return publicKey;
    }

    void setPublicKey(Ed25519PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}