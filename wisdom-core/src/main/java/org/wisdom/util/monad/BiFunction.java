package org.wisdom.util.monad;

public interface BiFunction<T, U, R, E extends Exception> {
    R apply(T t, U u) throws E;
}
