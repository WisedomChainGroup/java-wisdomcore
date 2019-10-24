package org.wisdom.util.monad;

public interface Runnable<E extends Exception>{
    void eval() throws E;
}