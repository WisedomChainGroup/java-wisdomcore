package org.wisdom;

import org.apache.commons.codec.binary.Hex;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tdf.common.store.DBSettings;
import org.tdf.common.store.LevelDb;
import org.tdf.common.util.FastByteComparisons;

import java.util.Optional;

@RunWith(JUnit4.class)
public class DumpTest {
    static final String blockHash = "8f6e69a4b2aba4c61622f82a53539796dfbf62f79eb49278fb89d03b6fba1592";
    static final String directory = "C:\\Users\\Sal\\Desktop\\roots";
    static final String[] names = new String[]{
            "account-trie-roots-fault",
            "account-trie-roots-true",
            "account-trie-roots(2)",
            "account-trie-roots(3)",
            "account-trie-roots(4)"
    };

    @Test
    public void test() throws Exception{
        for(String name: names){
            LevelDb db1 =
                    new LevelDb(Iq80DBFactory.factory, directory, name);
            db1.init(DBSettings.DEFAULT);
            if(db1.containsKey(Hex.decodeHex(blockHash.toCharArray()))){
                System.out.println(name);
                System.out.println(Hex.encodeHexString(db1.get(Hex.decodeHex(blockHash)).get()));
            }
        }

    }

    @Test
    public void test2() throws Exception{

            LevelDb db1 =
                    new LevelDb(Iq80DBFactory.factory, directory, names[0]);
            db1.init(DBSettings.DEFAULT);


        LevelDb db2 =
                new LevelDb(Iq80DBFactory.factory, directory, names[1]);
        db2.init(DBSettings.DEFAULT);

        db1.forEach((k, v) -> {
            Optional<byte[]> bytes = db2.get(k);
            if(!bytes.isPresent()){
                System.out.println(Hex.encodeHex(k));
            }
            if(bytes.isPresent() && !FastByteComparisons.equal(bytes.get(), v)){
                System.out.println(Hex.encodeHex(k));
            }
        });
    }
}
