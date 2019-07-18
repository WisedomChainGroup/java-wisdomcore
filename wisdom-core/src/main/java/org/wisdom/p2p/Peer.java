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

package org.wisdom.p2p;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Peer {
    @NotNull
    @Size(min = 1)
    public String host;

    @Max(65535)
    @Min(0)
    public int port;

    public byte[] privateKey;


    @Size(max = 32, min = 32)
    @NotNull
    public byte[] peerID;

    public String toString() {
        return null;
    }

    public int subTree(Peer that) {
        byte[] bits = new byte[32];
        for (int i = 0; i < 32; i++) {
            bits[i] = (byte) (peerID[i] ^ that.peerID[i]);
        }
        for(int i = 0; i < 256; i++){
            if((bits[i/8] & (1 << (8 - i%8))) != 0){
                return i;
            }
        }
        return 0;
    }

    public String hostPort() {
        return host + port;
    }
}
