package org.wisdom.util.trie;

import java.util.Set;

public interface DatabaseStore<K, V> extends BatchStore<K, V>{
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
     * @return DB keys if this option is available
     * @throws RuntimeException if the method is not supported
     */
    Set<byte[]> keys() throws RuntimeException;

    /**
     * Closes database, destroys its data and finally runs init()
     */
    void reset();

    /**
     * If supported, retrieves a value using a key prefix.
     * Prefix extraction is meant to be done on the implementing side.<br>
     *
     * @param key a key for the lookup
     * @param prefixBytes prefix length in bytes
     * @return first value picked by prefix lookup over DB or null if there is no match
     * @throws RuntimeException if operation is not supported
     */
    V prefixLookup(byte[] key, int prefixBytes);
}
