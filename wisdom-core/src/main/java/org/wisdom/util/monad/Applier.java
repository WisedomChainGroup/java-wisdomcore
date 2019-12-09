package org.wisdom.util.monad;

public interface Applier<T, U, E extends Exception> {
    U apply(T data) throws E;
}
