package org.wisdom.db;

import org.tdf.rlp.RLP;
import org.wisdom.account.PublicKeyHash;

import java.util.Objects;

public class Vote {
    @RLP(0)
    private PublicKeyHash from;
    @RLP(1)
    private long amount;
    @RLP(2)
    private long accumulated;

    public Vote(){
    }

    public Vote(PublicKeyHash from, long amount, long accumulated) {
        this.from = from;
        this.amount = amount;
        this.accumulated = accumulated;
    }

    public PublicKeyHash getFrom() {
        return from;
    }

    public void setFrom(PublicKeyHash from) {
        this.from = from;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return amount == vote.amount &&
                accumulated == vote.accumulated &&
                from.equals(vote.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, amount, accumulated);
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(long accumulated) {
        this.accumulated = accumulated;
    }



}
