package org.wisdom.core.validate;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.command.TransactionCheck;
import org.wisdom.consensus.pow.PackageCache;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.contract.AssetDefinition.AssetChangeowner;
import org.wisdom.contract.AssetDefinition.AssetIncreased;
import org.wisdom.contract.AssetDefinition.AssetTransfer;
import org.wisdom.core.Block;
import org.wisdom.core.WhitelistTransaction;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.db.AccountState;
import org.wisdom.db.StateDB;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;

import java.util.*;

import static org.wisdom.core.account.Transaction.Type.*;
import static org.wisdom.contract.AssetDefinition.Asset.AssetRule.CHANGEOWNER;
import static org.wisdom.contract.AssetDefinition.Asset.AssetRule.TRANSFER;

public class CheckoutTransactions {

    private List<Transaction> transactionList;

    private List<Transaction> pendingList;

    private byte[] parenthash;

    private Map<String, AccountState> map;

    private AccountState fromaccountstate;

    private long height;

    private Set<String> AssetcodeSet;

    private StateDB stateDB;

    private TransactionCheck transactionCheck;

    private PeningTransPool peningTransPool;

    private WhitelistTransaction whitelistTransaction;

    private RateTable rateTable;

    private MerkleRule merkleRule;

    public CheckoutTransactions() {
        this.transactionList = new ArrayList<>();
        this.pendingList = new ArrayList<>();
        this.map = new HashMap<>();
        this.fromaccountstate = new AccountState();
        this.AssetcodeSet = new HashSet<>();
    }

