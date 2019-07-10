/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.keystore.crypto;



import org.wisdom.keystore.util.ByteUtils;
import org.wisdom.keystore.util.ByteUtils;

import java.math.BigInteger;
import java.util.Arrays;

public class PrivateKey implements java.security.PrivateKey {
    // private key must less or equals t
    private static final String t = "1000000000000000000000000000000014def9dea2f79cd65812631a5cf5d3ec";

    private byte[] k;

    private String algorithm;
    private String format;

    public String getAlgorithm(){
        return algorithm;
    }

    public String getFormat(){
        return format;
    }

    public PrivateKey(){}

    public PrivateKey(byte[] k, String algorithm, String format) {
        this.k = k;
        this.algorithm = algorithm;
        this.format = format;
    }

    public PrivateKey(byte[] k){
        this.k = k;
    }


    public void setBytes(byte[] k){
    }

    public void setEncoded(byte[] k){
    }

    public void setValid(boolean t){

    }

    public void setDestroyed(boolean t){

    }
    /**
     * check the validity of the private key
     * @return validity
     */
    public boolean isValid(){
        return new BigInteger(this.k).compareTo(new BigInteger(ByteUtils.hexStringToBytes(t))) <= 0;
    }

    public byte[] getBytes(){
        return Arrays.copyOfRange(this.k,0, this.k.length);
    }

    public byte[] getEncoded(){
        return this.getBytes();
    }
}