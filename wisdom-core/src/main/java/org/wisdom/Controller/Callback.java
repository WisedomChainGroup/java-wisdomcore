package org.wisdom.Controller;

public interface Callback<T, V> {
    V call(T v);
}
