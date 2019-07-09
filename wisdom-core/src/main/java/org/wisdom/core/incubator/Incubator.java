package org.wisdom.core.incubator;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.encoding.BigEndian;
import org.wisdom.util.ByteUtil;

public class Incubator {
    private byte[] id;
    private byte[] share_pubkeyhash;
    private byte[] pubkeyhash;
    private byte[] txid_issue;
    private long height;
    private long cost;
    private long interest_amount;
    private long share_amount;
    private long last_blockheight_interest;
    private long last_blockheight_share;

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

}
