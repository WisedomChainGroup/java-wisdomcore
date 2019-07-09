package org.wisdom.core;

import org.wisdom.core.account.Transaction;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class TransactionPool {
    public static class TransactionComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return (int) (o2.getFee() - o1.getFee());
        }
    }

    private ReadWriteLock lock;

    private PriorityQueue<Transaction> pq;
    private Set<String> indices;

    public TransactionPool() {
        this.pq = new PriorityQueue<>(1000, new TransactionComparator());
        this.indices = new HashSet<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public void add(Transaction... txs) {
        lock.writeLock().lock();
        try {
            if (txs == null) {
                return;
            }
            for (Transaction tx : txs) {
                if(indices.contains(tx.getHashHexString())){
                    continue;
                }
                pq.add(tx);
                indices.add(tx.getHashHexString());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean has(String txHash){
        return indices.contains(txHash);
    }

    public Transaction poll() {
        if (pq.size() == 0){
            return null;
        }
        lock.writeLock().lock();
        try {
            Transaction tx = pq.poll();
            indices.remove(tx.getHashHexString());
            return tx;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return pq.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Transaction> getAll(){
        lock.readLock().lock();
        try {
            return Arrays.asList(pq.toArray(new Transaction[]{}));
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        Transaction t1 = new Transaction();
        t1.type = Transaction.Type.DEPOSIT.ordinal();
        t1.gasPrice = 1000;
        Transaction t2 = new Transaction();
        t2.type = Transaction.Type.DEPOSIT.ordinal();
        t2.gasPrice = 2000;
        TransactionPool pol = new TransactionPool();
        pol.add(t1, t2);
        System.out.println(pol.poll().getFee());
        System.out.println(pol.has(t1.getHashHexString()));
        System.out.println(pol.poll().getFee());
        System.out.println(pol.size());
    }
}
