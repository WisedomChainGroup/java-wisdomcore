package org.wisdom.util;

import org.tdf.rlp.*;
import org.wisdom.core.incubator.Incubator;

import java.util.HashMap;
import java.util.Map;

public class MapRLPUtil {

    public static class MapWrapper{
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
            RLPList list = element.getAsList();
            Map<String, String> map = new HashMap<>(list.size() / 2);
            for (int i = 0; i < list.size(); i += 2) {
                map.put(list.get(i).getAsItem().getString(), list.get(i + 1).getAsItem().getString());
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

    public static class IncubMapEncoderDecoder implements RLPEncoder<Map<String, Incubator>>, RLPDecoder<Map<String, Incubator>>{

        @Override
        public Map<String, Incubator> decode(RLPElement rlpElement) {
            RLPList list = rlpElement.getAsList();
            Map<String,Incubator> map=new HashMap<>(list.size() / 2);
            for(int i = 0; i < list.size(); i += 2){
                map.put(list.get(i).getAsItem().getString(),RLPDeserializer.deserialize(list.get(i + 1).getEncoded(),Incubator.class));
            }
            return map;
        }

        @Override
        public RLPElement encode(Map<String, Incubator> stringIncubatorMap) {
            RLPList list = RLPList.createEmpty(stringIncubatorMap.size() * 2);
            stringIncubatorMap.keySet().stream().sorted(String::compareTo).forEach(x ->{
                list.add(RLPItem.fromString(x));
                list.add(RLPElement.encode(stringIncubatorMap.get(x)));
            });
            return list;
        }
    }

    public static class TokenMapEncoderDecoder implements RLPEncoder<ByteArrayMap<Long>>, RLPDecoder<ByteArrayMap<Long>> {

        @Override
        public ByteArrayMap<Long> decode(RLPElement rlpElement) {
            RLPList list = rlpElement.getAsList();
            ByteArrayMap<Long> map=new ByteArrayMap<Long>();
            for(int i = 0; i < list.size(); i += 2){
                map.put(list.get(i).getAsItem().getEncoded(),list.get(i + 1).getAsItem().getLong());
            }
            return map;
        }

        @Override
        public RLPElement encode(ByteArrayMap<Long> longByteArrayMap) {
            RLPList list = RLPList.createEmpty(longByteArrayMap.size() * 2);
            longByteArrayMap.keySet().stream().forEach(x->{
                list.add(RLPItem.fromBytes(x));
                list.add(RLPItem.fromLong(longByteArrayMap.get(x)));
            });
            return list;
        }
    }
}
