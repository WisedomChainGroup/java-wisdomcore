package org.wisdom.store;

import lombok.NonNull;
import org.tdf.common.store.NoDeleteStore;
import org.tdf.common.store.Store;

import java.util.Optional;

public class NoDeleteByteArrayStore extends NoDeleteStore<byte[], byte[]> {
    private static final byte[] DUMMY = new byte[]{1};

    public NoDeleteByteArrayStore(Store<byte[], byte[]> delegate, Store<byte[], byte[]> removed) {
        super(delegate, removed);
    }

    @Override
    public void remove(byte @NonNull [] bytes) {
        Optional<byte[]> o = get(bytes);
        if(!o.isPresent()) return;
        // we not remove k, just add it to a cache
        // when flush() called, we remove k in the cache
        getRemoved().put(bytes, DUMMY);
    }
}
