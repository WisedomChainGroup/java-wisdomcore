package org.wisdom.store;

import org.wisdom.db.DBSettings;

import java.util.Set;

public interface DatabaseStore extends BatchStore<byte[], byte[]> {
    /**
     * Initializes DB (open table, connection, etc)
     * with default {@link DBSettings#DEFAULT}
     */
    void init();

    /**
     * Initializes DB (open table, connection, etc)
     * @param settings  DB settings
     */
    void init(DBSettings settings);

    /**
     * @return true if DB connection is alive
     */
    boolean isAlive();

    /**
     * Closes the DB table/connection
     */
    void close();

    /**
     * If supported, retrieves a value using a key prefix.
     * Prefix extraction is meant to be done on the implementing side.<br>
     *
     * @param key a key for the lookup
     * @param prefixBytes prefix length in bytes
     * @return first value picked by prefix lookup over DB or null if there is no match
     * @throws RuntimeException if operation is not supported
     */
    byte[] prefixLookup(byte[] key, int prefixBytes);


    /**
     * @return DB keys if this option is available
     * @throws RuntimeException if the method is not supported
     */
    @Override
    Set<byte[]> keySet();

    /**
     * Closes database, destroys its data and finally runs init()
     */
    @Override
    void clear();
}
