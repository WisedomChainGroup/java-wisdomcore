package org.wisdom.vm.hosts;

import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;
import org.wisdom.vm.abi.Uint256;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public class Uint256Host extends HostFunction {
    enum Type {
        PARSE,
        TOSTRING,
        ADD,
        SUB,
        MUL,
        DIV,
        MOD
    }

    public static final FunctionType FUNCTION_TYPE = new FunctionType(
            Arrays.asList(
                    ValueType.I64,
                    ValueType.I64, ValueType.I64,
                    ValueType.I64, ValueType.I64,
                    ValueType.I64, ValueType.I64
            ),
            Collections.singletonList(ValueType.I64)
    );

    public Uint256Host() {
        super("_u256", FUNCTION_TYPE);
    }

    @Override
    public long execute(long[] longs) {
        Type t = Type.values()[(int) longs[0]];
        byte[] data = null;
        boolean put = longs[6] != 0;
        long ret = 0;
        long offset = longs[5];
        switch (t) {
            case ADD:
                data = getX(longs).add(getY(longs)).getNoLeadZeroesData();
                ret = data.length;
                break;
            case SUB:
                data = getX(longs).sub(getY(longs)).getNoLeadZeroesData();
                ret = data.length;
                break;
            case MUL:
                data = getX(longs).mul(getY(longs)).getNoLeadZeroesData();
                ret = data.length;
                break;
            case DIV:
                data = getX(longs).div(getY(longs)).getNoLeadZeroesData();
                ret = data.length;
                break;
            case MOD:
                data = getX(longs).mod(getY(longs)).getNoLeadZeroesData();
                ret = data.length;
                break;
            case PARSE:
                String s = loadStringFromMemory((int) longs[1], (int) longs[2]);
                int radix = (int) longs[3];
                data = Uint256.of(s, radix).getNoLeadZeroesData();
                ret = data.length;
                break;
            case TOSTRING:
                data = getX(longs).value().toString((int) longs[3]).getBytes(StandardCharsets.US_ASCII);
                ret = data.length;
                break;
        }

        if (put){
            putMemory((int) offset, data);
        }
        return ret;
    }


    private Uint256 getX(long... longs) {
        return Uint256.of(loadMemory((int) longs[1], (int) longs[2]));
    }

    private Uint256 getY(long... longs) {
        return Uint256.of(loadMemory((int) longs[3], (int) longs[4]));
    }
}
