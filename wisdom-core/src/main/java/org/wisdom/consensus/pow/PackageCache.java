package org.wisdom.consensus.pow;

import lombok.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdf.common.util.ByteArrayMap;
import org.wisdom.command.Configuration;
import org.wisdom.command.IncubatorAddress;
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
import org.wisdom.core.Block;
import org.wisdom.core.TransactionVerifyUpdate;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.db.AccountState;
import org.wisdom.db.WisdomRepository;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.pool.TransPool;
import org.wisdom.pool.WaitCount;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.wisdom.core.account.Transaction.Type.EXIT_MORTGAGE;
import static org.wisdom.core.account.Transaction.Type.EXIT_VOTE;
import static org.wisdom.contract.AnalysisContract.MethodRule.CHANGEOWNER;
import static org.wisdom.contract.AnalysisContract.MethodRule.ASSETTRANSFER;
import static org.wisdom.contract.AnalysisContract.MethodRule.MULTTRANSFER;
import static org.wisdom.contract.AnalysisContract.MethodRule.HASHTIMERANSFER;
import static org.wisdom.contract.AnalysisContract.MethodRule.GETHASHTIME;
import static org.wisdom.contract.AnalysisContract.MethodRule.HASHHEIGHTRANSFER;
import static org.wisdom.contract.AnalysisContract.MethodRule.GETHASHHEIGHT;

public class PackageCache implements TransactionVerifyUpdate<Object> {

    private static final Logger logger = LoggerFactory.getLogger(PackageCache.class);

    private Map<byte[], AccountState> accountStateMap;

    private IdentityHashMap<String, Long> removemap;

    private Map<String, TreeMap<Long, TransPool>> maps;

    private List<Transaction> transactionList;

    private Set<String> AssetcodeSet;

    private Set<String> LockTransferSet;

    private boolean exit;

    private boolean state;

    private int size;

    private byte[] parenthash;

    private Block block;

    private long height;

    private String publicKeyHash;

    private Map<byte[], AccountState> newMap;

    private WisdomRepository repository;

    private Configuration configuration;

    private WisdomBlockChain wisdomBlockChain;

    private WaitCount waitCount;

    private PeningTransPool peningTransPool;

    private RateTable rateTable;

    private static final byte[] twentyBytes = new byte[20];

    private static final byte[] thirtytwoBytes = new byte[32];

    public PackageCache() {
        this.removemap = new IdentityHashMap<>();
        this.transactionList = new ArrayList<>();
        this.AssetcodeSet = new HashSet<>();
        this.LockTransferSet = new HashSet<>();
        this.exit = false;
        this.state = false;
    }

    public void init(PeningTransPool peningTransPool, WisdomRepository repository, Configuration configuration, WisdomBlockChain wisdomBlockChain,
                     WaitCount waitCount, RateTable rateTable, Map<byte[], AccountState> accountStateMap,
                     Map<String, TreeMap<Long, TransPool>> maps, byte[] parenthash,
                     Block block, long height, int size) {
        this.peningTransPool = peningTransPool;
        this.repository = repository;
        this.configuration = configuration;
        this.wisdomBlockChain = wisdomBlockChain;
        this.waitCount = waitCount;
        this.rateTable = rateTable;
        this.accountStateMap = accountStateMap;
        this.maps = maps;
        this.parenthash = parenthash;
        this.block = block;
        this.height = height;
        this.size = size;
    }

