package org.wisdom.keystore.util;

import java.math.BigInteger;

public class LargeNumCalculation {

    /*
    public static void main(String[] args){

        BigInteger b1  = new BigInteger("15", 10);
        BigInteger b2  = new BigInteger("00000001", 2);

        BigInteger b3  =  b2.add(b1);
        System.out.println(b3);

        BigInteger bi1=new BigInteger("100");
        BigInteger bi2=new BigInteger("2");
        String str=new BigInteger("3244",5).toString(30);


        System.out.println(str);

        String valLeft="abcde12342348909021341234f";
        String valRight="1000010011100111001";
        System.out.println(valAdd(valLeft,16,valRight,2));
        System.out.println(valSubtract(valLeft,16,valRight,2));
        System.out.println(valMultiply(valLeft,16,valRight,2));
        System.out.println(valDivide(valLeft,16,valRight,2));
    }
    */



    /**
     * Computing the sum of two arbitrary digits.
     * @param valLeft value to be added to this BigInteger.
     * @param radixLeft radix to be used in interpreting
     * @param valRight value to be added to this BigInteger.
     * @param radixRight radix to be used in interpreting
     * @return The desired decimal format.
     */
    public  static  BigInteger valAdd(String valLeft,int radixLeft,String valRight,int radixRight)
    {
        BigInteger vall  = new BigInteger(valLeft, radixLeft);
        BigInteger valr  = new BigInteger(valRight, radixRight);
        BigInteger val=vall.add(valr);
        return val;

    }

    /**
     * Computational subtraction
     * @param valLeft value to be subtracted from this BigInteger.
     * @param radixLeft radix to be used in interpreting
     * @param valRight value to be subtracted from this BigInteger.
     * @param radixRight radix to be used in interpreting
     * @return The desired decimal format.
     */
    public  static  BigInteger valSubtract(String valLeft,int radixLeft,String valRight,int radixRight)
    {
        BigInteger vall  = new BigInteger(valLeft, radixLeft);
        BigInteger valr  = new BigInteger(valRight, radixRight);
        BigInteger val=vall.subtract(valr);
        return val;

    }


    /**
     * Computation multiplication
     * @param valLeft value to be multiplied by this BigInteger.
     * @param radixLeft radix to be used in interpreting
     * @param valRight value to be multiplied by this BigInteger.
     * @param radixRight radix to be used in interpreting
     * @return The desired decimal format.
     */
    public  static  BigInteger valMultiply(String valLeft,int radixLeft,String valRight,int radixRight)
    {
        BigInteger vall  = new BigInteger(valLeft, radixLeft);
        BigInteger valr  = new BigInteger(valRight, radixRight);
        BigInteger val=vall.multiply(valr);
        return val;

    }

    /**
     * Computation Division
     * @param valDividend value to be added to this BigInteger.
     * @param radixDividend value by which this BigInteger is to be divided.
     * @param valDivisor value to be added to this BigInteger.
     * @param radixDivisor value by which this BigInteger is to be divided.
     * @return The desired decimal format.
     */
    public  static  BigInteger valDivide(String valDividend,int radixDividend,String valDivisor,int radixDivisor)
    {
        BigInteger vall  = new BigInteger(valDividend, radixDividend);
        BigInteger valr  = new BigInteger(valDivisor, radixDivisor);
        BigInteger val=vall.divide(valr);
        return val;

    }

}
