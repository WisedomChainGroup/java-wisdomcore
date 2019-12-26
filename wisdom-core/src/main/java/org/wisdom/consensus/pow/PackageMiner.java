package org.wisdom.consensus.pow;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.RateTable;
import org.wisdom.core.validate.MerkleRule;
import org.wisdom.db.AccountState;
import org.wisdom.db.StateDB;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.pool.TransPool;
import org.wisdom.pool.WaitCount;

import java.util.*;

//打包时选择事务
//校验转账事务和其他事务的余额,都更新AccountState
@Component
public class PackageMiner {

    @Autowired
    private PeningTransPool peningTransPool;

    @Autowired
    private StateDB stateDB;

    @Autowired
    private MerkleRule merkleRule;

    @Autowired
    private WaitCount waitCount;

    @Autowired
    private RateTable rateTable;

    public List<Transaction> TransferCheck(byte[] parenthash, long height, Block block) throws DecoderException {
        Map<String, TreeMap<Long, TransPool>> maps = peningTransPool.getAllMap();
        List<byte[]> pubhashlist = peningTransPool.getAllPubhash();
        Map<String, AccountState> accountStateMap = stateDB.getAccounts(parenthash, pubhashlist);
        if (accountStateMap.size() == 0) {
            return new ArrayList<>();
        }
        PackageCache packageCache = new PackageCache();
        packageCache.init(peningTransPool, stateDB, merkleRule, waitCount, rateTable,
                accountStateMap, maps, parenthash, block, height, block.size());
        List<Transaction> packageTransaction = packageCache.getRightTransactions();
        if (packageTransaction == null) {
            return new ArrayList<>();
        }
        return packageTransaction;
    }
}
