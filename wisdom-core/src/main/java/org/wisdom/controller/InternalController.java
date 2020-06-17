package org.wisdom.controller;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.wisdom.core.Block;
import org.wisdom.core.MemoryCachedWisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.dao.TransactionQuery;
import org.wisdom.db.*;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.sync.SyncManager;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * /internal/transaction/{} 包含未确认的事务
 * /internal/block/{} 包含未确认的区块 {} = ["unconfirmed", "orphan", 13232, "5ee54e5809601afa48f04d1aacd33d47fd358e4c9f4b0115502a2e08a09ac2ec"]
 * /internal/account/{} 未确认的 account
 * /internal/confirmed/transaction/{} 只有确认过的事务
 * /internal/confirmed/block/{} 只有确认过的区块
 * /internal/confirmed/account/{} 确认过的 account
 */
@RestController
@Slf4j
public class InternalController {
    @Autowired
    private WisdomRepository wisdomRepository;

    @Autowired
    private MemoryCachedWisdomBlockChain bc;

    @Autowired
    private AccountStateTrie accountStateTrie;

    @Autowired
    private CandidateStateTrie candidateStateTrie;

    @Autowired
    private JSONEncodeDecoder codec;

    @Autowired
    private SyncManager syncManager;

    @Autowired
    private BlocksDump blocksDump;

    // 根据区块哈希获取状态树根
    // 获取 forkdb 里面的事务
    @GetMapping(value = "/internal/trie-root/{hash}", produces = "application/json")
    public Object getTrieRoot(@PathVariable("hash") String hash) throws Exception {
        return HexBytes.fromBytes(accountStateTrie.getTrieByBlockHash(Hex.decodeHex(hash)).getRootHash());
    }

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
            return codec.encodeBlocks(syncManager.getOrphans());
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


    @GetMapping(value = "/internal/dump")
    public Object dump() {
        Double status = blocksDump.getDumpStatus();
        if (status != null) return status;
        CompletableFuture.runAsync(() -> {
            try {
                blocksDump.dump();
            } catch (Exception e) {
                e.printStackTrace();
                blocksDump.setDumpStatus(null);
            }
        });
        status = blocksDump.getDumpStatus();
        return status == null ? "dump success" : String.format("dump status %.2f %%", status * 100);
    }


    @GetMapping(value = "/internal/metric/cache")
    public Object getCacheMetric() {
        Map<String, Map<?, ?>> ret = new HashMap<>();
        Map<String, Double> hitRate = new HashMap<>();
        Map<String, Long> hits = new HashMap<>();
        Map<String, Long> miss = new HashMap<>();
        List<String> keys =
                Arrays.asList("blocksCache", "headerCache", "hasBlockCache",
                        "accountTrieCache", "candidateTrieCache");

        List<Cache<?, ?>> caches = Arrays.asList(
                bc.getBlockCache(), bc.getHeaderCache(), bc.getHasBlockCache()
        );

        for (int i = 0; i < keys.size(); i++) {
            hitRate.put(keys.get(i), caches.get(i).stats().hitRate());
            hits.put(keys.get(i), caches.get(i).stats().hitCount());
            miss.put(keys.get(i), caches.get(i).stats().missCount());
        }

        ret.put("hitRate", hitRate);
        ret.put("hits", hits);
        ret.put("miss", miss);
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

    @GetMapping(value = {
            "/internal/getTxrecordFromAddress",
            "/internal/transaction"
    }, produces = "application/json")
    public List<Transaction> getTransactionsByTo(
            @ModelAttribute @Validated TransactionQuery query
    ) {
        return wisdomRepository.getTransactionByQuery(query);
    }

    @GetMapping("/internal/dump-accounts")
    public void dumpAccounts(
            HttpServletResponse response
    ) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=" + "accounts.rlp");

        byte[] root = accountStateTrie
                .getRootStore()
                .get(wisdomRepository.getBestBlock().getHash()).get();
        log.info("root = " + HexBytes.fromBytes(root));
        List<AccountState> states = new ArrayList<>(accountStateTrie.getTrie().revert(root)
                .values());
        byte[] bytes = RLPCodec.encode(states);
        log.info("start transfer size = {} ", bytes.length);
        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.getOutputStream().close();
    }
}
