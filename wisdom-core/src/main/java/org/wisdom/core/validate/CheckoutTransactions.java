package org.wisdom.core.validate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.HexBytes;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Configuration;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.command.TransactionCheck;
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
import org.wisdom.core.TransactionVerifyUpdate;
import org.wisdom.core.WhitelistTransaction;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.db.AccountState;
import org.wisdom.db.WisdomRepository;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.wisdom.contract.AnalysisContract.MethodRule.*;
import static org.wisdom.core.account.Transaction.Type.EXIT_MORTGAGE;
import static org.wisdom.core.account.Transaction.Type.EXIT_VOTE;

@Slf4j("checkout-tx")
public class CheckoutTransactions implements TransactionVerifyUpdate<Result> {

    private List<Transaction> transactionList;

    private List<Transaction> pendingList;

    private byte[] parenthash;

    private Map<byte[], AccountState> map;

    private AccountState fromaccountstate;

    private long height;

    private Set<String> AssetcodeSet;

    private Set<String> LockTransferSet;

    private WisdomRepository wisdomRepository;

    private TransactionCheck transactionCheck;

    private PeningTransPool peningTransPool;

    private WhitelistTransaction whitelistTransaction;

    private RateTable rateTable;

    private Configuration configuration;

    private WisdomBlockChain wisdomBlockChain;

    private static final byte[] twentyBytes = new byte[20];

    private static final byte[] thirtytwoBytes = new byte[32];

    public CheckoutTransactions() {
        this.transactionList = new ArrayList<>();
        this.pendingList = new ArrayList<>();
        this.map = new ByteArrayMap<>();
        this.fromaccountstate = new AccountState();
        this.AssetcodeSet = new HashSet<>();
        this.LockTransferSet = new HashSet<>();
    }

    public void init(Block block, Map<byte[], AccountState> map, PeningTransPool peningTransPool, WisdomRepository wisdomRepository,
                     TransactionCheck transactionCheck, WhitelistTransaction whitelistTransaction, RateTable rateTable, Configuration configuration, WisdomBlockChain wisdomBlockChain) {
        this.transactionList = block.body;
        this.height = block.nHeight;
        this.parenthash = block.hashPrevBlock;
        this.map = map;
        this.peningTransPool = peningTransPool;
        this.wisdomRepository = wisdomRepository;
        this.transactionCheck = transactionCheck;
        this.whitelistTransaction = whitelistTransaction;
        this.rateTable = rateTable;
        this.configuration = configuration;
        this.wisdomBlockChain = wisdomBlockChain;
    }

