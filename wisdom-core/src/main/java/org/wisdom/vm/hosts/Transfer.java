package org.wisdom.vm.hosts;

import org.tdf.common.util.HexBytes;
import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;
import org.wisdom.core.account.Account;
import org.wisdom.db.AccountState;
import org.wisdom.vm.abi.SafeMath;
import org.wisdom.vm.abi.Uint256;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


public class Transfer extends HostFunction {
    private final Map<byte[], AccountState> states;
    private final byte[] contractAddress;
    private final boolean readonly;

    public Transfer(Map<byte[], AccountState> states, byte[] contractAddress, boolean readonly) {
        this.states = states;
        this.contractAddress = contractAddress;
        this.readonly = readonly;
        setType(
                new FunctionType(
                        Arrays.asList(
                                ValueType.I64, ValueType.I64,
                                ValueType.I64, ValueType.I64,
                                ValueType.I64
                        ),
                        Collections.emptyList()
                )
        );
        setName("_transfer");
    }

    @Override
    public long[] execute(long... parameters) {
        if(readonly)
            throw new RuntimeException("transfer is not allowed here");
        if (parameters[0] != 0) {
            throw new RuntimeException("unexpected");
        }
        Uint256 amount = Uint256.of(loadMemory((int) parameters[3], (int) parameters[4]));
        AccountState contractAccount = states.get(this.contractAddress);
        byte[] to = loadMemory((int) parameters[1], (int) parameters[2]);
        contractAccount.setBalance(SafeMath.sub(contractAccount.getBalance(), amount.longValue()));

        states.putIfAbsent(to, new AccountState(to));
        AccountState toAccount = states.get(to);
        toAccount.setBalance(SafeMath.add(toAccount.getBalance(), amount.longValue()));
        states.put(contractAddress, contractAccount);
        states.put(toAccount.getPubkeyHash(), toAccount);
        return new long[0];
    }
}
