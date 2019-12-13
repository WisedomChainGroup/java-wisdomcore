package org.wisdom.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.store.DatabaseStore;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DatabaseStoreFactory {

    private String directory;

    private int maxFiles;

    private final Map<String, DatabaseStore> stores = new HashMap<>();

    public DatabaseStoreFactory(@Value("${wisdom.database.directory}") String directory,
                                @Value("${max-open-files}") int maxFiles) {
        this.directory = directory;
        this.maxFiles = maxFiles;
    }

    public DatabaseStore create(String name, boolean reset) {
        if(stores.containsKey(name)) {
            return stores.get(name);
        }

        DatabaseStore store;
        switch (name.trim().toLowerCase()) {
            case "memory":
                store = new MemoryDatabaseStore(name);
                break;
            case "leveldb":
            default:
                store = new Leveldb(directory, name, maxFiles);
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
