package org.wisdom;

import org.apache.commons.codec.binary.Hex;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tdf.common.store.DBSettings;
import org.tdf.common.store.LevelDb;

@RunWith(JUnit4.class)
public class DumpTest {
    static final String parentHash = "33d0e70838ff832435f445cd2a0386f92ba6c1a03f1a5650f5fef3878b53c732";
    static final String blockHash = "f101aa7868e9d243f1f4bfec659f240fbe785a56528656349883febf4061e0c1";
    static final String directory = "C:\\Users\\Sal\\Desktop\\dumped";
    static final String[] names = new String[]{
            "validator-trie-roots",
            "account-trie-roots",
            "asset-code-trie-roots"
    };

    @Test
    public void test() throws Exception{
        for(String name: names){
            LevelDb db1 =
                    new LevelDb(Iq80DBFactory.factory, directory, name);
            db1.init(DBSettings.DEFAULT);
            assert db1.containsKey(Hex.decodeHex(parentHash.toCharArray()));
            if(!db1.containsKey(Hex.decodeHex(blockHash.toCharArray()))){
                System.out.println(name);
            }
        }

    }
}
