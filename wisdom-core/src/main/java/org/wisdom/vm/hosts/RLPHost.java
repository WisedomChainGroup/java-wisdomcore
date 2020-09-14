package org.wisdom.vm.hosts;

import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RLPHost extends HostFunction {
    enum Type {
        ENCODE_U64,
        ENCODE_BYTES,
        DECODE_BYTES,
        RLP_LIST_SET, // add rlp list to global env
        RLP_LIST_CLEAR,
        RLP_LIST_LEN,
        RLP_LIST_GET,
        RLP_LIST_PUSH,
        RLP_LIST_BUILD // build
    }

    private RLPList list;
    private List<byte[]> elements;
    private byte[] elementsEncoded;

    public RLPHost() {
        setType(new FunctionType(
                // offset, length, offset
                Arrays.asList(ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64),
                Collections.singletonList(ValueType.I64)
        ));
        setName("_rlp");
    }

    @Override
    public long[] execute(long... longs) {
        Type t = Type.values()[(int) longs[0]];
        long ret = 0;
        boolean put = longs[4] != 0;
        byte[] data = null;
        switch (t) {
            case ENCODE_U64: {
                data = RLPCodec.encode(longs[1]);
                ret = data.length;
                break;
            }
            case ENCODE_BYTES: {
                byte[] beforeEncode = loadMemory((int) longs[1], (int) longs[2]);
                data = RLPCodec.encodeBytes(beforeEncode);
                ret = data.length;
                break;
            }
            case DECODE_BYTES: {
                byte[] encoded = loadMemory((int) longs[1], (int) longs[2]);
                data = RLPElement.fromEncoded(encoded).asBytes();
                ret = data.length;
                break;
            }
            case RLP_LIST_SET: {
                put = false;
                this.list = RLPElement
                        .fromEncoded(loadMemory((int) longs[1], (int) longs[2]))
                        .asRLPList();
                break;
            }
            case RLP_LIST_CLEAR: {
                put = false;
                this.list = null;
                break;
            }
            case RLP_LIST_LEN: {
                put = false;
                ret = this.list.size();
                break;
            }
            case RLP_LIST_GET: {
                data = list.get((int) longs[1]).getEncoded();
                ret = data.length;
                break;
            }
            case RLP_LIST_PUSH: {
                put = false;
                if (elements == null)
                    elements = new ArrayList<>();
                this.elementsEncoded = null;
                byte[] bytes = loadMemory((int) longs[1], (int) longs[2]);
                this.elements.add(bytes);
                break;
            }
            case RLP_LIST_BUILD: {
                if (this.elementsEncoded == null)
                    this.elementsEncoded = RLPCodec.encodeElements(this.elements);
                data = this.elementsEncoded;
                ret = data.length;
                if (longs[4] != 0) {
                    this.elementsEncoded = null;
                    this.elements = null;
                }
                break;
            }
            default:
                throw new RuntimeException("unexpected");
        }
        if (put) {
            putMemory((int) longs[3], data);
        }
        return new long[]{ret};
    }
}
