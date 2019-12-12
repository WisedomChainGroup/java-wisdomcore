package org.wisdom.db;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.util.FileUtil;
import org.wisdom.util.trie.DBSettings;
import org.wisdom.util.trie.DatabaseStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

@Slf4j
@Component
public class Leveldb implements DatabaseStore<byte[],byte[]>{

    // subdirectory
    private String name;
    // parent directory
    private String directory;

    private DB db;
    private DBSettings dbSettings;
    private boolean alive;

    private ReadWriteLock resetDbLock = new ReentrantReadWriteLock();

    public Leveldb(@Value("${wisdom.database.directory}") String directory,
                    @Value("${wisdom.database.name}") String name,
                   @Value("${max-open-files}") int maxfiles) {
        this.directory = directory;
        this.name = name;
        init(DBSettings.newInstance()
                .withMaxOpenFiles(maxfiles)
                .withMaxThreads(Math.max(1, Runtime.getRuntime().availableProcessors() / 2)));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public void init() {
        init(DBSettings.DEFAULT);
    }

    @Override
    public void init(DBSettings settings) {
        this.dbSettings = settings;
        resetDbLock.writeLock().lock();
        try {
            log.debug("~> LevelDbDataSource.init(): " + name);

            if (isAlive()) return;

            if (name == null) throw new NullPointerException("no name set to the db");

            Options options = new Options();
            options.createIfMissing(true);
            options.compressionType(CompressionType.NONE);
            options.blockSize(10 * 1024 * 1024);
            options.writeBufferSize(10 * 1024 * 1024);
            options.cacheSize(0);
            options.paranoidChecks(true);
            options.verifyChecksums(true);
            options.maxOpenFiles(settings.getMaxOpenFiles());

            try {
                log.debug("Opening database");
                final Path dbPath = getPath();
                if (!Files.isSymbolicLink(dbPath.getParent())) Files.createDirectories(dbPath.getParent());

                log.debug("Initializing new or existing database: '{}'", name);
                try {
                    db = factory.open(dbPath.toFile(), options);
                } catch (IOException e) {
                    // database could be corrupted
                    // exception in std out may look:
                    // org.fusesource.leveldbjni.internal.NativeDB$DBException: Corruption: 16 missing files; e.g.: /Users/stan/ethereumj/database-test/block/000026.ldb
                    // org.fusesource.leveldbjni.internal.NativeDB$DBException: Corruption: checksum mismatch
                    if (e.getMessage().contains("Corruption:")) {
                        log.warn("Problem initializing database.", e);
                        log.info("LevelDB database must be corrupted. Trying to repair. Could take some time.");
                        factory.repair(dbPath.toFile(), options);
                        log.info("Repair finished. Opening database again.");
                        db = factory.open(dbPath.toFile(), options);
                    } else {
                        // must be db lock
                        // org.fusesource.leveldbjni.internal.NativeDB$DBException: IO error: lock /Users/stan/ethereumj/database-test/state/LOCK: Resource temporarily unavailable
                        throw e;
                    }
                }

                alive = true;
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
                throw new RuntimeException("Can't initialize database", ioe);
            }
            log.debug("<~ LevelDbDataSource.init(): " + name);
        } finally {
            resetDbLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void close() {
        resetDbLock.writeLock().lock();
        try {
            if (!isAlive()) return;

            try {
                log.debug("Close db: {}", name);
                db.close();

                alive = false;
            } catch (IOException e) {
                log.error("Failed to find the db file on the close: {} ", name);
            }
        } finally {
            resetDbLock.writeLock().unlock();
        }
    }

    @Override
    public Set<byte[]> keys() throws RuntimeException {
        resetDbLock.readLock().lock();
        try {
            try (DBIterator iterator = db.iterator()) {
                Set<byte[]> result = new HashSet<>();
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    result.add(iterator.peekNext().getKey());
                }
                return result;
            } catch (IOException e) {
                log.error("Unexpected", e);
                throw new RuntimeException(e);
            }
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public void reset() {
        close();
        FileUtil.recursiveDelete(getPath().toString());
        init(dbSettings);
    }

    @Override
    public byte[] prefixLookup(byte[] key, int prefixBytes) {
        throw new RuntimeException("LevelDbDataSource.prefixLookup() is not supported");
    }

    @Override
    public void putAll(Map rows) {
        resetDbLock.readLock().lock();
        try {
            try {
                updateBatchInternal(rows);
            } catch (Exception e) {
                log.error("Error, retrying one more time...", e);
                // try one more time
                try {
                    updateBatchInternal(rows);
                } catch (Exception e1) {
                    log.error("Error", e);
                    throw new RuntimeException(e);
                }
            }
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    private void updateBatchInternal(Map<byte[], byte[]> rows) throws IOException {
        try (WriteBatch batch = db.createWriteBatch()) {
            for (Map.Entry<byte[], byte[]> entry : rows.entrySet()) {
                if (entry.getValue() == null) {
                    batch.delete(entry.getKey());
                } else {
                    batch.put(entry.getKey(), entry.getValue());
                }
            }
            db.write(batch);
        }
    }

    @Override
    public Optional<byte[]> get(byte[] key) {
        resetDbLock.readLock().lock();
        try {
            try {
                byte[] ret = db.get(key);
                return Optional.ofNullable(ret);
            } catch (DBException e) {
                log.warn("Exception. Retrying again...", e);
                byte[] ret = db.get(key);
                return Optional.ofNullable(ret);
            }
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public void put(byte[] key, byte[] value) {
        resetDbLock.readLock().lock();
        try {
            db.put(key, value);
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public void putIfAbsent(byte[] key, byte[] value) {
        resetDbLock.readLock().lock();
        try {
            if (db.get(key) != null) {
                return;
            }
            db.put(key, value);
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public void remove(byte[] key) {
        resetDbLock.readLock().lock();
        try {
            db.delete(key);
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public Set keySet() {
        resetDbLock.readLock().lock();
        try {
            try (DBIterator iterator = db.iterator()) {
                Set<byte[]> result = new HashSet<>();
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    result.add(iterator.peekNext().getKey());
                }
                return result;
            } catch (IOException e) {
                log.error("Unexpected", e);
                throw new RuntimeException(e);
            }
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public Collection values() {
        resetDbLock.readLock().lock();
        try {
            DBIterator iterator = db.iterator();
            List<byte[]> result = new ArrayList<>();
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                result.add(iterator.peekNext().getValue());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(byte[] bytes) {
        resetDbLock.readLock().lock();
        try {
            return db.get(bytes) != null;
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        resetDbLock.readLock().lock();
        int res = 0;
        try {
            DBIterator iterator = db.iterator();
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                res++;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        resetDbLock.readLock().lock();
        try {
            DBIterator iterator = db.iterator();
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        reset();
    }

    private Path getPath() {
        return Paths.get(directory, name);
    }
}
