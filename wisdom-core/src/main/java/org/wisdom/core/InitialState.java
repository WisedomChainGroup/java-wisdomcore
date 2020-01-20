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

package org.wisdom.core;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.encoding.BigEndian;
import org.wisdom.genesis.Genesis;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

//@Component
public class InitialState {

    private static final Logger logger = LoggerFactory.getLogger("db");

    public ApplicationContext ctx;

    private IncubatorDB incubatorDB;

    private AccountDB accountDB;

    private Block block;

    @Autowired
    public InitialState(IncubatorDB incubatorDB, AccountDB accountDB, Block block, Genesis genesis) throws InvalidProtocolBufferException, DecoderException {
        this.incubatorDB = incubatorDB;
        this.block = block;
        this.accountDB = accountDB;
        int count = incubatorDB.count();
        List<Genesis.IncubateAmount> incubateAmountList = genesis.alloc.incubateAmount;
        Genesis.IncubateAmount incubateAmount = incubateAmountList.get(0);
        String address = incubateAmount.address;
        long balance = incubateAmount.balance * EconomicModel.WDC;
        byte[] totalpubhash = KeystoreAction.addressToPubkeyHash(address);
        Account totalaccount = new Account();
        if (count == 0) {
            List<Transaction> tranlist = block.body;
            List<Object[]> args = new ArrayList<>();
            List<Object[]> accountlist = new ArrayList<>();
            Map<String, Account> map = new HashMap<>();
            for (Transaction tx : tranlist) {
                if (tx.type == 0x09) {
                    byte[] playload = tx.payload;
                    HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(playload);
                    byte[] txamount = payloadproto.getTxId().toByteArray();
                    long interest = BigEndian.decodeUint64(ByteUtil.bytearraycopy(txamount, 0, 8));
                    long share = BigEndian.decodeUint64(ByteUtil.bytearraycopy(txamount, 8, 8));
                    String sharpub = payloadproto.getSharePubkeyHash();
                    byte[] share_pubkeyhash = null;
                    if (sharpub != null && sharpub != "") {
                        share_pubkeyhash = Hex.decodeHex(sharpub.toCharArray());
                    }
                    Incubator incubator = new Incubator(share_pubkeyhash, tx.from, tx.getHash(), tx.height, tx.amount, interest, share, 0, 0);
                    args.add(new Object[]{
                            incubator.getId(), incubator.getShare_pubkeyhash()
                            , incubator.getPubkeyhash(), incubator.getTxid_issue()
                            , incubator.getHeight(), incubator.getCost(), incubator.getInterest_amount()
                            , incubator.getShare_amount(), incubator.getLast_blockheight_interest(), incubator.getLast_blockheight_share()
                    });
                    balance -= (interest + share);
                    if (map.containsKey(Hex.encodeHexString(tx.to))) {
                        Account accounts = map.get(Hex.encodeHexString(tx.to));
                        long incubatecost = accounts.getIncubatecost();
                        long nonce = accounts.getNonce();
                        incubatecost = incubatecost + tx.amount;
                        if (nonce < tx.nonce) {
                            nonce = tx.nonce;
                        }
                        accounts.setIncubatecost(incubatecost);
                        accounts.setNonce(nonce);
                        map.put(Hex.encodeHexString(tx.to), accounts);
                    } else {
                        Account accounts = new Account(0, tx.to, tx.nonce, 0, tx.amount, 0, 0);
                        map.put(Hex.encodeHexString(tx.to), accounts);
                    }
//                } else {//0x00
//                    Account account = new Account(0, tx.to, 1, tx.amount, 0, 0, 0);
//                    if (!Arrays.equals(account.getPubkeyHash(), totalpubhash)) {
//                        accountlist.add(new Object[]{
//                                account.getId(), account.getBlockHeight(), account.getPubkeyHash(), account.getNonce()
//                                , account.getBalance(), account.getIncubatecost(), account.getMortgage(), account.getVote()
//                        });
//                    } else {
//                        totalaccount = account;
//                    }

                }
            }
//            accountlist.add(new Object[]{
//                    totalaccount.getId(), totalaccount.getBlockHeight(), totalaccount.getPubkeyHash(), totalaccount.getNonce()
//                    , balance, totalaccount.getIncubatecost(), totalaccount.getMortgage(), totalaccount.getVote()
//            });
            incubatorDB.insertIncubatorList(args);
            int accountcount = accountDB.count();
//            if (accountcount == 0) {
//                for (Map.Entry<String, Account> entry : map.entrySet()) {
//                    Account account = entry.getValue();
//                    accountlist.add(new Object[]{
//                            account.getId(), account.getBlockHeight(), account.getPubkeyHash()
//                            , account.getNonce(), account.getBalance(), account.getIncubatecost(), account.getMortgage(), account.getVote()
//                    });
//                }
//                accountDB.insertAccountList(accountlist);
//            }
            logger.info("The initial data load is complete");
        }
    }
}