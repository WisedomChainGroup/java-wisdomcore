package org.wisdom.util.monad;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

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
     * @param type generic parameter
     * @return an empty monad, which contains null pointer exception
     */
    public static <U> Monad<U, Exception> empty(Class<U> type) {
        return new Monad<>(null, new NullPointerException());
    }

    /**
     * a -> M a
     *
     * @param data nullable object
     * @return an empty monad if data is null or else a presented monad
     */
    public static <U> Monad<U, Exception> of(U data) {
        try {
            return new Monad<>(Objects.requireNonNull(data), null);
        } catch (Exception e) {
            return new Monad<>(null, e);
        }
    }

    /**
     * a -> M a
     *
     * @param data nullable object
     * @return an empty monad if data is null or else a presented monad
     */
    public static <R, U extends R> Monad<R, Exception> of(U data, Class<R> type) {
        return of(data);
    }

    /**
     * a -> M a
     *
     * @param supplier
     * @param <U>
     * @return
     */
    public static <U> Monad<U, Exception> supply(Supplier<U, ? extends Exception> supplier) {
        Objects.requireNonNull(supplier);
        try {
            return new Monad<>(Objects.requireNonNull(supplier.get()), null);
        } catch (Exception e) {
            return new Monad<>(null, e);
        }
    }

    public Monad<T, Exception> exceptAs() {
        return new Monad<>(data, error, cleaners);
    }

    public <V extends Exception> Monad<T, V> exceptAs(Function<Exception, ? extends V> function) {
        return new Monad<>(data, function.apply(error), cleaners);
    }

    /**
     * M a -> (a -> b) -> M b
     *
     * @param applier
     * @param <U>
     * @return
     */
    public <U> Monad<U, Exception> map(Applier<? super T, U, ? extends Exception> applier) {
        Objects.requireNonNull(applier);
        if (error != null) {
            return new Monad<>(null, error, cleaners);
        }
        try {
            return new Monad<>(Objects.requireNonNull(applier.apply(data)), null, cleaners);
        } catch (Exception e) {
            return new Monad<>(null, e, cleaners);
        }
    }

    /**
     * M a -> a -> M a
     *
     * @param consumer
     * @return
     */
    public Monad<T, Exception> ifPresent(Consumer<? super T, ? extends Exception> consumer) {
        Objects.requireNonNull(consumer);
        if (error != null) {
            return new Monad<>(null, error, cleaners);
        }
        try {
            consumer.consume(data);
            return new Monad<>(data, null, cleaners);
        } catch (Exception e) {
            return new Monad<>(null, e, cleaners);
        }
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
                                              BiFunction<? super T, U, V, ? extends Exception> function) {
        Objects.requireNonNull(other);
        Objects.requireNonNull(function);
        if (error != null) {
            return new Monad<>(null, error, cleaners);
        }
        List<Runnable> tmp = new LinkedList<>(cleaners);
        tmp.addAll(other.cleaners);
        if (other.error != null) {
            return new Monad<>(null, error, tmp);
        }
        try {
            return new Monad<>(Objects.requireNonNull(function.apply(data, other.data)), null, tmp);
        } catch (Exception e) {
            return new Monad<>(null, e, tmp);
        }
    }

    /**
     * M a -> (a -> M b) -> M b
     *
     * @param function
     * @return
     */
    public <U> Monad<U, Exception> flatMap(Function<? super T, Monad<U, ? extends Exception>> function) {
        Objects.requireNonNull(function);
        if (error != null) {
            return new Monad<>(null, error, cleaners);
        }
        try {
            Monad<U, ?> res = Objects.requireNonNull(function.apply(data));
            List<Runnable> tmp = new LinkedList<>(cleaners);
            tmp.addAll(res.cleaners);
            return new Monad<>(res.data, null, tmp);
        } catch (Exception e) {
            return new Monad<>(null, e, cleaners);
        }
    }


    /**
     * @param consumer invoke when error occurs
     * @return self
     */
    public Monad<T, E> except(java.util.function.Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);
        if (error != null){
            consumer.accept(error);
        }
        return this;
    }

    /**
     * @param consumer the clean up method of resource
     * @return self
     */
    public Monad<T, E> onClean(Consumer<? super T, ? extends Exception> consumer) {
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
     *
     * @param data complement value
     * @return data when error occurs
     */
    public <R extends T> Monad<T, E> orElseOf(R data) {
        cleanUp();
        Objects.requireNonNull(data);
        if (error != null) {
            return new Monad<>(data, null, cleaners);
        }
        return this;
    }

    /**
     * return value and clean resources
     *
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
     *
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
     *
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
     *
     * @return wrapped value
     * @throws E exception if error occurs
     */
    public <V extends Exception> T get(Function<E, V> function) throws V {
        cleanUp();
        Objects.requireNonNull(function);
        if (error != null) {
            throw Objects.requireNonNull(function.apply(error));
        }
        return data;
    }

    /**
     * return value and clean resources
     *
     * @return wrapped value
     */
    public <V extends Exception> T getOrThrow(V v) throws V {
        cleanUp();
        Objects.requireNonNull(v);
        if (error != null) {
            throw v;
        }
        return data;
    }


    public Monad<T, Exception> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (error != null) {
            return exceptAs();
        }
        if (predicate.test(data)) {
            return new Monad<>(data, null, cleaners);
        }
        return new Monad<>(null, error, cleaners);
    }

    public boolean isPresent() {
        return error == null;
    }
}
