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
import org.wisdom.account.PublicKeyHash;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.encoding.BigEndian;
import org.wisdom.genesis.Genesis;
import org.wisdom.keystore.crypto.PublicKey;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;

import java.util.*;

@Component
public class AccountStateUpdater {

    @Autowired
    private RateTable rateTable;

    @Autowired
    private MerkleRule merkleRule;

    private Map<byte[], AccountState> copy(Map<byte[], AccountState> accountStateMap) {
        Map<byte[], AccountState> res = new ByteArrayMap<>();
        for (Map.Entry<byte[], AccountState> entry : accountStateMap.entrySet()) {
            res.put(entry.getKey(), entry.getValue().copy());
        }
        return res;
    }

    public Map<byte[], AccountState> updateAll(Map<byte[], AccountState> accounts, Block block) {
        Map<byte[], AccountState> res = copy(accounts);
        for (Transaction tx : block.body) {
            getRelatedAccounts(tx).stream()
                    .map(res::get)
                    .peek(x -> {
                        if (x == null) throw new RuntimeException("unreachable here");
                    })
                    .forEach(x -> this.updateOne(tx, x , block.getnHeight()));
        }
        return res;
    }

    public AccountState updateOne(Transaction transaction, AccountState accountState,long height) {
        if (height == 22912 && Hex.encodeHexString(accountState.getAccount().getPubkeyHash()).equals("c017fd8d81fb6e5bbe56dc549c33abcf4f397332")){
            System.out.println();
        }
        try {
            switch (transaction.type) {
                case 0x00://coinbase
                    return UpdateCoinbase(transaction, accountState);
                case 0x01://TRANSFER
                    return UpdateTransfer(transaction, accountState);
                case 0x02://VOTE
                    return UpdateVote(transaction, accountState);
                case 0x03://DEPOSIT
                    return UpdateDeposit(transaction, accountState);
                case 0x07://DEPLOY_CONTRACT
                    return UpdateDeployContract(transaction, accountState);
                case 0x08://CALL_CONTRACT
                    return UpdateCallContract(transaction, accountState);
                case 0x09://INCUBATE
                    return UpdateIncubate(transaction, accountState);
                case 0x0a://EXTRACT_INTEREST
                    return UpdateExtractInterest(transaction, accountState);
                case 0x0b://EXTRACT_SHARING_PROFIT
                    return UpdateExtractShare(transaction, accountState);
                case 0x0c://EXTRACT_COST
                    return UpdateExtranctCost(transaction, accountState);
                case 0x0d://EXIT_VOTE
                    return UpdateCancelVote(transaction, accountState);
                case 0x0e://MORTGAGE
                    return UpdateMortgage(transaction, accountState);
                case 0x0f://EXTRACT_MORTGAGE
                    return UpdateCancelMortgage(transaction, accountState);
                default:
                    throw new Exception("unsupported transaction type: " + Transaction.Type.values()[transaction.type].toString());
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Set<byte[]> getRelatedAccounts(Transaction transaction) {
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
            case 0x07://DEPLOY_CONTRACT
            case 0x08://CALL_CONTRACT
                return getTransactionFrom(transaction);
        }
        return new ByteArraySet();
    }

    private Set<byte[]> getTransactionTo(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        bytes.add(tx.to);
        if(tx.type==0x09){
            bytes.add(IncubatorAddress.resultpubhash());
        }
        return bytes;
    }

    private Set<byte[]> getTransactionFromTo(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        byte[] fromhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from));
        bytes.add(fromhash);
        if (!Arrays.equals(fromhash, tx.to)) {
            bytes.add(tx.to);
        }
        return bytes;
    }

