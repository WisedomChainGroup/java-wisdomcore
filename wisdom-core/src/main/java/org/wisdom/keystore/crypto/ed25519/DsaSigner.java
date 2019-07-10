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

package org.wisdom.keystore.crypto.ed25519;

public interface DsaSigner {

    /**
     * Signs the SHA3 hash of an arbitrarily sized message.
     *
     * @param data The message to sign.
     * @return The generated signature.
     */
    Signature sign(final byte[] data);

    /**
     * Verifies that the signature is valid.
     *
     * @param data The original message.
     * @param signature The generated signature.
     * @return true if the signature is valid.
     */
    boolean verify(final byte[] data, final Signature signature);

    /**
     * Determines if the signature is canonical.
     *
     * @param signature The signature.
     * @return true if the signature is canonical.
     */
    boolean isCanonicalSignature(final Signature signature);

    /**
     * Makes this signature canonical.
     *
     * @param signature The signature.
     * @return Signature in canonical form.
     */
    Signature makeSignatureCanonical(final Signature signature);
}