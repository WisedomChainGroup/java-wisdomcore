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

import org.apache.commons.codec.binary.Hex;
import org.wisdom.crypto.KeyPair;
import org.wisdom.crypto.PrivateKey;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.Arrays;

public class Peer {
    private static final int DEFAULT_PORT = 9235;
    private static final int PUBLIC_KEY_LENGTH = 32;
    private static final int PRIVATE_KEY_LENGTH = 64;
    public static final String PROTOCOL_NAME = "wisdom";

    public static Peer parse(String url) throws Exception {
        URI u = new URI(url.trim());
        Peer p = new Peer();
        p.port = u.getPort();
        if (p.port <= 0) {
            p.port = DEFAULT_PORT;
        }
        p.host = u.getHost();
        byte[] info = Hex.decodeHex(u.getRawUserInfo());
        if (info.length != PRIVATE_KEY_LENGTH && info.length != PUBLIC_KEY_LENGTH) {
            throw new Exception("invalid key length");
        }
        p.peerID = info;
        if (info.length == PRIVATE_KEY_LENGTH) {
            p.privateKey = new Ed25519PrivateKey(Arrays.copyOfRange(info, 0, 32));
            p.peerID = Arrays.copyOfRange(info, 32, 64);
        }
        return p;
    }

    @NotNull
    @Size(min = 1)
    public String host;

    @Max(65535)
    @Min(0)
    public int port;
    public int score;

    public PrivateKey privateKey;

    @Size(max = 32, min = 32)
    @NotNull
    public byte[] peerID;

    public int subTree(Peer that) {
        byte[] bits = new byte[32];
        byte mask = (byte) (1 << 7);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (byte) (peerID[i] ^ that.peerID[i]);
        }
        for (int i = 0; i < 256; i++) {
            if ((bits[i / 8] & (mask >>> (i % 8))) != 0) {
                return i;
            }
        }
        return 0;
    }

    public String hostPort() {
        return host + ":" + port;
    }

    public static void main(String[] args) throws Exception {
        Peer.parse("wisdom://ff25eab70c89aa65e87de75dab7c20011eaa9ba217689aab5d2b7a9dd80ab704@192.168.0.104:9090");
    }

    public int distance(Peer that) {
        int res = 0;
        byte[] bits = new byte[32];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (byte) (peerID[i] ^ that.peerID[i]);
        }
        for (int i = 0; i < bits.length; i++) {
            for (int j = 0; j < 7; j++) {
                res += ((1 << j) & bits[i]) >>> j;
            }
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peer peer = (Peer) o;

        return Arrays.equals(peerID, peer.peerID);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(peerID);
    }

    public Peer() {
    }

    public static Peer newPeer(String url) throws Exception {
        URI u = new URI(url);
        if (u.getRawUserInfo() == null || u.getRawUserInfo().equals("")) {
            KeyPair kp = Ed25519.GenerateKeyPair();
            url = String.format("%s://%s@%s:%d", PROTOCOL_NAME,
                    Hex.encodeHexString(kp.getPrivateKey().getEncoded()) + Hex.encodeHexString(kp.getPublicKey().getEncoded()),
                    u.getHost(), u.getPort()
            );
        }
        return Peer.parse(url);
    }

    public String toString() {
        return String.format("%s://%s@%s", PROTOCOL_NAME, Hex.encodeHexString(peerID), hostPort());
    }

    public String key(){
        return Hex.encodeHexString(peerID);
    }
}
