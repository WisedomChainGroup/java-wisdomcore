package org.wisdom.vm.abi;

import lombok.RequiredArgsConstructor;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;

import java.util.*;

@RequiredArgsConstructor
public class PrevNonceWrapper implements Map<byte[], Long> {
    private final List<Transaction> transactionList;
    private final Map<byte[], AccountState> stateMap;

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long get(Object key) {
        byte[] k = (byte[]) key;
        long l = transactionList == null ? 0 : transactionList.stream().filter(x -> Arrays.equals(x.getFromPKHash(), k))
                .map(x -> x.nonce).reduce(Long::max).orElse(0L);
        if (l > 0)
            return l;
        AccountState a = stateMap.get(k);
        return a == null ? 0 : a.getNonce();
    }

    @Override
    public Long put(byte[] key, Long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends byte[], ? extends Long> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<byte[]> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Long> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<byte[], Long>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
