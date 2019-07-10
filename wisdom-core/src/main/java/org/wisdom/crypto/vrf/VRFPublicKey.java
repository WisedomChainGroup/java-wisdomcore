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

package org.wisdom.crypto.vrf;

import org.apache.commons.math3.fraction.BigFraction;
import org.wisdom.crypto.CryptoException;
import org.wisdom.crypto.HashUtil;
import org.wisdom.crypto.PublicKey;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.crypto.CryptoException;
import org.wisdom.crypto.HashUtil;
import org.wisdom.crypto.PublicKey;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;


import java.math.BigInteger;
import java.util.Arrays;

public class VRFPublicKey {
    private PublicKey verifier;
    private static final BigInteger maxUint256 = new BigInteger( "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
    private static final BigFraction one = new BigFraction(1);
    public VRFPublicKey(byte[] encoded, String algorithm){
        if (algorithm.equals(Ed25519.getAlgorithm())){
            this.verifier = new Ed25519PublicKey(encoded);
            return;
        }
        throw new CryptoException("unsupported signature policy");
    }


    public VRFPublicKey(PublicKey verifier) {
        this.verifier = verifier;
    }

    /**
     *
     * @param result the validity of the vrf result
     * @return
     */
    public boolean verify(byte[]seed, VRFResult result){
        if (!Arrays.equals(HashUtil.sha256(result.getProof()), result.getR())){
            return false;
        }
        return verifier.verify(seed, result.getProof());
    }

    public byte[] getEncoded(){
        return this.verifier.getEncoded();
    }

    /**
     *
     * @param seed random seed
     * @param result VRFResult
     * @param expected expected committee size
     * @param weight the weight of user
     * @param totalWeight sum of all user's weight
     * @return priority
     */
    public int calcPriority(byte[]seed, VRFResult result, int expected, int weight, int totalWeight){
        if (!this.verify(seed, result)){
            return 0;
        }
        BigFraction x = new BigFraction(new BigInteger(1, result.getR()), maxUint256);
        BigFraction p = new BigFraction(expected * weight, totalWeight);
        Binomial b = new Binomial(weight, p);
        BigFraction lower = b.cumulativeProbability(0);
        BigFraction upper = null;
        int priority;
        if(x.compareTo(lower) < 0){
            return 0;
        }
        for (priority = 0; priority < weight; priority++){
            if (upper != null){
                lower = upper;
            }
            upper = b.cumulativeProbability(priority + 1);
            if(x.compareTo(lower) >= 0 && x.compareTo(upper) < 0){
                return priority;
            }
        }
        return 0;
    }

    public static class Binomial{
        private BigFraction[] pbs;
        private BigFraction[] cbs;
        private int w;
        private BigFraction p;
        public Binomial(int w, BigFraction p){
            this.p = p;
            this.w = w;
            this.pbs = new BigFraction[w];
            this.cbs = new BigFraction[w];
        }

        public BigFraction prob(int k){
            if(k < 0 || k > w){
                return BigFraction.ZERO;
            }
            int k_w = this.w - k;
            if(k_w < k){
                return prob(k_w);
            }
            if(pbs[k] != null){
                return pbs[k];
            }
            BigFraction pb = B(k, this.w, this.p);
            pbs[k] = pb;
            return pbs[k];
        }

        public BigFraction cumulativeProbability(int k){
            if(k >= this.w){
                return BigFraction.ONE;
            }
            if(k == 0){
                return prob(0);
            }
            if(cbs[k] != null){
                return cbs[k];
            }
            cbs[k] = prob(k).add(
                    cumulativeProbability(k - 1)
            );
            return cbs[k];
        }

        private static BigFraction B(int k, int w, BigFraction p){
            return C(w,k).multiply(p.pow(k).multiply(one.subtract(p).pow(w-k)));
        }

        private static BigFraction C(int n, int r){
            return new BigFraction(
                    factorial(BigInteger.valueOf(n)),
                    factorial(BigInteger.valueOf(r)).multiply(factorial(BigInteger.valueOf(n - r)))
            );
        }

        private static BigInteger factorial(BigInteger n){
            if (n.compareTo(BigInteger.ZERO) == 0){
                return BigInteger.ONE;
            }
            return n.multiply(
                    factorial(n.subtract(BigInteger.ONE))
            );
        }
    }

}