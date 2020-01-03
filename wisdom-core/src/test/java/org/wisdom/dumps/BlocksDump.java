package org.wisdom.dumps;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.tdf.rlp.RLPCodec;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BlocksDump {
    private Double dumpStatus;

    private String directory;

    private WisdomBlockChain wisdomBlockChain;

    public void dump() throws Exception{
        File file = Paths.get(directory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(directory + " is not a valid directory");
        dumpStatus = 0.0;
        int last = (int) wisdomBlockChain.getLastConfirmedBlock().nHeight;

        int blocksPerDump = 100000;
        int blocksPerFetch = 4096;
        int i = 0;
        File[] files = file.listFiles();
        while (true) {
            if(files != null){
                int finalI = i;
                Optional<File> o = Arrays.stream(files).filter(f -> f.getName().matches("blocks-dump\\." + finalI + "\\..+")).findFirst();
                Optional<Block[]> preDumped = o.map(f -> {
                    try{
                        return Files.readAllBytes(f.toPath());
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }).map(data -> RLPCodec.decode(data, Block[].class))
                ;
                if(preDumped.map(x -> x.length >= blocksPerDump).orElse(false)){
                    i++;
                    continue;
                }
            }
            List<Block> all = new ArrayList<>(blocksPerDump);
            int start = i * blocksPerDump;
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
            i++;
            System.out.println(dumpStatus);
        }
        dumpStatus = null;
    }
}
