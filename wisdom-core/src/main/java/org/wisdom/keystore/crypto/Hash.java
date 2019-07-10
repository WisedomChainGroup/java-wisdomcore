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

package org.wisdom.keystore.crypto;

import org.bouncycastle.util.encoders.Hex;

public class Hash {

    /**
     * An empty hash.
     */
    public static final Hash EMPTYHASH = new Hash(new byte[32]);

    private final byte[] data;

    private final String hexHash;

    /**
     * Creates new Uint256 object.
     *
     * @param data The raw hash.
     */
    public Hash(final byte[] data) {
        this.data = data;
        this.hexHash= Hex.toHexString(data);
    }

    public String getHexHash(){
        return this.hexHash;
    }

    public byte[] getByteHash(){
        return this.data;
    }


}