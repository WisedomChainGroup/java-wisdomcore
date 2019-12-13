package org.wisdom.util;

import org.tdf.rlp.*;
import org.wisdom.core.incubator.Incubator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MapRLPUtil {

    public static class MapWrapper {
        @RLP
        @RLPEncoding(MapEncoderDecoder.class)
        @RLPDecoding(MapEncoderDecoder.class)
        public Map<String, String> map;

        public MapWrapper(Map<String, String> map) {
            this.map = map;
        }

        public MapWrapper() {
        }
    }

    public static class MapEncoderDecoder implements RLPEncoder<Map<String, String>>, RLPDecoder<Map<String, String>> {
        @Override
        public Map<String, String> decode(RLPElement element) {
            RLPList list = element.asRLPList();
            Map<String, String> map = new HashMap<>(list.size() / 2);
            for (int i = 0; i < list.size(); i += 2) {
                map.put(list.get(i).asString(), list.get(i + 1).asString());
            }
            return map;
        }

        @Override
        public RLPElement encode(Map<String, String> o) {
            RLPList list = RLPList.createEmpty(o.size() * 2);
            o.keySet().stream().sorted(String::compareTo).forEach(x -> {
                list.add(RLPItem.fromString(x));
                list.add(RLPItem.fromString(o.get(x)));
            });
            return list;
        }
    }

    public static class IncubMapEncoderDecoder implements RLPEncoder<Map<String, Incubator>>, RLPDecoder<Map<String, Incubator>> {

        @Override
        public Map<String, Incubator> decode(RLPElement rlpElement) {
            RLPElement list = rlpElement;
            Map<String, Incubator> map = new HashMap<>(list.size() / 2);
            for (int i = 0; i < list.size(); i += 2) {
                map.put(list.get(i).asString(), list.get(i + 1).as(Incubator.class));
            }
            return map;
        }

        @Override
        public RLPElement encode(Map<String, Incubator> stringIncubatorMap) {
            RLPList list = RLPList.createEmpty(stringIncubatorMap.size() * 2);
            stringIncubatorMap.keySet().stream().sorted(String::compareTo).forEach(x -> {
                list.add(RLPItem.fromString(x));
                list.add(RLPElement.readRLPTree(stringIncubatorMap.get(x)));
            });
            return list;
        }
    }

    public static class ByteArrayMapEncoder<V> implements RLPEncoder<Map<byte[], V>>, Comparator<byte[]> {
        @Override
        public int compare(byte[] x, byte[] y) {
            return FastByteComparisons.compareTo(
                    x, 0, x.length,
                    y, 0, y.length);
        }

        @Override
        public RLPElement encode(Map<byte[], V> vMap) {
            RLPList list = RLPList.createEmpty(vMap.size() * 2);
            vMap.keySet().stream().sorted(this::compare).forEach(x -> {
                list.add(RLPItem.fromBytes(x));
                list.add(RLPElement.readRLPTree(vMap.get(x)));
            });
            return list;
        }
    }

    public abstract static class ByteArrayMapDecoder<V> implements RLPDecoder<Map<byte[], V>> {
        abstract Class<V> getElementClass();

        @Override
        public Map<byte[], V> decode(RLPElement rlpElement) {
            Map<byte[], V> map = new ByteArrayMap<>();
            for (int i = 0; i < rlpElement.size(); i += 2) {
                map.put(rlpElement.get(i).asBytes(), rlpElement.get(i + 1).as(getElementClass()));
            }
            return map;
        }
    }

    public static class ByteArrayLongMapEncoder extends ByteArrayMapEncoder<Long>{
    }

    public static class ByteArrayLongMapDecoder extends ByteArrayMapDecoder<Long>{
        @Override
        Class<Long> getElementClass() {
            return Long.class;
        }
    }
}
