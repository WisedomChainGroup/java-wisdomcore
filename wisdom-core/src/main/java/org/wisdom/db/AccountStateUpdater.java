package org.wisdom.db;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
                        if(x == null) throw new RuntimeException("unreachable here");
                    })
                    .forEach(x -> this.updateOne(tx, x));
        }
        return res;
    }

    public AccountState updateOne(Transaction transaction, AccountState accountState) {
        switch (transaction.type){
            case 0x00://coinbase
                return UpdateCoinbase(transaction,accountState);
            case 0x01://TRANSFER
            case 0x02://VOTE
            case 0x03://DEPOSIT
            case 0x07://
            case 0x08://
            case 0x09://INCUBATE
            case 0x0a://EXTRACT_INTEREST
            case 0x0b://EXTRACT_SHARING_PROFIT
            case 0x0c://EXTRACT_COST
            case 0x0d://MORTGAGE
            case 0x0e://EXIT_MORTGAGE

        }

        return null;
    }

    public Set<byte[]> getRelatedAccounts(Transaction transaction) {
        return new ByteArraySet();
    }

    public Set<byte[]> getRelatedAccounts(Block block) {
        Set<byte[]> ret = new ByteArraySet();
        block.body.stream().map(this::getRelatedAccounts)
                .forEach(ret::addAll);
        return ret;
    }

    private AccountState UpdateCoinbase(Transaction tx, AccountState accountState){
        Account account=accountState.getAccount();
        if(!Arrays.equals(tx.to, account.getPubkeyHash())){
            return accountState;
        }
        long balance=account.getBalance();
        balance+=tx.amount;
        account.setBalance(balance);
        account.setBlockHeight(tx.height);
        accountState.setAccount(account);
        return accountState;
    }

    private AccountState UpdateTransfer(Transaction tx, AccountState accountState){
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

    private AccountState UpdateExtractInterest(Transaction tx, AccountState accountState){
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

        Map<byte[], Incubator> map = accountState.getInterestMap();
        Incubator incubator = map.get(Hex.encodeHexString(tx.payload));
        if (incubator == null) {
            log.info("Interest payload:" + Hex.encodeHexString(tx.payload) + "--->tx:" + tx.getHashHexString());
            return accountState;
        }
        incubator = merkleRule.UpdateExtIncuator(tx, tx.height, incubator);
        map.put(tx.payload, incubator);
        accountState.setInterestMap(map);
        return accountState;
    }
}
