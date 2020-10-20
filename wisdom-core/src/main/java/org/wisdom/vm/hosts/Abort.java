package org.wisdom.vm.hosts;

import org.tdf.common.util.LittleEndian;
import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Abort extends HostFunction {
    public Abort() {
        setType(new FunctionType(
                Arrays.asList(
                        ValueType.I32,
                        ValueType.I32,
                        ValueType.I32,
                        ValueType.I32
                ),
                new ArrayList<>())
        );
        setName("abort");
    }

    @Override
    public long[] execute(long... parameters) {
        String message = parameters[0] == 0 ? "" : this.loadString((int) parameters[0]);
        String filename = parameters[1] == 0 ? "" : this.loadString((int) parameters[1]);
        throw new RuntimeException(String.format("%s %s error at line %d column %d",
                filename, message,
                parameters[2], parameters[3])
        );
    }

    private String loadString(int offset) {
        int size = LittleEndian.decodeInt32(loadMemory(offset - 4, 4));
        byte[] mem = loadMemory(offset, size);
        return new String(mem, StandardCharsets.UTF_16LE);
    }
}
