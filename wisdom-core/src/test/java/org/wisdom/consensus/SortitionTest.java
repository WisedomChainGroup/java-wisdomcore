package org.wisdom.consensus;

import org.wisdom.crypto.HashUtil;
import org.wisdom.crypto.ed25519.Ed25519;
import org.wisdom.crypto.vrf.VRFPrivateKey;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class SortitionTest {
    private static final int nodes = 10000;
    private static final int weightPerNode = 20;
    private static final int totalWeights = nodes * weightPerNode;
    private static final byte[] seed = HashUtil.sha256("input".getBytes());
    private static final int expected = 26;
    private static final int rounds = 10;
    private static final Random random = new SecureRandom();

    @Test
    public void testSortition(){
        for(int j = 0; j < rounds; j++){
            VRFPrivateKey[] keys = new VRFPrivateKey[nodes];
            int proposals = 0;
            for(int i = 0; i < nodes; i++){
                keys[i] = new VRFPrivateKey(Ed25519.getAlgorithm());
                int priority = keys[i].generatePublicKey().calcPriority(seed, keys[i].rand(seed), expected, weightPerNode, totalWeights);
                if(priority > 0){
                    proposals++;
                }
            }
            System.out.print(proposals + " ");
        }
    }

    @Test
    public void testCompare(){
        for(int j = 0; j < rounds; j++){
            VRFPrivateKey[] keys = new VRFPrivateKey[nodes];
            BigInteger[] weights = new BigInteger[nodes];
            List<Integer> idx = new ArrayList<>(nodes);
            List<BigInteger> priorites = new ArrayList<>(nodes);
            for(int i = 0; i < nodes; i++){
                idx.add(i);
                keys[i] = new VRFPrivateKey(Ed25519.getAlgorithm());
                byte[] bytes = new byte[8];
                random.nextBytes(bytes);
                weights[i] = new BigInteger(bytes);
                priorites.add(new BigInteger(keys[i].rand(seed).getR()).multiply(weights[i]));
            }

            Collections.sort(idx, new Comparator<Integer>() {
                @Override
                public int compare(Integer x1, Integer x2) {
                    return priorites.get(x1).compareTo(priorites.get(x2));
                }
            });

            assert priorites.get(idx.get(nodes - 1)).compareTo(priorites.get(idx.get(nodes - 2))) > 0;
        }
    }
}
