package org.wisdom.pool;

import org.wisdom.core.account.Transaction;

public class TransPool {

    private Transaction transaction;
    private int state;//0待确认，1已引用，2已确认
    private long datetime;
    private long height;

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public TransPool(Transaction transaction, int state, long datetime) {
        this.transaction = transaction;
        this.state = state;
        this.datetime = datetime;
    }
}
