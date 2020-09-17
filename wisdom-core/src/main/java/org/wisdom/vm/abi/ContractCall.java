package org.wisdom.vm.abi;

import lombok.Getter;
import org.tdf.common.store.Store;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.HexBytes;
import org.tdf.common.util.LittleEndian;
import org.tdf.lotusvm.ModuleInstance;
import org.tdf.lotusvm.types.Module;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPItem;
import org.tdf.rlp.RLPList;
import org.wisdom.core.Header;
import org.wisdom.core.account.Transaction;
import org.wisdom.crypto.HashUtil;
import org.wisdom.db.AccountState;
import org.wisdom.vm.hosts.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.wisdom.vm.Constants.MAX_CONTRACT_CALL_DEPTH;
import static org.wisdom.vm.Constants.PK_HASH_SIZE;

@Getter
public class ContractCall {

    private final Map<byte[], AccountState> states;
    private final Header header;
    private final Transaction transaction;

    private final Function<byte[], Trie<byte[], byte[]>> storageTrieSupplier;

    // contract code store
    private final Store<byte[], byte[]> contractStore;

    // gas limit hook
    private final Limit limit;

    // call depth
    private final int depth;

    // msg.sender
    private final byte[] sender;

    private final boolean readonly;

    // contract called currently
    private byte[] recipient;

    private AtomicInteger atomicInteger;

    public ContractCall(Map<byte[], AccountState> states, Header header, Transaction transaction, Function<byte[], Trie<byte[], byte[]>> storageTrieSupplier, Store<byte[], byte[]> contractStore, Limit limit, int depth, byte[] sender, boolean readonly, AtomicInteger atomicInteger) {
        this.states = states;
        this.header = header;
        this.transaction = transaction;
        this.storageTrieSupplier = storageTrieSupplier;
        this.contractStore = contractStore;
        this.limit = limit;
        this.depth = depth;
        this.sender = sender;
        this.readonly = readonly;
        this.atomicInteger = atomicInteger;
    }


    public static void assertContractAddress(byte[] pkHash) {
        if (pkHash.length != PK_HASH_SIZE)
            throw new RuntimeException("invalid public key hash size " + pkHash.length);

        // address starts with 18 zero is reversed
        for (int i = 0; i < 18; i++) {
            if (pkHash[i] != 0)
                return;
        }
        throw new RuntimeException("cannot call reversed address " + HexBytes.fromBytes(pkHash));
    }

    public ContractCall fork() {
        if (depth + 1 == MAX_CONTRACT_CALL_DEPTH)
            throw new RuntimeException("exceed call max depth");
        return new ContractCall(
                states,
                header,
                transaction,
                storageTrieSupplier,
                contractStore,
                this.limit.fork(),
                this.depth + 1,
                this.recipient,
                this.readonly,
                this.atomicInteger
        );
    }

    public static int allocString(ModuleInstance moduleInstance, String s) {
        byte[] data = s.getBytes(StandardCharsets.UTF_16LE);
        long id = (int) moduleInstance.execute("__idof", AbiDataType.STRING.ordinal())[0];
        long offset = moduleInstance.execute("__alloc", data.length, id)[0];
        moduleInstance.getMemory().put((int) offset, data);
        moduleInstance.execute("__retain", offset);
        return (int) offset;
    }

    public static int allocBytes(ModuleInstance moduleInstance, byte[] buf) {
        long id = (int) moduleInstance.execute("__idof", AbiDataType.BYTES.ordinal())[0];
        int offset = (int) moduleInstance.execute("__alloc", buf.length, id)[0];
        moduleInstance.getMemory().put(offset, buf);
        moduleInstance.execute("__retain", offset);
        return offset;
    }

    public static int allocAddress(ModuleInstance moduleInstance, byte[] addr) {
        long id = (int) moduleInstance.execute("__idof", AbiDataType.ADDRESS.ordinal())[0];
        int offset = (int) moduleInstance.execute("__alloc", 4L, id)[0];
        int ptr = allocBytes(moduleInstance, addr);
        moduleInstance.getMemory().put(offset, LittleEndian.encodeInt32(ptr));
        moduleInstance.execute("__retain", offset);
        return offset;
    }

    public static int allocU256(ModuleInstance moduleInstance, Uint256 u) {
        long id = (int) moduleInstance.execute("__idof", AbiDataType.U256.ordinal())[0];
        int offset = (int) moduleInstance.execute("__alloc", 4L, id)[0];
        int ptr = allocBytes(moduleInstance, u.getNoLeadZeroesData());
        moduleInstance.getMemory().put(offset, LittleEndian.encodeInt32(ptr));
        moduleInstance.execute("__retain", offset);
        return offset;
    }


    static Object getResult(ModuleInstance module, long offset, AbiDataType type) {
        switch (type) {
            case I64:
            case U64:
            case F64:
            case BOOL: {
                return offset;
            }
            case BYTES: {
                int len = module.getMemory().load32((int) offset - 4);
                return module.getMemory().loadN((int) offset, len);
            }
            case STRING: {
                int len = module.getMemory().load32((int) offset - 4);
                return new String(module.getMemory().loadN((int) offset, len), StandardCharsets.UTF_16LE);
            }
            case U256:
            case ADDRESS: {
                int ptr = module.getMemory().load32((int) offset);
                return getResult(module, ptr, AbiDataType.BYTES);
            }
            default:
                throw new UnsupportedOperationException();
        }
    }

