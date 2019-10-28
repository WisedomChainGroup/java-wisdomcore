package org.wisdom.controller;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.core.Block;
import org.wisdom.core.OrphanBlocksManager;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.StateDB;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.util.Address;

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
    private StateDB stateDB;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private JSONEncodeDecoder codec;

    @Autowired
    private OrphanBlocksManager manager;

    // 获取 forkdb 里面的事务
    @GetMapping(value = "/internal/transaction/{transactionHash}", produces = "application/json")
    public Object getTransaction(@PathVariable("transactionHash") String hash) {
        try {
            Block best = stateDB.getBestBlock();
            byte[] h = Hex.decodeHex(hash.toCharArray());
            Transaction tx = stateDB.getTransaction(best.getHash(), h);
            if (tx != null) {
                return codec.encodeTransaction(tx);
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
        return stateDB.getBestBlock().nHeight;
    }

    // 获取孤块池/forkdb 中的区块
    @GetMapping(value = "/internal/block/{blockInfo}", produces = "application/json")
    public Object getBlocks(@PathVariable("blockInfo") String blockInfo) {
        if (blockInfo.equals("orphan")) {
            return codec.encodeBlocks(manager.getOrphans());
        }
        if (blockInfo.equals("unconfirmed")) {
            return codec.encodeBlocks(stateDB.getAll());
        }
        try {
            byte[] hash = Hex.decodeHex(blockInfo);
            return stateDB.getBlock(hash);
        } catch (Exception e) {
            return getBlocksByHeight(blockInfo);
        }
    }

    public Object getBlocksByHeight(String height) {
        try {
            long h = Long.parseLong(height);
            return codec.encodeBlocks(stateDB.getBlocks(h, h, Integer.MAX_VALUE, false));
        } catch (Exception e) {
            return "invalid block path variable " + height;
        }
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
        if (limit == null || limit <= 0 ) {
            limit = Integer.MAX_VALUE;
        }

        if (!from.equals("") && !to.equals("")) {
            byte[] publicKey;
            try {
                publicKey = Hex.decodeHex(from);
            } catch (Exception e) {
                return e.getMessage();
            }
            if (typeParsed == null) {
                return codec.encodeTransactions(
                        stateDB.getTransactionsByFromAndTo(publicKey, Address.getPublicKeyHash(to), offset, limit)
                );
            }
            return codec.encodeTransactions(
                    stateDB.getTransactionsByFromToAndType(typeParsed, publicKey, Address.getPublicKeyHash(to), offset, limit)
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
                        stateDB.getTransactionsByFrom(publicKey, offset, limit)
                );
            }
            return codec.encodeTransactions(
                    stateDB.getTransactionsByFromAndType(typeParsed, publicKey, offset, limit)
            );
        }
        if (!to.equals("")) {
            if (typeParsed == null) {
                return codec.encodeTransactions(
                        stateDB.getTransactionsByTo(Address.getPublicKeyHash(to), offset, limit)
                );
            }
            return codec.encodeTransactions(
                    stateDB.getTransactionsByToAndType(typeParsed, Address.getPublicKeyHash(to), offset, limit)
            );
        }
        return "please provide from or to";
    }
}
