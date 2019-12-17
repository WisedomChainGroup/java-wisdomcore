package org.wisdom.pool;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class TraceCeoAddress {

//    private final String CeoFrom = "ca33fead17d601b83e220927703a703c6d1ea1c9697375fabf0bf307f003f999";
    private final String CeoFrom = "833fd9fda2273787fbbdb789374e1fd8f431c6062383428eb3b0030f58a3a11f";

    private TreeSet<NonceInfo> treeSetQueued;
    private TreeSet<NonceInfo> treeSetPend;

    private ReadWriteLock readWriteLock=new ReentrantReadWriteLock();

    public TraceCeoAddress() {
        treeSetQueued = new TreeSet<>();
        treeSetPend = new TreeSet<>();
    }

    public void addQueued(String from, long nonce) {
        readWriteLock.writeLock().lock();
        try{
            if (CeoFrom.equals(from)) {
                treeSetQueued.add(new NonceInfo(nonce, DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")));
            }
        }finally {
            readWriteLock.writeLock().unlock();
        }

    }

    public void addPend(String from, long nonce) {
        readWriteLock.writeLock().lock();
        try{
            if (CeoFrom.equals(from)) {
                treeSetPend.add(new NonceInfo(nonce, DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")));
            }
        }finally {
            readWriteLock.writeLock().unlock();
        }

    }

    public TreeSet<NonceInfo> getTreeSetQueued() {
        readWriteLock.readLock().lock();
        try{
            return this.treeSetQueued;
        }finally {
            readWriteLock.readLock().unlock();
        }

    }

    public TreeSet<NonceInfo> getTreeSetPend() {
        readWriteLock.readLock().lock();
        try{
            return this.treeSetPend;
        }finally {
            readWriteLock.readLock().unlock();
        }

    }

    public void clean() {
        treeSetQueued = new TreeSet<>();
        treeSetPend = new TreeSet<>();
    }

    public TreeSet<Long> getNoContinuous(TreeSet<NonceInfo> treeSet) {
        TreeSet<Long> longTreeSet = new TreeSet<>();
        if (treeSet != null && treeSet.size() != 0) {
            long indexs = treeSet.first().getNonce();
            Iterator<NonceInfo> itSet = treeSet.iterator();
            while (itSet.hasNext()) {
                long nonces = itSet.next().getNonce();
                while (indexs != nonces) {
                    longTreeSet.add(indexs);
                    indexs++;
                }
                indexs++;
            }
        }
        return longTreeSet;
    }

    public static class NonceInfo  implements Comparable<NonceInfo>{
        private long nonce;
        private String datetime;

        public NonceInfo() {
        }

        public NonceInfo(long nonce, String datetime) {
            this.nonce = nonce;
            this.datetime = datetime;
        }

        public long getNonce() {
            return nonce;
        }

        public void setNonce(long nonce) {
            this.nonce = nonce;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        @Override
        public int compareTo(NonceInfo o) {
            return (int)(this.nonce - o.nonce);
        }
    }
}
