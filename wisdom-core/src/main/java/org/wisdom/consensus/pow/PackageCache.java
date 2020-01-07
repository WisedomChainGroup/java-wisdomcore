package org.wisdom.consensus.pow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdf.common.util.ByteArrayMap;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.contract.AssetDefinition.AssetChangeowner;
import org.wisdom.contract.AssetDefinition.AssetIncreased;
import org.wisdom.contract.AssetDefinition.AssetTransfer;
import org.wisdom.contract.MultipleDefinition.Multiple;
import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.core.validate.MerkleRule;
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
import static org.wisdom.contract.AssetDefinition.Asset.AssetRule.CHANGEOWNER;
import static org.wisdom.contract.AssetDefinition.Asset.AssetRule.TRANSFER;

public class PackageCache {

    private static final Logger logger = LoggerFactory.getLogger(PackageCache.class);

    private Map<byte[], AccountState> accountStateMap;

    private IdentityHashMap<String, Long> removemap;

    private Map<String, TreeMap<Long, TransPool>> maps;

    private List<Transaction> transactionList;

    private Set<String> AssetcodeSet;

    private boolean exit;

    private boolean state;

    private int size;

    private byte[] parenthash;

    private Block block;

    private long height;

    private String publicKeyHash;

    private Map<byte[], AccountState> newMap;

    private WisdomRepository repository;

    private MerkleRule merkleRule;

    private WaitCount waitCount;

    private PeningTransPool peningTransPool;

    private RateTable rateTable;

    public PackageCache() {
        this.removemap = new IdentityHashMap<>();
        this.transactionList = new ArrayList<>();
        this.AssetcodeSet = new HashSet<>();
        this.exit = false;
        this.state = false;
    }

