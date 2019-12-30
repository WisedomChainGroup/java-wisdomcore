package org.wisdom.dumps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdf.common.util.ByteArraySet;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.context.TestContext;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestContext.class)
// set SPRING_CONFIG_LOCATION=classpath:application-test.yml to run dump tasks
public class DumpTests {
    @Autowired
    private WisdomBlockChain wisdomBlockChain;

    @Autowired
    private AccountDB accountDB;

    private Double dumpStatus;

    @Test
    public void dumpBlocks() throws Exception {
        String directory = "c:\\Users\\Sal\\Desktop\\dumps\\blocks";

        File file = Paths.get(directory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(directory + " is not a valid directory");
        dumpStatus = 0.0;
        int last = (int) wisdomBlockChain.getLastConfirmedBlock().nHeight;

        int blocksPerDump = 100000;
        int blocksPerFetch = 4096;
        int start = 0;
        int i = 0;
        while (true) {
            List<Block> all = new ArrayList<>(blocksPerDump);
            final int end = start + blocksPerDump;
            int cursor = start;
            while (true) {
                List<Block> lists =
                        wisdomBlockChain.getCanonicalBlocks(cursor, blocksPerFetch)
                                .stream().filter(x -> x.getnHeight() < end).collect(Collectors.toList());
                all.addAll(lists);
                cursor += blocksPerFetch;
                if (lists.size() < blocksPerFetch) break;
            }
            Path path =
                    Paths.get(directory,
                            String.format("blocks-dump.%d.%d-%d.rlp", i, all.get(0).nHeight, all.get(all.size() - 1).nHeight + 1)
                    );
            Files.write(path, RLPCodec.encode(all), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.SYNC);
            if (all.size() < blocksPerDump) {
                break;
            }
            dumpStatus = all.get(all.size() - 1).nHeight * 1.0 / last;
            start += blocksPerDump;
            i++;
            System.out.println(dumpStatus);
        }
        dumpStatus = null;
    }

    @Test
    public void restoreDumps(){

    }

    @Test
    public void createNewGenesis() throws Exception{
        String blocksDirectory = "z:\\dumps\\blocks";
        String genesisDirectory = "z:\\dumps\\accounts";
        final Block[] newGenesis = {null};
        int height = 800040;
        byte[] zeroPublicKey = new byte[32];
        byte[] zeroPublicKeyHash = new byte[20];

        Double restoreStatus;
        File file = Paths.get(blocksDirectory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(blocksDirectory + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        restoreStatus = 0.0;
        int filesCount = files.length;
        double p = 1.0 / filesCount;
        ByteArraySet set = new ByteArraySet();

        Arrays.stream(files)
                .sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getName().split("\\.")[1])))
                .flatMap(x -> {
                    try{
                        byte[] bytes = Files.readAllBytes(x.toPath());
                        return Arrays.stream(RLPElement.fromEncoded(bytes).as(Block[].class));
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                })
                .filter(b -> b.nHeight <= height)
                .peek(b -> {
                    if(b.nHeight == height) newGenesis[0] = b;
                })
                .flatMap(b -> b.body.stream())
                .forEach(tx -> {
                    if(!Arrays.equals(zeroPublicKey, tx.from))
                        set.add(PublicKeyHash.fromPublicKey(tx.from).getPublicKeyHash());
                    if(!Arrays.equals(zeroPublicKeyHash, tx.to))
                        set.add(tx.to);
                });

        System.out.println(set.size());
        assert newGenesis[0] != null;
//        List<AccountState> states = set.stream()
//                .map(x -> {
//                    try{
//                        return accountDB.getAccounstate(x, height);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        System.out.println(HexBytes.encode(x));
//                        return null;
//                    }
//                }).collect(Collectors.toList());
//        Path path =
//                Paths.get(genesisDirectory,
//                        String.format("accounts-dump.%d.rlp", height)
//                );
//        Files.write(path, RLPCodec.encode(states), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.SYNC);
        restoreStatus = null;

    }

}
