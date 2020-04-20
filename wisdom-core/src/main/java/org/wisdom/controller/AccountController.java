package org.wisdom.controller;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tdf.common.trie.Trie;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
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

    private TreeMap<Long, List<AccountState>> cache = new TreeMap<>();

    private Lock cacheLock = new ReentrantLock();

    private final WisdomRepository wisdomRepository;

    private final WisdomBlockChain bc;

    @RequestMapping(method = RequestMethod.GET, value = "/internal/accountState")
    public Object getAccount(@RequestParam("blockHash") String blockHash, @RequestParam("publicKeyHash") String publicKeyHash) {
        return accountStateTrie.get(Hex.decode(blockHash), Hex.decode(publicKeyHash)).get();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/internal/accountStates")
    public PagedView<AccountState> getAccounts(@ModelAttribute PageSize pageSize) {
        Block latestConfirmed = wisdomRepository.getLatestConfirmed();
        long height = latestConfirmed.nHeight - (latestConfirmed.nHeight % 120);
        cacheLock.lock();
        try {
            List<AccountState> li = cache.get(height);
            if(li == null){
                Block h = bc.getHeaderByHeight(height);
                Trie<byte[], AccountState> trie = accountStateTrie.getTrieByBlockHash(h.getHash());
                li = trie.values().stream().sorted((x, y) ->
                    -Long.compare(x.getAccount().getBalance(), y.getAccount().getBalance()))
                        .collect(Collectors.toList());
                cache.put(height, li);
            }

            List<AccountState> states =
                    li.stream().skip(pageSize.getPage() * pageSize.getSize()).limit(pageSize.getSize())
                            .collect(Collectors.toList());

            if(cache.size() > 8){
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