    public void init(PeningTransPool peningTransPool, WisdomRepository repository, MerkleRule merkleRule,
                     WaitCount waitCount, RateTable rateTable, Map<byte[], AccountState> accountStateMap,
                     Map<String, TreeMap<Long, TransPool>> maps, byte[] parenthash,
                     Block block, long height, int size) {
        this.peningTransPool = peningTransPool;
        this.repository = repository;
        this.merkleRule = merkleRule;
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
                    AccountState accountState = accountStateMap.get(publicKeyHash);
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
                            CheckFirstKind(accountState, fromaccount, transaction, publicKeyHash);
                            break;
                        case 3://存证事务,只需要扣除手续费
                        case 9://孵化事务
                        case 10://提取利息
                        case 11://提取分享
                        case 12://本金
                        case 14://抵押
                        case 15://撤回抵押
                            CheckOtherKind(accountState, fromaccount, transaction, publicKeyHash);
                            break;
                        case 7://部署合约
                            CheckDeployContract(accountState, fromaccount, transaction, publicKeyHash);
                            break;
                        case 8://调用合约
                            CheckCallContract(accountState, fromaccount, transaction, publicKeyHash);
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

    private void CheckCallContract(AccountState accountState, Account fromaccount, Transaction tx, String publicKeyHash) throws DecoderException {
        long balance = fromaccount.getBalance();
        balance -= tx.getFee();
        if (balance < 0) {
            AddRemoveMap(publicKeyHash, tx.nonce);
            return;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tx.nonce);
        accountState.setAccount(fromaccount);
        if (tx.getContractType() == 0) {//代币
            AccountState assetaccountstate = getMapAccountState(tx);
            byte[] contract = assetaccountstate.getContract();
            Asset asset = Asset.getAsset(contract);
            if (asset == null) {
                AddRemoveMap(publicKeyHash, tx.nonce);
                logger.error("Block packaging error, " + Hex.encodeHexString(tx.to) + ": asset definition RLP error");
                return;
            }
            if (tx.getMethodType() == CHANGEOWNER.ordinal()) {//跟换所有者
                byte[] owner = asset.getOwner();
                if (Arrays.equals(owner, new byte[32]) || !Arrays.equals(owner, tx.from)) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                AssetChangeowner assetChangeowner = AssetChangeowner.getAssetChangeowner(ByteUtil.bytearrayridfirst(tx.payload));
                if (Arrays.equals(owner, assetChangeowner.getNewowner())) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                asset.setOwner(assetChangeowner.getNewowner());
                assetaccountstate.setContract(asset.RLPserialization());
                newMap.put(tx.to, assetaccountstate);
            } else if (tx.getMethodType() == TRANSFER.ordinal()) {//资产转账
                AssetTransfer assetTransfer = AssetTransfer.getAssetTransfer(ByteUtil.bytearrayridfirst(tx.payload));
                Map<byte[], Long> maps = accountState.getTokensMap();
                long tokenbalance = maps.get(tx.to);
                tokenbalance -= assetTransfer.getValue();
                if (tokenbalance < 0) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
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
                newMap.put(assetTransfer.getTo(), toaccountstate);
            } else {//increased
                if (asset.getAllowincrease() == 0 || !Arrays.equals(asset.getOwner(), tx.from)
                        || Arrays.equals(asset.getOwner(), new byte[32])) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                AssetIncreased assetIncreased = AssetIncreased.getAssetIncreased(ByteUtil.bytearrayridfirst(tx.payload));
                long totalamount = asset.getTotalamount();
                totalamount += assetIncreased.getAmount();
                if (totalamount <= 0) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                asset.setTotalamount(totalamount);
                assetaccountstate.setContract(asset.RLPserialization());
                newMap.put(tx.to, assetaccountstate);

                Map<byte[], Long> tokensmap = accountState.getTokensMap();
                long tokensbalance=0;
                if(tokensmap.containsKey(tx.to)){
                    tokensbalance = tokensmap.get(tx.to);
                }
                tokensbalance += assetIncreased.getAmount();
                tokensmap.put(tx.to, tokensbalance);
                accountState.setTokensMap(tokensmap);
            }
        }
        newMap.put(Hex.decodeHex(publicKeyHash.toCharArray()), accountState);
    }

    private void CheckDeployContract(AccountState accountState, Account fromaccount, Transaction tx, String publicKeyHash) throws DecoderException {
        long balance = fromaccount.getBalance();
        balance -= tx.getFee();
        if (balance < 0) {
            AddRemoveMap(publicKeyHash, tx.nonce);
            return;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tx.nonce);
        accountState.setAccount(fromaccount);
        switch (tx.getContractType()) {
            case 0://代币
                Asset asset = new Asset();
                if (!asset.RLPdeserialization(ByteUtil.bytearrayridfirst(tx.payload))) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                //判断forkdb中是否有重复的代币合约code存在
                if (repository.containsAssetCodeAt(parenthash, asset.getCode().getBytes(StandardCharsets.UTF_8))) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                //同一区块是否重复 Asset code
                if (AssetcodeSet.contains(asset.getCode())) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                AssetcodeSet.add(asset.getCode());
                break;
            case 1://多签
                Multiple multiple = new Multiple();
                if (!multiple.RLPdeserialization(ByteUtil.bytearrayridfirst(tx.payload))) {
                    AddRemoveMap(publicKeyHash, tx.nonce);
                    return;
                }
                break;
        }
        newMap.put(Hex.decodeHex(publicKeyHash.toCharArray()), accountState);
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

    private AccountState getKeyAccounState(byte[] key) {
        if (accountStateMap.containsKey(key)) {
            return accountStateMap.get(key);
        } else {
            return repository.getAccountStateAt(parenthash, key).get();
        }
    }

    private AccountState getMapAccountState(Transaction tx) {
        return getKeyAccounState(tx.to);
    }

    private AccountState getIncubatorTotal() {
        byte[] totalhash = IncubatorAddress.resultpubhash();
        if (accountStateMap.containsKey(totalhash)) {
            return accountStateMap.get(totalhash);
        } else {
            return repository.getAccountStateAt(parenthash, totalhash).get();
        }
    }

    private void CheckOtherKind(AccountState accountState, Account fromaccount, Transaction tx, String publicKeyHash) throws DecoderException {
        if (tx.type == 12) {
            if (repository.containsPayloadAt(block.hashPrevBlock, Transaction.Type.EXTRACT_COST.ordinal(), tx.payload)) {
                AddRemoveMap(publicKeyHash, tx.nonce);
                return;
            }
        }
        if (tx.type == 15) {
            if (repository.containsPayloadAt(block.hashPrevBlock, EXIT_MORTGAGE.ordinal(), tx.payload)) {
                AddRemoveMap(publicKeyHash, tx.nonce);
                return;
            }
        }
        Account account = UpdateOtherAccount(fromaccount, tx);
        if (account == null) {
            AddRemoveMap(publicKeyHash, tx.nonce);
            return;
        }
        if (CheckIncubatorTotal(tx)) {
            AddRemoveMap(publicKeyHash, tx.nonce);
            return;
        }
        accountState.setAccount(account);
        //校验type 10、11、12事务
        VerifyHatch verifyHatch = updateHatch(accountState, tx, block.nHeight);
        if (!verifyHatch.state) {
            AddRemoveMap(publicKeyHash, tx.nonce);
            return;
        }
        newMap.put(Hex.decodeHex(publicKeyHash.toCharArray()), accountState);
    }

    private void AddRemoveMap(String key, long nonce) {
        removemap.put(new String(key), nonce);
        state = true;
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
                newMap.put(IncubatorAddress.resultpubhash(), totalaccountState);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    private void CheckFirstKind(AccountState accountState, Account fromaccount, Transaction tx, String publicKeyHash) throws DecoderException {
        AccountState toaccountState = getMapAccountState(tx);
        Account toaccount = toaccountState.getAccount();
        List<Account> accountList = null;
        if (tx.type == 1) {
            accountList = updateTransfer(fromaccount, toaccount, tx);
        } else if (tx.type == 2) {
            accountList = updateVote(fromaccount, toaccount, tx);
        } else {
            if (repository.containsPayloadAt(block.hashPrevBlock, EXIT_VOTE.ordinal(), tx.payload)) {
                AddRemoveMap(publicKeyHash, tx.nonce);
                return;
            }
            accountList = UpdateCancelVote(fromaccount, toaccount, tx);
        }
        if (accountList == null) {
            AddRemoveMap(publicKeyHash, tx.nonce);
            return;
        }
        for (Account account : accountList) {
            if (account.getKey().equals(publicKeyHash)) {
                accountState.setAccount(account);
                newMap.put(Hex.decodeHex(publicKeyHash.toCharArray()), accountState);
            } else {
                toaccountState.setAccount(account);
                newMap.put(toaccount.getPubkeyHash(), toaccountState);
            }
        }
    }

    public static List<Account> UpdateCancelVote(Account fromaccount, Account votetoccount, Transaction transaction) {
        List<Account> list = new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.getFee();
        if (balance < 0) {
            return null;
        }
        balance += transaction.amount;
        //to-
        long vote;
        if (Arrays.equals(fromaccount.getPubkeyHash(), votetoccount.getPubkeyHash())) {//撤回自己投给自己的投票
            vote = fromaccount.getVote();
            vote -= transaction.amount;
            fromaccount.setVote(vote);
        } else {
            vote = votetoccount.getVote();
            vote -= transaction.amount;
            votetoccount.setVote(vote);
            list.add(votetoccount);
        }
        if (vote < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        list.add(fromaccount);
        return list;
    }

    public static List<Account> updateTransfer(Account fromaccount, Account toaccount, Transaction transaction) {
        List<Account> list = new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.amount;
        balance -= transaction.getFee();
        if (Arrays.equals(fromaccount.getPubkeyHash(), toaccount.getPubkeyHash())) {
            balance += transaction.amount;
        } else {
            long tobalance = toaccount.getBalance();
            tobalance += transaction.amount;
            toaccount.setBalance(tobalance);
            list.add(toaccount);
        }
        if (balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        list.add(fromaccount);
        return list;
    }

    public static List<Account> updateVote(Account fromaccount, Account toaccount, Transaction transaction) {
        List<Account> list = new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= transaction.amount;
        balance -= transaction.getFee();
        if (Arrays.equals(fromaccount.getPubkeyHash(), toaccount.getPubkeyHash())) {
            long vote = fromaccount.getVote();
            vote += transaction.amount;
            fromaccount.setVote(vote);
        } else {
            long vote = toaccount.getVote();
            vote += transaction.amount;
            toaccount.setVote(vote);
            list.add(toaccount);
        }
        if (balance < 0) {
            return null;
        }
        fromaccount.setBalance(balance);
        fromaccount.setNonce(transaction.nonce);
        list.add(fromaccount);
        return list;
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

    public VerifyHatch updateHatch(AccountState accountState, Transaction transaction, long nHeight) {
        VerifyHatch verifyHatch = new VerifyHatch();
        Map<byte[], Incubator> map = null;
        if (transaction.type == 10) {
            map = accountState.getInterestMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            if (incubator.getInterest_amount() < 0 || incubator.getLast_blockheight_interest() > nHeight) {
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setInterestMap(map);
        } else if (transaction.type == 11) {
            map = accountState.getShareMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
            if (incubator.getShare_amount() < 0 || incubator.getLast_blockheight_share() > nHeight) {
                verifyHatch.setState(false);
                return verifyHatch;
            }
            map.put(transaction.payload, incubator);
            accountState.setShareMap(map);
        } else if (transaction.type == 12) {
            map = accountState.getInterestMap();
            Incubator incubator = UpdateIncubtor(map, transaction, nHeight);
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

    public Incubator UpdateIncubtor(Map<byte[], Incubator> map, Transaction transaction, long hieght) {
        Incubator incubator = map.get(transaction.payload);
        if (transaction.type == 10 || transaction.type == 11) {
            incubator = merkleRule.UpdateExtIncuator(transaction, hieght, incubator);
        }
        if (transaction.type == 12) {
            incubator = merkleRule.UpdateCostIncubator(incubator, hieght);
        }
        return incubator;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class VerifyHatch {
        public AccountState accountState;
        public boolean state;
    }

    public boolean updateWaitCount(String publicKeyHash, long nonce) {
        if (waitCount.IsExist(publicKeyHash, nonce)) {
            return waitCount.updateNonce(publicKeyHash);//单个节点最长旷工数量的7个区块，可以加入
        } else {
            waitCount.add(publicKeyHash, nonce);
        }
        return false;
    }
}