    public List<Transaction> getRightTransactions() {
        for (Map.Entry<String, TreeMap<Long, TransPool>> entry : maps.entrySet()) {
            publicKeyHash = entry.getKey();
            TreeMap<Long, TransPool> treeMap = entry.getValue();
            for (Map.Entry<Long, TransPool> entry1 : treeMap.entrySet()) {
                state = false;
                TransPool transPool = entry1.getValue();
                Transaction transaction = transPool.getTransaction();
                try {
                    //区块大小
                    if (CheckSize(transaction)) {
                        break;
                    }
                    //防止写入重复事务
                    if (CheckRepetition(transaction)) {
                        continue;
                    }
                    //没有获取到 AccountState
                    if (CheckMapRedo(publicKeyHash)) {
                        break;
                    }
                    newMap = new ByteArrayMap<>();
                    AccountState accountState = accountStateMap.get(Hex.decodeHex(publicKeyHash.toCharArray()));
                    Account fromaccount = accountState.getAccount();
                    long nowNonce = fromaccount.getNonce();

                    // nonce是否合法
                    if (fromaccount.getNonce() >= transaction.nonce) {
                        removemap.put(new String(entry.getKey()), transaction.nonce);
                        continue;
                    }
                    switch (transaction.type) {
                        case 1://转账
                        case 2://投票
                        case 13://撤回投票
                            CheckFirstKind(accountState, fromaccount, transaction, Hex.decodeHex(publicKeyHash.toCharArray()));
                            break;
                        case 3://存证事务,只需要扣除手续费
                        case 9://孵化事务
                        case 10://提取利息
                        case 11://提取分享
                        case 12://本金
                        case 14://抵押
                        case 15://撤回抵押
                            CheckOtherKind(accountState, fromaccount, transaction, Hex.decodeHex(publicKeyHash.toCharArray()));
                            break;
                        case 7://部署合约
                            CheckDeployContract(accountState, fromaccount, transaction, Hex.decodeHex(publicKeyHash.toCharArray()));
                            break;
                        case 8://调用合约
                            CheckCallContract(accountState, fromaccount, transaction, Hex.decodeHex(publicKeyHash.toCharArray()));
                            break;
                    }
                    if (state) {
                        continue;
                    }
                    //nonce是否跳号
                    if (nowNonce + 1 != transaction.nonce) {
                        if (!updateWaitCount(publicKeyHash, transaction.nonce)) break;
                    }
                    //更新缓存
                    accountStateMap.putAll(newMap);
                    transaction.height = height;
                    size += transaction.size();
                    transactionList.add(transaction);
                } catch (Exception e) {
                    e.printStackTrace();
                    removemap.put(new String(entry.getKey()), transaction.nonce);
                }
            }
            if (exit) {//内循环退出
                break;
            }
        }
        //删除事务内存池事务
        peningTransPool.remove(removemap);
        return transactionList;
    }

    @Override
    public Object CheckCallContract(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        long balance = fromaccount.getBalance();
        balance -= tx.getFee();
        if (balance < 0) {
            AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tx.nonce);
        accountState.setAccount(fromaccount);
        AccountState contractaccountstate = getMapAccountState(tx);
        byte[] contract = contractaccountstate.getContract();
        if (tx.getContractType() == 0) {//代币
            return ChechAssetMethod(contract, tx, contractaccountstate, accountState, publicKeyHash);
        } else if (tx.getContractType() == 1 && tx.getMethodType() == MULTTRANSFER.ordinal()) {//多签
            return ChechMultMethod(contract, tx, contractaccountstate, accountState, publicKeyHash);
        } else if (tx.getContractType() == 2) {//锁定时间哈希
            return CheckHashtimeMethod(contract, tx, accountState, publicKeyHash);
        } else if (tx.getContractType() == 3) {//锁定高度哈希
            return CheckHashheightMethod(contract, tx, accountState, publicKeyHash);
        }
        return null;
    }

