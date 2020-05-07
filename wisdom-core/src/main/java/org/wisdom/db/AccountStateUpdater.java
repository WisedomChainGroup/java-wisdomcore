package org.wisdom.db;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
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

import java.math.BigDecimal;
import java.util.*;

@Component
@Setter
// TODO: make omit branch unreachable
public class AccountStateUpdater extends AbstractStateUpdater<AccountState> {

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

    @Override
    public AccountState update(Map<byte[], AccountState> related, byte[] id, AccountState state, TransactionInfo info) {
        return updateOne(info, state.copy(), related);
    }

    public AccountState updateOne(TransactionInfo info, AccountState accountState, Map<byte[], AccountState> store) {
        Transaction transaction = info.getTransaction();
        long height = info.getHeight();
        transaction.height = height;
        try {
            switch (transaction.type) {
                case 0x00://coinbase
                    return updateCoinBase(transaction, accountState, height);
                case 0x01://TRANSFER
                    return updateTransfer(transaction, accountState, height);
                case 0x02://VOTE
                    return updateVote(transaction, accountState, height);
                case 0x03://DEPOSIT
                    return updateDeposit(transaction, accountState, height);
                case 0x07://DEPLOY_CONTRACT
                    return updateDeployContract(transaction, accountState, height);
                case 0x08://CALL_CONTRACT
                    return updateCallContract(transaction, accountState, height, store);
                case 0x09://INCUBATE
                    return updateIncubate(transaction, accountState, height);
                case 0x0a://EXTRACT_INTEREST
                    return updateExtractInterest(transaction, accountState, height);
                case 0x0b://EXTRACT_SHARING_PROFIT
                    return updateExtractShare(transaction, accountState, height);
                case 0x0c://EXTRACT_COST
                    return updateExtranctCost(transaction, accountState, height);
                case 0x0d://EXIT_VOTE
                    return updateCancelVote(transaction, accountState, height);
                case 0x0e://MORTGAGE
                    return updateMortgage(transaction, accountState, height);
                case 0x0f://EXTRACT_MORTGAGE
                    return updateCancelMortgage(transaction, accountState, height);
                default:
                    throw new Exception("unsupported transaction type: " + Transaction.Type.values()[transaction.type].toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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
        } else if (tx.getContractType() == 9) {//定额条件比例获取
            bytes.add(tx.to);
            RateheightlockWithdraw rateheightlockWithdraw = RateheightlockWithdraw.getRateheightlockWithdraw(ByteUtil.bytearrayridfirst(tx.payload));
            if (!Arrays.equals(fromhash, rateheightlockWithdraw.getTo())) {
                bytes.add(rateheightlockWithdraw.getTo());
            }
        } else {//锁定合约、定额条件比例支付
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


    private AccountState updateCoinBase(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            throw new RuntimeException("CoinBase transaction account do not match");
        }
        long balance = account.getBalance();
        balance += tx.amount;
        if (balance < 0) {
            throw new RuntimeException("math overflow");
        }
        account.setBalance(balance);
        account.setBlockHeight(height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateTransfer(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        long balance;
        boolean state = true;
        if (Arrays.equals(Address.publicKeyToHash(tx.from), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            state = false;
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            balance = account.getBalance();
            balance += tx.amount;
            account.setBalance(balance);
            account.setBlockHeight(height);
            state = false;
        }
        if (state) {
            throw new RuntimeException("Transfer transaction account do not match");
        }
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateVote(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        long balance;
        boolean state = true;
        if (Arrays.equals(Address.publicKeyToHash(tx.from), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            state = false;
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            long vote = account.getVote();
            vote += tx.amount;
            account.setVote(vote);
            account.setBlockHeight(height);
            state = false;
        }
        if (state) {
            throw new RuntimeException("Vote transaction account do not match");
        }
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateDeposit(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(Address.publicKeyToHash(tx.from), account.getPubkeyHash())) {
            throw new RuntimeException("Deposit transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateDeployContract(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        switch (tx.getContractType()) {
            case 0:
                byte[] rlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
                Asset asset = Asset.getAsset(rlpbyte);

                byte[] fromhash = Address.publicKeyToHash(tx.from);
                if (Arrays.equals(fromhash, account.getPubkeyHash())) {//from
                    long balance = account.getBalance();
                    balance -= tx.getFee();
                    account.setBalance(balance);
                    account.setNonce(tx.nonce);
                    account.setBlockHeight(height);
                    accountState.setAccount(account);

                    if (Arrays.equals(fromhash, asset.getOwner())) {//from和owner相同
                        Map<byte[], Long> tokensmap = accountState.getTokensMap();
                        tokensmap.put(RipemdUtility.ripemd160(tx.getHash()), asset.getTotalamount());
                        accountState.setTokensMap(tokensmap);
                    }
                } else if (Arrays.equals(RipemdUtility.ripemd160(tx.getHash()), account.getPubkeyHash())) {//合约hash
                    accountState.setType(1);
                    accountState.setContract(rlpbyte);
                } else if (Arrays.equals(asset.getOwner(), account.getPubkeyHash())) {//owner
                    Map<byte[], Long> tokensmap = accountState.getTokensMap();
                    tokensmap.put(RipemdUtility.ripemd160(tx.getHash()), asset.getTotalamount());
                    accountState.setTokensMap(tokensmap);
                } else {
                    throw new RuntimeException("Deploy asset transaction account do not match");
                }
                break;
            case 1://多签
                if (Arrays.equals(Address.publicKeyToHash(tx.from), account.getPubkeyHash())) {
                    long balance = account.getBalance();
                    balance -= tx.getFee();
                    account.setBalance(balance);
                    account.setNonce(tx.nonce);
                    account.setBlockHeight(height);
                    accountState.setAccount(account);
                } else if (Arrays.equals(RipemdUtility.ripemd160(tx.getHash()), account.getPubkeyHash())) {//合约hash
                    byte[] mulrlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
                    accountState.setType(2);
                    accountState.setContract(mulrlpbyte);
                } else {
                    throw new RuntimeException("Deploy mult transaction account do not match");
                }
                break;
            case 2://锁定时间哈希
            case 3://锁定高度哈希
                if (Arrays.equals(Address.publicKeyToHash(tx.from), account.getPubkeyHash())) {
                    long balance = account.getBalance();
                    balance -= tx.getFee();
                    account.setBalance(balance);
                    account.setNonce(tx.nonce);
                    account.setBlockHeight(height);
                    accountState.setAccount(account);
                } else if (Arrays.equals(RipemdUtility.ripemd160(tx.getHash()), account.getPubkeyHash())) {//合约hash
                    byte[] lockrlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
                    if (tx.getContractType() == 2) {
                        accountState.setType(3);
                    } else {
                        accountState.setType(4);
                    }
                    accountState.setContract(lockrlpbyte);
                } else {
                    throw new RuntimeException("Deploy lock transaction account do not match");
                }
                break;
            case 4://定额条件比例支付
                if (Arrays.equals(Address.publicKeyToHash(tx.from), account.getPubkeyHash())) {
                    long balance = account.getBalance();
                    balance -= tx.getFee();
                    account.setBalance(balance);
                    account.setNonce(tx.nonce);
                    account.setBlockHeight(height);
                    accountState.setAccount(account);
                } else if (Arrays.equals(RipemdUtility.ripemd160(tx.getHash()), account.getPubkeyHash())) {//合约hash
                    byte[] lockrlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
                    accountState.setType(5);
                    accountState.setContract(lockrlpbyte);
                } else {
                    throw new RuntimeException("Deploy rate lock transaction account do not match");
                }
                break;
        }
        return accountState;
    }

    private AccountState updateCallContract(Transaction tx, AccountState accountState, long height, Map<byte[], AccountState> store) {
        Account account = accountState.getAccount();
        byte[] rlpbyte = ByteUtil.bytearrayridfirst(tx.payload);
        byte[] fromhash = Address.publicKeyToHash(tx.from);
        switch (tx.getMethodType()) {
            case 0://更改所有者
                accountState = updateAssetChangeowner(fromhash, accountState, account, tx, height, rlpbyte);
                break;
            case 1://转发资产
                accountState = updateAssetTransfer(fromhash, accountState, account, tx, height, rlpbyte);
                break;
            case 2://增发
                accountState = updateAssetIncreased(fromhash, accountState, account, tx, height, rlpbyte);
                break;
            case 3://多签转账
                accountState = updateMultTransfer(fromhash, accountState, account, tx, height, rlpbyte, store);
                break;
            case 4://锁定时间哈希资产转发
                accountState = updateHashtimeTransfer(fromhash, accountState, account, tx, height, rlpbyte, store);
                break;
            case 5://锁定时间哈希获取资产
                accountState = updategetHashtimeTransfer(fromhash, accountState, account, tx, height, rlpbyte, store);
                break;
            case 6://锁定高度哈希资产转发
                accountState = updateHashheightTransfer(fromhash, accountState, account, tx, height, rlpbyte, store);
                break;
            case 7://锁定高度哈希获取资产
                accountState = updategetHashheightTransfer(fromhash, accountState, account, tx, height, rlpbyte, store);
                break;
            case 8://定额条件比例支付
                accountState = updateRateheightDeposit(fromhash, accountState, account, tx, height, rlpbyte, store);
                break;
            case 9://定额条件比例获取
                accountState = updateRateheightWithdraw(fromhash, accountState, account, tx, height, rlpbyte, store);
                break;
        }
        return accountState;
    }

    private AccountState updateRateheightWithdraw(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        AccountState contractaccountstate = store.get(tx.to);
        Rateheightlock rateheightlock = Rateheightlock.getRateheightlock(contractaccountstate.getContract());
        RateheightlockWithdraw rateheightlockWithdraw = RateheightlockWithdraw.getRateheightlockWithdraw(rlpbyte);
        byte[] deposithash = rateheightlockWithdraw.getDeposithash();
        Transaction deposittran = wisdomBlockChain.getTransaction(deposithash);
        RateheightlockDeposit rateheightlockDeposit = RateheightlockDeposit.getRateheightlockDeposit(ByteUtil.bytearrayridfirst(deposittran.payload));
        BigDecimal bigDecimal = new BigDecimal(rateheightlockDeposit.getValue());
        BigDecimal onceamount = bigDecimal.multiply(new BigDecimal(rateheightlock.getWithdrawrate()));
        long amount = onceamount.longValue();
        if (Arrays.equals(fromhash, account.getPubkeyHash())) {//from
            long balance = account.getBalance();
            balance -= tx.getFee();

            if (Arrays.equals(fromhash, rateheightlockWithdraw.getTo())) {//from和to一致
                if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
                    Map<byte[], Long> quotaMap = account.getQuotaMap();
                    long quotabalance = quotaMap.get(twentyBytes);
                    quotabalance -= amount;
                    quotaMap.put(twentyBytes, quotabalance);
                    account.setQuotaMap(quotaMap);

                    balance += amount;
                } else {
                    Map<byte[], Long> quotaMap = account.getQuotaMap();
                    long quotabalance = quotaMap.get(rateheightlock.getAssetHash());
                    quotabalance -= amount;
                    quotaMap.put(rateheightlock.getAssetHash(), quotabalance);
                    account.setQuotaMap(quotaMap);

                    Map<byte[], Long> tokensMap = accountState.getTokensMap();
                    long tokenbalance = tokensMap.get(rateheightlock.getAssetHash());
                    tokenbalance += amount;
                    tokensMap.put(rateheightlock.getAssetHash(), tokenbalance);
                    accountState.setTokensMap(tokensMap);
                }
            } else {
                if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
                    Map<byte[], Long> quotaMap = account.getQuotaMap();
                    long quotabalance = quotaMap.get(twentyBytes);
                    quotabalance -= amount;
                    quotaMap.put(twentyBytes, quotabalance);
                    account.setQuotaMap(quotaMap);
                } else {
                    Map<byte[], Long> quotaMap = account.getQuotaMap();
                    long quotabalance = quotaMap.get(rateheightlock.getAssetHash());
                    quotabalance -= amount;
                    quotaMap.put(rateheightlock.getAssetHash(), quotabalance);
                    account.setQuotaMap(quotaMap);
                }
            }
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(account.getPubkeyHash(), rateheightlockWithdraw.getTo())) {//to
            long balance = account.getBalance();
            if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
                Map<byte[], Long> quotaMap = account.getQuotaMap();
                long quotabalance = quotaMap.get(twentyBytes);
                quotabalance -= amount;
                quotaMap.put(twentyBytes, quotabalance);
                account.setQuotaMap(quotaMap);

                balance += amount;
                account.setBalance(balance);
                accountState.setAccount(account);
            } else {
                Map<byte[], Long> quotaMap = account.getQuotaMap();
                long quotabalance = quotaMap.get(rateheightlock.getAssetHash());
                quotabalance -= amount;
                quotaMap.put(rateheightlock.getAssetHash(), quotabalance);
                account.setQuotaMap(quotaMap);

                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long tokenbalance = tokensMap.get(rateheightlock.getAssetHash());
                tokenbalance += amount;
                tokensMap.put(rateheightlock.getAssetHash(), tokenbalance);
                accountState.setTokensMap(tokensMap);
            }
        }
        if (Arrays.equals(account.getPubkeyHash(), tx.to)) {//合约
            Map<byte[], Extract> statMap = rateheightlock.getStateMap();
            Extract extract = statMap.get(deposithash);
            int surplus = extract.getSurplus();
            surplus--;
            if (surplus == 0) {//已全部领取完
                statMap.remove(deposithash);
                rateheightlock.setStateMap(statMap);
            } else {
                long extractheight = extract.getExtractheight();
                extractheight += rateheightlock.getWithdrawperiodheight();
                extract.setSurplus(surplus);
                extract.setExtractheight(extractheight);
                statMap.put(deposithash, extract);
                rateheightlock.setStateMap(statMap);
            }
            accountState.setContract(rateheightlock.RLPserialization());
        }
        return accountState;
    }

    private AccountState updateRateheightDeposit(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte, Map<byte[], AccountState> store) {
        AccountState contractaccountstate = store.get(tx.to);
        Rateheightlock rateheightlock = Rateheightlock.getRateheightlock(contractaccountstate.getContract());
        RateheightlockDeposit rateheightlockDeposit = RateheightlockDeposit.getRateheightlockDeposit(rlpbyte);
        if (Arrays.equals(fromhash, account.getPubkeyHash())) {//from
            long balance = account.getBalance();
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);

            Map<byte[], Long> quotaMap = accountState.getTokensMap();
            long quotabalance = 0;
            if (Arrays.equals(rateheightlock.getAssetHash(), twentyBytes)) {//WDC
                balance = account.getBalance();
                balance -= rateheightlockDeposit.getValue();
                account.setBalance(balance);

                if (quotaMap.containsKey(twentyBytes)) {
                    quotabalance = quotaMap.get(twentyBytes);
                }
                quotabalance += rateheightlockDeposit.getValue();
                quotaMap.put(twentyBytes, quotabalance);
                account.setQuotaMap(quotaMap);
            } else {
                Map<byte[], Long> tokensMap = accountState.getTokensMap();
                long tokenbalance = tokensMap.get(rateheightlock.getAssetHash());
                tokenbalance -= rateheightlockDeposit.getValue();
                tokensMap.put(rateheightlock.getAssetHash(), tokenbalance);
                accountState.setTokensMap(tokensMap);

                if (quotaMap.containsKey(rateheightlock.getAssetHash())) {
                    quotabalance = quotaMap.get(rateheightlock.getAssetHash());
                }
                quotabalance += rateheightlockDeposit.getValue();
                quotaMap.put(rateheightlock.getAssetHash(), quotabalance);
                account.setQuotaMap(quotaMap);
            }
            accountState.setAccount(account);
        } else if (Arrays.equals(tx.to, account.getPubkeyHash())) {//合约
            BigDecimal bigDecimal = new BigDecimal(rateheightlockDeposit.getValue());
            BigDecimal onceamount = bigDecimal.multiply(new BigDecimal(rateheightlock.getWithdrawrate()));
            int count = bigDecimal.divide(onceamount).intValue();
            Map<byte[], Extract> stateMap = rateheightlock.getStateMap();
            stateMap.put(tx.getHash(), new Extract(height, count));
            rateheightlock.setStateMap(stateMap);
            accountState.setContract(rateheightlock.RLPserialization());
        } else {
            throw new RuntimeException("RateheightDeposit transaction account do not match");
        }
        return accountState;
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

    private AccountState updateAssetIncreased(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte) {
        AssetIncreased assetIncreased = AssetIncreased.getAssetIncreased(rlpbyte);
        if (Arrays.equals(fromhash, account.getPubkeyHash())) {//事务from
            long balance = account.getBalance();
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            accountState.setAccount(account);

            Map<byte[], Long> tokensmap = accountState.getTokensMap();
            long tokenbalance = 0;
            if (tokensmap.containsKey(tx.to)) {
                tokenbalance = tokensmap.get(tx.to);
            }
            tokenbalance += assetIncreased.getAmount();
            tokensmap.put(tx.to, tokenbalance);
            accountState.setTokensMap(tokensmap);
        } else if (Arrays.equals(tx.to, account.getPubkeyHash())) {//合约
            byte[] contract = accountState.getContract();
            Asset asset = Asset.getAsset(contract);
            long totalbalance = asset.getTotalamount();
            totalbalance += assetIncreased.getAmount();
            asset.setTotalamount(totalbalance);
            accountState.setContract(asset.RLPserialization());
        } else {
            throw new RuntimeException("AssetIncreased transaction account do not match");
        }
        return accountState;
    }

    private AccountState updateAssetTransfer(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte) {
        AssetTransfer assetTransfer = AssetTransfer.getAssetTransfer(rlpbyte);
        boolean state = true;
        if (Arrays.equals(fromhash, account.getPubkeyHash())) {//合约from
            long balance = account.getBalance();
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            accountState.setAccount(account);

            Map<byte[], Long> tokensmap = accountState.getTokensMap();
            long tokenbalance = tokensmap.get(tx.to);
            tokenbalance -= assetTransfer.getValue();
            tokensmap.put(tx.to, tokenbalance);
            accountState.setTokensMap(tokensmap);
            state = false;
        }
        if (Arrays.equals(assetTransfer.getTo(), account.getPubkeyHash())) {//to
            Map<byte[], Long> tokensmap = accountState.getTokensMap();
            long balance = 0;
            if (tokensmap.containsKey(tx.to)) {
                balance = tokensmap.get(tx.to);
            }
            balance += assetTransfer.getValue();
            tokensmap.put(tx.to, balance);
            accountState.setTokensMap(tokensmap);
            state = false;
        }
        if (state) {
            throw new RuntimeException("AssetTransfer transaction account do not match");
        }
        return accountState;
    }

    private AccountState updateAssetChangeowner(byte[] fromhash, AccountState accountState, Account account, Transaction tx, long height, byte[] rlpbyte) {
        if (Arrays.equals(fromhash, account.getPubkeyHash())) {//事务from
            long balance = account.getBalance();
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            accountState.setAccount(account);
        } else if (Arrays.equals(tx.to, account.getPubkeyHash())) {//合约
            AssetChangeowner assetChangeowner = AssetChangeowner.getAssetChangeowner(rlpbyte);
            byte[] contract = accountState.getContract();
            Asset asset = Asset.getAsset(contract);
            asset.setOwner(assetChangeowner.getNewowner());
            accountState.setContract(asset.RLPserialization());
        } else {
            throw new RuntimeException("AssetChangeowner transaction account do not match");
        }
        return accountState;
    }

    private AccountState updateIncubate(Transaction tx, AccountState accountState, long height) throws InvalidProtocolBufferException, DecoderException {
        Account account = accountState.getAccount();
        HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(tx.payload);
        int days = payloadproto.getType();
        String sharpub = payloadproto.getSharePubkeyHash();
        long balance;
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.getFee();
            balance -= tx.amount;
            long incub = account.getIncubatecost();
            incub += tx.amount;
            account.setBalance(balance);
            account.setIncubatecost(incub);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            Incubator incubator = new Incubator(tx.to, tx.getHash(), height, tx.amount, tx.getInterest(height, rateTable, days), height, days);
            Map<byte[], Incubator> maps = accountState.getInterestMap();
            maps.put(tx.getHash(), incubator);
            accountState.setInterestMap(maps);
            accountState.setAccount(account);
        } else if (sharpub != null && !sharpub.equals("")) {
            byte[] sharepublic = Hex.decodeHex(sharpub.toCharArray());
            if (Arrays.equals(sharepublic, account.getPubkeyHash())) {
                Incubator share = new Incubator(sharepublic, tx.getHash(), height, tx.amount, days, tx.getShare(height, rateTable, days), height);
                Map<byte[], Incubator> sharemaps = accountState.getShareMap();
                sharemaps.put(tx.getHash(), share);
                accountState.setShareMap(sharemaps);
            }
        } else if (Arrays.equals(IncubatorAddress.resultpubhash(), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.getInterest(height, rateTable, days);
            if (sharpub != null && !sharpub.equals("")) {
                balance -= tx.getShare(height, rateTable, days);
            }
            long nonce = account.getNonce();
            nonce++;
            account.setBalance(balance);
            account.setNonce(nonce);
            account.setBlockHeight(height);
            accountState.setAccount(account);
        } else {
            throw new RuntimeException("Incubate transaction account do not match");
        }
        return accountState;
    }

    @Override
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

    private AccountState updateExtractInterest(Transaction tx, AccountState accountState, long height) {
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
        return accountState;
    }

    private AccountState updateExtractShare(Transaction tx, AccountState accountState, long height) {
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
        return accountState;
    }

    private AccountState updateExtranctCost(Transaction tx, AccountState accountState, long height) {
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
        return accountState;
    }

    private AccountState updateCancelVote(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        long balance;
        boolean state = true;
        if (Arrays.equals(Address.publicKeyToHash(tx.from), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance += tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(height);
            state = false;
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            long vote = account.getVote();
            vote -= tx.amount;
            account.setVote(vote);
            account.setBlockHeight(height);
            state = false;
        }
        if (state) {
            throw new RuntimeException("CancelVote transaction account do not match");
        }
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateMortgage(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            throw new RuntimeException("Mortgage transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance -= tx.amount;
        long mortgage = account.getMortgage();
        mortgage += tx.amount;
        account.setBalance(balance);
        account.setMortgage(mortgage);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState updateCancelMortgage(Transaction tx, AccountState accountState, long height) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            throw new RuntimeException("CancelMortgage transaction account do not match");
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        long mortgage = account.getMortgage();
        mortgage -= tx.amount;
        account.setBalance(balance);
        account.setMortgage(mortgage);
        account.setNonce(tx.nonce);
        account.setBlockHeight(height);
        accountState.setAccount(account);
        return accountState;
    }

    // 构造一个数据全为空的账户
    public AccountState createEmpty(byte[] publicKeyHash) {
        return new AccountState(publicKeyHash);
    }
}
