package org.wisdom.controller;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.StateDB;
import org.wisdom.encoding.JSONEncodeDecoder;

/**
 * /internal/transaction/{} 包含未确认的事务
 * /internal/block/{} 包含未确认的区块
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
    private JSONEncodeDecoder codec;

    // 获取 forkdb 里面的事务
    @GetMapping(value = "/internal/transaction/{transactionHash}", produces = "application/json")
    public Object getTransaction(@PathVariable("transactionHash") String hash){
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
}
