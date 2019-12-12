package org.wisdom.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdf.rlp.*;
import org.wisdom.db.Leveldb;
import org.wisdom.util.MapRLPUtil;

import java.util.Map;
import java.util.Optional;

@Component
public class AssetCode {

    private static final String AssetCode="assetcode";
    private MapRLPUtil.MapWrapper assetMap=new MapRLPUtil.MapWrapper();

    @Autowired
    private Leveldb leveldb;

    private void add(String code){
        Optional<byte[]> value=leveldb.get(AssetCode.getBytes());
        value.ifPresent(bytes->{
            assetMap=RLPDeserializer.deserialize(bytes, MapRLPUtil.MapWrapper.class);
            assetMap.map.put(code,"");
        });
        leveldb.put(AssetCode.getBytes(),RLPElement.encode(assetMap).getEncoded());
    }

    private boolean isContainsKey(String code){
        Optional<byte[]> value=leveldb.get(AssetCode.getBytes());
        value.ifPresent(bytes->{
            assetMap=RLPDeserializer.deserialize(bytes, MapRLPUtil.MapWrapper.class);
        });
        if(assetMap.map.containsKey(code)){
            return true;
        }
        return false;
    }

    private Map<String,String> getAll(){
        Optional<byte[]> value=leveldb.get(AssetCode.getBytes());
        Map<String, String> maps=null;
        value.ifPresent(bytes->{
            assetMap=RLPDeserializer.deserialize(bytes, MapRLPUtil.MapWrapper.class);
            maps.putAll(assetMap.map);
        });
        return maps;
    }
}
