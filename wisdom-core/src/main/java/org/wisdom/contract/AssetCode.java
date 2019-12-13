package org.wisdom.contract;

import org.springframework.stereotype.Component;
import org.tdf.rlp.*;
import org.wisdom.db.DatabaseStoreFactory;
import org.wisdom.store.Store;
import org.wisdom.util.MapRLPUtil;

import java.util.Map;
import java.util.Optional;

@Component
public class AssetCode {

    private static final String AssetCode="assetcode";
    private MapRLPUtil.MapWrapper assetMap=new MapRLPUtil.MapWrapper();


    private Store<byte[], byte[]> leveldb;

    public AssetCode(DatabaseStoreFactory factory) {
        leveldb = factory.create("leveldb", false);
    }

    private void add(String code){
        Optional<byte[]> value=leveldb.get(AssetCode.getBytes());
        value.ifPresent(bytes->{
            assetMap= RLPCodec.decode(bytes, MapRLPUtil.MapWrapper.class);
            assetMap.map.put(code,"");
        });
        leveldb.put(AssetCode.getBytes(),RLPCodec.encode(assetMap));
    }

    public boolean isContainsKey(String code){
        Optional<byte[]> value=leveldb.get(AssetCode.getBytes());
        value.ifPresent(bytes->{
            assetMap=RLPCodec.decode(bytes, MapRLPUtil.MapWrapper.class);
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
            assetMap=RLPCodec.decode(bytes, MapRLPUtil.MapWrapper.class);
            maps.putAll(assetMap.map);
        });
        return maps;
    }
}
