package org.wisdom.vm.hosts;

import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.HexBytes;
import org.tdf.lotusvm.runtime.HostFunction;
import org.tdf.lotusvm.types.FunctionType;
import org.tdf.lotusvm.types.ValueType;
import org.wisdom.db.AccountState;
import org.wisdom.util.Address;
import org.wisdom.vm.abi.Context;
import org.wisdom.vm.abi.Uint256;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class ContextHost extends HostFunction {
    public enum Type {
        HEADER_PARENT_HASH,
        HEADER_CREATED_AT,
        HEADER_HEIGHT,
        TX_TYPE,
        TX_CREATED_AT,
        TX_NONCE,
        TX_ORIGIN,
        TX_GAS_PRICE,
        TX_AMOUNT,
        TX_TO,
        TX_SIGNATURE,
        TX_HASH,
        CONTRACT_PK_HASH,
        CONTRACT_NONCE,
        ACCOUNT_NONCE,
        ACCOUNT_BALANCE,
        MSG_SENDER,
        MSG_AMOUNT,
        CONTRACT_CODE,
        CONTRACT_ABI
    }

    private Context context;
    private Map<byte[], AccountState> states;
    private Store<byte[], byte[]> contractCodeStore;
    private Function<byte[], Trie<byte[], byte[]>> storageTrieSupplier;
    private boolean readonly;

    public ContextHost(
            Context context,
            Map<byte[], AccountState> states,
            Store<byte[], byte[]> contractCodeStore,
            Function<byte[], Trie<byte[], byte[]>> storageTrieSupplier,
            boolean readonly
    ) {
        setName("_context");
        setType(
                new FunctionType(Arrays.asList(ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64, ValueType.I64),
                        Collections.singletonList(ValueType.I64))
        );
        this.context = context;
        this.states = states;
        this.contractCodeStore = contractCodeStore;
        this.storageTrieSupplier = storageTrieSupplier;
        this.readonly = readonly;
    }

    @Override
    public long[] execute(long... parameters) {
        Type type = Type.values()[(int) parameters[0]];
        long ret = 0;
        boolean isPut = parameters[2] != 0;
        byte[] data = null;
        long offset = parameters[1];
        if ((type != Type.CONTRACT_PK_HASH
                && type != Type.CONTRACT_NONCE
                && type != Type.ACCOUNT_NONCE
                && type != Type.ACCOUNT_BALANCE
                && type != Type.CONTRACT_CODE
                && type != Type.CONTRACT_ABI
        ) && readonly) {
            throw new RuntimeException("not available here");
        }
        switch (type) {
            case HEADER_PARENT_HASH: {
                data = context.getParentHash();
                ret = data.length;
                break;
            }
            case HEADER_CREATED_AT: {
                isPut = false;
                ret = context.getTimestamp();
                break;
            }
            case HEADER_HEIGHT: {
                isPut = false;
                ret = context.getHeight();
                break;
            }
            case TX_TYPE: {
                isPut = false;
                ret = context.getTransaction().type;
                break;
            }
            case TX_CREATED_AT: {
                isPut = false;
                ret = 0;
                break;
            }
            case TX_NONCE: {
                isPut = false;
                ret = context.getTransaction().nonce;
                break;
            }
            case TX_ORIGIN: {
                data = Address.publicKeyToHash(context.getTransaction().from);
                ret = data.length;
                break;
            }
            case TX_GAS_PRICE: {
                data = Uint256.of(context.getTransaction().gasPrice).getNoLeadZeroesData();
                ret = data.length;
                break;
            }
            case TX_AMOUNT: {
                data = Uint256.of(context.getTransaction().amount).getNoLeadZeroesData();
                ret = data.length;
                break;
            }
            case TX_TO: {
                data = context.getTransaction().to;
                ret = data.length;
                break;
            }
            case TX_SIGNATURE: {
                data = context.getTransaction().signature;
                ret = data.length;
                break;
            }
            case TX_HASH: {
                data = context.getTransaction().getHash();
                ret = data.length;
                break;
            }
            case CONTRACT_PK_HASH: {
                data = context.getContractAccount().getAccount().getPubkeyHash();
                ret = data.length;
                break;
            }
            case CONTRACT_NONCE: {
                isPut = false;
                ret = context.getContractAccount().getAccount().getNonce();
                break;
            }
            case ACCOUNT_NONCE: {
                isPut = false;
                byte[] pkHash = loadMemory((int) parameters[1], (int) parameters[2]);
                AccountState a = states.get(pkHash);
                ret = a.getAccount().getNonce();
                break;
            }
            case ACCOUNT_BALANCE: {
                byte[] pkHash = loadMemory((int) parameters[1], (int) parameters[2]);
                AccountState a = states.get(pkHash);
                data = Uint256.of(a.getAccount().getBalance()).getNoLeadZeroesData();
                offset = parameters[3];
                isPut = parameters[4] != 0;
                ret = data.length;
                break;
            }
            case MSG_SENDER: {
                data = context.getMsgSender();
                ret = data.length;
                break;
            }
            case MSG_AMOUNT: {
                data = context.getAmount().getNoLeadZeroesData();
                ret = data.length;
                break;
            }
            case CONTRACT_CODE: {
                byte[] pkHash = loadMemory((int) parameters[1], (int) parameters[2]);
                AccountState a = states.get(pkHash);
                if (a.getContractHash() == null || a.getContractHash().length == 0)
                    throw new RuntimeException(HexBytes.fromBytes(pkHash) + " is not a contract account");
                data = this.contractCodeStore.get(a.getContractHash()).get();
                ret = data.length;
                isPut = parameters[4] != 0;
                offset = parameters[3];
                break;
            }
            case CONTRACT_ABI:{
                byte[] pkHash = loadMemory((int) parameters[1], (int) parameters[2]);
                AccountState a = states.get(pkHash);
                if (a.getContractHash() == null || a.getContractHash().length == 0)
                    throw new RuntimeException(HexBytes.fromBytes(pkHash) + " is not a contract account");
                data = this.storageTrieSupplier.apply(a.getStorageRoot()).get("__abi".getBytes(StandardCharsets.UTF_8)).get();
                ret = data.length;
                isPut = parameters[4] != 0;
                offset = parameters[3];
                break;
            }
            default:
                throw new RuntimeException("unexpected type " + type);
        }

        if (isPut) {
            putMemory((int) offset, data);
        }
        return new long[]{ret};
    }

}
