package org.wisdom.util.monad;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @param <T> Functor for functionally exception handling
 */
public class Monad<T, E extends Exception> {
    private T data;
    private E error;
    private List<Runnable> cleaners;

    private Monad(T data, E error) {
        this.data = data;
        this.error = error;
        this.cleaners = new LinkedList<>();
    }

    private Monad(T data, E error, List<Runnable> cleaners) {
        this.data = data;
        this.error = error;
        this.cleaners = cleaners;
    }

    /**
     * @param dataType generic parameter
     * @return an empty monad, which contains null pointer exception
     */
    public static <U> Monad<U, Exception> empty(Class<U> dataType) {
        return new Monad<>(null, new NullPointerException());
    }

    /**
     * @param dataType generic parameter
     * @param supplier exception provider
     * @return an empty monad, which contains exception provided by supplier
     */
    public static <U, V extends Exception> Monad<U, V> empty(Class<U> dataType, java.util.function.Supplier<V> supplier) {
        Objects.requireNonNull(supplier);
        return new Monad<>(null, Objects.requireNonNull(supplier.get()));
    }

    /**
     * a -> M a
     *
     * @param data non-nullable object
     * @return an empty monad if data is null or else a presented monad
     */
    public static <U> Monad<U, Exception> of(U data) {
        return of(data, e -> e);
    }

