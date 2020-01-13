package org.wisdom.controller;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tdf.rlp.RLPCodec;
import org.wisdom.core.Block;
import org.wisdom.core.MemoryCachedWisdomBlockChain;
import org.wisdom.core.OrphanBlocksManager;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.WisdomRepository;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.util.Address;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * /internal/transaction/{} 包含未确认的事务
 * /internal/block/{} 包含未确认的区块 {} = ["unconfirmed", "orphan", 13232, "5ee54e5809601afa48f04d1aacd33d47fd358e4c9f4b0115502a2e08a09ac2ec"]
 * /internal/account/{} 未确认的 account
 * /internal/confirmed/transaction/{} 只有确认过的事务
 * /internal/confirmed/block/{} 只有确认过的区块
 * /internal/confirmed/account/{} 确认过的 account
 */
@RestController
public class InternalController {
    @Autowired
    private WisdomRepository wisdomRepository;

    @Autowired
    private MemoryCachedWisdomBlockChain bc;

    @Autowired
    private JSONEncodeDecoder codec;

    @Autowired
    private OrphanBlocksManager manager;

    private Double dumpStatus;

    private Double restoreStatus;

    // 获取 forkdb 里面的事务
    @GetMapping(value = "/internal/transaction/{transactionHash}", produces = "application/json")
    public Object getTransaction(@PathVariable("transactionHash") String hash) {
        try {
            Block best = wisdomRepository.getBestBlock();
            byte[] h = Hex.decodeHex(hash.toCharArray());
            Optional<Transaction> tx = wisdomRepository.getTransactionAt(best.getHash(), h);
            if (tx.isPresent()) {
                return codec.encodeTransaction(tx.get());
            }
        } catch (Exception e) {
            return "invalid transaction hash hex string " + hash;
        }
        return "the transaction " + hash + " not exists";
    }

    // 获取 主账本 里面的事务
    @GetMapping(value = "/internal/confirmed/transaction/{transactionHash}", produces = "application/json")
    public Object getTransactionConfirmed(@PathVariable("transactionHash") String hash) {
        try {
            byte[] h = Hex.decodeHex(hash.toCharArray());
            Transaction tx = bc.getTransaction(h);
            if (tx != null) {
                return codec.encodeTransaction(tx);
            }
        } catch (Exception e) {
            return "invalid transaction hash hex string " + hash;
        }
        return "the transaction " + hash + " not exists";
    }

    // 获取 forkdb 区块高度
    @GetMapping(value = "/internal/height", produces = "application/json")
    public Object getHeight() {
        return wisdomRepository.getBestBlock().nHeight;
    }

    // 获取孤块池/forkdb 中的区块
    @GetMapping(value = "/internal/block/{blockInfo}", produces = "application/json")
    public Object getBlocks(@PathVariable("blockInfo") String blockInfo) {
        if (blockInfo.equals("orphan")) {
            return codec.encodeBlocks(manager.getOrphans());
        }
        if (blockInfo.equals("unconfirmed")) {
            return codec.encodeBlocks(wisdomRepository.getStaged());
        }
        try {
            byte[] hash = Hex.decodeHex(blockInfo);
            return wisdomRepository.getBlockByHash(hash);
        } catch (Exception e) {
            return getBlocksByHeight(blockInfo);
        }
    }

    public Object getBlocksByHeight(String height) {
        try {
            long h = Long.parseLong(height);
            return codec.encodeBlocks(wisdomRepository.getBlocksBetween(h, h));
        } catch (Exception e) {
            return "invalid block path variable " + height;
        }
    }

    @GetMapping(value = "/internal/restore-dump")
    public Object restoreDump(@RequestParam(value = "directory") String directory) {
        if (restoreStatus != null) return "restoring... " + restoreStatus;
        if (dumpStatus != null) return "dumping... " + dumpStatus;
        restoreDB(directory);
        return "start restoring... ";
    }

    @GetMapping(value = "/internal/restore-dump/status")
    public Object restoreDumpStatus() {
        if (restoreStatus != null) return "restoring... " + restoreStatus;
        if (dumpStatus != null) return "dumping... " + dumpStatus;
        return "no dump or restore task running";
    }

    @GetMapping(value = "/internal/dump")
    public Object dump(@RequestParam(value = "directory") String directory) {
        if (restoreStatus != null) return "restoring... " + restoreStatus;
        if (dumpStatus != null) return "dumping... " + dumpStatus;
        dumpDB(directory);
        return "start dumping...";
    }

    @GetMapping(value = "/internal/dump/status")
    public Object dumpStatus() {
        if (restoreStatus != null) return "restoring... " + restoreStatus;
        if (dumpStatus != null) return "dumping... " + dumpStatus;
        return "no dump task running";
    }

