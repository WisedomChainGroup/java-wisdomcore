package org.wisdom.util.trie;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class Codecs {
    static Codec IDENTITY = Codec.newInstance(Function.identity(), Function.identity());

    /**
     * Converter from string to byte array and vice versa
     */
    public static final Codec<String, byte[]> STRING = Codec
            .newInstance(
                    (x) -> x.getBytes(StandardCharsets.UTF_8),
                    x -> new String(x, StandardCharsets.UTF_8)
            );

}