    private Set<byte[]> getTransactionFrom(Transaction tx) {
        Set<byte[]> bytes = new ByteArraySet();
        byte[] fromhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from));
        bytes.add(fromhash);
        return bytes;
    }

    public Set<byte[]> getRelatedAccounts(Block block) {
        Set<byte[]> ret = new ByteArraySet();
        block.body.stream().map(this::getRelatedAccounts)
                .forEach(ret::addAll);
        return ret;
    }

    private AccountState UpdateCoinbase(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance += tx.amount;
        if(balance < 0) {
            throw new RuntimeException("math overflow");
        }
        account.setBalance(balance);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState UpdateTransfer(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            balance = account.getBalance();
            balance += tx.amount;
            account.setBalance(balance);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState UpdateVote(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            long vote = account.getVote();
            vote += tx.amount;
            account.setVote(vote);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState UpdateDeposit(Transaction tx, AccountState accountState){
        Account account = accountState.getAccount();
        if (!Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState UpdateDeployContract(Transaction tx, AccountState accountState) {
        return accountState;
    }

    private AccountState UpdateCallContract(Transaction tx, AccountState accountState) {
        return accountState;
    }

    private AccountState UpdateIncubate(Transaction tx, AccountState accountState) throws InvalidProtocolBufferException, DecoderException {
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
            account.setBlockHeight(tx.height);
            Incubator incubator = new Incubator(tx.to, tx.getHash(), tx.height, tx.amount, tx.getInterest(tx.height, rateTable, days), tx.height, days);
            Map<byte[], Incubator> maps = accountState.getInterestMap();
            maps.put(tx.getHash(), incubator);
            accountState.setInterestMap(maps);
            accountState.setAccount(account);
        }
        if (sharpub != null && !sharpub.equals("")) {
            byte[] sharepublic = Hex.decodeHex(sharpub.toCharArray());
            if (Arrays.equals(sharepublic, account.getPubkeyHash())) {
                Incubator share = new Incubator(sharepublic, tx.getHash(), tx.height, tx.amount, days, tx.getShare(tx.height, rateTable, days), tx.height);
                Map<byte[], Incubator> sharemaps = accountState.getShareMap();
                sharemaps.put(tx.getHash(), share);
                accountState.setShareMap(sharemaps);
            }
        }

        if (Arrays.equals(IncubatorAddress.resultpubhash(), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance -= tx.getInterest(tx.height, rateTable, days);
            if(sharpub != null && !sharpub.equals("")){
                balance-=tx.getShare(tx.height, rateTable, days);
            }
            long nonce = account.getNonce();
            nonce++;
            account.setBalance(balance);
            account.setNonce(nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    public Map<byte[], AccountState> generateGenesisStates(Block block, Genesis genesis) {
        List<Genesis.IncubateAmount> incubateAmountList = genesis.alloc.incubateAmount;
        Genesis.IncubateAmount incubateAmount = incubateAmountList.get(0);
        String address = incubateAmount.address;
        long balance = incubateAmount.balance * EconomicModel.WDC;
        byte[] totalpubhash = KeystoreAction.addressToPubkeyHash(address);
        Account totalaccount = new Account();
        Map<byte[], AccountState> AccountStateMap=new ByteArrayMap();
        try{
            for(Transaction tx:block.body){
                   if (Hex.encodeHexString(tx.to).equals("fbdacd374729b74c594cf955dc207fbb1d203a10")){
                       System.out.println();
                   }
                if(tx.type == 0x09){
                    HatchReturned hatchReturned=HatchStates(AccountStateMap,tx,balance);
                    hatchReturned.getAccountStateList().forEach(s->{
                        AccountStateMap.put(s.getKey(),s);
                    });
                    balance=hatchReturned.getBalance();
                }else{
                    AccountState accountState=getMapAccountState(AccountStateMap,tx.to);
                    Account account=accountState.getAccount();
                    account.setNonce(1);
                    account.setBalance(tx.amount);
                    if(!Arrays.equals(account.getPubkeyHash(), totalpubhash)){
                        accountState.setAccount(account);
                        AccountStateMap.put(accountState.getKey(),accountState);
                    }else{
                        totalaccount=account;

                    }
                }
            }
            // 1PxgikfZGWW1L3eFJWpBrowjX5omFiy9ba
            //孵化总地址
            AccountState totalState=createEmpty(totalpubhash);
            totalaccount.setBalance(balance);
            totalState.setAccount(totalaccount);
            AccountStateMap.put(totalState.getKey(),totalState);
        }catch (Exception e){
            return null;
        }
        return new ByteArrayMap<>(AccountStateMap);
    }

    private AccountState getMapAccountState(Map<byte[], AccountState> AccountStateMap,byte[] key){
        if(AccountStateMap.containsKey(key)){
            return AccountStateMap.get(key);
        }else{
            return createEmpty(key);
        }
    }

    private HatchReturned HatchStates(Map<byte[], AccountState> AccountStateMap,Transaction tx,long balance) throws InvalidProtocolBufferException, DecoderException {
        AccountState accountState=getMapAccountState(AccountStateMap,tx.to);
        byte[] playload = tx.payload;
        HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(playload);
        byte[] txamount = payloadproto.getTxId().toByteArray();
        long interestamount = BigEndian.decodeUint64(ByteUtil.bytearraycopy(txamount, 0, 8));
        Incubator incubator = new Incubator(tx.to, tx.getHash(), tx.height, tx.amount, interestamount, tx.height, payloadproto.getType());
        Map<byte[],Incubator> incubatorMap= accountState.getInterestMap();
        incubatorMap.put(incubator.getTxid_issue(),incubator);
        accountState.setInterestMap(incubatorMap);
        Account account=accountState.getAccount();
        long incubatecost = account.getIncubatecost();
        long nonce = account.getNonce();
        incubatecost = incubatecost + tx.amount;
        if(nonce<tx.nonce){
            nonce = tx.nonce;
        }
        account.setIncubatecost(incubatecost);
        account.setNonce(nonce);
        accountState.setAccount(account);

        //share
        long shareamount = BigEndian.decodeUint64(ByteUtil.bytearraycopy(txamount, 8, 8));
        String sharpub = payloadproto.getSharePubkeyHash();
        byte[] share_pubkeyhash = null;
        if(sharpub != null && sharpub != ""){
            share_pubkeyhash = Hex.decodeHex(sharpub.toCharArray());
        }
        Incubator shareIncubator=new Incubator(share_pubkeyhash, tx.getHash(), tx.height, tx.amount,payloadproto.getType(), shareamount , tx.height);
        AccountState Shareaccountstate=getMapAccountState(AccountStateMap,share_pubkeyhash);
        Map<byte[],Incubator> shareMap=Shareaccountstate.getShareMap();
        shareMap.put(shareIncubator.getTxid_issue(),shareIncubator);
        Shareaccountstate.setShareMap(shareMap);
        return new HatchReturned(Arrays.asList(accountState,Shareaccountstate),balance-interestamount-shareamount);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private class HatchReturned{
        private List<AccountState> accountStateList;
        private long balance;
    }

    private AccountState UpdateExtractInterest(Transaction tx, AccountState accountState) {
        Map<byte[], Incubator> map = accountState.getInterestMap();
        Incubator incubator = map.get(tx.payload);
        if (incubator == null) {
            throw new RuntimeException("Update extract interest error,tx:" + tx.getHashHexString());
        }
        incubator = merkleRule.UpdateExtIncuator(tx, tx.height, incubator);
        map.put(tx.payload, incubator);
        accountState.setInterestMap(map);

        Account account = accountState.getAccount();
        long balance;
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState UpdateExtractShare(Transaction tx, AccountState accountState) {
        Map<byte[], Incubator> map = accountState.getShareMap();
        Incubator incubator = map.get(tx.payload);
        if (incubator == null) {
            throw new RuntimeException("Update extract share error,tx:" + tx.getHashHexString());
        }
        incubator = merkleRule.UpdateExtIncuator(tx, tx.height, incubator);
        map.put(tx.payload, incubator);
        incubator = merkleRule.UpdateExtIncuator(tx, tx.height, incubator);
        map.put(tx.payload, incubator);

        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        account.setBalance(balance);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState UpdateExtranctCost(Transaction tx, AccountState accountState) {
        Map<byte[], Incubator> map = accountState.getInterestMap();
        Incubator incubator = map.get(tx.payload);
        if (incubator == null) {
            throw new RuntimeException("Update extract cost error,tx:" + tx.getHashHexString());
        }
        incubator.setCost(0);
        incubator.setHeight(tx.height);
        map.put(tx.payload, incubator);
        accountState.setInterestMap(map);

        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        long incub = account.getIncubatecost();
        incub -= tx.amount;
        account.setBalance(balance);
        account.setIncubatecost(incub);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState UpdateCancelVote(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        long balance;
        if (Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(tx.from)), account.getPubkeyHash())) {
            balance = account.getBalance();
            balance += tx.amount;
            balance -= tx.getFee();
            account.setBalance(balance);
            account.setNonce(tx.nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        if (Arrays.equals(tx.to, account.getPubkeyHash())) {
            long vote = account.getVote();
            vote -= tx.amount;
            account.setVote(vote);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState UpdateMortgage(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance -= tx.amount;
        long mortgage = account.getMortgage();
        mortgage += tx.amount;
        account.setBalance(balance);
        account.setMortgage(mortgage);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState UpdateCancelMortgage(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
            return accountState;
        }
        long balance = account.getBalance();
        balance -= tx.getFee();
        balance += tx.amount;
        long mortgage = account.getMortgage();
        mortgage -= tx.amount;
        account.setBalance(balance);
        account.setMortgage(mortgage);
        account.setNonce(tx.nonce);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    // 构造一个数据全为空的账户
    public AccountState createEmpty(byte[] publicKeyHash){
        return new AccountState(publicKeyHash);
    }
}
