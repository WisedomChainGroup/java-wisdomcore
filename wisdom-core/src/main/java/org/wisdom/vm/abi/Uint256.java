package org.wisdom.vm.abi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.NonNull;
import org.tdf.common.util.FastByteComparisons;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import static org.wisdom.vm.abi.ByteUtil.EMPTY_BYTE_ARRAY;
import static org.wisdom.vm.abi.ByteUtil.firstNonZeroByte;


@JsonDeserialize(using = Uint256.Uint256Deserializer.class)
@JsonSerialize(using = Uint256.Uint256Serializer.class)
@RLPEncoding(Uint256.Uint256EncoderDecoder.class)
@RLPDecoding(Uint256.Uint256EncoderDecoder.class)
public class Uint256 {
    public static class Uint256Deserializer extends StdDeserializer<Uint256> {
        public Uint256Deserializer() {
            super(Uint256.class);
        }

        @Override
        public Uint256 deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if (node.isNull()) return Uint256.ZERO;
            String encoded = node.asText();
            if (encoded == null || encoded.equals("")) {
                return Uint256.ZERO;
            }
            if (encoded.startsWith("0x")) {
                return Uint256.of(encoded.substring(2), 16);
            }
            return Uint256.of(encoded, 10);
        }

    }

    public static class Uint256Serializer extends StdSerializer<Uint256> {
        public Uint256Serializer() {
            super(Uint256.class);
        }

        @Override
        public void serialize(Uint256 value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.value().toString(10));
        }
    }

    public static class Uint256EncoderDecoder implements RLPEncoder<Uint256>, RLPDecoder<Uint256> {
        @Override
        public RLPElement encode(@NonNull Uint256 uint256) {
            return RLPItem.fromBytes(uint256.getNoLeadZeroesData());
        }

        @Override
        public Uint256 decode(@NonNull RLPElement rlpElement) {
            return Uint256.of(rlpElement.asBytes());
        }
    }

    public static final int MAX_POW = 256;
    public static final BigInteger _2_256 = BigInteger.valueOf(2).pow(MAX_POW);
    public static final BigInteger MAX_VALUE = _2_256.subtract(BigInteger.ONE);

    private final byte[] data;
    public static final Uint256 ZERO = new Uint256(new byte[32]);
    public static final Uint256 ONE = of((byte) 1);
    public static final Uint256 MAX_U256 = Uint256.of(HexBytes.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"));

    public static Uint256 of(String pattern, int radix) {
        BigInteger i = new BigInteger(pattern, radix);
        if (i.compareTo(BigInteger.ZERO) < 0 || i.compareTo(MAX_VALUE) > 0)
            throw new RuntimeException(pattern + " overflow");
        return of(ByteUtil.bigIntegerToBytes(i, 32));
    }

    /**
     * Unsafe private constructor
     * Doesn't guarantee immutability if byte[] contents are changed later
     * Use one of factory methods instead:
     * - {@link #of(byte[])}
     * - {@link #of(String)}
     * - {@link #of(long)}
     * - {@link #of(int)}
     *
     * @param data Byte Array[32] which is guaranteed to be immutable
     */
    private Uint256(byte[] data) {
        if (data == null || data.length != 32)
            throw new RuntimeException("Input byte array should have 32 bytes in it!");
        this.data = data;
    }

    public static Uint256 of(byte[] data) {
        if (data == null || data.length == 0) {
            return Uint256.ZERO;
        }

        int leadingZeroBits = ByteUtil.numberOfLeadingZeros(data);
        int valueBits = 8 * data.length - leadingZeroBits;
        if (valueBits <= 8) {
            if (data[data.length - 1] == 0) return ZERO;
            if (data[data.length - 1] == 1) return ONE;
        }

        if (data.length == 32)
            return new Uint256(Arrays.copyOf(data, data.length));
        else if (data.length <= 32) {
            byte[] bytes = new byte[32];
            System.arraycopy(data, 0, bytes, 32 - data.length, data.length);
            return new Uint256(bytes);
        } else {
            throw new RuntimeException(String.format("Data word can't exceed 32 bytes: 0x%s", ByteUtil.toHexString(data)));
        }
    }

    public static Uint256 of(String data) {
        return of(HexBytes.decode(data));
    }

    public static Uint256 of(byte num) {
        byte[] bb = new byte[32];
        bb[31] = num;
        return new Uint256(bb);
    }


    public static Uint256 of(int num) {
        return of(ByteUtil.intToBytes(num));
    }

    public static Uint256 of(long num) {
        return of(ByteUtil.longToBytes(num));
    }


    /**
     * Returns instance data
     * Actually copy of internal byte array is provided
     * in order to protect DataWord immutability
     *
     * @return instance data
     */
    public byte[] getData() {
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Returns copy of instance data
     *
     * @return copy of instance data
     */
    private byte[] copyData() {
        return Arrays.copyOf(data, data.length);
    }


    public static byte[] getNoLeadZeroesData(byte[] data) {
        final int firstNonZero = firstNonZeroByte(data);
        switch (firstNonZero) {
            case -1:
                return EMPTY_BYTE_ARRAY;
            case 0:
                return data;
            default:
                byte[] result = new byte[data.length - firstNonZero];
                System.arraycopy(data, firstNonZero, result, 0, data.length - firstNonZero);

                return result;
        }
    }

    public byte[] getNoLeadZeroesData() {
        return getNoLeadZeroesData(this.data);
    }


    public BigInteger value() {
        return new BigInteger(1, data);
    }

    public boolean isZero() {
        if (this == ZERO) return true;
        return this.compareTo(ZERO) == 0;
    }

    public Uint256 add(Uint256 word) {
        byte[] newData = new byte[32];
        for (int i = 31, overflow = 0; i >= 0; i--) {
            int v = (this.data[i] & 0xff) + (word.data[i] & 0xff) + overflow;
            newData[i] = (byte) v;
            overflow = v >>> 8;
        }
        return new Uint256(newData);
    }

    public Uint256 safeAdd(Uint256 word) {
        Uint256 ret = this.add(word);
        if (ret.compareTo(this) < 0 || ret.compareTo(word) < 0)
            throw new RuntimeException("unexpected exception");
        return ret;
    }

    // TODO: improve with no BigInteger
    public Uint256 div(Uint256 word) {
        if (word.isZero()) {
            throw new RuntimeException("divided by zero");
        }

        BigInteger result = value().divide(word.value());
        return new Uint256(ByteUtil.copyToArray(result.and(MAX_VALUE)));
    }

    // TODO: improve with no BigInteger
    public Uint256 sub(Uint256 word) {
        BigInteger result = value().subtract(word.value());
        return new Uint256(ByteUtil.copyToArray(result.and(MAX_VALUE)));
    }

    public Uint256 safeSub(Uint256 word) {
        if (this.compareTo(word) < 0)
            throw new RuntimeException("overflow");
        return this.sub(word);
    }


    // TODO: improve with no BigInteger
    public Uint256 mod(Uint256 word) {

        if (word.isZero()) {
            return ZERO;
        }

        BigInteger result = value().mod(word.value());
        return new Uint256(ByteUtil.copyToArray(result.and(MAX_VALUE)));
    }

    public Uint256 mul(Uint256 word) {
        BigInteger result = value().multiply(word.value());
        return new Uint256(ByteUtil.copyToArray(result.and(MAX_VALUE)));
    }

    public Uint256 safeMul(Uint256 word) {
        if (this.compareTo(ZERO) == 0) {
            return ZERO;
        }

        Uint256 ret = this.mul(word);
        if (ret.div(this).compareTo(word) != 0) throw new RuntimeException("SafeMath: multiplication overflow ");
        return ret;
    }

    public int compareTo(Uint256 o) {
        if (o == null) return -1;
        int result = FastByteComparisons.compareTo(
                data, 0, data.length,
                o.data, 0, o.data.length);
        // Convert result into -1, 0 or 1 as is the convention
        return (int) Math.signum(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Uint256 uint256 = (Uint256) o;
        return this.compareTo(uint256) == 0;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    public long longValue(){
        return value().longValueExact();
    }

    public static void main(String[] args) {
        // 正常加法
        assertTrue(MAX_U256.safeAdd(Uint256.ZERO).equals(MAX_U256));
        assertTrue(Uint256.ZERO.safeAdd(MAX_U256).equals(MAX_U256));
        assertTrue(MAX_U256.safeSub(Uint256.ONE).safeAdd(Uint256.ONE).equals(MAX_U256));
        assertTrue(Uint256.ZERO.safeAdd(Uint256.ZERO).equals(Uint256.ZERO));
        assertTrue(Uint256.ZERO.safeAdd(Uint256.ONE).equals(Uint256.ONE));
        assertTrue(Uint256.ONE.safeAdd(Uint256.ZERO).equals(Uint256.ONE));
        assertTrue(Uint256.ONE.safeAdd(Uint256.ONE).equals(Uint256.of(2)));

        // 加法溢出
        assertException(() -> Uint256.ONE.safeAdd(Uint256.MAX_U256));
        assertException(() -> Uint256.MAX_U256.safeAdd(Uint256.ONE));
        assertException(() -> Uint256.MAX_U256.safeSub(Uint256.ONE).safeAdd(Uint256.of(2)));

        // 普通减法
        assertTrue(Uint256.ONE.safeSub(Uint256.ZERO).equals(Uint256.ONE));
        assertException(() -> Uint256.ZERO.safeSub(Uint256.ONE));
        assertTrue(Uint256.MAX_U256.safeSub(Uint256.ONE).equals(Uint256.of("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe", 16)));
        assertTrue(Uint256.MAX_U256.safeSub(Uint256.ZERO).equals(Uint256.MAX_U256));

        // 减法溢出
        assertException(() -> Uint256.ONE.safeSub(Uint256.of(2)));


        assertTrue(Uint256.ONE.safeMul(Uint256.ONE).equals(Uint256.ONE));
        assertTrue(Uint256.MAX_U256.safeMul(Uint256.ONE).equals(Uint256.MAX_U256));
        assertTrue(Uint256.MAX_U256.safeMul(Uint256.ZERO).equals(Uint256.ZERO));

        // 乘法溢出
        assertException(() -> Uint256.MAX_U256.div(Uint256.of(2)).add(Uint256.ONE).safeMul(Uint256.of(2)));

        // 除法溢出
        assertException(() -> Uint256.ONE.div(Uint256.ZERO));
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
