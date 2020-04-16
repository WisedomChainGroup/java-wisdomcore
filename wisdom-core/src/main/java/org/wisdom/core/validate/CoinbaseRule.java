/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.core.validate;

import lombok.Setter;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.db.WisdomRepository;
import org.wisdom.util.Arrays;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Coinbase 校验规则
@Component
@Setter
public class CoinbaseRule implements BlockRule, TransactionRule {

    @Autowired
    private WisdomRepository repository;

    @Autowired
    private EconomicModel economicModel;

    public CoinbaseRule() {
    }

    @Override
    public Result validateBlock(Block block) {
        if(block.body == null || block.body.size() == 0){
            return Result.Error("missing block body");
        }
        // the first transaction of block must be coin base
        Transaction coinbase = block.body.get(0);
        if (coinbase == null) {
            return Result.Error("missing coin base transaction");
        }
        if (coinbase.type != Transaction.Type.COINBASE.ordinal()) {
            return Result.Error("the first transaction of block body must be coin base");
        }
        Block parent = repository.getBlockByHash(block.hashPrevBlock);
        if (parent == null) {
            return Result.Error("cannot find parent " + Hex.encodeHexString(block.hashPrevBlock) + " " + (block.nHeight-1));
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
        if (coinbase.amount != economicModel.getConsensusRewardAtHeight1(block.nHeight) + fees) {
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
                || (transaction.payload != null && transaction.payload.length != 0)
                || !Arrays.areEqual(transaction.signature, new byte[64])
        ) {
            return Result.Error("coin base transaction has zero field other than null");
        }
        return Result.SUCCESS;
    }


}