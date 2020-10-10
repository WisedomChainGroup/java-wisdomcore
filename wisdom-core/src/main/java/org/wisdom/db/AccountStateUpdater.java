package org.wisdom.db;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.wisdom.command.Configuration;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.contract.AssetDefinition.AssetChangeowner;
import org.wisdom.contract.AssetDefinition.AssetIncreased;
import org.wisdom.contract.AssetDefinition.AssetTransfer;
import org.wisdom.contract.HashheightblockDefinition.Hashheightblock;
import org.wisdom.contract.HashheightblockDefinition.HashheightblockGet;
import org.wisdom.contract.HashheightblockDefinition.HashheightblockTransfer;
import org.wisdom.contract.HashtimeblockDefinition.Hashtimeblock;
import org.wisdom.contract.HashtimeblockDefinition.HashtimeblockGet;
import org.wisdom.contract.HashtimeblockDefinition.HashtimeblockTransfer;
import org.wisdom.contract.MultipleDefinition.MultTransfer;
import org.wisdom.contract.MultipleDefinition.Multiple;
import org.wisdom.contract.RateheightlockDefinition.Extract;
import org.wisdom.contract.RateheightlockDefinition.Rateheightlock;
import org.wisdom.contract.RateheightlockDefinition.RateheightlockDeposit;
import org.wisdom.contract.RateheightlockDefinition.RateheightlockWithdraw;
import org.wisdom.core.Block;
import org.wisdom.core.DB;
import org.wisdom.core.Header;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.encoding.BigEndian;
import org.wisdom.genesis.Genesis;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.Address;
import org.wisdom.util.ByteUtil;
import org.wisdom.vm.abi.*;
import org.wisdom.vm.hosts.Limit;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Setter
// TODO: make omit branch unreachable
public class AccountStateUpdater {

    @Autowired
    private RateTable rateTable;

    @Autowired
    private Genesis genesisJSON;

    @Autowired
    private Block genesis;

    @Autowired
    private Configuration configuration;

    private WisdomBlockChain wisdomBlockChain;

    private static final byte[] twentyBytes = new byte[20];

    public WASMResult update(DB db, Header header, Transaction tx) {
        return updateOne(db, header, tx);
    }

    public WASMResult updateOne(
            DB db, Header header, Transaction transaction) {
        Map<byte[], AccountState> store = db.getAccountStore();
        long height = header.getnHeight();
        transaction.height = height;
        Transaction.Type t = Transaction.Type.values()[transaction.type];
        switch (t) {
            case WASM_DEPLOY: {
                AccountState createdBy = store.get(transaction.getFromPKHash());

                // 校验 nonce
                if (createdBy.getNonce() + 1 != transaction.nonce)
                    throw new RuntimeException("nonce is too small");

                createdBy.setNonce(transaction.nonce);
                store.put(createdBy.getPubkeyHash(), createdBy);


                ContractDeployPayload contractDeployPayload =
                        RLPCodec.decode(transaction.payload, ContractDeployPayload.class);

                Limit limit = new Limit(0, 0, contractDeployPayload.getGasLimit(), transaction.payload.length / 1024);

                // execute constructor of contract
                ContractCall contractCall = new ContractCall(
                        store, header,
                        transaction, root -> db.getStorageTrie().revert(root), db.getContractCodeStore(),
                        limit, 0, transaction.getFromPKHash(),
                        false, new AtomicInteger()
                );

                WASMResult ret = contractCall.call(
                        contractDeployPayload.getBinary(),
                        "init",
                        contractDeployPayload.getParameters(),
                        Uint256.of(transaction.amount),
                        false,
                        contractDeployPayload.getContractABIs()
                );


                // restore from map
                createdBy = store.get(transaction.getFromPKHash());

                // estimate gas
                Uint256 fee = Uint256.of(limit.getGas()).safeMul(Uint256.of(transaction.gasPrice));
                createdBy.subBalance(fee.longValue());
                store.put(createdBy.getPubkeyHash(), createdBy);
                return ret;
            }
            case WASM_CALL: {
                AccountState originAccount = store.get(transaction.getFromPKHash());

                // 校验 nonce
                if (originAccount.getNonce() + 1 != transaction.nonce)
                    throw new RuntimeException("nonce is too small");

                originAccount.setNonce(transaction.nonce);
                store.put(originAccount.getPubkeyHash(), originAccount);

                AccountState contractAccount = store.get(transaction.to);
                if (contractAccount == null) {
                    throw new RuntimeException("contract " + HexBytes.fromBytes(transaction.to) + " not found");
                }

                ContractCallPayload callPayload = RLPCodec.decode(transaction.payload, ContractCallPayload.class);

                // execute method
                Limit limit = new Limit(0, 0, callPayload.getGasLimit(), transaction.payload.length / 1024);
                ContractCall contractCall = new ContractCall(
                        store, header,
                        transaction, root -> db.getStorageTrie().revert(root),
                        db.getContractCodeStore(),
                        limit, 0, transaction.getFromPKHash(),
                        false, new AtomicInteger()
                );

                WASMResult result = contractCall.call(
                        contractAccount.getPubkeyHash(),
                        callPayload.getMethod(),
                        callPayload.getParameters(),
                        Uint256.of(transaction.amount),
                        false,
                        null
                );

                contractAccount = store.get(transaction.to);
                AccountState caller = store.get(transaction.getFromPKHash());

                Uint256 fee = Uint256.of(limit.getGas()).safeMul(Uint256.of(transaction.gasPrice));
                caller.subBalance(fee.longValue());
                store.put(contractAccount.getPubkeyHash(), contractAccount);
                store.put(caller.getPubkeyHash(), caller);
                return result;
            }
        }
        long gas = Transaction.GAS_TABLE[transaction.type];
        WASMResult empty = WASMResult.empty(gas);

        switch (transaction.type) {
            case 0x00://coinbase
                updateCoinBase(transaction, store, height);
                return empty;
            case 0x01://TRANSFER
                updateTransfer(transaction, store, height);
                return empty;
            case 0x02://VOTE
                updateVote(transaction, store, height);
                return empty;
            case 0x03://DEPOSIT
                updateDeposit(transaction, store, height);
                return empty;
            case 0x07://DEPLOY_CONTRACT
                updateDeployContract(transaction, store, height);
                return empty;
            case 0x08://CALL_CONTRACT
                updateCallContract(transaction, store, height);
                return empty;
            case 0x09://INCUBATE
                updateIncubate(transaction, store, height);
                return empty;
            case 0x0a://EXTRACT_INTEREST
                updateExtractInterest(transaction, store, height);
                return empty;
            case 0x0b://EXTRACT_SHARING_PROFIT
                updateExtractShare(transaction, store, height);
                return empty;
            case 0x0c://EXTRACT_COST
                updateExtranctCost(transaction, store, height);
                return empty;
            case 0x0d://EXIT_VOTE
                updateCancelVote(transaction, store, height);
                return empty;
            case 0x0e://MORTGAGE
                updateMortgage(transaction, store, height);
                return empty;
            case 0x0f://EXTRACT_MORTGAGE
                updateCancelMortgage(transaction, store, height);
                return empty;
            default:
                throw new RuntimeException("unsupported transaction type: " + Transaction.Type.values()[transaction.type].toString());
        }
    }

