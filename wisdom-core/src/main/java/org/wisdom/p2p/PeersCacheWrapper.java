package org.wisdom.p2p;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PeersCacheWrapper extends PeersCache{

    private ReadWriteLock readWriteLock;

    public PeersCacheWrapper(String self, String bootstraps, String trusted, boolean enableDiscovery) throws Exception {
        super(self, bootstraps, trusted, enableDiscovery);
        readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public int size() {
        readWriteLock.readLock().lock();
        try{
            return super.size();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean hasPeer(Peer peer) {
        readWriteLock.readLock().lock();
        try{
            return super.hasPeer(peer);
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void pend(Peer peer) {
        readWriteLock.writeLock().lock();
        try{
            super.pend(peer);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void keepPeer(Peer peer) {
        readWriteLock.writeLock().lock();
        try{
            super.keepPeer(peer);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void removePeer(Peer peer) {
        readWriteLock.writeLock().lock();
        try{
            super.removePeer(peer);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Set<HostPort> getUnresolved() {
        readWriteLock.readLock().lock();
        try{
            return super.getUnresolved();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<Peer> getBootstraps() {
        readWriteLock.readLock().lock();
        try{
            return super.getBootstraps();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<Peer> getBlocked() {
        readWriteLock.readLock().lock();
        try{
            return super.getBlocked();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<Peer> getPended() {
        readWriteLock.readLock().lock();
        try{
            return super.getPended();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Peer> getPeers(int limit) {
        readWriteLock.readLock().lock();
        try{
            return super.getPeers(limit);
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<Peer> getPeers() {
        readWriteLock.readLock().lock();
        try{
            return super.getPeers();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void blockPeer(Peer peer) {
        readWriteLock.writeLock().lock();
        try{
            super.blockPeer(peer);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void half(Peer peer) {
        readWriteLock.writeLock().lock();
        try{
            super.half(peer);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void half() {
        readWriteLock.writeLock().lock();
        try{
            super.half();
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<Peer> popPended() {
        readWriteLock.writeLock().lock();
        try{
            return super.popPended();
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isFull() {
        readWriteLock.readLock().lock();
        try{
            return super.isFull();
        }finally {
            readWriteLock.readLock().unlock();
        }
    }
}
