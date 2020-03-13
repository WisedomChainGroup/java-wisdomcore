package org.wisdom.core;

import org.wisdom.command.Configuration;
import org.wisdom.contract.MultipleDefinition.MultTransfer;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.db.AccountState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface TransactionVerifyUpdate<T> {

    T CheckFirstKind(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash);

    T CheckOtherKind(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash);

    T CheckDeployContract(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash);

    T CheckCallContract(AccountState accountState, Account fromaccount, Transaction tx, byte[] publicKeyHash);

    T ChechAssetMethod(byte[] contract, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash);

    T ChechMultMethod(byte[] contract, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash);

    T CheckMultTransferWDC(MultTransfer multTransfer, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash);

    T CheckMultTransferOther(byte[] assetHash, MultTransfer multTransfer, Transaction tx, AccountState contractaccountstate, AccountState accountState, byte[] publicKeyHash);

    T CheckHashtimeMethod(byte[] contract, Transaction tx, AccountState accountState, byte[] publicKeyHash);

    T CheckHashheightMethod(byte[] contract, Transaction tx, AccountState accountState, byte[] publicKeyHash);

    AccountState getKeyAccountState(byte[] key);

    default AccountState getMapAccountState(Transaction tx) {
        return getKeyAccountState(tx.to);
    }

    AccountState getIncubatorTotal();

    boolean CheckIncubatorTotal(Transaction tx);

    default List<Account> updateCancelVote(Account fromaccount, Account votetoccount, Transaction transaction) {
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

    default List<Account> updateTransfer(Account fromaccount, Account toaccount, Transaction transaction) {
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

    default List<Account> updateVote(Account fromaccount, Account toaccount, Transaction transaction) {
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

    default Account UpdateOtherAccount(Account fromaccount, Transaction transaction) {
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

    default Incubator updateExtIncuator(WisdomBlockChain wisdomBlockChain, RateTable rateTable, Configuration configuration, Transaction tran, long nowheight, Incubator incubator) {
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

    default Incubator updateCostIncubator(Incubator incubator, long nowheight) {
        incubator.setCost(0);
        incubator.setHeight(nowheight);
        return incubator;
    }

    default Incubator updateIncubtor(WisdomBlockChain wisdomBlockChain, RateTable rateTable, Configuration configuration, Map<byte[], Incubator> map, Transaction transaction, long hieght) {
        Incubator incubator = map.get(transaction.payload);
        if (transaction.type == 10 || transaction.type == 11) {
            incubator = updateExtIncuator(wisdomBlockChain, rateTable, configuration, transaction, hieght, incubator);
        }
        if (transaction.type == 12) {
            incubator = updateCostIncubator(incubator, hieght);
        }
        return incubator;
    }
}
