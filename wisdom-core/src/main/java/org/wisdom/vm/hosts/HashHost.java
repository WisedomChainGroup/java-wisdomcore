package org.wisdom.vm.hosts;

import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;
import org.wisdom.crypto.HashUtil;

import java.util.Arrays;
import java.util.Collections;

public class HashHost extends HostFunction {
    enum Algorithm{
        KECCAK256
    }
    public static final FunctionType FUNCTION_TYPE = new FunctionType(
            // offset, length, offset
            Arrays.asList(ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64),
            Collections.singletonList(ValueType.I64)
    );

    public HashHost() {
        super("_hash", FUNCTION_TYPE);
    }

    @Override
    public long execute(long[] parameters) {
        byte[] data = loadMemory((int) parameters[1], (int) parameters[2]);
        Algorithm a = Algorithm.values()[(int) parameters[0]];
        byte[] ret;
        switch (a){
            case KECCAK256:
                ret = HashUtil.keccak256(data);
                break;
            default:
                throw new RuntimeException("unreachable");
        }
        if(parameters[4] != 0)
            putMemory((int) parameters[3], ret);
        return ret.length;
    }
}
