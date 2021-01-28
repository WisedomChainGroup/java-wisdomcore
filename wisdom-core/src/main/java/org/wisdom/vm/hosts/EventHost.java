package org.wisdom.vm.hosts;

import lombok.Getter;
import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;
import org.wisdom.controller.WebSocket;
import org.wisdom.vm.abi.WASMEvent;

import java.util.*;

public class EventHost extends HostFunction {
    private byte[] pkHash;
    private boolean readonly;
    @Getter
    private final List<WASMEvent> events;
    public static final FunctionType FUNCTION_TYPE = new FunctionType(
            Arrays.asList
                    (ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64),
            Collections.emptyList()
    );

    public EventHost(byte[] pkHash, boolean readonly) {
        super("_event", FUNCTION_TYPE);
        this.pkHash = pkHash;
        this.readonly = readonly;
        this.events = new ArrayList<>();
    }

    @Override
    public long execute(long[] parameters) {
        if (readonly)
            throw new RuntimeException("cannot call event here");
        String x = loadStringFromMemory((int) parameters[0], (int) parameters[1]);
        byte[] y = loadMemory((int) parameters[2], (int) parameters[3]);
        RLPList li = RLPElement.fromEncoded(y).asRLPList();
        WASMEvent e = new WASMEvent(x, li);
        WebSocket.broadcastEvent(pkHash, e.getName(), e.getOutputs());
        events.add(e);
        return 0;
    }
}
