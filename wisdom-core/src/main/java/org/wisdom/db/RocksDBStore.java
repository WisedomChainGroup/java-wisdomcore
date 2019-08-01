package org.wisdom.db;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Component
public class RocksDBStore {
    @Resource
    private RocksDB rocksDB;

    public void put(String key, String value) {
        try {
            rocksDB.put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }


    public String get(String key) {
        try {
            byte[] bytes = rocksDB.get(key.getBytes(StandardCharsets.UTF_8));
            if (bytes != null) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void remove(String key) {
        try {
            rocksDB.delete(rocksDB.get(key.getBytes(StandardCharsets.UTF_8)));
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public RocksIterator getIterator(){
        return rocksDB.newIterator();
    }
}