    public void init(Block block, Map<String, AccountState> map, PeningTransPool peningTransPool, StateDB stateDB,
                     TransactionCheck transactionCheck, WhitelistTransaction whitelistTransaction, RateTable rateTable, MerkleRule merkleRule) {
        this.transactionList = block.body;
        this.height = block.nHeight;
        this.parenthash = block.hashPrevBlock;
        this.map = map;
        this.peningTransPool = peningTransPool;
        this.stateDB = stateDB;
        this.transactionCheck = transactionCheck;
        this.whitelistTransaction = whitelistTransaction;
        this.rateTable = rateTable;
        this.merkleRule = merkleRule;
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
            String publichash = Hex.encodeHexString(pubkeyhash);
            //校验撤回事务是否存在
            Result resultexit = CheckExitTransaction(tx, publichash);
            if (!resultexit.isSuccess()) {
                return resultexit;
            }
            //事务校验格式和数据
            Result resulttransa = CheckTransaction(tx, publichash);
            if (!resulttransa.isSuccess()) {
                return resulttransa;
            }
            //更新状态
            switch (tx.type) {
                case 1://转账
                case 2://投票
                case 13://撤回投票
                    Result resultfirst = CheckFirstKind(fromaccountstate, fromaccountstate.getAccount(), tx, publichash);
                    if (!resultfirst.isSuccess()) {
                        return resultfirst;
                    }
                    break;
                case 3://存证事务,只需要扣除手续费
                case 9://孵化事务
                case 10://提取利息
                case 11://提取分享
                case 12://本金
                case 14://抵押
                case 15://撤回抵押
                    Result resultother = CheckOtherKind(fromaccountstate, fromaccountstate.getAccount(), tx, publichash);
                    if (!resultother.isSuccess()) {
                        return resultother;
                    }
                    break;
                case 7://部署合约
                    Result resultdeploy = CheckDeployContract(tx, publichash);
                    if (!resultdeploy.isSuccess()) {
                        return resultdeploy;
                    }
                    break;
                case 8://调用合约
                    Result resultcall = CheckCallContract(fromaccountstate, fromaccountstate.getAccount(), tx, publichash);
                    if (!resultcall.isSuccess()) {
                        return resultcall;
                    }
                    break;
            }
            pendingList.add(tx);
        }
        peningTransPool.updatePool(transactionList, 1, height);
        return Result.SUCCESS;
    }

    private Result CheckCallContract(AccountState accountState, Account fromaccount, Transaction tx, String publicKeyHash) {
        long balance = fromaccount.getBalance();
        balance -= tx.getFee();
        if (balance < 0) {
            peningTransPool.removeOne(publicKeyHash, tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tx.nonce);
        accountState.setAccount(fromaccount);
        if (tx.getContractType() == 0) {//代币
            AccountState assetaccountstate = getMapAccountState(tx);
            byte[] contract = assetaccountstate.getContract();
            Asset asset = Asset.getAsset(contract);
            if (tx.getMethodType() == CHANGEOWNER.ordinal()) {//更换所有者
                byte[] owner = asset.getOwner();
                if (Arrays.equals(owner, new byte[32]) || !Arrays.equals(owner, tx.from)) {
                    peningTransPool.removeOne(publicKeyHash, tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Asset definition does not have permission to replace the owner");
                }
                AssetChangeowner assetChangeowner = AssetChangeowner.getAssetChangeowner(ByteUtil.bytearrayridfirst(tx.payload));
                asset.setOwner(assetChangeowner.getNewowner());
                assetaccountstate.setContract(asset.RLPserialization());
                map.put(Hex.encodeHexString(tx.to), assetaccountstate);
            } else if (tx.getMethodType() == TRANSFER.ordinal()) {//资产转账
                AssetTransfer assetTransfer = AssetTransfer.getAssetTransfer(ByteUtil.bytearrayridfirst(tx.payload));
                Map<byte[], Long> maps = accountState.getTokensMap();
                long tokenbalance = maps.get(tx.to);
                tokenbalance -= assetTransfer.getValue();
                if (tokenbalance < 0) {
                    peningTransPool.removeOne(publicKeyHash, tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Insufficient balance in asset transfer");
                }
                maps.put(tx.to, tokenbalance);
                accountState.setTokensMap(maps);

                //to
                AccountState toaccountstate = getKeyAccounState(assetTransfer.getTo());
                Map<byte[], Long> tomaps = toaccountstate.getTokensMap();
                long tobalance = 0;
                if (tomaps.containsKey(tx.to)) {
                    tobalance = tomaps.get(tx.to);
                }
                tobalance += assetTransfer.getValue();
                tomaps.put(tx.to, tobalance);
                toaccountstate.setTokensMap(tomaps);
                map.put(Hex.encodeHexString(assetTransfer.getTo()), toaccountstate);
            } else {//increased
                if (asset.getAllowincrease() == 0 || !Arrays.equals(asset.getOwner(), tx.from)
                        || Arrays.equals(asset.getOwner(), new byte[32])) {
                    peningTransPool.removeOne(publicKeyHash, tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": The asset definition does not have rights to issue shares");
                }
                AssetIncreased assetIncreased = AssetIncreased.getAssetIncreased(ByteUtil.bytearrayridfirst(tx.payload));
                long totalamount = asset.getTotalamount();
                totalamount += assetIncreased.getAmount();
                if(totalamount<=0){
                    peningTransPool.removeOne(publicKeyHash, tx.nonce);
                    return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Total asset issuance exceeded the maximum");
                }
                asset.setTotalamount(totalamount);
                assetaccountstate.setContract(asset.RLPserialization());
                map.put(Hex.encodeHexString(tx.to), assetaccountstate);

                Map<byte[], Long> tokensmap = accountState.getTokensMap();
                long tokensbalance = tokensmap.get(tx.to);
                tokensbalance += assetIncreased.getAmount();
                tokensmap.put(tx.to, tokensbalance);
                accountState.setTokensMap(tokensmap);
            }
        }
        map.put(publicKeyHash, accountState);
        return Result.SUCCESS;
    }

    private Result CheckDeployContract(Transaction tx, String publicKeyHash) {
        if (tx.getContractType() == 0) {//代币
            Asset asset = Asset.getAsset(ByteUtil.bytearrayridfirst(tx.payload));
            //判断forkdb中是否有重复的代币合约code存在
            if (stateDB.hasAssetCode(parenthash, DEPLOY_CONTRACT.ordinal(), asset.getCode())) {
                peningTransPool.removeOne(publicKeyHash, tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Asset token code repeats");
            }
            if (AssetcodeSet.contains(asset.getCode())) {
                peningTransPool.removeOne(publicKeyHash, tx.nonce);
                return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Asset token code repeats");
            }
            AssetcodeSet.add(asset.getCode());
        }
        return Result.SUCCESS;
    }

    private Result CheckFirstKind(AccountState accountState, Account fromaccount, Transaction tx, String publicKeyHash) {
        AccountState toaccountState = getMapAccountState(tx);
        Account toaccount = toaccountState.getAccount();
        List<Account> accountList = null;
        if (tx.type == 1) {
            accountList = PackageCache.updateTransfer(fromaccount, toaccount, tx);
        } else if (tx.type == 2) {
            accountList = PackageCache.updateVote(fromaccount, toaccount, tx);
        } else {
            accountList = PackageCache.UpdateCancelVote(fromaccount, toaccount, tx);
        }
        if (accountList == null) {
            peningTransPool.removeOne(publicKeyHash, tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
        }
        accountList.forEach(account -> {
            if (account.getKey().equals(publicKeyHash)) {
                accountState.setAccount(account);
                map.put(publicKeyHash, accountState);
            } else {
                toaccountState.setAccount(account);
                map.put(toaccount.getKey(), toaccountState);
            }
        });
        return Result.SUCCESS;
    }

    private Result CheckOtherKind(AccountState accountState, Account fromaccount, Transaction tx, String publicKeyHash) {
        Account account = UpdateOtherAccount(fromaccount, tx);
        if (account == null) {
            peningTransPool.removeOne(publicKeyHash, tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ": Update account cannot be null");
        }
        if (CheckIncubatorTotal(tx)) {//孵化总地址余额校验
            peningTransPool.removeOne(publicKeyHash, tx.nonce);
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
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        } else if (transaction.type == 11) {
            map = accountState.getShareMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            map.put(transaction.payload, incubator);
            accountState.setShareMap(map);
        } else if (transaction.type == 12) {
            map = accountState.getInterestMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        }
        return accountState;
    }

    public Incubator UpdateIncubtor(Map<byte[], Incubator> map, Transaction transaction, long hieght) {
        Incubator incubator = map.get(Hex.encodeHexString(transaction.payload));
        if (transaction.type == 10 || transaction.type == 11) {
            incubator = merkleRule.UpdateExtIncuator(transaction, hieght, incubator);
        }
        if (transaction.type == 12) {
            incubator = merkleRule.UpdateCostIncubator(incubator, hieght);
        }
        return incubator;
    }

    public Account UpdateOtherAccount(Account fromaccount, Transaction transaction) {
        boolean state = false;
        long balance = fromaccount.getBalance();
        if (transaction.type == 3) {//存证事务,只需要扣除手续费
            balance -= transaction.getFee();
        } else if (transaction.type == 9) {//孵化事务
            balance -= transaction.getFee();
            balance -= transaction.amount;
            long incubatecost = fromaccount.getIncubatecost();
            incubatecost += transaction.amount;
            fromaccount.setIncubatecost(incubatecost);
        } else if (transaction.type == 10 || transaction.type == 11) {//提取利息、分享
            balance -= transaction.getFee();
            if (balance < 0) {
                state = true;
            }
            balance += transaction.amount;
        } else if (transaction.type == 12) {//本金
            balance -= transaction.getFee();
            if (balance < 0) {
                state = true;
            }
            balance += transaction.amount;
            long incubatecost = fromaccount.getIncubatecost();
            incubatecost -= transaction.amount;
            if (incubatecost < 0) {
                state = true;
            }
            fromaccount.setIncubatecost(incubatecost);
        } else if (transaction.type == 14) {//抵押
            balance -= transaction.getFee();
            balance -= transaction.amount;
            long mortgage = fromaccount.getMortgage();
            mortgage += transaction.amount;
            fromaccount.setMortgage(mortgage);
        } else if (transaction.type == 15) {//撤回抵押
            balance -= transaction.getFee();
            if (balance < 0) {
                state = true;
            }
            balance += transaction.amount;
            long mortgage = fromaccount.getMortgage();
            mortgage -= transaction.amount;
            if (mortgage < 0) {
                state = true;
            }
            fromaccount.setMortgage(mortgage);
        }
        if (state || balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        return fromaccount;
    }

    private boolean CheckIncubatorTotal(Transaction transaction) {
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
                map.put(IncubatorAddress.Hexpubhash(), totalaccountState);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    private AccountState getIncubatorTotal() {
        String totalhash = IncubatorAddress.Hexpubhash();
        if (map.containsKey(totalhash)) {
            return map.get(totalhash);
        } else {
            return stateDB.getAccount(parenthash, IncubatorAddress.resultpubhash());
        }
    }

    private AccountState getKeyAccounState(byte[] key) {
        String keyHex = Hex.encodeHexString(key);
        if (map.containsKey(keyHex)) {
            return map.get(keyHex);
        } else {
            return stateDB.getAccount(parenthash, key);
        }
    }

    private AccountState getMapAccountState(Transaction tx) {
        return getKeyAccounState(tx.to);
    }

    private Result CheckTransaction(Transaction tx, String publichash) {
        APIResult apiResult = transactionCheck.TransactionFormatCheck(tx.toRPCBytes());
        if (apiResult.getCode() == 5000) {
            peningTransPool.removeOne(publichash, tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
        }
        if (map.containsKey(publichash)) {
            fromaccountstate = map.get(publichash);
        } else {
            peningTransPool.removeOne(publichash, tx.nonce);
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
            peningTransPool.removeOne(publichash, tx.nonce);
            return Result.Error("Transaction validation failed ," + Hex.encodeHexString(tx.getHash()) + ":" + apiResult.getMessage());
        }
        return Result.SUCCESS;
    }

    private Result CheckExitTransaction(Transaction tx, String publichash) {
        switch (Transaction.Type.values()[tx.type]) {
            case EXIT_VOTE: {
                // 投票没有撤回过
                if (stateDB.hasPayload(parenthash, EXIT_VOTE.ordinal(), tx.payload)) {
                    peningTransPool.removeOne(publichash, tx.nonce);
                    return Result.Error("the vote transaction " + Hex.encodeHexString(tx.payload) + " had been exited");
                }
                break;
            }
            case EXIT_MORTGAGE: {
                // 抵押没有撤回过
                if (stateDB.hasPayload(parenthash, EXIT_MORTGAGE.ordinal(), tx.payload)) {
                    peningTransPool.removeOne(publichash, tx.nonce);
                    return Result.Error("the mortgage transaction " + Hex.encodeHexString(tx.payload) + " had been exited");
                }
                break;
            }
            case EXTRACT_COST: {
                //本金没有被撤回过
                if (stateDB.hasPayload(parenthash, Transaction.Type.EXTRACT_COST.ordinal(), tx.payload)) {
                    peningTransPool.removeOne(publichash, tx.nonce);
                    return Result.Error("the incubate transaction " + Hex.encodeHexString(tx.payload) + " had been exited");
                }
                break;
            }
        }
        return Result.SUCCESS;
    }
}
