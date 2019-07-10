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

import java.net.URI;
import java.util.Objects;
import java.util.regex.Pattern;

public class Peer {


    private static final Pattern IP_PATTERN = Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");


    public static Peer newPeer(String uri) {
        try {
            URI u = new URI(uri);
            String[] ips = u.getHost().split("\\.");
            return new Peer(
                    u.getUserInfo(), Integer.parseInt(ips[0]),
                    Integer.parseInt(ips[1]), Integer.parseInt(ips[2]),
                    Integer.parseInt(ips[3]), u.getPort() > 0 ? u.getPort() : 19585
            );
        } catch (Exception e) {
            return null;
        }
    }

    private String address;
    private int ip0;
    private int ip1;
    private int ip2;
    private int ip3;
    private int port;

    private Peer(String address, int ip0, int ip1, int ip2, int ip3, int port) {
        this.address = address;
        this.ip0 = ip0;
        this.ip1 = ip1;
        this.ip2 = ip2;
        this.ip3 = ip3;
        this.port = port;
    }

    public Peer() {

    }

    public String toString() {
        return String.format("wisdom://%s@%d.%d.%d.%d:%d", address, ip0, ip1, ip2, ip3, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return ip0 == peer.ip0 &&
                ip1 == peer.ip1 &&
                ip2 == peer.ip2 &&
                ip3 == peer.ip3 &&
                port == peer.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip0, ip1, ip2, ip3, port);
    }

    public int ipDistance(Peer other) {
        int bits = ((ip0 ^ other.ip0) << 24) | ((ip1 ^ other.ip1) << 16) | ((ip2 ^ other.ip2) << 8) | (ip3 ^ other.ip3);
        int dis = 0;
        for (int i = 0; i < 32; i++) {
            dis += ((bits & (1 << i)) >>> i);
        }
        return dis;
    }

    public static void main(String[] args){
        System.out.println(Peer.newPeer("wisdom://abcabc@192.168.0.1").ipDistance(
                Peer.newPeer("wisdom://abcabc@192.168.0.2")
        ));
        System.out.println(Peer.newPeer("wisdom://abcabc@192.168.0.116"));
    }
}