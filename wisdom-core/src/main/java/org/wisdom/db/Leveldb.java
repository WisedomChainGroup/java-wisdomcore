package org.wisdom.db;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class Leveldb {

    private Charset CHARSET;
    private File file;
    private Options options;

    public Leveldb(@Value("${wisdom.cache-dir}") String cacheDir) {
        this.CHARSET = StandardCharsets.UTF_8;
        if (cacheDir == null || cacheDir.equals("")) {
            cacheDir = System.getProperty("user.dir") + File.separator + "leveldb";
        }
        this.file = new File(cacheDir);
        this.options = new Options();
    }

    public void addPoolDb(String key, String noncepoolval) {
        write(key.getBytes(CHARSET), noncepoolval.getBytes(CHARSET));
    }

    public String readPoolDb(String key) {
        byte[] res = read(key.getBytes(CHARSET));
        if (res != null && res.length > 0) {
            return new String(res);
        }
        return "";
    }

    public void write(byte[] key, byte[] value) {
        DB db = null;
        try {
            DBFactory factory = new Iq80DBFactory();
            db = factory.open(file, options);
            // 会写入磁盘中
            db.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] read(byte[] key) {
        DB db = null;
        try {
            DBFactory factory = new Iq80DBFactory();
            db = factory.open(file, options);
            return db.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
