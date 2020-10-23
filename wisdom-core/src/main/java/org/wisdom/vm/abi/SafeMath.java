package org.wisdom.vm.abi;

public class SafeMath {
    public static long add(long x, long y) {
        if (x < 0 || y < 0)
            throw new RuntimeException("math overflow");
        long z = x + y;
        if (z < x || z < y)
            throw new RuntimeException("math overflow");
        return z;
    }

    public static long sub(long x, long y) {
        if (x < 0 || y < 0)
            throw new RuntimeException("math overflow");
        long z = x - y;
        if (z > x || z < 0)
            throw new RuntimeException("math overflow");
        return z;
    }

    public static long mul(long x, long y) {
        if (x < 0 || y < 0)
            throw new RuntimeException("math overflow");
        if (y == 0 || x == 0)
            return 0;
        long z = x * y;
        if (z / y != x)
            throw new RuntimeException("math overflow");
        return z;
    }

    public static void main(String[] args) {
        // 正常加法
        assertTrue(add(Long.MAX_VALUE, 0) == Long.MAX_VALUE);
        assertTrue(add(0, Long.MAX_VALUE) == Long.MAX_VALUE);
        assertTrue(add(Long.MAX_VALUE - 1, 1) == Long.MAX_VALUE);
        assertTrue(add(0, 0) == 0);
        assertTrue(add(0, 1) ==  1);
        assertTrue(add(1, 0) == 1);
        assertTrue(add(1, 1) == 2);

        // 加法溢出
        assertException(() -> add(1, Long.MAX_VALUE));
        assertException(() -> add(Long.MAX_VALUE, 1));
        assertException(() -> add(-1, 0));

        // 普通减法
        assertTrue(sub(1, 0) == 1);
        assertException(() -> sub(0, 1));
        assertTrue(sub(Long.MAX_VALUE, 1) == Long.MAX_VALUE - 1);
        assertTrue(sub(Long.MAX_VALUE, 0) == Long.MAX_VALUE);

        // 减法溢出
        assertException(() -> sub(1, 2));

        assertException(() -> sub(-1, 0));

        assertTrue(mul(1, 1) == 1);
        assertTrue(mul(1, Long.MAX_VALUE) == Long.MAX_VALUE);
        assertTrue(mul(0, Long.MAX_VALUE) == 0);

        // 乘法溢出
        assertException(() -> mul(Long.MAX_VALUE/2 + 1, 2));
        assertException(() -> mul(-1, 1));
    }

    public static void assertException(Runnable r){
        Exception e0 = null;
        try{
            r.run();
        }catch (Exception e){
            e0 = e;
        }
        if(e0 == null)
            throw new RuntimeException("assert failed");
    }

    public static void assertTrue(boolean b){
        if(!b)
            throw new RuntimeException("assert failed");
    }
}
