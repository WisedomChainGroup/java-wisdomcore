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

import org.apache.commons.codec.binary.Hex;
import org.tdf.rlp.RLP;
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.ByteUtil;

import java.util.Arrays;
import java.util.Objects;

public class Account {
    @RLP(0)
    private byte[] id;
    @RLP(1)
    private long blockHeight;
    @RLP(2)
    private byte[] pubkeyHash;
    @RLP(3)
    private long nonce;
    @RLP(4)
    private long balance;
    @RLP(5)
    private long incubatecost;
    @RLP(6)
    private long mortgage;
    @RLP(7)
    private long vote;

    public Account() {
    }

    public Account(long blockHeight, byte[] pubkeyHash, long nonce, long balance, long incubatecost, long mortgage, long vote) {
        this.blockHeight = blockHeight;
        this.pubkeyHash = pubkeyHash;
        this.nonce = nonce;
        this.balance = balance;
        this.incubatecost = incubatecost;
        this.mortgage = mortgage;
        this.vote = vote;
    }

    public String getIdHexString() {
        return Hex.encodeHexString(getId());
    }

    public byte[] getId() {
        return ByteUtil.merge(pubkeyHash, BigEndian.encodeUint32(blockHeight));
    }

    public void setId(byte[] id) {
        this.id = id;
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

}
