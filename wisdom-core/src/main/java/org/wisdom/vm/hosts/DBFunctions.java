package org.wisdom.vm.hosts;

import lombok.Getter;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.HexBytes;
import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;

import java.util.*;

public class DBFunctions extends HostFunction {
    enum Type {
        SET, GET, REMOVE, HAS, NEXT, CURRENT_KEY, CURRENT_VALUE, HAS_NEXT, RESET
    }

    @Getter
    private final Trie<byte[], byte[]> storageTrie;
    private List<Map.Entry<HexBytes, byte[]>> entries;
    private int index;
    private final boolean readonly;
    public static final FunctionType FUNCTION_TYPE = new FunctionType(
            Arrays.asList(ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64),
            Collections.singletonList(ValueType.I64)
    );

    private void reset() {
        Map<HexBytes, byte[]> m = new TreeMap<>();
        storageTrie.forEach((x, y) -> m.put(HexBytes.fromBytes(x), y));
    }

    public DBFunctions(Trie<byte[], byte[]> storageTrie, boolean readonly) {
        super("_db", FUNCTION_TYPE);
        this.storageTrie = storageTrie;
        reset();
        this.readonly = readonly;
    }

    private void assertReadOnly(Type t){
        switch (t){
            case SET:
            case REMOVE:
                if (readonly)
                    throw new RuntimeException("readonly");
                break;
        }

    }

    @Override
    public long execute(long[] longs) {
        Type t = Type.values()[(int) longs[0]];
        assertReadOnly(t);
        switch (t) {
            case SET: {
                byte[] key = loadMemory((int) longs[1], (int) longs[2]);
                byte[] value = loadMemory((int) longs[3], (int) longs[4]);
                this.storageTrie.put(key, value);
                break;
            }
            case GET: {
                byte[] key = loadMemory((int) longs[1], (int) longs[2]);
                byte[] value = storageTrie.get(key).orElseThrow(() -> new RuntimeException(HexBytes.fromBytes(key) + " not found"));
                if (longs[4] != 0) {
                    putMemory((int) longs[3], value);
                }
                return value.length;
            }
            case HAS: {
                byte[] key = loadMemory((int) longs[1], (int) longs[2]);
                return storageTrie.containsKey(key) ? 1 : 0;
            }
            case REMOVE: {
                if (readonly)
                    throw new RuntimeException("readonly");
                byte[] key = loadMemory((int) longs[1], (int) longs[2]);
                storageTrie.remove(key);
                break;
            }
            case NEXT: {
                this.index++;
                break;
            }
            case HAS_NEXT: {
                return this.index < entries.size() - 1 ? 1 : 0;
            }
            case CURRENT_KEY: {
                Map.Entry<HexBytes, byte[]> entry = entries.get(index);
                if (longs[2] != 0) {
                    putMemory((int) longs[1], entry.getKey().getBytes());
                }
                return entry.getKey().size();
            }
            case CURRENT_VALUE: {
                Map.Entry<HexBytes, byte[]> entry = entries.get(index);
                if (longs[2] != 0) {
                    putMemory((int) longs[1], entry.getValue());
                }
                return entry.getValue().length;
            }
            case RESET: {
                reset();
                break;
            }
            default:
                throw new RuntimeException("unreachable");
        }
        return 0;
    }
}
