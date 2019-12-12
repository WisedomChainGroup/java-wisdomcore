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

package org.wisdom.core.incubator;

import org.apache.commons.codec.binary.Hex;
import org.tdf.rlp.RLP;
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.ByteUtil;

public class Incubator {
    @RLP(0)
    private byte[] id;
    @RLP(1)
    private byte[] share_pubkeyhash;
    @RLP(2)
    private byte[] pubkeyhash;
    @RLP(3)
    private byte[] txid_issue;
    @RLP(4)
    private long height;
    @RLP(5)
    private long cost;
    @RLP(6)
    private long interest_amount;
    @RLP(7)
    private long share_amount;
    @RLP(8)
    private long last_blockheight_interest;
    @RLP(9)
    private long last_blockheight_share;
    @RLP(10)
    private int days;

    public Incubator(){}

    public Incubator(byte[] share_pubkeyhash, byte[] pubkeyhash, byte[] txid_issue, long height, long cost, long interest_Amount, long share_Amount, long last_BlockHeight_Interest, long last_BlockHeight_Share) {
        this.share_pubkeyhash = share_pubkeyhash;
        this.pubkeyhash = pubkeyhash;
        this.txid_issue = txid_issue;
        this.height = height;
        this.cost = cost;
        this.interest_amount = interest_Amount;
        this.share_amount = share_Amount;
        this.last_blockheight_interest = last_BlockHeight_Interest;
        this.last_blockheight_share = last_BlockHeight_Share;
    }

    public Incubator(byte[] pubkeyhash, byte[] txid_issue, long height, long cost, long interest_Amount,long last_BlockHeight_Interest,int days){
        this.pubkeyhash = pubkeyhash;
        this.txid_issue = txid_issue;
        this.height = height;
        this.cost = cost;
        this.interest_amount = interest_Amount;
        this.last_blockheight_interest = last_BlockHeight_Interest;
        this.days=days;
    }

    public Incubator(byte[] share_pubkeyhash,byte[] txid_issue,long height, long cost, int days, long share_Amount,long last_BlockHeight_Share){
        this.share_pubkeyhash = share_pubkeyhash;
        this.txid_issue = txid_issue;
        this.height = height;
        this.cost = cost;
        this.days = days;
        this.share_amount = share_Amount;
        this.last_blockheight_share = last_BlockHeight_Share;
    }

    public String getTxhash(){
        return Hex.encodeHexString(this.txid_issue);
    }

    public String getIdHexString(){
        return Hex.encodeHexString(getId());
    }

    public byte[] getId() {
        return ByteUtil.merge(txid_issue,BigEndian.encodeUint32(height));
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getShare_pubkeyhash() {
        return share_pubkeyhash;
    }

    public void setShare_pubkeyhash(byte[] share_pubkeyhash) {
        this.share_pubkeyhash = share_pubkeyhash;
    }

    public byte[] getPubkeyhash() {
        return pubkeyhash;
    }

    public void setPubkeyhash(byte[] pubkeyhash) {
        this.pubkeyhash = pubkeyhash;
    }

    public byte[] getTxid_issue() {
        return txid_issue;
    }

    public void setTxid_issue(byte[] txid_issue) {
        this.txid_issue = txid_issue;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }


    public long getInterest_amount() {
        return interest_amount;
    }

    public void setInterest_amount(long interest_amount) {
        this.interest_amount = interest_amount;
    }

    public long getShare_amount() {
        return share_amount;
    }

    public void setShare_amount(long share_amount) {
        this.share_amount = share_amount;
    }

    public long getLast_blockheight_interest() {
        return last_blockheight_interest;
    }

    public void setLast_blockheight_interest(long last_blockheight_interest) {
        this.last_blockheight_interest = last_blockheight_interest;
    }

    public long getLast_blockheight_share() {
        return last_blockheight_share;
    }

    public void setLast_blockheight_share(long last_blockheight_share) {
        this.last_blockheight_share = last_blockheight_share;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Incubator copy(){
        Incubator incubator = new Incubator(share_pubkeyhash, pubkeyhash, txid_issue, height, cost, interest_amount, share_amount, last_blockheight_interest, last_blockheight_share);
        incubator.setDays(days);
        incubator.setId(id);
        return incubator;
    }
}