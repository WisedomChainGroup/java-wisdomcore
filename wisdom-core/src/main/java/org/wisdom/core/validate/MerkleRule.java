/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.core.validate;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.wisdom.command.Configuration;
import org.wisdom.command.IncubatorAddress;
import org.wisdom.core.Block;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class MerkleRule implements BlockRule {

    private static final Logger logger = LoggerFactory.getLogger(MerkleRule.class);

    @Autowired
    AccountDB accountDB;

    @Autowired
    IncubatorDB incubatorDB;

    @Autowired
    RateTable rateTable;

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    Configuration configuration;

    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();

    private boolean validateIncubator;

    public MerkleRule(@Value("${node-character}") String character) {
        this.validateIncubator = !character.equals("exchange");
    }

    @Override
    public Result validateBlock(Block block) {
        // 梅克尔根校验
        if (!org.bouncycastle.util.Arrays.areEqual(block.hashMerkleRoot, Block.calculateMerkleRoot(block.body))) {
            return Result.Error("merkle root validate fail " + new String(codec.encodeBlock(block)) + " " + Hex.encodeHexString(block.hashMerkleRoot) + " " + Hex.encodeHexString(Block.calculateMerkleRoot(block.body)));
        }
        try {
            Map<String, Object> merklemap = validateMerkle(block.body, block.nHeight);
            List<Account> accountList = (List<Account>) merklemap.get("account");
            List<Incubator> incubatorList = (List<Incubator>) merklemap.get("incubator");
            if (!org.bouncycastle.util.Arrays.areEqual(block.hashMerkleState, Block.calculateMerkleState(accountList))) {
                return Result.Error("merkle state validate fail " + new String(codec.encodeBlock(block)) + " " + Hex.encodeHexString(block.hashMerkleState) + " " + Hex.encodeHexString(Block.calculateMerkleState(accountList)));
            }
            // 交易所不校验孵化状态
            if (!validateIncubator) {
                return Result.SUCCESS;
            }
            if (!org.bouncycastle.util.Arrays.areEqual(block.hashMerkleIncubate, Block.calculateMerkleIncubate(incubatorList))) {
                return Result.Error("merkle incubate validate fail " + new String(codec.encodeBlock(block)) + " " + Hex.encodeHexString(block.hashMerkleIncubate) + " " + Hex.encodeHexString(Block.calculateMerkleIncubate(incubatorList)));
            }
        } catch (Exception e) {
            return Result.Error("error occurs when validate merle hash");
        }
        return Result.SUCCESS;
    }

    public Map<String, Object> validateMerkle(List<Transaction> transactionList, long nowheight) throws InvalidProtocolBufferException, DecoderException {
        Map<String, Account> accmap = new HashMap<>();
        Map<String, Incubator> incumap = new HashMap<>();
        Account totalaccount = accountDB.selectaccount(IncubatorAddress.resultpubhash());
        long totalbalance = totalaccount.getBalance();
        boolean isdisplay = false;
        for (Transaction tran : transactionList) {
            Account toaccount;
            if (accmap.containsKey(Hex.encodeHexString(tran.to))) {
                toaccount = accmap.get(Hex.encodeHexString(tran.to));
            } else {
                toaccount = Optional.ofNullable(accountDB.selectaccount(tran.to))
                        .orElse(new Account(nowheight, tran.to, 0, 0, 0, 0, 0));
            }
            switch (tran.type) {
                case 0x00://CoinBase
                    Account cionaccount = UpdateCoinBase(tran, toaccount, nowheight);
                    accmap.put(Hex.encodeHexString(tran.to), cionaccount);
                    break;
                case 0x01://transfer
                    Account fromaccount = new Account();
                    byte[] frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
                    if (accmap.containsKey(Hex.encodeHexString(frompubhash))) {
                        fromaccount = accmap.get(Hex.encodeHexString(frompubhash));
                    } else {
                        fromaccount = accountDB.selectaccount(frompubhash);
                    }
                    List<Account> accountList = UpdateTransfer(tran, fromaccount, toaccount, nowheight, frompubhash);
                    accountList.stream().forEach(a -> accmap.put(Hex.encodeHexString(a.getPubkeyHash()), a));
                    break;
                case 0x02://Vote
                    frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
                    if (accmap.containsKey(Hex.encodeHexString(frompubhash))) {
                        fromaccount = accmap.get(Hex.encodeHexString(frompubhash));
                    } else {
                        fromaccount = accountDB.selectaccount(frompubhash);
                    }
                    List<Account> list = UpdateVoteAccount(tran, fromaccount, toaccount, nowheight, frompubhash);
                    list.stream().forEach(a -> accmap.put(Hex.encodeHexString(a.getPubkeyHash()), a));
                    break;
                case 0x03://Deposit
                    frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
                    if (accmap.containsKey(Hex.encodeHexString(frompubhash))) {
                        fromaccount = accmap.get(Hex.encodeHexString(frompubhash));
                    } else {
                        fromaccount = accountDB.selectaccount(frompubhash);
                    }
                    Account dopaccount = UpdateDepAccount(tran, fromaccount, nowheight);
                    accmap.put(Hex.encodeHexString(frompubhash), dopaccount);
                    break;
                case 0x09://hatch
                    Account hatchaccount = UpdateHatAccount(tran, toaccount, nowheight);
                    accmap.put(Hex.encodeHexString(tran.to), hatchaccount);
                    //分享
                    Incubator hatchincubator = UpdateHatIncuator(tran, nowheight);
                    incumap.put(Hex.encodeHexString(tran.to), hatchincubator);
                    isdisplay = true;
                    totalbalance -= (hatchincubator.getShare_amount() + hatchincubator.getInterest_amount());
                    break;
                case 0x0a:case 0x0b://extract
                    Account extractaccount = UpdateExtAccount(tran, toaccount, nowheight);
                    accmap.put(Hex.encodeHexString(tran.to), extractaccount);
                    //孵化状态
                    Incubator incubator = incubatorDB.selectIncubator(tran.payload);
                    if (nowheight > 30800 && nowheight < 40271) {
                        if (Arrays.equals(incubator.getPubkeyhash(), tran.to)) {
                            if (incubator.getShare_pubkeyhash() != null) {
                                if (incumap.containsKey(Hex.encodeHexString(incubator.getShare_pubkeyhash()))) {
                                    incubator = incumap.get(Hex.encodeHexString(incubator.getShare_pubkeyhash()));
                                }
                            }
                        }
                        if (Arrays.equals(incubator.getShare_pubkeyhash(), tran.to)) {
                            if (incumap.containsKey(Hex.encodeHexString(incubator.getPubkeyhash()))) {
                                incubator = incumap.get(Hex.encodeHexString(incubator.getPubkeyhash()));
                            }
                        }
                    }
                    if (nowheight >= 40271) {
                        if (Arrays.equals(incubator.getPubkeyhash(), tran.to)) {
                            if (incubator.getShare_pubkeyhash() != null) {
                                if (incumap.containsKey(Hex.encodeHexString(incubator.getShare_pubkeyhash()))) {
                                    Incubator incubators = incumap.get(Hex.encodeHexString(incubator.getShare_pubkeyhash()));
                                    if (Arrays.equals(incubators.getTxid_issue(), tran.payload)) {
                                        incubator = incubators;
                                    }
                                }
                            }
                        }
                        if (Arrays.equals(incubator.getShare_pubkeyhash(), tran.to)) {
                            if (incumap.containsKey(Hex.encodeHexString(incubator.getPubkeyhash()))) {
                                Incubator incubators = incumap.get(Hex.encodeHexString(incubator.getPubkeyhash()));
                                if (Arrays.equals(incubators.getTxid_issue(), tran.payload)) {
                                    incubator = incubators;
                                }
                            }
                        }
                    }
                    Incubator extrincubator = UpdateExtIncuator(tran, nowheight, incubator);
                    incumap.put(Hex.encodeHexString(tran.to), extrincubator);
                    break;
                case 0x0c://extract cost
                    Account costaccount = UpdateCostAccount(tran, toaccount, nowheight);
                    accmap.put(Hex.encodeHexString(tran.to), costaccount);
                    //孵化状态
                    byte[] playload = tran.payload;//孵化哈希
                    incubator = incubatorDB.selectIncubator(playload);
                    if (nowheight > 30800 && nowheight < 40271) {
                        if (incubator.getShare_pubkeyhash() != null) {
                            if (incumap.containsKey(Hex.encodeHexString(incubator.getShare_pubkeyhash()))) {
                                incubator = incumap.get(Hex.encodeHexString(incubator.getShare_pubkeyhash()));
                            }
                        }
                    }
                    if (nowheight >= 40271) {
                        if (incubator.getShare_pubkeyhash() != null) {
                            if (incumap.containsKey(Hex.encodeHexString(incubator.getShare_pubkeyhash()))) {
                                Incubator incubators = incumap.get(Hex.encodeHexString(incubator.getShare_pubkeyhash()));
                                if (Arrays.equals(incubators.getTxid_issue(), tran.payload)) {
                                    incubator = incubators;
                                }
                            }
                        }
                    }
                    Incubator costIncubator = UpdateCostIncubator(incubator, nowheight);
                    incumap.put(Hex.encodeHexString(tran.to), costIncubator);
                    break;
                case 0x0d://Cancel Vote
                    frompubhash = RipemdUtility.ripemd160(SHA3Utility.keccak256(tran.from));
                    if (accmap.containsKey(Hex.encodeHexString(frompubhash))) {
                        fromaccount = accmap.get(Hex.encodeHexString(frompubhash));
                    } else {
                        fromaccount = accountDB.selectaccount(frompubhash);
                    }
                    List<Account> celvotelist = UpdateCancelVote(tran,fromaccount, toaccount, nowheight, frompubhash);
                    celvotelist.stream().forEach(a -> accmap.put(Hex.encodeHexString(a.getPubkeyHash()), a));
                    break;
                case 0x0e://mortgage

                    break;
            }
        }
        if (isdisplay) {
            totalaccount.setBalance(totalbalance);
            long nonce = totalaccount.getNonce();
            nonce++;
            totalaccount.setNonce(nonce);
            totalaccount.setBlockHeight(nowheight);
            accmap.put(Hex.encodeHexString(totalaccount.getPubkeyHash()), totalaccount);
        }
        List<Account> accountList = new ArrayList<>();
        List<Incubator> incubatorList = new ArrayList<>();
        for (Map.Entry<String, Account> entry : accmap.entrySet()) {
            accountList.add(entry.getValue());
        }
        for (Map.Entry<String, Incubator> entry : incumap.entrySet()) {
            incubatorList.add(entry.getValue());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("account", accountList);
        result.put("incubator", incubatorList);
        return result;
    }

    private Account UpdateCoinBase(Transaction tran,Account toaccount,long nowheight){
        long balance = toaccount.getBalance();
        balance += tran.amount;
        toaccount.setBalance(balance);
        toaccount.setBlockHeight(nowheight);
        return toaccount;
    }

    private Account UpdateHatAccount(Transaction tran,Account toaccount,long nowheight) {
        long balance = toaccount.getBalance();
        balance -= tran.amount;
        balance -= tran.getFee();
        toaccount.setBalance(balance);
        long cost = toaccount.getIncubatecost();
        cost += tran.amount;
        toaccount.setIncubatecost(cost);
        toaccount.setBlockHeight(nowheight);
        toaccount.setNonce(tran.nonce);
        return toaccount;
    }

    private Incubator UpdateHatIncuator(Transaction tran,long nowheight) throws InvalidProtocolBufferException, DecoderException {
        byte[] playload = tran.payload;
        HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(playload);
        int days = payloadproto.getType();
        String sharpub = payloadproto.getSharePubkeyHash();
        byte[] share_pubkeyhash = null;
        long share = 0;
        if (sharpub != null && sharpub != "") {
            share_pubkeyhash = Hex.decodeHex(sharpub.toCharArray());
            share = tran.getShare(nowheight, rateTable, days);
        }
        long interest = tran.getInterest(nowheight, rateTable, days);
        Incubator incubator = new Incubator(share_pubkeyhash, tran.to, tran.getHash(), nowheight, tran.amount, interest, share, nowheight, nowheight);
        return incubator;
    }

    private Account UpdateExtAccount(Transaction tran,Account toaccount,long nowheight){
        long balance = toaccount.getBalance();
        balance += tran.amount;
        balance -= tran.getFee();
        toaccount.setBalance(balance);
        toaccount.setBlockHeight(nowheight);
        toaccount.setNonce(tran.nonce);
        return toaccount;
    }

    public Incubator UpdateExtIncuator(Transaction tran,long nowheight,Incubator incubator){
        Transaction transaction = wisdomBlockChain.getTransaction(tran.payload);
        int days = transaction.getdays();
        String rate = rateTable.selectrate(transaction.height, days);//利率
        if (tran.type == 0x0a) {//interset
            BigDecimal amounbig = BigDecimal.valueOf(transaction.amount);
            BigDecimal ratebig = new BigDecimal(rate);
            long dayinterset = ratebig.multiply(amounbig).longValue();
            long lastheight = incubator.getLast_blockheight_interest();
            if (dayinterset > tran.amount) {
                lastheight += configuration.getDay_count();
            } else {
                int extractday = (int) (tran.amount / dayinterset);
                long extractheight = extractday * configuration.getDay_count();
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
                lastheight += configuration.getDay_count();
            } else {
                int extractday = (int) (tran.amount / dayinterset);
                long extractheight = extractday * configuration.getDay_count();
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

    private List<Account> UpdateTransfer(Transaction tran,Account fromaccount,Account toaccount,long nowheight,byte[] frompubhash){
        List<Account> list=new ArrayList<>();
        long frombalance = fromaccount.getBalance();
        frombalance -= tran.amount;
        frombalance -= tran.getFee();
        fromaccount.setBalance(frombalance);
        fromaccount.setNonce(tran.nonce);
        fromaccount.setBlockHeight(nowheight);
        if (!Arrays.equals(frompubhash, tran.to)) {
            long tobalance = toaccount.getBalance();
            tobalance += tran.amount;
            toaccount.setBalance(tobalance);
            toaccount.setBlockHeight(nowheight);
            list.add(fromaccount);
            list.add(toaccount);
        } else {//转账from和to相同
            frombalance += tran.amount;
            fromaccount.setBalance(frombalance);
            list.add(fromaccount);
        }
        return list;
    }

    private Account UpdateCostAccount(Transaction tran,Account toaccount,long nowheight){
        long balance = toaccount.getBalance();
        balance += tran.amount;
        balance -= tran.getFee();
        long cost = toaccount.getIncubatecost();
        cost -= tran.amount;
        toaccount.setBalance(balance);
        toaccount.setBlockHeight(nowheight);
        toaccount.setNonce(tran.nonce);
        toaccount.setIncubatecost(cost);
        return toaccount;
    }

    public Incubator UpdateCostIncubator(Incubator incubator,long nowheight){
        incubator.setCost(0);
        incubator.setHeight(nowheight);
        return incubator;
    }

    private Account UpdateDepAccount(Transaction tran,Account fromaccount,long nowheight){
        long balance = fromaccount.getBalance();
        balance -= tran.getFee();
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tran.nonce);
        fromaccount.setBlockHeight(nowheight);
        return fromaccount;
    }

    private List<Account> UpdateVoteAccount(Transaction tran,Account fromaccount,Account toaccount,long nowheight,byte[] frompubhash){
        List<Account> list=new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= tran.amount;
        balance -= tran.getFee();
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tran.nonce);
        fromaccount.setBlockHeight(nowheight);
        if (!Arrays.equals(frompubhash, tran.to)) {
            long vote = toaccount.getVote();
            vote += tran.amount;
            toaccount.setVote(vote);
            toaccount.setBlockHeight(nowheight);
            list.add(fromaccount);
            list.add(toaccount);
        } else {//投票自己投给自己
            long vote = fromaccount.getVote();
            vote += tran.amount;
            fromaccount.setVote(vote);
            list.add(fromaccount);
        }
        return list;
    }

    private List<Account> UpdateCancelVote(Transaction tran,Account fromaccount,Account toaccount,long nowheight,byte[] frompubhash){
        List<Account> list=new ArrayList<>();
        long balance = fromaccount.getBalance();
        balance -= tran.getFee();
        balance += tran.amount;
        fromaccount.setBalance(balance);
        fromaccount.setNonce(tran.nonce);
        fromaccount.setBlockHeight(nowheight);
        if(Arrays.equals(frompubhash, tran.to)){//撤回自己投给自己的投票
            long vote = fromaccount.getVote();
            vote-=tran.amount;
            fromaccount.setVote(vote);
            list.add(fromaccount);
        }else{
            long vote = toaccount.getVote();
            vote -= tran.amount;
            toaccount.setVote(vote);
            toaccount.setBlockHeight(nowheight);
            list.add(fromaccount);
            list.add(toaccount);
        }
        return list;
    }
}
