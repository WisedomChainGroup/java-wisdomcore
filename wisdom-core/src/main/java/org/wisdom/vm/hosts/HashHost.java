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

    public HashHost() {
        setType(new FunctionType(
                // offset, length, offset
                Arrays.asList(ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64),
                Collections.singletonList(ValueType.I64)
        ));
        setName("_hash");
    }

    @Override
    public long[] execute(long... parameters) {
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
        return new long[]{ret.length};
    }
}