    @Override
    public Object CheckHashheightMethod(byte[] contract, Transaction tx, AccountState accountState, byte[] publicKeyHash) {
        Hashheightblock hashheightblock = Hashheightblock.getHashheightblock(contract);
        if (tx.getMethodType() == HASHHEIGHTRANSFER.ordinal()) {//转发资产
            HashheightblockTransfer hashheightblockTransfer = HashheightblockTransfer.getHashheightblockTransfer(ByteUtil.bytearrayridfirst(tx.payload));
            if (Arrays.equals(hashheightblock.getAssetHash(), twentyBytes)) {//WDC
                Account account = accountState.getAccount();
                long balance = account.getBalance();
                balance -= hashheightblockTransfer.getValue();
                if (balance < 0) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long balance = tokensMap.get(hashheightblock.getAssetHash());
                balance -= hashheightblockTransfer.getValue();
                if (balance < 0) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
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
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            //判断forkdb中是否有重复获取
            if (repository.containsgetLockgetTransferAt(parenthash, hashheightblockGet.getTransferhash())) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            //高度是否满足
            if (height < hashheightblockTransfer.getHeight()) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
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
            LockTransferSet.add(transaction.getHashHexString());
        }
        newMap.put(publicKeyHash, accountState);
        return null;
    }

    @Override
    public Object CheckHashtimeMethod(byte[] contract, Transaction tx, AccountState accountState, byte[] publicKeyHash) {
        Hashtimeblock hashtimeblock = Hashtimeblock.getHashtimeblock(contract);
        if (tx.getMethodType() == HASHTIMERANSFER.ordinal()) {//转发资产
            HashtimeblockTransfer hashtimeblockTransfer = HashtimeblockTransfer.getHashtimeblockTransfer(ByteUtil.bytearrayridfirst(tx.payload));
            if (Arrays.equals(hashtimeblock.getAssetHash(), twentyBytes)) {//WDC
                Account account = accountState.getAccount();
                long balance = account.getBalance();
                balance -= hashtimeblockTransfer.getValue();
                if (balance < 0) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long balance = tokensMap.get(hashtimeblock.getAssetHash());
                balance -= hashtimeblockTransfer.getValue();
                if (balance < 0) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
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
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            //判断forkdb中是否有重复获取
            if (repository.containsgetLockgetTransferAt(parenthash, hashtimeblockGet.getTransferhash())) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            //时间戳是否满足
            Long nowTimestamp = System.currentTimeMillis() / 1000;
            if (hashtimeblockTransfer.getTimestamp() > nowTimestamp) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
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
            LockTransferSet.add(transaction.getHashHexString());
        }
        newMap.put(publicKeyHash, accountState);
        return null;
    }

    @Override
    public Object CheckMultTransferWDC(MultTransfer multTransfer, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 1) {//单-->多
            Account account = accountState.getAccount();
            long balance = account.getBalance();
            balance -= multTransfer.getValue();
            if (balance < 0) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            account.setBalance(balance);
            accountState.setAccount(account);
            newMap.put(publicKeyHash, accountState);

            //to
            Account toaccount = contractaccountstate.getAccount();
            long tobalance = toaccount.getBalance();
            tobalance += multTransfer.getValue();
            toaccount.setBalance(tobalance);
            contractaccountstate.setAccount(toaccount);
            newMap.put(tx.to, contractaccountstate);
        } else {//多-->多 || 多-->单
            Account account = contractaccountstate.getAccount();
            long balance = account.getBalance();
            balance -= multTransfer.getValue();
            if (balance < 0) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            account.setBalance(balance);
            contractaccountstate.setAccount(account);
            newMap.put(tx.to, contractaccountstate);

            //to
            AccountState toaccountstate = getKeyAccountState(multTransfer.getTo());
            Account toaccount = toaccountstate.getAccount();
            long tobalance = toaccount.getBalance();
            tobalance += multTransfer.getValue();
            toaccount.setBalance(tobalance);
            toaccountstate.setAccount(toaccount);
            newMap.put(multTransfer.getTo(), toaccountstate);
        }
        return null;
    }

    @Override
    public Object CheckMultTransferOther(byte[] assetHash, MultTransfer multTransfer, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        if (multTransfer.getOrigin() == 0 && multTransfer.getDest() == 1) {//单-->多
            Map<byte[], Long> tokensMap = accountState.getTokensMap();
            long tokenbalance = tokensMap.get(assetHash);
            tokenbalance -= multTransfer.getValue();
            if (tokenbalance < 0) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            tokensMap.put(assetHash, tokenbalance);
            accountState.setTokensMap(tokensMap);
            newMap.put(publicKeyHash, accountState);

            //to=多
            long tobalance = 0;
            Map<byte[], Long> totokenMap = contractaccountstate.getTokensMap();
            if (totokenMap.containsKey(assetHash)) {
                tobalance = totokenMap.get(assetHash);
            }
            tobalance += multTransfer.getValue();
            totokenMap.put(assetHash, tobalance);
            contractaccountstate.setTokensMap(totokenMap);
            newMap.put(tx.to, contractaccountstate);
        } else {//多-->多 || 多-->单
            Map<byte[], Long> fromMap = contractaccountstate.getTokensMap();
            long frombalance = fromMap.get(assetHash);
            frombalance -= multTransfer.getValue();
            if (frombalance < 0) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            fromMap.put(assetHash, frombalance);
            contractaccountstate.setTokensMap(fromMap);
            newMap.put(tx.to, contractaccountstate);

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
            newMap.put(multTransfer.getTo(), toaccountstate);
        }
        return null;
    }

    @Override
    public Object ChechMultMethod(byte[] contract, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
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
    public Object ChechAssetMethod(byte[] contract, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash) {
        Asset asset = Asset.getAsset(contract);
        if (tx.getMethodType() == CHANGEOWNER.ordinal()) {//跟换所有者
            byte[] owner = asset.getOwner();
            if (Arrays.equals(owner, twentyBytes) || !Arrays.equals(owner, publicKeyHash)) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            AssetChangeowner assetChangeowner = AssetChangeowner.getAssetChangeowner(ByteUtil.bytearrayridfirst(tx.payload));
            if (Arrays.equals(owner, assetChangeowner.getNewowner())) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            asset.setOwner(assetChangeowner.getNewowner());
            contractaccountstate.setContract(asset.RLPserialization());
            newMap.put(tx.to, contractaccountstate);
        } else if (tx.getMethodType() == ASSETTRANSFER.ordinal()) {//资产转账
            AssetTransfer assetTransfer = AssetTransfer.getAssetTransfer(ByteUtil.bytearrayridfirst(tx.payload));
            Map<byte[], Long> maps = accountState.getTokensMap();
            long tokenbalance = maps.get(tx.to);
            tokenbalance -= assetTransfer.getValue();
            if (tokenbalance < 0) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            maps.put(tx.to, tokenbalance);
            accountState.setTokensMap(maps);

            //from=to
            if (Arrays.equals(publicKeyHash, assetTransfer.getTo())) {
                newMap.put(publicKeyHash, accountState);
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
            newMap.put(assetTransfer.getTo(), toaccountstate);
        } else {//increased
            if (asset.getAllowincrease() == 0 || !Arrays.equals(asset.getOwner(), publicKeyHash)
                    || Arrays.equals(asset.getOwner(), twentyBytes)) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            AssetIncreased assetIncreased = AssetIncreased.getAssetIncreased(ByteUtil.bytearrayridfirst(tx.payload));
            long totalamount = asset.getTotalamount();
            totalamount += assetIncreased.getAmount();
            if (totalamount <= 0) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            asset.setTotalamount(totalamount);
            contractaccountstate.setContract(asset.RLPserialization());
            newMap.put(tx.to, contractaccountstate);

            Map<byte[], Long> tokensmap = accountState.getTokensMap();
            long tokensbalance = 0;
            if (tokensmap.containsKey(tx.to)) {
                tokensbalance = tokensmap.get(tx.to);
            }
            tokensbalance += assetIncreased.getAmount();
            tokensmap.put(tx.to, tokensbalance);
            accountState.setTokensMap(tokensmap);
        }
        newMap.put(publicKeyHash, accountState);
        return null;
    }

    @Override
    public Object CheckDeployContract(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        long balance = fromaccount.getBalance();
        balance -= tx.getFee();
        if (balance < 0) {
            AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tx.nonce);
        accountState.setAccount(fromaccount);
        switch (tx.getContractType()) {
            case 0://代币
                Asset asset = new Asset();
                if (!asset.RLPdeserialization(ByteUtil.bytearrayridfirst(tx.payload))) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                //判断forkdb+Db中是否有重复的代币合约code存在
                if (repository.containsAssetCodeAt(parenthash, asset.getCode().getBytes(StandardCharsets.UTF_8))) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                //同一区块是否重复 Asset code
                if (AssetcodeSet.contains(asset.getCode())) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                AssetcodeSet.add(asset.getCode());
                break;
            case 1://多签
                Multiple multiple = new Multiple();
                if (!multiple.RLPdeserialization(ByteUtil.bytearrayridfirst(tx.payload))) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                break;
            case 2://锁定时间哈希
                Hashtimeblock hashtimeblock = new Hashtimeblock();
                if (!hashtimeblock.RLPdeserialization((ByteUtil.bytearrayridfirst(tx.payload)))) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                break;
            case 3://锁定高度哈希
                Hashheightblock hashheightblock = new Hashheightblock();
                if (!hashheightblock.RLPdeserialization((ByteUtil.bytearrayridfirst(tx.payload)))) {
                    AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                    return null;
                }
                break;
        }
        newMap.put(publicKeyHash, accountState);
        return null;
    }

    private boolean CheckSize(Transaction tx) {
        if (size > Block.MAX_BLOCK_SIZE || (size + tx.size()) > Block.MAX_BLOCK_SIZE) {
            exit = true;
            return true;
        }
        return false;
    }

    private boolean CheckRepetition(Transaction tx) {
        return repository.containsTransactionAt(parenthash, tx.getHash());
    }

    private boolean CheckMapRedo(String publicKeyHash) throws DecoderException {
        return !accountStateMap.containsKey(Hex.decodeHex(publicKeyHash.toCharArray()));
    }

    @Override
    public AccountState getKeyAccountState(byte[] key) {
        if (accountStateMap.containsKey(key)) {
            return accountStateMap.get(key);
        } else {
            return repository.getAccountStateAt(parenthash, key).orElse(new AccountState(key));
        }
    }

    @Override
    public AccountState getIncubatorTotal() {
        byte[] totalhash = IncubatorAddress.resultpubhash();
        if (accountStateMap.containsKey(totalhash)) {
            return accountStateMap.get(totalhash);
        } else {
            return repository.getAccountStateAt(parenthash, totalhash).get();
        }
    }

    @Override
    public Object CheckOtherKind(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        if (tx.type == 12) {
            if (repository.containsPayloadAt(block.hashPrevBlock, Transaction.Type.EXTRACT_COST.ordinal(), tx.payload)) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
        }
        if (tx.type == 15) {
            if (repository.containsPayloadAt(block.hashPrevBlock, EXIT_MORTGAGE.ordinal(), tx.payload)) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
        }
        Account account = UpdateOtherAccount(fromaccount, tx);
        if (account == null) {
            AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return null;
        }
        if (CheckIncubatorTotal(tx)) {
            AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return null;
        }
        accountState.setAccount(account);
        //校验type 10、11、12事务
        VerifyHatch verifyHatch = updateHatch(accountState, tx, block.nHeight);
        if (!verifyHatch.state) {
            AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return null;
        }
        newMap.put(publicKeyHash, accountState);
        return null;
    }

    private void AddRemoveMap(String key, long nonce) {
        removemap.put(new String(key), nonce);
        state = true;
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
                newMap.put(IncubatorAddress.resultpubhash(), totalaccountState);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object CheckFirstKind(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash) {
        AccountState toaccountState = getMapAccountState(tx);
        Account toaccount = toaccountState.getAccount();
        List<Account> accountList = null;
        if (tx.type == 1) {
            accountList = updateTransfer(fromaccount, toaccount, tx);
        } else if (tx.type == 2) {
            accountList = updateVote(fromaccount, toaccount, tx);
        } else {
            if (repository.containsPayloadAt(block.hashPrevBlock, EXIT_VOTE.ordinal(), tx.payload)) {
                AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
                return null;
            }
            accountList = updateCancelVote(fromaccount, toaccount, tx);
        }
        if (accountList == null) {
            AddRemoveMap(Hex.encodeHexString(publicKeyHash), tx.nonce);
            return null;
        }
        accountList.stream().forEach(account -> {
            if (Arrays.equals(account.getPubkeyHash(), publicKeyHash)) {
                accountState.setAccount(account);
                newMap.put(publicKeyHash, accountState);
            } else {
                toaccountState.setAccount(account);
                newMap.put(toaccount.getPubkeyHash(), toaccountState);
            }
        });
        return null;
    }

    public VerifyHatch updateHatch(AccountState accountState, Transaction transaction, long nHeight) {
        VerifyHatch verifyHatch = new VerifyHatch();
        Map<byte[], Incubator> map = null;
        if (transaction.type == 10) {
            map = accountState.getInterestMap();
            Incubator incubator = updateIncubtor(wisdomBlockChain, rateTable, configuration, map, transaction, nHeight);
            if (incubator.getInterest_amount() < 0 || incubator.getLast_blockheight_interest() > nHeight) {
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        } else if (transaction.type == 11) {
            map = accountState.getShareMap();
            Incubator incubator = updateIncubtor(wisdomBlockChain, rateTable, configuration, map, transaction, nHeight);
            if (incubator.getShare_amount() < 0 || incubator.getLast_blockheight_share() > nHeight) {
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setShareMap(map);
        } else if (transaction.type == 12) {
            map = accountState.getInterestMap();
            Incubator incubator = updateIncubtor(wisdomBlockChain, rateTable, configuration, map, transaction, nHeight);
            if (incubator.getInterest_amount() != 0) {
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        }
        verifyHatch.setAccountState(accountState);
        return verifyHatch;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private class VerifyHatch {
        public AccountState accountState;
        public boolean state = true;
    }

    private boolean updateWaitCount(String publicKeyHash, long nonce) {
        if (waitCount.IsExist(publicKeyHash, nonce)) {
            return waitCount.updateNonce(publicKeyHash);//单个节点最长旷工数量的7个区块，可以加入
        } else {
            waitCount.add(publicKeyHash, nonce);
        }
        return false;
    }
}
