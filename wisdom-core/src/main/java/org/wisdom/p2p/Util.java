package org.wisdom.p2p;

import org.wisdom.encoding.BigEndian;
import org.wisdom.util.Arrays;

import java.nio.charset.StandardCharsets;

class Util {
    static byte[] getRawForSign(WisdomOuterClass.Message msg) {
        return Arrays.concatenate(new byte[][]{
                        BigEndian.encodeUint32(msg.getCode().getNumber()),
                        BigEndian.encodeUint64(msg.getCreatedAt().getSeconds()),
                        msg.getRemotePeer().getBytes(StandardCharsets.UTF_8),
                        BigEndian.encodeUint64(msg.getTtl()),
                        BigEndian.encodeUint64(msg.getNonce()),
                        msg.getBody().toByteArray()
                }
        );
    }
}
