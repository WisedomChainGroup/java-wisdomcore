package org.wisdom.db;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.protobuf.tcp.command.HatchModel;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

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
                    .forEach(x -> this.updateOne(tx, x));
        }
        return res;
    }

    public AccountState updateOne(Transaction transaction, AccountState accountState) {
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
                    return UpdateVote(transaction, accountState);
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
            case 0x0d://EXIT_VOTE
            case 0x0e://MORTGAGE
            case 0x0f://EXTRACT_MORTGAGE
                return getTransactionTo(transaction);
            case 0x01://TRANSFER
            case 0x02://VOTE
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

    private AccountState UpdateDeposit(Transaction tx, AccountState accountState) {
        Account account = accountState.getAccount();
        if (!Arrays.equals(tx.to, account.getPubkeyHash())) {
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
            balance -= tx.amount;
            long nonce = account.getNonce();
            nonce++;
            account.setBalance(balance);
            account.setNonce(nonce);
            account.setBlockHeight(tx.height);
            accountState.setAccount(account);
        }
        return accountState;
    }

    private AccountState UpdateExtractInterest(Transaction tx, AccountState accountState) {
        Map<byte[], Incubator> map = accountState.getInterestMap();
        Incubator incubator = map.get(Hex.encodeHexString(tx.payload));
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
        Incubator incubator = map.get(Hex.encodeHexString(tx.payload));
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
        Incubator incubator = map.get(Hex.encodeHexString(tx.payload));
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
}