    /**
     * a -> M a
     *
     * @param data    nullable object
     * @param handler handle null exception when object is null
     * @return an empty monad if data is null or else a presented monad
     */
    public static <U, V extends Exception> Monad<U, V> of(U data, Function<Exception, V> handler) {
        Objects.requireNonNull(handler);
        try {
            return new Monad<>(Objects.requireNonNull(data), null);
        } catch (Exception e) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(e)));
        }
    }

    /**
     * a -> M a
     *
     * @param supplier
     * @param <U>
     * @return
     */
    public static <U> Monad<U, Exception> supply(Supplier<U, ? extends Exception> supplier) {
        return supply(supplier, e -> e);
    }

    /**
     * a -> M a
     *
     * @param supplier
     * @param handler
     * @param <U>
     * @param <V>
     * @return
     */
    public static <U, V extends Exception> Monad<U, V> supply(Supplier<U, ? extends Exception> supplier,
                                                              Function<Exception, V> handler) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(handler);
        try {
            return new Monad<>(Objects.requireNonNull(supplier.get()), null);
        } catch (Exception e) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(e)));
        }
    }

    /**
     * M a -> (a -> b) -> M b
     *
     * @param applier
     * @param <U>
     * @return
     */
    public <U> Monad<U, Exception> map(Applier<T, U, ? extends Exception> applier) {
        return map(applier, e -> e);
    }

    /**
     * M a -> (a -> b) -> M b
     *
     * @param applier
     * @param handler
     * @param <U>
     * @param <V>
     * @return
     */
    public <U, V extends Exception> Monad<U, V> map(Applier<T, U, ?> applier, Function<Exception, V> handler) {
        Objects.requireNonNull(handler);
        Objects.requireNonNull(applier);
        if (error != null) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(error)), cleaners);
        }
        try {
            return new Monad<>(Objects.requireNonNull(applier.apply(data)), null, cleaners);
        } catch (Exception e) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(e)), cleaners);
        }
    }

    /**
     * M a -> a -> M a
     *
     * @param consumer
     * @param handler
     * @param <V>
     * @return
     */
    public <V extends Exception> Monad<T, V> ifPresent(Consumer<T, ? extends Exception> consumer,
                                                       Function<Exception, V> handler) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(handler);
        if (error != null) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(error)), cleaners);
        }
        try {
            consumer.consume(data);
            return new Monad<>(data, null, cleaners);
        } catch (Exception e) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(e)), cleaners);
        }
    }

    /**
     * M a -> a -> M a
     *
     * @param consumer
     * @return
     */
    public Monad<T, Exception> ifPresent(Consumer<T, ? extends Exception> consumer) {
        return ifPresent(consumer, e -> e);
    }


    /**
     * M a -> a -> M b -> M b
     *
     * @param function
     * @param <U>
     * @return
     */
    public <U> Monad<U, Exception> flatMap(Function<T, Monad<U, ? extends Exception>> function) {
        return flatMap(function, e -> e);
    }


    /**
     * M a -> M b -> a -> b -> c -> M c
     *
     * @param other
     * @param function
     * @param <U>
     * @param <V>
     * @return
     */
    public <U, V> Monad<V, Exception> compose(Monad<U, ? extends Exception> other,
                                              BiFunction<T, U, V, ? extends Exception> function) {
        return compose(other, function, e -> e);
    }

    /**
     * M a -> M b -> a -> b -> c -> M c
     *
     * @param other
     * @param function
     * @param handler
     * @param <U>
     * @param <V>
     * @param <R>
     * @return
     */
    public <U, V, R extends Exception> Monad<V, R> compose(Monad<U, ? extends Exception> other,
                                                           BiFunction<T, U, V, ? extends Exception> function, Function<Exception, R> handler) {
        Objects.requireNonNull(other);
        Objects.requireNonNull(function);
        Objects.requireNonNull(handler);
        if (error != null) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(error)), cleaners);
        }
        List<Runnable> tmp = new LinkedList<>(cleaners);
        tmp.addAll(other.cleaners);
        if (other.error != null) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(error)), tmp);
        }
        try {
            return new Monad<>(Objects.requireNonNull(function.apply(data, other.data)), null, tmp);
        } catch (Exception e) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(e)), tmp);
        }
    }

    /**
     * M a -> (a -> M b) -> M b
     *
     * @param function 
     * @param handler
     * @param <U>
     * @param <V>
     * @return
     */
    public <U, V extends Exception> Monad<U, V> flatMap(Function<T, Monad<U, ? extends Exception>> function,
                                                        Function<Exception, V> handler) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(handler);
        if (error != null) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(error)), cleaners);
        }
        try {
            Monad<U, ?> res = Objects.requireNonNull(function.apply(data));
            List<Runnable> tmp = new LinkedList<>(cleaners);
            tmp.addAll(res.cleaners);
            return new Monad<>(res.data, null, tmp);
        } catch (Exception e) {
            return new Monad<>(null, Objects.requireNonNull(handler.apply(e)), cleaners);
        }
    }

    /**
     * @param function mapper to convert wrapped exception
     * @return self with mapped exception
     */
    public <V extends Exception> Monad<T, V> handle(Function<? super E, V> function) {
        Objects.requireNonNull(function);
        if (error != null) {
            return new Monad<>(null, Objects.requireNonNull(function.apply(error)), cleaners);
        }
        return new Monad<>(data, null, cleaners);
    }

    /**
     *
     * @param consumer invoke when error occurs
     * @return self
     */
    public Monad<T, E> except(java.util.function.Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);
        return handle((e) -> {
            consumer.accept(e);
            return e;
        });
    }

    /**
     * @param consumer the clean up method of resource
     * @return self
     */
    public Monad<T, E> onClean(Consumer<T, ? extends Exception> consumer) {
        Objects.requireNonNull(consumer);
        this.cleaners.add(() -> consumer.consume(data));
        return this;
    }

    /**
     * @return invoke onClean function of every resource
     */
    public Monad<T, E> cleanUp() {
        this.cleaners.forEach(p -> {
            try {
                p.eval();
            } catch (Exception ignored) {
            }
        });
        this.cleaners = new LinkedList<>();
        return this;
    }

    /**
     * return value and clean resources
     * @param data complement value
     * @return data when error occurs
     */
    public T orElse(T data) {
        cleanUp();
        Objects.requireNonNull(data);
        if (error != null) {
            return data;
        }
        return this.data;
    }

    /**
     * return value and clean resources
     * @param supplier provide the complement value
     * @return supplied value when error occurs
     */
    public T orElseGet(java.util.function.Supplier<? extends T> supplier) {
        cleanUp();
        Objects.requireNonNull(supplier);
        if (error != null) {
            return Objects.requireNonNull(supplier.get());
        }
        return data;
    }

    /**
     * return value and clean resources
     * @return wrapped value
     * @throws E exception if error occurs
     */
    public T get() throws E {
        cleanUp();
        if (error != null) {
            throw error;
        }
        return data;
    }

    /**
     * return value and clean resources
     * @param handler exception to throw
     * @return wrapped value
     */
    public <V extends Exception> T get(Function<? super E, V> handler) throws V {
        cleanUp();
        Objects.requireNonNull(handler);
        if (error != null) {
            throw Objects.requireNonNull(handler.apply(error));
        }
        return data;
    }

    public boolean isPresent() {
        return error == null;
    }
}
