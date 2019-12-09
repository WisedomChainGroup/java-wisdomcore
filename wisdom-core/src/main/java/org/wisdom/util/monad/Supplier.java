package org.wisdom.util.monad;

public interface Supplier<T, E extends Exception> {
    T get() throws E;
}
