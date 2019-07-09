package org.wisdom.encoding;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.util.Arrays;
import org.wisdom.util.Arrays;

public class Uint256 implements Comparable<Uint256>{
    private long[] data;

    public static Uint256 fromEncoded(byte[] in){
        if(in.length != 32){
            return null;
        }
        Uint256 res = new Uint256();
        res.data = new long[8];
        res.data[0] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 0, 4));
        res.data[1] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 4, 8));
        res.data[2] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 8, 12));
        res.data[3] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 12, 16));
        res.data[4] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 16, 20));
        res.data[5] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 20, 24));
        res.data[6] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 24, 28));
        res.data[7] = BigEndian.decodeUint32(Arrays.copyOfRange(in, 28, 32));
        return res;
    }

    @Override
    public int compareTo(Uint256 o) {
        for(int i = 0; i < data.length; i++){
            if(data[i] > o.data[i]){
                return 1;
            }
            if(data[i] < o.data[i]){
                return -1;
            }
        }
        return 0;
    }

    public byte[] getEncoded(){
        return Arrays.concatenate(
                new byte[][]{
                        BigEndian.encodeUint32(data[0]),
                        BigEndian.encodeUint32(data[1]),
                        BigEndian.encodeUint32(data[2]),
                        BigEndian.encodeUint32(data[3]),
                        BigEndian.encodeUint32(data[4]),
                        BigEndian.encodeUint32(data[5]),
                        BigEndian.encodeUint32(data[6]),
                        BigEndian.encodeUint32(data[7]),
                }
        );
    }

    public static void main(String[] args) throws Exception{
        System.out.println(Uint256.fromEncoded(Hex.decodeHex("0000000000010000000000000000000000000000000000000000000000000000".toCharArray())).compareTo(
                Uint256.fromEncoded(Hex.decodeHex("0000000000000000000000000100000000000000000000000000000000000000".toCharArray())
        )));
    }
}
