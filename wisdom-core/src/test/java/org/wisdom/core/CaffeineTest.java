package org.wisdom.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CaffeineTest {
    private Cache<String, String> cache;

    @Before
    public void before(){
        cache = Caffeine
                .newBuilder()
                .build();
    }

    @Test
    public void test0(){
        cache.get("1", (x) -> null);
        assert cache.get("1", (x) -> "1").equals("1");
    }
}
