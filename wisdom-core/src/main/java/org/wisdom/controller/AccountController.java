package org.wisdom.controller;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.web.bind.annotation.*;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLPCodec;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.crypto.HashUtil;
import org.wisdom.db.AccountState;
import org.wisdom.db.AccountStateTrie;
import org.wisdom.db.WisdomRepository;
import org.wisdom.type.PageSize;
import org.wisdom.type.PagedView;
import org.wisdom.util.Address;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class AccountController {


    private final AccountStateTrie accountStateTrie;

    private TreeMap<Long, List<AccountInfo>> cache = new TreeMap<>();

    private Lock cacheLock = new ReentrantLock();

    private final WisdomRepository wisdomRepository;

    private final WisdomBlockChain bc;

    private final EconomicModel model;

    @Value
    public static class AccountInfo {
        private long balance;
        private String address;
        private int index;
        private double proportion;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/internal/accountState")
    public Object getAccount(@RequestParam("blockHash") String blockHash, @RequestParam("publicKeyHash") String publicKeyHash) {
        Map<String, Object> ret = new HashMap<>();
        AccountState a = accountStateTrie.get(Hex.decode(blockHash), Hex.decode(publicKeyHash)).get();
        ret.put("data", a);
        HexBytes hash = HexBytes.fromBytes(HashUtil.keccak256(RLPCodec.encode(a)));
        ret.put("hash", hash);
        return ret;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/internal/accountStates")
    public PagedView<AccountInfo> getAccounts(@ModelAttribute PageSize pageSize) {
        Block latestConfirmed = wisdomRepository.getLatestConfirmed();
        long height = latestConfirmed.nHeight - (latestConfirmed.nHeight % 120);
        cacheLock.lock();
        try {
            List<AccountInfo> li = cache.get(height);
            if (li == null) {
                li = new ArrayList<>();
                Block h = bc.getHeaderByHeight(height);
                Trie<byte[], AccountState> trie = accountStateTrie.getTrieByBlockHash(h.getHash());
                List<AccountState> states = trie.values().stream().sorted((x, y) ->
                        -Long.compare(x.getAccount().getBalance(), y.getAccount().getBalance()))
                        .collect(Collectors.toList());

                long all = model.getTotal();
                for (int i = 0; i < states.size(); i++) {
                    AccountState s = states.get(i);
                    li.add(new AccountInfo(
                                    s.getAccount().getBalance(),
                                    Address.publicKeyHashToAddressByType(s.getAccount().getPubkeyHash(), s.getType()),
                                    i,
                                    s.getAccount().getBalance() * 1.0 / all
                            )
                    );
                }

                cache.put(height, li);
            }

            List<AccountInfo> states =
                    li.stream().skip(pageSize.getPage() * pageSize.getSize()).limit(pageSize.getSize())
                            .collect(Collectors.toList());

            if (cache.size() > 8) {
                cache.remove(cache.firstKey());
            }
            return new PagedView<>(li.size(), pageSize.getPage(), states);
        } finally {
            cacheLock.unlock();
        }
    }


    public static void main(String[] args) {
        System.out.println(Hex.toHexString(Address.addressToPublicKeyHash("1PpBHEx782C4VrtnQcJRTogn5UYmzCWAPH")));
    }

}