    @Deprecated
    public Set<byte[]> getRelatedKeys(Transaction transaction, Map<byte[], AccountState> store) {
        switch (transaction.type) {
            case 0x00://coinbase
            case 0x09://INCUBATE
            case 0x0a://EXTRACT_INTEREST
            case 0x0b://EXTRACT_SHARING_PROFIT
            case 0x0c://EXTRACT_COST
            case 0x0e://MORTGAGE
            case 0x0f://EXTRACT_MORTGAGE
                return getTransactionTo(transaction);
            case 0x01://TRANSFER
            case 0x02://VOTE
            case 0x0d://EXIT_VOTE
                return getTransactionFromTo(transaction);
            case 0x03://DEPOSIT
                return getTransactionFrom(transaction);
            case 0x07://DEPLOY_CONTRACT
                return getTransactionHash(transaction);
            case 0x08://CALL_CONTRACT
                return getTransactionPayload(transaction);
        }
        return new ByteArraySet();
    }

    private Set<byte[]> getTransactionPayload(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        tx.setMethodType(tx.payload[0]);
        byte[] fromhash = Address.publicKeyToHash(tx.from);
        bytes.add(fromhash);
        if (tx.getContractType() == 0) {//代币
            byte[] rlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
            switch (tx.getMethodType()) {
                case 0://更换所有者
                case 2://增发
                    bytes.add(tx.to);
                    break;
                case 1://转发资产
                    AssetTransfer assetTransfer = AssetTransfer.getAssetTransfer(rlpbyte);
                    if (!Arrays.equals(fromhash, assetTransfer.getTo())) {
                        bytes.add(assetTransfer.getTo());
                    }
                    break;
            }
        } else if (tx.getContractType() == 1) {//多签
            byte[] rlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
            MultTransfer multTransfer = MultTransfer.getMultTransfer(rlpbyte);
            if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 1) {//单->多
                bytes.add(multTransfer.getTo());
            } else {//多->多 || 多->单
                if (!Arrays.equals(tx.to, multTransfer.getTo())) {
                    bytes.add(multTransfer.getTo());
                }
            }
            bytes.add(tx.to);
        } else if (tx.getContractType() == 4) {//定额条件比例支付
            bytes.add(tx.to);
            if (tx.getMethodType() == 9) {//定额条件比例获取
                RateheightlockWithdraw rateheightlockWithdraw = RateheightlockWithdraw.getRateheightlockWithdraw(ByteUtil.bytearrayridfirst(tx.payload));
                if (!Arrays.equals(fromhash, rateheightlockWithdraw.getTo())) {
                    bytes.add(rateheightlockWithdraw.getTo());
                }
            }
        } else {//锁定合约
            bytes.add(tx.to);
        }
        return bytes;
    }

    private Set<byte[]> getTransactionHash(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        bytes.add(RipemdUtility.ripemd160(tx.getHash()));
        byte[] fromhash = Address.publicKeyToHash(tx.from);
        bytes.add(fromhash);
        if (tx.getContractType() == 0) {//代币
            byte[] rlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
            Asset asset = Asset.getAsset(rlpbyte);
            if (!Arrays.equals(fromhash, asset.getOwner())) {
                bytes.add(asset.getOwner());
            }
        }
        return bytes;
    }

    private Set<byte[]> getTransactionTo(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        bytes.add(tx.to);
        // INCUBATE
        if (tx.type == 0x09) {
            bytes.add(IncubatorAddress.resultpubhash());
            try {
                //分享地址
                HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(tx.payload);
                String sharpub = payloadproto.getSharePubkeyHash();
                if (sharpub != null && !sharpub.equals("")) {
                    bytes.add(Hex.decodeHex(sharpub.toCharArray()));
                }
            } catch (Exception e) {
                return bytes;
            }
        }
        return bytes;
    }

    private Set<byte[]> getTransactionFromTo(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        byte[] fromhash = Address.publicKeyToHash(tx.from);
        bytes.add(fromhash);
        if (!Arrays.equals(fromhash, tx.to)) {
            bytes.add(tx.to);
        }
        return bytes;
    }

    private Set<byte[]> getTransactionFrom(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        byte[] fromhash = Address.publicKeyToHash(tx.from);
        bytes.add(fromhash);
        return bytes;
    }


    private void updateCoinBase(Transaction tx, Map<byte[], AccountState> states, long height) {
        AccountState toAccount = states.getOrDefault(tx.to, new AccountState(tx.to));
        toAccount.addBalance(tx.amount);
        toAccount.setBlockHeight(height);
        states.put(toAccount.getPubkeyHash(), toAccount);
    }

    private void updateTransfer(Transaction tx, Map<byte[], AccountState> states, long height) {
        AccountState from = states.get(tx.getFromPKHash());
        from.subBalance(tx.amount);
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        states.put(from.getPubkeyHash(), from);
        AccountState to = states.getOrDefault(tx.to, new AccountState(tx.to));
        to.addBalance(tx.amount);
        to.setBlockHeight(height);
        states.put(to.getPubkeyHash(), to);
    }

    private void updateVote(Transaction tx, Map<byte[], AccountState> states, long height) {
        AccountState from = states.get(tx.getFromPKHash());
        from.subBalance(tx.amount);
        from.subBalance(tx.getFee());
        from.setBlockHeight(height);
        from.setNonce(tx.nonce);
        states.put(from.getPubkeyHash(), from);
        AccountState to = states.getOrDefault(tx.to, new AccountState(tx.to));
        to.addVote(tx.amount);
        to.setBlockHeight(height);
        states.put(to.getPubkeyHash(), to);
    }

    private void updateDeposit(Transaction tx, Map<byte[], AccountState> states, long height) {
        AccountState from = states.get(tx.getFromPKHash());
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        states.put(from.getPubkeyHash(), from);
    }

    private void updateDeployContract(Transaction tx, Map<byte[], AccountState> states, long height) {
        tx.setContractType(tx.payload[0]);
        byte[] rlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
        AccountState from;
        AccountState contract = new AccountState(RipemdUtility.ripemd160(tx.getHash()));
        contract.setContract(rlpbyte);
        switch (tx.getContractType()) {
            case 0:
                Asset asset = Asset.getAsset(rlpbyte);
                //from
                from = states.get(tx.getFromPKHash());
                from.subBalance(tx.getFee());
                from.setNonce(tx.nonce);
                from.setBlockHeight(height);
                if (Arrays.equals(from.getPubkeyHash(), asset.getOwner())) {//from和owner相同
                    Map<byte[], Long> tokensmap = from.getTokensMap();
                    tokensmap.put(RipemdUtility.ripemd160(tx.getHash()), asset.getTotalamount());
                    from.setTokensMap(tokensmap);
                } else {
                    AccountState owner = states.getOrDefault(asset.getOwner(), new AccountState(asset.getOwner()));
                    Map<byte[], Long> tokensmap = owner.getTokensMap();
                    tokensmap.put(RipemdUtility.ripemd160(tx.getHash()), asset.getTotalamount());
                    owner.setTokensMap(tokensmap);
                    states.put(asset.getOwner(), owner);
                }
                contract.setType(1);
                states.put(tx.getFromPKHash(), from);
                states.put(contract.getPubkeyHash(), contract);
                break;
            case 1://多签
            case 2://锁定时间哈希
            case 3://锁定高度哈希
                break;
            case 4://定额条件比例支付
                //from
                from = states.get(tx.getFromPKHash());
                from.subBalance(tx.getFee());
                from.setNonce(tx.nonce);
                from.setBlockHeight(height);
                contract.setType(5);
                states.put(tx.getFromPKHash(), from);
                states.put(contract.getPubkeyHash(), contract);
                break;
        }
    }

    private void updateCallContract(Transaction tx, Map<byte[], AccountState> store, long height) {
        tx.setMethodType(tx.payload[0]);
        byte[] rlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
        switch (tx.getMethodType()) {
            case 0://更改所有者
                updateAssetChangeowner(tx, height, rlpbyte, store);
                break;
            case 1://转发资产
                updateAssetTransfer(tx, height, rlpbyte, store);
                break;
            case 2://增发
                updateAssetIncreased(tx, height, rlpbyte, store);
                break;
            case 3://多签转账
            case 4://锁定时间哈希资产转发
            case 5://锁定时间哈希获取资产
            case 6://锁定高度哈希资产转发
            case 7://锁定高度哈希获取资产
                break;
            case 8://定额条件比例支付
                updateRateheightDeposit(tx, height, rlpbyte, store);
                break;
            case 9://定额条件比例获取
                updateRateheightWithdraw(tx, height, rlpbyte, store);
                break;
        }
    }

    private void updateRateheightWithdraw(Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        AccountState contractaccountstate = store.get(tx.to);
        Rateheightlock rateheightlock = Rateheightlock.getRateheightlock(contractaccountstate.getContract());
        RateheightlockWithdraw rateheightlockWithdraw = RateheightlockWithdraw.getRateheightlockWithdraw(rlpbyte);
        byte[] deposithash = rateheightlockWithdraw.getDeposithash();
        Transaction deposittran = wisdomBlockChain.getTransaction(deposithash);
        RateheightlockDeposit rateheightlockDeposit = RateheightlockDeposit.getRateheightlockDeposit(ByteUtil.bytearrayridfirst(deposittran.payload));
        BigDecimal bigDecimal = new BigDecimal(rateheightlockDeposit.getValue());
        BigDecimal onceamount = bigDecimal.multiply(new BigDecimal(rateheightlock.getWithdrawrate()));
        long amount = onceamount.longValue();
        //from
        AccountState from = store.get(tx.getFromPKHash());
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        Map<byte[], Long> quotaMap = from.getQuotaMap();
        long quotabalance = quotaMap.get(rateheightlock.getAssetHash());
        quotabalance -= amount;
        quotaMap.put(rateheightlock.getAssetHash(), quotabalance);
        from.setQuotaMap(quotaMap);
        store.put(tx.getFromPKHash(), from);
        //to
        AccountState to = store.getOrDefault(rateheightlockWithdraw.getTo(), new AccountState(rateheightlockWithdraw.getTo()));
        if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
            to.addBalance(amount);
        } else {
            Map<byte[], Long> tokensMap = to.getTokensMap();
            long tokenbalance = 0;
            if (tokensMap.containsKey(rateheightlock.getAssetHash())) {
                tokenbalance = tokensMap.get(rateheightlock.getAssetHash());
            }
            tokenbalance += amount;
            tokensMap.put(rateheightlock.getAssetHash(), tokenbalance);
            to.setTokensMap(tokensMap);
        }
        //contract
        AccountState contract = store.get(tx.to);
        rateheightlock = Rateheightlock.getRateheightlock(contract.getContract());
        Map<HexBytes, Extract> statMap = rateheightlock.getStateMap();
        Extract extract = statMap.get(HexBytes.fromBytes(deposithash));
        int surplus = extract.getSurplus();
        surplus--;
        if (surplus == 0) {//已全部领取完
            statMap.remove(HexBytes.fromBytes(deposithash));
            rateheightlock.setStateMap(statMap);
        } else {
            long extractheight = extract.getExtractheight();
            extractheight += rateheightlock.getWithdrawperiodheight();
            extract.setSurplus(surplus);
            extract.setExtractheight(extractheight);
            statMap.put(HexBytes.fromBytes(deposithash), extract);
            rateheightlock.setStateMap(statMap);
        }
        contract.setContract(rateheightlock.RLPserialization());
        store.put(rateheightlockWithdraw.getTo(), to);
        store.put(tx.to, contract);
    }

    private void updateRateheightDeposit(Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        AccountState contractaccountstate = store.get(tx.to);
        Rateheightlock rateheightlock = Rateheightlock.getRateheightlock(contractaccountstate.getContract());
        RateheightlockDeposit rateheightlockDeposit = RateheightlockDeposit.getRateheightlockDeposit(rlpbyte);
        //from
        AccountState from = store.get(tx.getFromPKHash());
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        Map<byte[], Long> quotaMap = from.getQuotaMap();
        if (quotaMap == null) {
            quotaMap = new ByteArrayMap<>();
        }
        if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
            from.subBalance(rateheightlockDeposit.getValue());
        } else {
            Map<byte[], Long> tokensMap = from.getTokensMap();
            long tokenbalance = tokensMap.get(rateheightlock.getAssetHash());
            tokenbalance -= rateheightlockDeposit.getValue();
            tokensMap.put(rateheightlock.getAssetHash(), tokenbalance);
            from.setTokensMap(tokensMap);
        }
        long quotabalance = 0;
        if (quotaMap.containsKey(rateheightlock.getAssetHash())) {
            quotabalance = quotaMap.get(rateheightlock.getAssetHash());
        }
        quotabalance += rateheightlockDeposit.getValue();
        quotaMap.put(rateheightlock.getAssetHash(), quotabalance);
        from.setQuotaMap(quotaMap);
        //contract
        AccountState contract = store.get(tx.to);
        rateheightlock = Rateheightlock.getRateheightlock(contract.getContract());
        BigDecimal bigDecimal = new BigDecimal(rateheightlockDeposit.getValue());
        BigDecimal onceamount = bigDecimal.multiply(new BigDecimal(rateheightlock.getWithdrawrate()));
        int count = bigDecimal.divide(onceamount).intValue();
        Map<HexBytes, Extract> stateMap = rateheightlock.getStateMap();
        stateMap.put(HexBytes.fromBytes(tx.getHash()), new Extract(height, count));
        rateheightlock.setStateMap(stateMap);
        contract.setContract(rateheightlock.RLPserialization());
        store.put(tx.getFromPKHash(), from);
        store.put(tx.to, contract);
    }

    private AccountState updategetHashheightTransfer(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        //Pass the tx.to
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        if (!Arrays.equals(fromhash, account.getPubkeyHash())) {
            throw new RuntimeException("HashheightTransfer transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);

        AccountState contractaccountstate = store.get(tx.to);
        Hashheightblock hashheightblock = Hashheightblock.getHashheightblock(contractaccountstate.getContract());
        HashheightblockGet hashheightblockGet = HashheightblockGet.getHashheightblockGet(rlpbyte);
        Transaction transTransfer = wisdomBlockChain.getTransaction(hashheightblockGet.getTransferhash());
        HashheightblockTransfer hashheightblockTransfer = HashheightblockTransfer.getHashheightblockTransfer(ByteUtil.bytearrayridfirst(transTransfer.payload));
        if (Arrays.equals(hashheightblock.getAssetHash(), twentyBytes)) {//WDC
            balance += hashheightblockTransfer.getValue();
            account.setBalance(balance);
        } else {
            Map<byte[], Long> tokensMap = accountState.getTokensMap();
            long tokenbalance = 0;
            if (tokensMap.containsKey(hashheightblock.getAssetHash())) {
                tokenbalance = tokensMap.get(hashheightblock.getAssetHash());
            }
            tokenbalance += hashheightblockTransfer.getValue();
            tokensMap.put(hashheightblock.getAssetHash(), tokenbalance);
            accountState.setTokensMap(tokensMap);
        }
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateHashheightTransfer(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        //Pass the tx.to
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        if (!Arrays.equals(fromhash, account.getPubkeyHash())) {
            throw new RuntimeException("HashheightTransfer transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);

        AccountState contractaccountstate = store.get(tx.to);
        Hashheightblock hashheightblock = Hashheightblock.getHashheightblock(contractaccountstate.getContract());
        HashheightblockTransfer hashheightblockTransfer = HashheightblockTransfer.getHashheightblockTransfer(rlpbyte);
        if (Arrays.equals(hashheightblock.getAssetHash(), twentyBytes)) {//WDC
            balance -= hashheightblockTransfer.getValue();
            account.setBalance(balance);
        } else {
            Map<byte[], Long> tokensMap = accountState.getTokensMap();
            long tokenbalance = tokensMap.get(hashheightblock.getAssetHash());
            tokenbalance -= hashheightblockTransfer.getValue();
            tokensMap.put(hashheightblock.getAssetHash(), tokenbalance);
            accountState.setTokensMap(tokensMap);
        }
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updategetHashtimeTransfer(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        //Pass the tx.to
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        if (!Arrays.equals(fromhash, account.getPubkeyHash())) {
            throw new RuntimeException("HashtimeTransfer transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);

        AccountState contractaccountstate = store.get(tx.to);
        Hashtimeblock hashtimeblock = Hashtimeblock.getHashtimeblock(contractaccountstate.getContract());
        HashtimeblockGet hashtimeblockGet = HashtimeblockGet.getHashtimeblockGet(rlpbyte);
        Transaction transTransfer = wisdomBlockChain.getTransaction(hashtimeblockGet.getTransferhash());
        HashtimeblockTransfer hashtimeblockTransfer = HashtimeblockTransfer.getHashtimeblockTransfer(ByteUtil.bytearrayridfirst(transTransfer.payload));
        if (Arrays.equals(hashtimeblock.getAssetHash(), twentyBytes)) {//WDC
            balance += hashtimeblockTransfer.getValue();
            account.setBalance(balance);
        } else {
            Map<byte[], Long> tokensMap = accountState.getTokensMap();
            long tokenbalance = 0;
            if (tokensMap.containsKey(hashtimeblock.getAssetHash())) {
                tokenbalance = tokensMap.get(hashtimeblock.getAssetHash());
            }
            tokenbalance += hashtimeblockTransfer.getValue();
            tokensMap.put(hashtimeblock.getAssetHash(), tokenbalance);
            accountState.setTokensMap(tokensMap);
        }
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateHashtimeTransfer(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        //Pass the tx.to
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        if (!Arrays.equals(fromhash, account.getPubkeyHash())) {
            throw new RuntimeException("HashtimeTransfer transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);

        AccountState contractaccountstate = store.get(tx.to);
        Hashtimeblock hashtimeblock = Hashtimeblock.getHashtimeblock(contractaccountstate.getContract());
        HashtimeblockTransfer hashtimeblockTransfer = HashtimeblockTransfer.getHashtimeblockTransfer(rlpbyte);
        if (Arrays.equals(hashtimeblock.getAssetHash(), twentyBytes)) {//WDC
            balance -= hashtimeblockTransfer.getValue();
            account.setBalance(balance);
        } else {
            Map<byte[], Long> tokensMap = accountState.getTokensMap();
            long tokenbalance = tokensMap.get(hashtimeblock.getAssetHash());
            tokenbalance -= hashtimeblockTransfer.getValue();
            tokensMap.put(hashtimeblock.getAssetHash(), tokenbalance);
            accountState.setTokensMap(tokensMap);
        }
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateMultTransferWDC(byte[] fromhash, MultTransfer multTransfer, AccountState accountState, Account account, Transaction tx, long height) {
        boolean state = true;
        if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 1) {//单->多
            if (Arrays.equals(fromhash, account.getPubkeyHash())) {
                long balance = account.getBalance();
                balance -= tx.getFee();
                balance -= multTransfer.getValue();
                account.setBalance(balance);
                account.setNonce(tx.nonce);
                account.setBlockHeight(height);
                state = false;
            }
            if (Arrays.equals(multTransfer.getTo(), account.getPubkeyHash())) {
                long balance = account.getBalance();
                balance += multTransfer.getValue();
                account.setBalance(balance);
                state = false;
            }

        } else {//多->多 || 多->单
            if (Arrays.equals(fromhash, account.getPubkeyHash())) {
                long balance = account.getBalance();
                balance -= tx.getFee();
                account.setBalance(balance);
                account.setNonce(tx.nonce);
                account.setBlockHeight(height);
                state = false;
            }
            if (Arrays.equals(tx.to, account.getPubkeyHash())) {
                long balance = account.getBalance();
                balance -= multTransfer.getValue();
                account.setBalance(balance);
                state = false;
            }
            if (Arrays.equals(multTransfer.getTo(), account.getPubkeyHash())) {
                long balance = account.getBalance();
                balance += multTransfer.getValue();
                account.setBalance(balance);
                state = false;
            }
        }
        accountState.setAccount(account);
        if (state) {
            throw new RuntimeException("MultTransferWDC transaction account do not match");
        }
        return accountState;
    }

    private AccountState updateMultTransferOther(byte[] fromhash, byte[] assetHash, MultTransfer multTransfer, AccountState accountState, Account account, Transaction tx, long height) {
        boolean state = true;
        if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 1) {//单->多
            if (Arrays.equals(fromhash, account.getPubkeyHash())) {
                long balance = account.getBalance();
                balance -= tx.getFee();
                account.setBalance(balance);
                account.setNonce(tx.nonce);
                account.setBlockHeight(height);
                accountState.setAccount(account);

                Map<byte[], Long> tokenMap = accountState.getTokensMap();
                long tokenbalance = tokenMap.get(assetHash);
                tokenbalance -= multTransfer.getValue();
                tokenMap.put(assetHash, tokenbalance);
                accountState.setTokensMap(tokenMap);
                state = false;
            } else if (Arrays.equals(multTransfer.getTo(), account.getPubkeyHash())) {
                Map<byte[], Long> tokenMap = accountState.getTokensMap();
                long tokenbalance = 0;
                if (tokenMap.containsKey(assetHash)) {
                    tokenbalance = tokenMap.get(assetHash);
                }
                tokenbalance += multTransfer.getValue();
                tokenMap.put(assetHash, tokenbalance);
                accountState.setTokensMap(tokenMap);
                state = false;
            }
        } else {//多->多 || 多->单
            if (Arrays.equals(fromhash, account.getPubkeyHash())) {
                long balance = account.getBalance();
                balance -= tx.getFee();
                account.setBalance(balance);
                account.setNonce(tx.nonce);
                account.setBlockHeight(height);
                accountState.setAccount(account);
                state = false;
            }
            if (Arrays.equals(tx.to, account.getPubkeyHash())) {
                Map<byte[], Long> tokenMap = accountState.getTokensMap();
                long tokenbalance = tokenMap.get(assetHash);
                tokenbalance -= multTransfer.getValue();
                tokenMap.put(assetHash, tokenbalance);
                accountState.setTokensMap(tokenMap);
                state = false;
            }
            if (Arrays.equals(multTransfer.getTo(), account.getPubkeyHash())) {
                Map<byte[], Long> tokenMap = accountState.getTokensMap();
                long balance = 0;
                if (tokenMap.containsKey(assetHash)) {
                    balance = tokenMap.get(assetHash);
                }
                balance += multTransfer.getValue();
                tokenMap.put(assetHash, balance);
                accountState.setTokensMap(tokenMap);
                state = false;
            }
        }
        if (state) {
            throw new RuntimeException("MultTransferOther transaction account do not match");
        }
        return accountState;
    }

    private AccountState updateMultTransfer(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        AccountState contractaccountstate = store.get(tx.to);
        Multiple multiple = Multiple.getMultiple(contractaccountstate.getContract());
        byte[] assetHash = multiple.getAssetHash();
        MultTransfer multTransfer = MultTransfer.getMultTransfer(rlpbyte);
        if (Arrays.equals(assetHash, twentyBytes)) {
            return updateMultTransferWDC(fromhash, multTransfer, accountState, account, tx, height);
        } else {
            return updateMultTransferOther(fromhash, assetHash, multTransfer, accountState, account, tx, height);
        }
    }

    private void updateAssetIncreased(Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        AssetIncreased assetIncreased = AssetIncreased.getAssetIncreased(rlpbyte);
        //from
        AccountState from = store.get(tx.getFromPKHash());
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        Map<byte[], Long> tokensmap = from.getTokensMap();
        long tokenbalance = 0;
        if (tokensmap.containsKey(tx.to)) {
            tokenbalance = tokensmap.get(tx.to);
        }
        tokenbalance += assetIncreased.getAmount();
        tokensmap.put(tx.to, tokenbalance);
        from.setTokensMap(tokensmap);
        //contract
        AccountState contract = store.get(tx.to);
        byte[] contractbyte = contract.getContract();
        Asset asset = Asset.getAsset(contractbyte);
        long totalbalance = asset.getTotalamount();
        totalbalance += assetIncreased.getAmount();
        asset.setTotalamount(totalbalance);
        contract.setContract(asset.RLPserialization());
        store.put(tx.getFromPKHash(), from);
        store.put(tx.to, contract);
    }

    private void updateAssetTransfer(Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        AssetTransfer assetTransfer = AssetTransfer.getAssetTransfer(rlpbyte);
        //from
        AccountState from = store.get(tx.getFromPKHash());
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        Map<byte[], Long> tokensmap = from.getTokensMap();
        long tokenbalance = tokensmap.get(tx.to);
        tokenbalance -= assetTransfer.getValue();
        tokensmap.put(tx.to, tokenbalance);
        from.setTokensMap(tokensmap);
        //to
        AccountState to = store.getOrDefault(assetTransfer.getTo(), new AccountState(assetTransfer.getTo()));
        Map<byte[], Long> tokensmapTo = to.getTokensMap();
        long balance = 0;
        if (tokensmapTo.containsKey(tx.to)) {
            balance = tokensmapTo.get(tx.to);
        }
        balance += assetTransfer.getValue();
        tokensmapTo.put(tx.to, balance);
        to.setTokensMap(tokensmapTo);
        store.put(tx.getFromPKHash(), from);
        store.put(assetTransfer.getTo(), to);
    }

    private void updateAssetChangeowner(Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        //from
        AccountState from = store.get(tx.getFromPKHash());
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        //contract
        AccountState contract = store.get(tx.to);
        AssetChangeowner assetChangeowner = AssetChangeowner.getAssetChangeowner(rlpbyte);
        byte[] contractbyte = contract.getContract();
        Asset asset = Asset.getAsset(contractbyte);
        asset.setOwner(assetChangeowner.getNewowner());
        contract.setContract(asset.RLPserialization());
        store.put(tx.getFromPKHash(), from);
        store.put(tx.to, contract);
    }

    @SneakyThrows
    private void updateIncubate(Transaction tx, Map<byte[], AccountState> store, long height) {
        HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(tx.payload);
        int days = payloadproto.getType();
        String sharpub = payloadproto.getSharePubkeyHash();
        //from
        AccountState from = store.get(tx.getFromPKHash());
        from.subBalance(tx.getFee());
        from.subBalance(tx.amount);
        from.addIncubatecost(tx.amount);
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        Incubator incubator = new Incubator(tx.to, tx.getHash(), height, tx.amount, tx.getInterest(height, rateTable, days), height, days);
        Map<byte[], Incubator> maps = from.getInterestMap();
        maps.put(tx.getHash(), incubator);
        from.setInterestMap(maps);
        //IncubatorAddress
        AccountState incubatorTotal = store.get(IncubatorAddress.resultpubhash());
        incubatorTotal.subBalance(tx.getInterest(height, rateTable, days));
        long nonce = incubatorTotal.getNonce();
        incubatorTotal.setNonce(++nonce);
        incubatorTotal.setBlockHeight(height);
        //share
        if (sharpub != null && !sharpub.equals("")) {
            incubatorTotal.subBalance(tx.getShare(height, rateTable, days));

            byte[] sharepublic = Hex.decodeHex(sharpub.toCharArray());
            AccountState share = store.getOrDefault(sharepublic, new AccountState(sharepublic));
            Incubator shareIb = new Incubator(sharepublic, tx.getHash(), height, tx.amount, days, tx.getShare(height, rateTable, days), height);
            Map<byte[], Incubator> sharemaps = share.getShareMap();
            sharemaps.put(tx.getHash(), shareIb);
            share.setShareMap(sharemaps);
            store.put(sharepublic, share);
        }
        store.put(tx.getFromPKHash(), from);
        store.put(IncubatorAddress.resultpubhash(), incubatorTotal);
    }

    public Map<byte[], AccountState> getGenesisStates() {
        List<Genesis.IncubateAmount> incubateAmountList = genesisJSON.alloc.incubateAmount;
        Genesis.IncubateAmount incubateAmount = incubateAmountList.get(0);
        String address = incubateAmount.address;
        long balance = incubateAmount.balance * EconomicModel.WDC;
        byte[] totalpubhash = KeystoreAction.addressToPubkeyHash(address);
        Account totalaccount = new Account();
        Map<byte[], AccountState> AccountStateMap = new ByteArrayMap<>();
        try {
            for (Transaction tx : genesis.body) {
                if (Hex.encodeHexString(tx.to).equals("fbdacd374729b74c594cf955dc207fbb1d203a10")) {
                    System.out.println();
                }
                if (tx.type == 0x09) {
                    HatchReturned hatchReturned = hatchStates(AccountStateMap, tx, balance);
                    hatchReturned.getAccountStateList().forEach(s -> {
                        AccountStateMap.put(s.getKey(), s);
                    });
                    balance = hatchReturned.getBalance();
                } else {
                    AccountState accountState = getMapAccountState(AccountStateMap, tx.to);
                    Account account = accountState.getAccount();
                    account.setNonce(1);
                    account.setBalance(tx.amount);
                    if (!Arrays.equals(account.getPubkeyHash(), totalpubhash)) {
                        accountState.setAccount(account);
                        AccountStateMap.put(accountState.getKey(), accountState);
                    } else {
                        totalaccount = account;

                    }
                }
            }
            // 1PxgikfZGWW1L3eFJWpBrowjX5omFiy9ba
            //孵化总地址
            AccountState totalState = createEmpty(totalpubhash);
            totalaccount.setBalance(balance);
            totalState.setAccount(totalaccount);
            AccountStateMap.put(totalState.getKey(), totalState);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayMap<>(AccountStateMap);
    }

    private AccountState getMapAccountState(Map<byte[], AccountState> AccountStateMap, byte[] key) {
        if (AccountStateMap.containsKey(key)) {
            return AccountStateMap.get(key);
        } else {
            return createEmpty(key);
        }
    }

    private HatchReturned hatchStates(Map<byte[], AccountState> AccountStateMap, Transaction tx, long balance) throws InvalidProtocolBufferException, DecoderException {
        List<AccountState> accountStateList = new ArrayList<>();
        AccountState accountState = getMapAccountState(AccountStateMap, tx.to);
        byte[] playload = tx.payload;
        HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(playload);
        byte[] txamount = payloadproto.getTxId().toByteArray();
        long interestamount = BigEndian.decodeUint64(ByteUtil.bytearraycopy(txamount, 0, 8));
        Incubator incubator = new Incubator(tx.to, tx.getHash(), tx.height, tx.amount, interestamount, tx.height, payloadproto.getType());
        Map<byte[], Incubator> incubatorMap = accountState.getInterestMap();
        incubatorMap.put(incubator.getTxid_issue(), incubator);
        accountState.setInterestMap(incubatorMap);
        Account account = accountState.getAccount();
        long incubatecost = account.getIncubatecost();
        long nonce = account.getNonce();
        incubatecost = incubatecost + tx.amount;
        if (nonce < tx.nonce) {
            nonce = tx.nonce;
        }
        account.setIncubatecost(incubatecost);
        account.setNonce(nonce);
        accountState.setAccount(account);

        accountStateList.add(accountState);

        //share
        long shareamount = BigEndian.decodeUint64(ByteUtil.bytearraycopy(txamount, 8, 8));
        String sharpub = payloadproto.getSharePubkeyHash();
        byte[] share_pubkeyhash = null;
        if (sharpub != null && sharpub != "") {
            share_pubkeyhash = Hex.decodeHex(sharpub.toCharArray());
            Incubator shareIncubator = new Incubator(share_pubkeyhash, tx.getHash(), tx.height, tx.amount, payloadproto.getType(), shareamount, tx.height);
            AccountState Shareaccountstate = getMapAccountState(AccountStateMap, share_pubkeyhash);
            Map<byte[], Incubator> shareMap = Shareaccountstate.getShareMap();
            shareMap.put(shareIncubator.getTxid_issue(), shareIncubator);
            Shareaccountstate.setShareMap(shareMap);
            accountStateList.add(Shareaccountstate);
        }
        return new HatchReturned(accountStateList, balance - interestamount - shareamount);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private class HatchReturned {
        private List<AccountState> accountStateList;
        private long balance;
    }

    public Incubator UpdateExtIncuator(Transaction tran, long nowheight, Incubator incubator) {
        Transaction transaction = wisdomBlockChain.getTransaction(tran.payload);
        int days = transaction.getdays();
        String rate = rateTable.selectrate(transaction.height, days);//利率
        if (tran.type == 0x0a) {//interset
            BigDecimal amounbig = BigDecimal.valueOf(transaction.amount);
            BigDecimal ratebig = new BigDecimal(rate);
            long dayinterset = ratebig.multiply(amounbig).longValue();
            long lastheight = incubator.getLast_blockheight_interest();
            if (dayinterset > tran.amount) {
                lastheight += configuration.getDay_count(nowheight);
            } else {
                int extractday = (int) (tran.amount / dayinterset);
                long extractheight = extractday * configuration.getDay_count(nowheight);
                lastheight += extractheight;
            }
            long lastinterset = incubator.getInterest_amount();
            lastinterset -= tran.amount;
            incubator.setHeight(nowheight);
            incubator.setInterest_amount(lastinterset);
            incubator.setLast_blockheight_interest(lastheight);
        } else {//share
            BigDecimal amounbig = BigDecimal.valueOf(transaction.amount);
            BigDecimal ratebig = new BigDecimal(rate);
            BigDecimal onemul = amounbig.multiply(ratebig);
            BigDecimal bl = BigDecimal.valueOf(0.1);
            long dayinterset = onemul.multiply(bl).longValue();
            long lastheight = incubator.getLast_blockheight_share();
            if (dayinterset > tran.amount) {
                lastheight += configuration.getDay_count(nowheight);
            } else {
                int extractday = (int) (tran.amount / dayinterset);
                long extractheight = extractday * configuration.getDay_count(nowheight);
                lastheight += extractheight;
            }
            long lastshare = incubator.getShare_amount();
            lastshare -= tran.amount;
            incubator.setHeight(nowheight);
            incubator.setShare_amount(lastshare);
            incubator.setLast_blockheight_share(lastheight);
        }
        return incubator;
    }

    private void updateExtractInterest(Transaction tx, Map<byte[], AccountState> store, long height) {
        AccountState accountState = store.get(tx.to);
        Map<byte[], Incubator> map = accountState.getInterestMap();
        Incubator incubator = map.get(tx.payload);
        if (incubator == null) {
            throw new RuntimeException("Update extract interest error,tx:" + tx.getHashHexString());
        }
        incubator = UpdateExtIncuator(tx, tx.height, incubator);
        map.put(tx.payload, incubator);
        accountState.setInterestMap(map);

        Account account = accountState.getAccount();
        long balance;
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            throw new RuntimeException("ExtractInterest transaction account do not match");
        }
        balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);
        accountState.setAccount(account);
        store.put(tx.to, accountState);
    }

    private void updateExtractShare(Transaction tx, Map<byte[], AccountState> store, long height) {
        AccountState accountState = store.get(tx.to);
        Map<byte[], Incubator> map = accountState.getShareMap();
        Incubator incubator = map.get(tx.payload);
        if (incubator == null) {
            throw new RuntimeException("Update extract share error,tx:" + tx.getHashHexString());
        }
        incubator = UpdateExtIncuator(tx, height, incubator);
        map.put(tx.payload, incubator);
        accountState.setShareMap(map);

        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            throw new RuntimeException("ExtractShare transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);
        accountState.setAccount(account);
        store.put(tx.to, accountState);
    }

    private void updateExtranctCost(Transaction tx, Map<byte[], AccountState> store, long height) {
        AccountState accountState = store.get(tx.to);
        Map<byte[], Incubator> map = accountState.getInterestMap();
        Incubator incubator = map.get(tx.payload);
        if (incubator == null) {
            throw new RuntimeException("Update extract cost error,tx:" + tx.getHashHexString());
        }
        incubator.setCost(0);
        incubator.setHeight(height);
        map.put(tx.payload, incubator);
        accountState.setInterestMap(map);

        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            throw new RuntimeException("ExtranctCost transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        long incub = account.getIncubatecost();
        incub -= tx.amount;
        account.setBalance(balance);
        account.setIncubatecost(incub);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);
        accountState.setAccount(account);
        store.put(tx.to, accountState);
    }

    private void updateCancelVote(Transaction tx, Map<byte[], AccountState> store, long height) {
        AccountState from = store.get(tx.getFromPKHash());
        from.addBalance(tx.amount);
        from.subBalance(tx.getFee());
        from.setNonce(tx.nonce);
        from.setBlockHeight(height);
        store.put(from.getPubkeyHash(), from);
        AccountState to = store.get(tx.to);
        to.subVote(tx.amount);
        to.setBlockHeight(height);
        store.put(to.getPubkeyHash(), to);
    }

    private void updateMortgage(Transaction tx, Map<byte[], AccountState> store, long height) {
        AccountState to = store.get(tx.to);
        to.subBalance(tx.getFee());
        to.subBalance(tx.amount);
        to.addMortgage(tx.amount);
        to.setNonce(tx.nonce);
        to.setBlockHeight(height);
        store.put(to.getPubkeyHash(), to);
    }

    private void updateCancelMortgage(Transaction tx, Map<byte[], AccountState> store, long height) {
        AccountState to = store.get(tx.to);
        to.subBalance(tx.getFee());
        to.addBalance(tx.amount);
        to.subMortgage(tx.amount);
        to.setNonce(tx.nonce);
        to.setBlockHeight(height);
        store.put(to.getPubkeyHash(), to);
    }

    // 构造一个数据全为空的账户
    public AccountState createEmpty(byte[] publicKeyHash) {
        return new AccountState(publicKeyHash);
    }
}