    public Result CheckoutResult() {
        for (Transaction tx : transactionList) {
            if (whitelistTransaction.IsUnchecked(tx.getHashHexString())) {
                continue;
            }
            if (tx.type == Transaction.Type.COINBASE.ordinal()) {
                continue;
            }
            byte[] pubkeyhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from));
            //校验撤回事务是否存在
            Result resultexit = CheckExitTransaction(tx, pubkeyhash);
            if (!resultexit.isSuccess()) {
                return resultexit;
            }
            //事务校验格式和数据
            Result resulttransa = CheckTransaction(tx, pubkeyhash);
            if (!resulttransa.isSuccess()) {
                return resulttransa;
            }
            //更新状态
            Result result = Result.SUCCESS;
            switch (tx.type) {
                case 1://转账
                case 2://投票
                case 13://撤回投票
                    result = CheckFirstKind(fromaccountstate, fromaccountstate.getAccount(), tx, pubkeyhash);
                    break;
                case 3://存证事务,只需要扣除手续费
                case 9://孵化事务
                case 10://提取利息
                case 11://提取分享
                case 12://本金
                case 14://抵押
                case 15://撤回抵押
                    result = CheckOtherKind(fromaccountstate, fromaccountstate.getAccount(), tx, pubkeyhash);
                    break;
                case 7://部署合约
                    result = CheckDeployContract(fromaccountstate, fromaccountstate.getAccount(), tx, pubkeyhash);
                    break;
                case 8://调用合约
                    result = CheckCallContract(fromaccountstate, fromaccountstate.getAccount(), tx, pubkeyhash);
                    break;
            }
            if (!result.isSuccess()) {
                return result;
            }
            pendingList.add(tx);
        }
        peningTransPool.updatePool(transactionList, 1, height);
        return Result.SUCCESS;
    }

    @Override
    public Result CheckCallContract(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        long balance = fromaccount.getBalance();
        balance -= tx.getFee();
        if (balance < 0) {
            peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tx.nonce);
        accountState.setAccount(fromaccount);
        AccountState contractaccountstate = getMapAccountState(tx);
        byte[] contract = contractaccountstate.getContract();
        if (tx.getContractType() == 0) {//代币
            return ChechAssetMethod(contract, tx, contractaccountstate, accountState, publicKeyHash);
        } else if (tx.getContractType() == 1) {//多签
            return ChechMultMethod(contract, tx, contractaccountstate, accountState, publicKeyHash);
        } else if (tx.getContractType() == 2) {//锁定时间哈希
            return CheckHashtimeMethod(contract, tx, accountState, publicKeyHash);
        } else if (tx.getContractType() == 3) {//锁定高度哈希
            return CheckHashheightMethod(contract, tx, accountState, publicKeyHash);
        } else if (tx.getContractType() == 4) {//定额条件比例支付
            return CheckRateheightMethod(contract, tx, contractaccountstate, accountState, publicKeyHash);
        }
        return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Call contract exception error");
    }

    @Override
    public Result CheckHashheightMethod(byte[] contract, Transaction tx, AccountState accountState, byte[] publicKeyHash) {
        Hashheightblock hashheightblock = Hashheightblock.getHashheightblock(contract);
        if (tx.getMethodType() == HASHHEIGHTRANSFER.ordinal()) {//转发资产
            HashheightblockTransfer hashheightblockTransfer = HashheightblockTransfer.getHashheightblockTransfer(ByteUtil.bytearrayridfirst(tx.payload));
            if (Arrays.equals(hashheightblock.getAssetHash(), twentyBytes)) {//WDC
                Account account = accountState.getAccount();
                long balance = account.getBalance();
                balance -= hashheightblockTransfer.getValue();
                if (balance < 0) {
                    peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
                }
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long balance = tokensMap.get(hashheightblock.getAssetHash());
                balance -= hashheightblockTransfer.getValue();
                if (balance < 0) {
                    peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
                }
                tokensMap.put(hashheightblock.getAssetHash(), balance);
                accountState.setTokensMap(tokensMap);
            }
        } else if (tx.getMethodType() == GETHASHHEIGHT.ordinal()) {//获取资产
            HashheightblockGet hashheightblockGet = HashheightblockGet.getHashheightblockGet(ByteUtil.bytearrayridfirst(tx.payload));
            Transaction transaction = wisdomBlockChain.getTransaction(hashheightblockGet.getTransferhash());
            HashheightblockTransfer hashheightblockTransfer = HashheightblockTransfer.getHashheightblockTransfer(ByteUtil.bytearrayridfirst(transaction.payload));
            //判断同一区块是否有重复获取
            if (LockTransferSet.contains(transaction.getHashHexString())) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": lockheightHash repeats");
            }
            //判断forkdb+db中是否有重复获取
            if (wisdomRepository.containsgetLockgetTransferAt(parenthash, hashheightblockGet.getTransferhash())) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("the lockheightHash get transaction " + transaction.getHashHexString() + " had been exited");
            }
            if (Arrays.equals(hashheightblock.getAssetHash(), twentyBytes)) {//WDC
                Account account = accountState.getAccount();
                long balance = account.getBalance();
                balance += hashheightblockTransfer.getValue();
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long balance = 0;
                if (tokensMap.containsKey(hashheightblock.getAssetHash())) {
                    balance = tokensMap.get(hashheightblock.getAssetHash());
                }
                balance += hashheightblockTransfer.getValue();
                tokensMap.put(hashheightblock.getAssetHash(), balance);
                accountState.setTokensMap(tokensMap);
            }
        }
        map.put(publicKeyHash, accountState);
        return Result.SUCCESS;
    }

    @Override
    public Result CheckRateheightMethod(byte[] contract, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        Rateheightlock rateheightlock = Rateheightlock.getRateheightlock(contract);
        if (tx.getMethodType() == DEPOSITRATE.ordinal()) {//转入资产
            RateheightlockDeposit rateheightlockDeposit = RateheightlockDeposit.getRateheightlockDeposit(ByteUtil.bytearrayridfirst(tx.payload));
            if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
                Account account = accountState.getAccount();
                long balance = account.getBalance();
                balance -= rateheightlockDeposit.getValue();
                if (balance < 0) {
                    peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
                }
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long balance = tokensMap.get(rateheightlock.getAssetHash());
                balance -= rateheightlockDeposit.getValue();
                if (balance < 0) {
                    peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
                }
                tokensMap.put(rateheightlock.getAssetHash(), balance);
                accountState.setTokensMap(tokensMap);
            }
        } else if (tx.getMethodType() == WITHDRAWRATE.ordinal()) {//获取比例资产
            Map<HexBytes, Extract> stateMap = rateheightlock.getStateMap();
            //合约
            RateheightlockWithdraw rateheightlockWithdraw = RateheightlockWithdraw.getRateheightlockWithdraw(ByteUtil.bytearrayridfirst(tx.payload));
            byte[] deposithash = rateheightlockWithdraw.getDeposithash();
            Extract extract = stateMap.get(HexBytes.fromBytes(deposithash));
            long extractheight = extract.getExtractheight();
            extractheight += rateheightlock.getWithdrawperiodheight();
            int surplus = extract.getSurplus();
            surplus--;
            //判断高度和次数
            if (height <= extractheight || surplus < 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": There is no drawable amount");
            }
            extract.setExtractheight(extractheight);
            extract.setSurplus(surplus);
            stateMap.put(HexBytes.fromBytes(deposithash), extract);
            rateheightlock.setStateMap(stateMap);
            contractaccountstate.setContract(rateheightlock.RLPserialization());

            //from
            Transaction transaction = wisdomBlockChain.getTransaction(deposithash);
            RateheightlockDeposit rateheightlockDeposit = RateheightlockDeposit.getRateheightlockDeposit(ByteUtil.bytearrayridfirst(transaction.payload));
            int onceamount = new BigDecimal(rateheightlockDeposit.getValue()).multiply(new BigDecimal(rateheightlock.getWithdrawrate())).intValue();
            Account account = accountState.getAccount();
            Map<byte[], Long> quotaMap = account.getQuotaMap();
            long lockbalance = quotaMap.get(rateheightlock.getAssetHash());
            lockbalance -= onceamount;
            if (lockbalance < 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
            }
            quotaMap.put(rateheightlock.getAssetHash(), lockbalance);
            account.setQuotaMap(quotaMap);

            map.put(contractaccountstate.getAccount().getPubkeyHash(), contractaccountstate);

            //to
            if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
                if (Arrays.equals(publicKeyHash, rateheightlockWithdraw.getTo())) {//from和to一致
                    long balance = account.getBalance();
                    balance += onceamount;
                    account.setBalance(balance);
                } else {
                    AccountState toaccountstate = getKeyAccountState(rateheightlockWithdraw.getTo());
                    Account toaccount = toaccountstate.getAccount();
                    long balance = toaccount.getBalance();
                    balance += onceamount;
                    toaccount.setBalance(balance);
                    toaccountstate.setAccount(toaccount);
                    map.put(rateheightlockWithdraw.getTo(), toaccountstate);
                }
            } else {
                if (Arrays.equals(publicKeyHash, rateheightlockWithdraw.getTo())) {//from和to一致
                    Map<byte[], Long> tokensMap = accountState.getTokensMap();
                    long tokensbalance = 0;
                    if (tokensMap.containsKey(rateheightlock.getAssetHash())) {
                        tokensbalance = tokensMap.get(rateheightlock.getAssetHash());
                    }
                    tokensbalance += onceamount;
                    tokensMap.put(rateheightlock.getAssetHash(), tokensbalance);
                    accountState.setTokensMap(tokensMap);
                } else {
                    AccountState toaccountstate = getKeyAccountState(rateheightlockWithdraw.getTo());
                    Map<byte[], Long> tokensMap = toaccountstate.getTokensMap();
                    long tokensbalance = 0;
                    if (tokensMap.containsKey(rateheightlock.getAssetHash())) {
                        tokensbalance = tokensMap.get(rateheightlock.getAssetHash());
                    }
                    tokensbalance += onceamount;
                    tokensMap.put(rateheightlock.getAssetHash(), tokensbalance);
                    toaccountstate.setTokensMap(tokensMap);
                    map.put(rateheightlockWithdraw.getTo(), toaccountstate);
                }
            }
        }
        map.put(publicKeyHash, accountState);
        return Result.SUCCESS;
    }

    @Override
    public Result CheckHashtimeMethod(byte[] contract, Transaction tx, AccountState accountState, byte[] publicKeyHash) {
        Hashtimeblock hashtimeblock = Hashtimeblock.getHashtimeblock(contract);
        if (tx.getMethodType() == HASHTIMERANSFER.ordinal()) {//转发资产
            HashtimeblockTransfer hashtimeblockTransfer = HashtimeblockTransfer.getHashtimeblockTransfer(ByteUtil.bytearrayridfirst(tx.payload));
            if (Arrays.equals(hashtimeblock.getAssetHash(), twentyBytes)) {//WDC
                Account account = accountState.getAccount();
                long balance = account.getBalance();
                balance -= hashtimeblockTransfer.getValue();
                if (balance < 0) {
                    peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
                }
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long balance = tokensMap.get(hashtimeblock.getAssetHash());
                balance -= hashtimeblockTransfer.getValue();
                if (balance < 0) {
                    peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
                }
                tokensMap.put(hashtimeblock.getAssetHash(), balance);
                accountState.setTokensMap(tokensMap);
            }
        } else if (tx.getMethodType() == GETHASHTIME.ordinal()) {//获取资产
            HashtimeblockGet hashtimeblockGet = HashtimeblockGet.getHashtimeblockGet(ByteUtil.bytearrayridfirst(tx.payload));
            Transaction transaction = wisdomBlockChain.getTransaction(hashtimeblockGet.getTransferhash());
            HashtimeblockTransfer hashtimeblockTransfer = HashtimeblockTransfer.getHashtimeblockTransfer(ByteUtil.bytearrayridfirst(transaction.payload));
            //判断同一区块是否有重复获取
            if (LockTransferSet.contains(transaction.getHashHexString())) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": locktimeHash repeats");
            }
            //判断forkdb+db中是否有重复获取
            if (wisdomRepository.containsgetLockgetTransferAt(parenthash, hashtimeblockGet.getTransferhash())) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("the locktimeHash get transaction " + transaction.getHashHexString() + " had been exited");
            }
            if (Arrays.equals(hashtimeblock.getAssetHash(), twentyBytes)) {//WDC
                Account account = accountState.getAccount();
                long balance = account.getBalance();
                balance += hashtimeblockTransfer.getValue();
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long balance = 0;
                if (tokensMap.containsKey(hashtimeblock.getAssetHash())) {
                    balance = tokensMap.get(hashtimeblock.getAssetHash());
                }
                balance += hashtimeblockTransfer.getValue();
                tokensMap.put(hashtimeblock.getAssetHash(), balance);
                accountState.setTokensMap(tokensMap);
            }
        }
        map.put(publicKeyHash, accountState);
        return Result.SUCCESS;
    }

    @Override
    public Result ChechMultMethod(byte[] contract, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        Multiple multiple = Multiple.getMultiple(contract);
        MultTransfer multTransfer = MultTransfer.getMultTransfer(ByteUtil.bytearrayridfirst(tx.payload));
        byte[] assetHash = multiple.getAssetHash();
        if (Arrays.equals(assetHash, twentyBytes)) {//WDC
            return CheckMultTransferWDC(multTransfer, tx, contractaccountstate, accountState, publicKeyHash);
        } else {
            return CheckMultTransferOther(assetHash, multTransfer, tx, contractaccountstate, accountState, publicKeyHash);
        }
    }

    @Override
    public Result CheckMultTransferWDC(MultTransfer multTransfer, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 1) {//单-->多
            Account account = accountState.getAccount();
            long balance = account.getBalance();
            balance -= multTransfer.getValue();
            if (balance < 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Transfer from ordinary account to multi-signature account shows insufficient balance");
            }
            account.setBalance(balance);
            accountState.setAccount(account);
            map.put(publicKeyHash, accountState);

            //to
            Account toaccount = contractaccountstate.getAccount();
            long tobalance = toaccount.getBalance();
            tobalance += multTransfer.getValue();
            toaccount.setBalance(tobalance);
            contractaccountstate.setAccount(toaccount);
            map.put(tx.to, contractaccountstate);
        } else {//多-->多 || 多-->单
            Account account = contractaccountstate.getAccount();
            long balance = account.getBalance();
            balance -= multTransfer.getValue();
            if (balance < 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Multi-signature account to multi-signature account or regular account transfer shows insufficient balance");
            }
            account.setBalance(balance);
            contractaccountstate.setAccount(account);
            map.put(tx.to, contractaccountstate);

            //to
            AccountState toaccountstate = getKeyAccountState(multTransfer.getTo());
            Account toaccount = toaccountstate.getAccount();
            long tobalance = toaccount.getBalance();
            tobalance += multTransfer.getValue();
            toaccount.setBalance(tobalance);
            toaccountstate.setAccount(toaccount);
            map.put(multTransfer.getTo(), toaccountstate);
        }
        return Result.SUCCESS;
    }

    @Override
    public Result CheckMultTransferOther(byte[] assetHash, MultTransfer multTransfer, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 1) {//单-->多
            Map<byte[], Long> tokensMap = accountState.getTokensMap();
            long tokenbalance = tokensMap.get(assetHash);
            tokenbalance -= multTransfer.getValue();
            if (tokenbalance < 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Transfer from ordinary account to multi-signature account shows insufficient balance");
            }
            tokensMap.put(assetHash, tokenbalance);
            accountState.setTokensMap(tokensMap);
            map.put(publicKeyHash, accountState);

            //to=多
            long tobalance = 0;
            Map<byte[], Long> totokenMap = contractaccountstate.getTokensMap();
            if (totokenMap.containsKey(assetHash)) {
                tobalance = totokenMap.get(assetHash);
            }
            tobalance += multTransfer.getValue();
            totokenMap.put(assetHash, tobalance);
            contractaccountstate.setTokensMap(totokenMap);
            map.put(tx.to, contractaccountstate);
        } else {//多-->多 || 多-->单
            Map<byte[], Long> fromMap = contractaccountstate.getTokensMap();
            long frombalance = fromMap.get(assetHash);
            frombalance -= multTransfer.getValue();
            if (frombalance < 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Multi-signature account to multi-signature account or regular account transfer shows insufficient balance");
            }
            fromMap.put(assetHash, frombalance);
            contractaccountstate.setTokensMap(fromMap);
            map.put(tx.to, contractaccountstate);

            //to
            AccountState toaccountstate = getKeyAccountState(multTransfer.getTo());
            long tobalance = 0;
            Map<byte[], Long> totokenMap = toaccountstate.getTokensMap();
            if (totokenMap.containsKey(assetHash)) {
                tobalance = totokenMap.get(assetHash);
            }
            tobalance += multTransfer.getValue();
            totokenMap.put(assetHash, tobalance);
            toaccountstate.setTokensMap(totokenMap);
            map.put(multTransfer.getTo(), toaccountstate);
        }
        return Result.SUCCESS;
    }

    @Override
    public Result ChechAssetMethod(byte[] contract, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        Asset asset = Asset.getAsset(contract);
        if (tx.getMethodType() == CHANGEOWNER.ordinal()) {//更换所有者
            byte[] owner = asset.getOwner();
            if (Arrays.equals(owner, twentyBytes) || !Arrays.equals(owner, publicKeyHash)) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Asset definition does not have permission to replace the owner");
            }
            AssetChangeowner assetChangeowner = AssetChangeowner.getAssetChangeowner(ByteUtil.bytearrayridfirst(tx.payload));
            asset.setOwner(assetChangeowner.getNewowner());
            contractaccountstate.setContract(asset.RLPserialization());
            map.put(tx.to, contractaccountstate);
        } else if (tx.getMethodType() == ASSETTRANSFER.ordinal()) {//资产转账
            AssetTransfer assetTransfer = AssetTransfer.getAssetTransfer(ByteUtil.bytearrayridfirst(tx.payload));
            Map<byte[], Long> maps = accountState.getTokensMap();
            long tokenbalance = maps.get(tx.to);
            tokenbalance -= assetTransfer.getValue();
            if (tokenbalance < 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient balance in asset transfer");
            }
            maps.put(tx.to, tokenbalance);
            accountState.setTokensMap(maps);

            //from=to
            if (Arrays.equals(publicKeyHash, assetTransfer.getTo())) {
                map.put(publicKeyHash, accountState);
            }

            //to
            AccountState toaccountstate = getKeyAccountState(assetTransfer.getTo());
            Map<byte[], Long> tomaps = toaccountstate.getTokensMap();
            long tobalance = 0;
            if (tomaps.containsKey(tx.to)) {
                tobalance = tomaps.get(tx.to);
            }
            tobalance += assetTransfer.getValue();
            tomaps.put(tx.to, tobalance);
            toaccountstate.setTokensMap(tomaps);
            map.put(assetTransfer.getTo(), toaccountstate);
        } else {//increased
            if (asset.getAllowincrease() == 0 || !Arrays.equals(asset.getOwner(), publicKeyHash)
                    || Arrays.equals(asset.getOwner(), twentyBytes)) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": The asset definition does not have rights to issue shares");
            }
            AssetIncreased assetIncreased = AssetIncreased.getAssetIncreased(ByteUtil.bytearrayridfirst(tx.payload));
            long totalamount = asset.getTotalamount();
            totalamount += assetIncreased.getAmount();
            if (totalamount <= 0) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Total asset issuance exceeded the maximum");
            }
            asset.setTotalamount(totalamount);
            contractaccountstate.setContract(asset.RLPserialization());
            map.put(tx.to, contractaccountstate);

            Map<byte[], Long> tokensmap = accountState.getTokensMap();
            long tokensbalance = 0;
            if (tokensmap.containsKey(tx.to)) {
                tokensbalance = tokensmap.get(tx.to);
            }
            tokensbalance += assetIncreased.getAmount();
            tokensmap.put(tx.to, tokensbalance);
            accountState.setTokensMap(tokensmap);
        }
        map.put(publicKeyHash, accountState);
        return Result.SUCCESS;
    }

    @Override
    public Result CheckDeployContract(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        if (tx.getContractType() == 0) {//代币
            Asset asset = Asset.getAsset(ByteUtil.bytearrayridfirst(tx.payload));
            //判断forkdb+db中是否有重复的代币合约code存在
            if (wisdomRepository.containsAssetCodeAt(parenthash, asset.getCode().getBytes(StandardCharsets.UTF_8))) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Asset token code repeats");
            }
            if (AssetcodeSet.contains(asset.getCode())) {
                peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Asset token code repeats");
            }
            AssetcodeSet.add(asset.getCode());
        }
        long balance = fromaccount.getBalance();
        balance -= tx.getFee();
        if (balance < 0) {
            peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient account balance");
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tx.nonce);
        accountState.setAccount(fromaccount);
        map.put(publicKeyHash, accountState);
        return Result.SUCCESS;
    }

    @Override
    public Result CheckFirstKind(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        AccountState toaccountState = getMapAccountState(tx);
        Account toaccount = toaccountState.getAccount();
        List<Account> accountList;
        if (tx.type == 1) {
            accountList = updateTransfer(fromaccount, toaccount, tx);
        } else if (tx.type == 2) {
            accountList = updateVote(fromaccount, toaccount, tx);
        } else {
            accountList = updateCancelVote(fromaccount, toaccount, tx);
        }
        if (accountList == null) {
            peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
        }
        accountList.forEach(account -> {
            if (Arrays.equals(account.getPubkeyHash(), publicKeyHash)) {
                accountState.setAccount(account);
                map.put(publicKeyHash, accountState);
            } else {
                toaccountState.setAccount(account);
                map.put(toaccount.getPubkeyHash(), toaccountState);
            }
        });
        return Result.SUCCESS;
    }

    @Override
    public Result CheckOtherKind(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        Account account = UpdateOtherAccount(fromaccount, tx);
        if (account == null) {
            peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
        }
        if (CheckIncubatorTotal(tx)) {//孵化总地址余额校验
            peningTransPool.removeOne(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Hatching total address balance insufficient");
        }
        accountState.setAccount(account);
        //更改type 10、11、12事务Accountstate
        accountState = updateHatch(accountState, tx, height);
        map.put(publicKeyHash, accountState);
        return Result.SUCCESS;
    }

    public AccountState updateHatch(AccountState accountState, Transaction transaction, long nHeight) {
        Map<byte[], Incubator> map = null;
        if (transaction.type == 10) {
            map = accountState.getInterestMap();
            Incubator incubator = updateIncubtor(wisdomBlockChain, rateTable, configuration, map, transaction, nHeight);
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        } else if (transaction.type == 11) {
            map = accountState.getShareMap();
            Incubator incubator = updateIncubtor(wisdomBlockChain, rateTable, configuration, map, transaction, nHeight);
            map.put(transaction.payload, incubator);
            accountState.setShareMap(map);
        } else if (transaction.type == 12) {
            map = accountState.getInterestMap();
            Incubator incubator = updateIncubtor(wisdomBlockChain, rateTable, configuration, map, transaction, nHeight);
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        }
        return accountState;
    }

    @Override
    public boolean CheckIncubatorTotal(Transaction transaction) {
        if (transaction.type == 9) {//孵化总地址校验
            try {
                HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(transaction.payload);
                int days = payloadproto.getType();
                String sharpub = payloadproto.getSharePubkeyHash();

                AccountState totalaccountState = getIncubatorTotal();
                Account account = totalaccountState.getAccount();
                long balance = account.getBalance();

                balance -= transaction.getInterest(height, rateTable, days);
                if (sharpub != null && !sharpub.equals("")) {
                    balance -= transaction.getShare(height, rateTable, days);
                }
                if (balance < 0) {
                    return true;
                }
                account.setBalance(balance);
                totalaccountState.setAccount(account);
                map.put(IncubatorAddress.resultpubhash(), totalaccountState);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AccountState getIncubatorTotal() {
        byte[] totalhash = IncubatorAddress.resultpubhash();
        if (map.containsKey(totalhash)) {
            return map.get(totalhash);
        } else {
            return wisdomRepository.getAccountStateAt(parenthash, IncubatorAddress.resultpubhash()).get();
        }
    }

    @Override
    public AccountState getKeyAccountState(byte[] key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return wisdomRepository.getAccountStateAt(parenthash, key).orElse(new AccountState(key));
        }
    }

    private Result CheckTransaction(Transaction tx, byte[] publichash) {
        APIResult apiResult = transactionCheck.TransactionFormatCheck(tx.toRPCBytes());
        if (apiResult.getCode() == 5000) {
            peningTransPool.removeOne(Hex.encodeHexString(publichash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
        }
        if (map.containsKey(publichash)) {
            fromaccountstate = map.get(publichash);
        } else {
            peningTransPool.removeOne(Hex.encodeHexString(publichash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Cannot query the account for the from ");
        }
        Account account = fromaccountstate.getAccount();
        Map<byte[], Incubator> interestMap = null;
        if (tx.type == 0x0a || tx.type == 0x0c) {
            interestMap = fromaccountstate.getInterestMap();
        } else if (tx.type == 0x0b) {
            interestMap = fromaccountstate.getShareMap();
        }
        Incubator forkincubator = null;
        if (interestMap != null) {
            forkincubator = interestMap.get(tx.payload);
        }
        //数据校验
        apiResult = transactionCheck.TransactionVerify(tx, account, forkincubator);
        if (apiResult.getCode() == 5000) {
            peningTransPool.removeOne(Hex.encodeHexString(publichash), tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
        }
        return Result.SUCCESS;
    }

    private Result CheckExitTransaction(Transaction tx, byte[] publichash) {
        switch (Transaction.Type.values()[tx.type]) {
            case EXIT_VOTE: {
                // 投票没有撤回过
                boolean b;
                try {
                    b = wisdomRepository.containsPayloadAt(parenthash, EXIT_VOTE.ordinal(), tx.payload);
                } catch (Exception e) {
                    log.error("check exit transaction failed for parent hash " + HexBytes.fromBytes(publichash) + " tx hash = " + tx.getHashHexString() + " public hash = " + HexBytes.fromBytes(publichash));
                    throw e;
                }
                if (b) {
                    peningTransPool.removeOne(Hex.encodeHexString(publichash), tx.nonce);
                    return Result.Error("the vote transaction " + Hex.encodeHexString(tx.payload) + " had been exited");
                }
                break;
            }
            case EXIT_MORTGAGE: {
                // 抵押没有撤回过
                if (wisdomRepository.containsPayloadAt(parenthash, EXIT_MORTGAGE.ordinal(), tx.payload)) {
                    peningTransPool.removeOne(Hex.encodeHexString(publichash), tx.nonce);
                    return Result.Error("the mortgage transaction " + Hex.encodeHexString(tx.payload) + " had been exited");
                }
                break;
            }
            case EXTRACT_COST: {
                //本金没有被撤回过
                if (wisdomRepository.containsPayloadAt(parenthash, Transaction.Type.EXTRACT_COST.ordinal(), tx.payload)) {
                    peningTransPool.removeOne(Hex.encodeHexString(publichash), tx.nonce);
                    return Result.Error("the incubate transaction " + Hex.encodeHexString(tx.payload) + " had been exited");
                }
                break;
            }
        }
        return Result.SUCCESS;
    }
}
