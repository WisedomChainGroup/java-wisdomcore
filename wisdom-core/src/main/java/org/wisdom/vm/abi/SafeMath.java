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
        if (z > x || z > y || z < 0)
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
}
