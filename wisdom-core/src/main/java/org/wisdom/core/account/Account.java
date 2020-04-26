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

package org.wisdom.core.account;

import lombok.Builder;
import org.apache.commons.codec.binary.Hex;
import org.checkerframework.checker.units.qual.A;
import org.tdf.rlp.*;

import java.util.Arrays;

@RLPDecoding(value = Account.AccountDecoder.class)
public class Account {
    static class AccountDecoder implements RLPDecoder<Account>{

        @Override
        public Account decode(RLPElement rlpElement) {
            RLPList li = rlpElement.asRLPList();
            Account a = new Account();
            int i = 0;
            a.setBlockHeight(li.get(i++).asLong());
            a.setPubkeyHash(li.get(i++).asBytes());
            a.setNonce(li.get(i++).asLong());
            a.setBalance(li.get(i++).asLong());
            a.setIncubatecost(li.get(i++).asLong());
            a.setMortgage(li.get(i++).asLong());
            a.setVote(li.get(i++).asLong());
            if(i >= li.size())
                return a;
            a.field = li.get(i++).asLong();
            return a;
        }
    }

    @RLP(0)
    private long blockHeight;
    @RLP(1)
    private byte[] pubkeyHash;
    @RLP(2)
    private long nonce;
    @RLP(3)
    private long balance;
    @RLP(4)
    private long incubatecost;
    @RLP(5)
    private long mortgage;
    @RLP(6)
    private long vote;

    @RLP(7)
    private long field;

    public Account() {
    }

    @Builder
    public Account(long blockHeight, byte[] pubkeyHash, long nonce, long balance, long incubatecost, long mortgage, long vote) {
        this.blockHeight = blockHeight;
        this.pubkeyHash = pubkeyHash;
        this.nonce = nonce;
        this.balance = balance;
        this.incubatecost = incubatecost;
        this.mortgage = mortgage;
        this.vote = vote;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public byte[] getPubkeyHash() {
        return pubkeyHash;
    }

    public void setPubkeyHash(byte[] pubkeyHash) {
        this.pubkeyHash = pubkeyHash;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getIncubatecost() {
        return incubatecost;
    }

    public void setIncubatecost(long incubatecost) {
        this.incubatecost = incubatecost;
    }

    public long getMortgage() {
        return mortgage;
    }

    public void setMortgage(long mortgage) {
        this.mortgage = mortgage;
    }

    public long getVote() {
        return vote;
    }

    public void setVote(long vote) {
        this.vote = vote;
    }

    public Account copy() {
        return new Account(blockHeight, pubkeyHash, nonce, balance, incubatecost, mortgage, vote);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return blockHeight == account.blockHeight &&
                nonce == account.nonce &&
                balance == account.balance &&
                incubatecost == account.incubatecost &&
                mortgage == account.mortgage &&
                vote == account.vote &&
                Arrays.equals(pubkeyHash, account.pubkeyHash);
    }

    public String getKey(){
        return Hex.encodeHexString(this.pubkeyHash);
    }

}
