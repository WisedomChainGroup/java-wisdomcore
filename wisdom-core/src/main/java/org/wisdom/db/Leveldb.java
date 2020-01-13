package org.wisdom.db;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.*;
import org.tdf.common.store.DBSettings;
import org.tdf.common.store.DatabaseStore;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

@Slf4j
@Deprecated // use org.tdf.common.store.LevelDb instead
public class Leveldb implements DatabaseStore {

    // sub directory under database directory
    @Getter
    private final String name;
    // database directory
    @Getter
    private final String directory;

    private DB db;
    private DBSettings dbSettings;
    private boolean alive;

    private ReadWriteLock resetDbLock = new ReentrantReadWriteLock();

    public Leveldb(String directory,
                    String name) {
        this.directory = directory;
        this.name = name;
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
        return alive;
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

    public Set<byte[]> keySet() throws RuntimeException {
        resetDbLock.readLock().lock();
        try {
            try (DBIterator iterator = db.iterator()) {
                Set<byte[]> result = new ByteArraySet();
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
    public void clear() {
        close();
        FileUtil.recursiveDelete(getPath().toString());
        init(dbSettings);
    }

    @Override
    public Optional<byte[]> prefixLookup(byte[] key, int prefixBytes) {
        throw new RuntimeException("LevelDbDataSource.prefixLookup() is not supported");
    }

    @Override
    public void putAll(Collection<? extends Map.Entry<? extends byte[], ? extends byte[]>> collection) {
        
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
            if(containsKey(key)) return;
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


    public Collection<byte[]> values() {
        Set<byte[]> ret = new ByteArraySet();
        keySet().stream().map(this::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(ret::add);
        return ret;
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
        return keySet().size();
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
            throw new RuntimeException(e);
        } finally {
            resetDbLock.readLock().unlock();
        }
    }

    private Path getPath() {
        return Paths.get(directory, name);
    }

    @Override
    public void traverse(BiFunction<? super byte[], ? super byte[], Boolean> biFunction) {

    }
}
