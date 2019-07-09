package org.wisdom.core.validate;

import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.consensus.pow.ValidatorStateFactory;
import org.wisdom.util.Arrays;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Coinbase 校验规则
@Component
public class CoinbaseRule implements BlockRule, TransactionRule {

    @Autowired
    private ValidatorStateFactory factory;

    @Autowired
    private WisdomBlockChain bc;

    @Override
    public Result validateBlock(Block block) {

        // the first transaction of block must be coin base
        Transaction coinbase = block.body.get(0);
        if (coinbase == null) {
            return Result.Error("missing coin base transaction");
        }
        if (coinbase.type != Transaction.Type.COINBASE.ordinal()) {
            return Result.Error("the first transaction of block body must be coin base");
        }
        Block parent = bc.getBlock(block.hashPrevBlock);
        long nonce = factory.getInstance(parent).getNonceFromPublicKeyHash(coinbase.to);
        if (nonce + 1 != coinbase.nonce) {
            return Result.Error("the nonce of coin base transaction is invalid");
        }
        Result res = validateTransaction(coinbase);
        if (!res.isSuccess()) {
            return res;
        }

        long fees = 0;
        // the block body contains at most one coin base transaction
        for (int i = 1; i < block.body.size(); i++) {
            fees += block.body.get(i).getFee();
            if (block.body.get(i).type == Transaction.Type.COINBASE.ordinal()) {
                return Result.Error("a block contains at most one coin base ");
            }
        }

        // check amount = consensus amount + fees
        if (coinbase.amount != EconomicModel.getConsensusRewardAtHeight(block.nHeight) + fees) {
            return Result.Error("amount not equals to consensus reward plus fees");
        }
        return Result.SUCCESS;
    }

    // coin base transaction contains zero fields
    @Override
    public Result validateTransaction(Transaction transaction) {
        if (transaction == null) {
            return Result.Error("missing coin base transaction");
        }
        if (transaction.type != Transaction.Type.COINBASE.ordinal()) {
            return Result.Error("the first transaction of block body must be coin base");
        }
        if (!Arrays.areEqual(transaction.from, new byte[32])
                || transaction.gasPrice != 0
                || (transaction.payload != null &&  transaction.payload.length != 0)
                || !Arrays.areEqual(transaction.signature, new byte[64])
        ) {
            return Result.Error("coin base transaction has zero field other than null");
        }
        return Result.SUCCESS;
    }


}