    private long[] putParameters(ModuleInstance module, Parameters params) {
        long[] ret = new long[params.getTypes().length];
        for (int i = 0; i < ret.length; i++) {
            AbiDataType t = AbiDataType.values()[(int) params.getTypes()[i]];
            switch (t) {
                case I64:
                case U64:
                case F64:
                case BOOL: {
                    ret[i] = params.getLi().get(i).asLong();
                    break;
                }
                case BYTES: {
                    ret[i] = allocBytes(module, params.getLi().get(i).asBytes());
                    break;
                }
                case STRING: {
                    ret[i] = allocString(module, params.getLi().get(i).asString());
                    break;
                }
                case U256: {
                    ret[i] = allocU256(module, Uint256.of(params.getLi().get(i).asBytes()));
                    break;
                }
                case ADDRESS: {
                    ret[i] = allocAddress(module, params.getLi().get(i).asBytes());
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return ret;
    }

    public WASMResult call(byte[] binaryOrAddress, String method, Parameters parameters, Uint256 amount, boolean returnAddress, List<ContractABI> contractABIs) {
        boolean isDeploy = "init".equals(method);
        AccountState contractAccount;
        AccountState originAccount = readonly ? null : states.get(this.transaction.getFromPKHash());
        Module m = null;
        byte[] contractPKHash;

        if (isDeploy) {
            if (this.readonly)
                throw new RuntimeException("cannot deploy contract here");
            m = new Module(binaryOrAddress);
            byte[] hash = HashUtil.keccak256(binaryOrAddress);
            contractStore.put(hash, binaryOrAddress);
            contractPKHash = Transaction.createContractPKHash(transaction.getHash(), atomicInteger.get());
            this.atomicInteger.incrementAndGet();

            contractAccount = AccountState.emptyWASMAccount(contractPKHash, hash);
        } else {
            contractPKHash = binaryOrAddress;
            contractAccount = states.get(contractPKHash);
        }

        // transfer amount from origin account to contract account
        if (!readonly) {
            originAccount.setBalance(SafeMath.sub(originAccount.getBalance(), amount.longValue()));
            contractAccount.setBalance(SafeMath.add(originAccount.getBalance(), amount.longValue()));
            states.put(contractAccount.getPubkeyHash(), contractAccount);
            states.put(originAccount.getPubkeyHash(), originAccount);
        }


        this.recipient = contractPKHash;
        assertContractAddress(contractPKHash);


        // build Parameters here
        Context ctx = new Context(
                header,
                transaction,
                contractAccount,
                sender,
                amount
        );

        DBFunctions DBFunctions = new DBFunctions(
                storageTrieSupplier.apply(contractAccount.getStorageRoot()),
                this.readonly
        );

        if(isDeploy && contractABIs != null){
            DBFunctions.getStorageTrie().put("__abi".getBytes(StandardCharsets.UTF_8), RLPCodec.encode(contractABIs));
            contractAccount.setStorageRoot(DBFunctions.getStorageTrie().commit());
            states.put(contractPKHash, contractAccount);
        }

        Hosts hosts = new Hosts()
                .withTransfer(
                        states,
                        this.recipient,
                        readonly
                )
                .withReflect(new Reflect(this, readonly))
                .withContext(new ContextHost(ctx, states, contractStore, storageTrieSupplier, readonly))
                .withDB(DBFunctions)
                .withEvent(contractAccount.getPubkeyHash(), readonly);

        // every contract should have a init method
        ModuleInstance instance = ModuleInstance
                .builder()
                .hooks(Collections.singleton(limit))
                .hostFunctions(hosts.getAll())
                .binary(contractStore.get(contractAccount.getContractHash())
                        .orElseThrow(() -> new RuntimeException(
                                "contract " + HexBytes.fromBytes(this.recipient) + " not found in db")))
                .build();


        RLPList ret = RLPList.createEmpty();
        if (!isDeploy || instance.containsExport("init")) {
            long steps = limit.getSteps();
            long[] offsets = putParameters(instance, parameters);
            limit.setSteps(steps);

            long[] rets = instance.execute(method, offsets);
            if (parameters.getReturnType().length > 0) {
                ret.add(
                       RLPElement.readRLPTree(getResult(instance, rets[0], AbiDataType.values()[parameters.getReturnType()[0]]))
                );
            }
        }

        if (!readonly) {
            DBFunctions.getStorageTrie().commit();
            contractAccount = states.get(this.recipient);
            contractAccount.setStorageRoot(DBFunctions.getStorageTrie().getRootHash());
            states.put(contractAccount.getPubkeyHash(), contractAccount);
        }

        List<WASMEvent> events = hosts.getEventHost().getEvents();
        RLPList returns = returnAddress ? RLPList.fromElements(Collections.singleton(RLPItem.fromBytes(contractPKHash))) : ret;
        return new WASMResult(limit.getGas(), returns, events);
    }
}
