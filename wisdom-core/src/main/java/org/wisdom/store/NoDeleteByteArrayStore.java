package org.wisdom.store;

import lombok.NonNull;
import org.tdf.common.store.NoDeleteStore;
import org.tdf.common.store.Store;

import java.util.Optional;

public class NoDeleteByteArrayStore extends NoDeleteStore<byte[], byte[]> {
    private static final byte[] DUMMY = new byte[]{1};

    public NoDeleteByteArrayStore(Store<byte[], byte[]> delegate) {
        super(delegate);
    }
}
