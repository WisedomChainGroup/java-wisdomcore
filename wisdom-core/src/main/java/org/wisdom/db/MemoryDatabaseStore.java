package org.wisdom.db;

import org.wisdom.store.ByteArrayMapStore;
import org.wisdom.store.DatabaseStore;

import java.util.Map;
import java.util.Optional;

public class MemoryDatabaseStore extends ByteArrayMapStore<byte[]> implements DatabaseStore {
    private final String name;

    public MemoryDatabaseStore(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init(DBSettings settings) {

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public Optional<byte[]> prefixLookup(byte[] key, int prefixBytes) {
        throw new RuntimeException("not supported");
    }

    @Override
    public void putAll(Map<byte[], byte[]> rows) {
        getMap().putAll(rows);
    }
}
