package org.wisdom.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.util.trie.DatabaseStore;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DatabaseStoreFactory {

    private String directory;

    private int maxFiles;

    private static final List<DatabaseStore> STORES_LIST = new ArrayList<>();

    public DatabaseStoreFactory(@Value("${wisdom.database.directory}") String directory,
                                @Value("${max-open-files}") int maxFiles) {
        this.directory = directory;
        this.maxFiles = maxFiles;
    }

    public DatabaseStore create(String name, boolean reset) {
        DatabaseStore store = null;
        switch (name.trim().toLowerCase()) {
            case "storedb":
                // store = new MemoryDatabaseStore();
                break;
            case "rootdb":
            case "deletedb":
            default:
                store = new Leveldb(directory, name, maxFiles);
                break;
        }
        STORES_LIST.add(store);
        if (reset) {
            store.clear();
        }
        return store;
    }

    @PreDestroy
    public void destroy() {
        STORES_LIST.forEach(DatabaseStore::close);
    }

}
