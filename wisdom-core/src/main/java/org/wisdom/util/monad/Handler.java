package org.wisdom.util.monad;

public interface Handler<T, E extends Throwable, R, V extends Exception> {
    R handle(T data, E error) throws V;
}
