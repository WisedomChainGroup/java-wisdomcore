package org.wisdom.vm.hosts;

import lombok.Getter;
import org.tdf.common.util.HexBytes;
import org.tdf.lotusvm.runtime.HostFunction;
import org.wisdom.db.AccountState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Hosts {
    private ContextHost contextHost;

    private DBFunctions dbFunctions;

    private Transfer transfer;

    @Getter
    private EventHost eventHost;

    private Reflect reflect;

    public Hosts() {
    }

    public Hosts withEvent(byte[] pkHash, boolean readonly) {
        this.eventHost = new EventHost(pkHash, readonly);
        return this;
    }

    public Hosts withReflect(Reflect reflect) {
        this.reflect = reflect;
        return this;
    }

    public Hosts withTransfer(
            Map<byte[], AccountState> states,
            byte[] contractPKHash,
            boolean readonly
    ) {
        this.transfer = new Transfer(states, contractPKHash, readonly);
        return this;
    }

    public Set<HostFunction> getAll() {
        Set<HostFunction> all = new HashSet<>(
                Arrays.asList(
                        new Abort(), new HashHost(),
                        new Log(), new RLPHost(),
                        new Util(), new Uint256Host()
                )
        );

        if(reflect != null)
            all.add(this.reflect);

        if (eventHost != null)
            all.add(this.eventHost);

        if (contextHost != null) {
            all.add(contextHost);
        }

        if (this.transfer != null) {
            all.add(this.transfer);
        }

        if (dbFunctions != null) {
            all.add(dbFunctions);
        }
        return all;
    }

    public Hosts withContext(ContextHost host) {
        this.contextHost = host;
        return this;
    }

    public Hosts withDB(DBFunctions dbFunctions) {
        this.dbFunctions = dbFunctions;
        return this;
    }
}
