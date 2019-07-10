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
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.ByteUtil;

public class Account {
    private byte[] id;

    private long blockHeight;

    private byte[] pubkeyHash;

    private long nonce;

    private long balance;

    private long incubatecost;

    private long mortgage;

    public Account(){}

    public Account(long blockHeight, byte[] pubkeyHash, long nonce, long balance, long incubatecost, long mortgage) {
        this.blockHeight = blockHeight;
        this.pubkeyHash = pubkeyHash;
        this.nonce = nonce;
        this.balance = balance;
        this.incubatecost = incubatecost;
        this.mortgage = mortgage;
    }

    public String getIdHexString(){
        return Hex.encodeHexString(getId());
    }

    public byte[] getId() {
        return ByteUtil.merge(pubkeyHash,BigEndian.encodeUint32(blockHeight));
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
}