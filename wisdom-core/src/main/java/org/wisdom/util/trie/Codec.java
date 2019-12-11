package org.wisdom.util.trie;

import java.util.function.Function;

public interface Codec<K, V> {
    Function<? super K, ? extends V> getEncoder();

    Function<? super V, ? extends K> getDecoder();

    static <K> Codec<K, K> identity() {
        return (Codec<K, K>) Codecs.IDENTITY;
    }

    static <K, V> Codec<K, V> newInstance(Function<? super K, ? extends V> encoder, Function<? super V, ? extends K> decoder) {
        return new Codec<K, V>() {
            @Override
            public Function<? super K, ? extends V> getEncoder() {
                return encoder;
            }

            @Override
            public Function<? super V, ? extends K> getDecoder() {
                return decoder;
            }
        };
    }
}
