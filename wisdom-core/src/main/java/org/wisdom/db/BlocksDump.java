package org.wisdom.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BlocksDump {
    @Getter
    @Setter
    private volatile Double dumpStatus;

    private String directory;

    private WisdomBlockChain wisdomBlockChain;

    public BlocksDump(
            @Value("${wisdom.consensus.fast-sync.directory}") String directory,
            WisdomBlockChain wisdomBlockChain
    ) {
        this.directory = directory;
        this.wisdomBlockChain = wisdomBlockChain;
    }

    private static final String PREFIX = "blocks-dump.";

    public void dump() throws Exception {
        File file = Paths.get(directory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(directory + " is not a valid directory");
        dumpStatus = 0.0;
        int last = (int) wisdomBlockChain.getTopBlock().nHeight;

        int blocksPerDump = 100000;
        int blocksPerFetch = 4096;
        int i = 0;
        File[] files = file.listFiles();
        while (true) {
            // try to find pre dumped blocks
            final int finalI = i;
            Optional<File> o =
                    Optional.ofNullable(files)
                            .map(Arrays::stream)
                            .orElse(Stream.empty())
                            .filter(f -> !f.isDirectory()
                                    && f.getName().startsWith(PREFIX + finalI)
                            )
                            .findFirst();

            RLPList all = o.map(f -> {
                try {
                    return Files.readAllBytes(f.toPath());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).map(x -> RLPElement.fromEncoded(x).asRLPList()).orElse(RLPList.createEmpty());

            // skip if pre dumped blocks contains more than 100000 blocks
            if(all.size() > blocksPerDump) throw new IllegalArgumentException();
            if (all.size() == blocksPerDump
            ) {
                i++;
                continue;
            }

            final long start = all.isEmpty() ?
                    i * blocksPerDump :
                    all.get(all.size() - 1).as(Block.class).nHeight + 1;

            final long end = (i + 1) * blocksPerDump;

            long cursor = start;

            while (true) {
                List<Block> list =
                        wisdomBlockChain
                                .getBlocksSince(cursor, blocksPerFetch)
                                .stream().filter(x -> x.getnHeight() < end)
                                .collect(Collectors.toList());

                all.addAll(RLPElement.readRLPTree(list).asRLPList());
                cursor += blocksPerFetch;
                dumpStatus = (list.isEmpty() ? 0 : list.get(list.size() - 1).nHeight) * 1.0 / last;
                if (list.size() < blocksPerFetch) break;
            }

            // override the prebuilt file
            o.map(File::delete);

            Path path =
                    Paths.get(directory,
                            String.format("%s%d.%d-%d.rlp", PREFIX, i, all.get(0).as(Block.class).nHeight,
                                    all.get(all.size() - 1).as(Block.class).nHeight + 1)
                    );

            Files.write(path, RLPCodec.encode(all), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.SYNC);
            if (all.size() < blocksPerDump) {
                break;
            }
            i++;
        }
        dumpStatus = null;
    }
}
