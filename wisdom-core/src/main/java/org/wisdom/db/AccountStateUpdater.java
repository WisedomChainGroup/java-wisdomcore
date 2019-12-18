package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.util.ByteArrayMap;
import org.wisdom.util.ByteArraySet;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Component
public class AccountStateUpdater {
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

    public AccountState UpdateCoinbase(Transaction tx, AccountState accountState){
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

    public AccountState UpdateTransfer(Transaction tx, AccountState accountState){
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
}
