package org.wisdom.db;

import lombok.extern.slf4j.Slf4j;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.common.store.*;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DatabaseStoreFactory {

    private String directory;

    private int maxFiles;

    private String type;

    private final Map<String, DatabaseStore> stores = new HashMap<>();

    public DatabaseStoreFactory(
            @Value("${wisdom.database.directory}") String directory,
            @Value("${max-open-files}") int maxFiles,
            @Value("${wisdom.database.type}") String type
    ) {
        this.directory = directory;
        this.maxFiles = maxFiles;
        this.type = type == null ? "" : type;
    }

    public DatabaseStore create(String name, boolean reset) {
        if(stores.containsKey(name)) {
            return stores.get(name);
        }

        DatabaseStore store;
        switch (type.trim().toLowerCase()) {
            case "memory":
                store = new MemoryDatabaseStore();
                break;
            case "rocksdb":
                store = new RocksDb(directory, name);
                break;
            case "leveldb-iq80":
                store = new LevelDb(Iq80DBFactory.factory, directory, name);
                break;
            case "leveldb":
            default:
                store = new LevelDb(JniDBFactory.factory, directory, name);
                break;
        }
        store.init(DBSettings.newInstance()
                .withMaxOpenFiles(maxFiles)
                .withMaxThreads(Math.max(1, Runtime.getRuntime().availableProcessors() / 2)));
        stores.put(name, store);
        if (reset) {
            store.clear();
        }
        return store;
    }

    @PreDestroy
    public void destroy() {
        stores.values().forEach(DatabaseStore::close);
    }

}
