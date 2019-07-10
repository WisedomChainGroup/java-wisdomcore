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

import java.util.Arrays;

public class PublicKey implements java.security.PublicKey {
    private byte[] k;
    private String algorithm;
    private String format;

    public PublicKey(){}

    public PublicKey(byte[] k){
        this.k = k;
    }

    public PublicKey(byte[] k, String algorithm, String format) {
        this.k = k;
        this.algorithm = algorithm;
        this.format = format;
    }

    public void setBytes(byte[] k) {
    }

    public void setEncoded(byte[] k) {
    }

    public void setFormat(String s){

    }

    public byte[] getBytes(){
        return Arrays.copyOfRange(this.k,0, this.k.length);
    }

    public byte[] getEncoded(){
        return this.getBytes();
    }

    public String getAlgorithm(){
        return algorithm;
    }

    public String getFormat(){
        return format;
    }
}