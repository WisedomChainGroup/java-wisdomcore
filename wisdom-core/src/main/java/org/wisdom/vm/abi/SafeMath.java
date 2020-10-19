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
        // 加法溢出
        assertException(() -> {
            add(1, Long.MAX_VALUE);
        });
        assertException(() -> {
            add(Long.MAX_VALUE, 1);
        });
        // 减法溢出
        assertException(() -> {
            sub(1, 2);
        });
        // 乘法溢出
        assertException(() -> {
            mul(Long.MAX_VALUE/2 + 1, 2);
        });
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
}
