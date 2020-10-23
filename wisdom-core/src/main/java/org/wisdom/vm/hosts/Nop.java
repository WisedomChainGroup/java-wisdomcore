package org.tdf.sunflower.vm.hosts;

import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;

import java.util.ArrayList;

// sizeof host function
public class Nop extends HostFunction {
    public Nop(String name) {
        setType(new FunctionType(new ArrayList<>(), new ArrayList<>()));
        setName(name);
    }

    @Override
    public long[] execute(long... parameters) {
        throw new UnsupportedOperationException();
    }
}