    @GetMapping(value = "/internal/metric/cache")
    public Object getCacheMetric() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("cache-stats", bc.getCacheStats());
        ret.put("hit-rate", bc.getHitRate());
        return ret;
    }

    @GetMapping(value = "/internal/metric/query")
    public Object getQueryMetric() {
        Map<String, Long> calls = bc.getCallsCounter();
        Map<String, Long> consumings = bc.getTimeConsuming();
        Map<String, String> ret = new HashMap<>();
        calls.forEach((k, v) -> {
            ret.put(k,
                    String.format(
                            "calls %d, total time consuming %d ms, average time consuming %f ms",
                            calls.get(k),
                            consumings.get(k),
                            consumings.get(k) * 1.0 / calls.get(k)
                    )
            );
        });
        return ret;
    }

    private Future<Void> restoreDB(String directory) {
        File file = Paths.get(directory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(directory + " is not a valid directory");
        File[] files = file.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("empty directory " + file);
        restoreStatus = 0.0;
        int filesCount = files.length;
        double p = 1.0 / filesCount;
        Runnable task = () -> {
            Arrays.stream(files)
                    .sorted(Comparator.comparingInt(x -> Integer.parseInt(x.getName().split("\\.")[1])))
                    .forEach(f -> {
                        try {
                            Block[] blocks = RLPCodec.decode(Files.readAllBytes(f.toPath()), Block[].class);
                            for (Block b : blocks) {
                                wisdomRepository.writeBlock(b);
                            }
                        } catch (Exception e) {
                            restoreStatus = null;
                            throw new RuntimeException(e);
                        }
                        restoreStatus += p;
                    });
            restoreStatus = null;
        };
        return CompletableFuture.runAsync(task);
    }

    private Future<Void> dumpDB(String directory) {
        File file = Paths.get(directory).toFile();
        if (!file.isDirectory()) throw new RuntimeException(directory + " is not a valid directory");
        dumpStatus = 0.0;
        int last = (int) bc.getTopHeight();
        return CompletableFuture
                .runAsync(() -> {
                    int blocksPerDump = 100000;
                    int blocksPerDumpIndex = 100000;
                    int start = 0;
                    int i = 0;
                    while (true) {

                        List<Block> all = new ArrayList<>();
                        do {
                            List<Block> lists = bc.getBlocksSince(start, 1000);
                            all.addAll(lists);
                            start += 1000;
                        } while (start < blocksPerDumpIndex);
                        Path path =
                                Paths.get(directory,
                                        String.format("blocks-dump.%d.rlp", i)
                                );
                        try {
                            FileOutputStream out = new FileOutputStream(path.toFile());
                            out.write(RLPCodec.encode(all));
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            dumpStatus = null;
                            throw new RuntimeException(e);
                        }
                        if (all.size() < blocksPerDump) {
                            break;
                        }
                        dumpStatus = all.get(all.size() - 1).nHeight * 1.0 / last;
                        blocksPerDumpIndex += blocksPerDump;
                        i++;
                    }
                    dumpStatus = null;
                });
    }


    @GetMapping(value = "/internal/getTxrecordFromAddress", produces = "application/json")
    public Object getTransactionsByTo(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "type", required = false) String type
    ) {
        Integer typeParsed = Transaction.getTypeFromInput(type);

        if (from == null) {
            from = "";
        }
        if (to == null) {
            to = "";
        }
        if (offset == null) {
            offset = 0;
        }
        if (limit == null || limit <= 0) {
            limit = Integer.MAX_VALUE;
        }
        Block best = wisdomRepository.getBestBlock();
        if (!from.equals("") && !to.equals("")) {
            byte[] publicKey;
            try {
                publicKey = Hex.decodeHex(from);
            } catch (Exception e) {
                return e.getMessage();
            }
            if (typeParsed == null) {
                return codec.encodeTransactions(
                        wisdomRepository.getTransactionsAtByFromAndTo(best.getHash(), publicKey, Address.getPublicKeyHash(to), offset, limit)
                );
            }
            return codec.encodeTransactions(
                    wisdomRepository.getTransactionsAtByTypeFromAndTo(best.getHash(), typeParsed, publicKey, Address.getPublicKeyHash(to), offset, limit)
            );
        }
        if (!from.equals("")) {
            byte[] publicKey;
            try {
                publicKey = Hex.decodeHex(from);
            } catch (Exception e) {
                return e.getMessage();
            }
            if (typeParsed == null) {
                return codec.encodeTransactions(
                        wisdomRepository.getTransactionsAtByFrom(best.getHash(), publicKey, offset, limit)
                );
            }
            return codec.encodeTransactions(
                    wisdomRepository.getTransactionsAtByTypeAndFrom(best.getHash(), typeParsed, publicKey, offset, limit)
            );
        }
        if (!to.equals("")) {
            if (typeParsed == null) {
                return codec.encodeTransactions(
                        wisdomRepository.getLatestTransactionsByTo(Address.getPublicKeyHash(to), offset, limit)
                );
            }
            return codec.encodeTransactions(
                    wisdomRepository.getTransactionsAtByTypeAndTo(best.getHash(), typeParsed, Address.getPublicKeyHash(to), offset, limit)
            );
        }
        return "please provide from or to";
    }
}
