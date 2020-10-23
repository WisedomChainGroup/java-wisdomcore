package org.wisdom.vm.hosts;

import com.google.common.primitives.Bytes;
import org.tdf.common.util.BigEndian;
import org.tdf.common.util.HexBytes;
import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;
import org.wisdom.vm.abi.Uint256;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public class Util extends HostFunction {
    enum Type {
        CONCAT_BYTES,
        DECODE_HEX,
        ENCODE_HEX,
        BYTES_TO_U64,
        U64_TO_BYTES
    }

    public Util() {
        setType(
                new FunctionType(
                        Arrays.asList(
                                ValueType.I64, ValueType.I64,
                                ValueType.I64, ValueType.I64,
                                ValueType.I64, ValueType.I64,
                                ValueType.I64
                        ),
                        Collections.singletonList(ValueType.I64)
                )
        );
        setName("_util");
    }

    @Override
    public long[] execute(long... longs) {
        Type t = Type.values()[(int) longs[0]];
        boolean put = longs[6] != 0;
        byte[] data = null;
        long ret = 0;
        switch (t) {
            case CONCAT_BYTES: {
                byte[] a = loadMemory((int) longs[1], (int) longs[2]);
                byte[] b = loadMemory((int) longs[3], (int) longs[4]);
                data = Bytes.concat(a, b);
                ret = data.length;
                break;
            }
            case DECODE_HEX: {
                byte[] a = loadMemory((int) longs[1], (int) longs[2]);
                String str = new String(a, StandardCharsets.US_ASCII);
                data = HexBytes.decode(str);
                ret = data.length;
                break;
            }
            case ENCODE_HEX: {
                byte[] a = loadMemory((int) longs[1], (int) longs[2]);
                String str = HexBytes.encode(a);
                data = str.getBytes(StandardCharsets.US_ASCII);
                ret = data.length;
                break;
            }
            case BYTES_TO_U64: {
                put = false;
                byte[] bytes = loadMemory((int) longs[1], (int) longs[2]);
                byte[] padded = new byte[8];
                System.arraycopy(bytes, 0, padded, padded.length - bytes.length, bytes.length);
                ret = BigEndian.decodeInt64(padded);
                break;
            }
            case U64_TO_BYTES: {
                long u = longs[1];
                data = Uint256.getNoLeadZeroesData(BigEndian.encodeInt64(u));
                ret = data.length;
                break;
            }
        }
        if (put) {
            putMemory((int) longs[5], data);
        }
        return new long[]{ret};
    }
}
