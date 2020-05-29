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

import org.bouncycastle.util.Arrays;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.WisdomRepository;
import org.wisdom.encoding.BigEndian;

import javax.validation.Validation;


// 基本规则校验 校验区块版本号，字段类型, pow，交易 merkle root
@Component
public class BasicRule implements BlockRule, TransactionRule {

    @Autowired
    private WisdomRepository wisdomRepository;

    @Value("${wisdom.consensus.block-interval}")
    private int blockInterval;

    private Block genesis;
    private static javax.validation.Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory().getValidator();

    @Value("${p2p.max-blocks-per-transfer}")
    private int orphanHeightsRange;

    @Override
    public Result validateBlock(Block block) {
        Block best = wisdomRepository.getBestBlock();
        Block latestConfirmed = wisdomRepository.getLatestConfirmed();
        if (block == null) {
            return Result.Error("null block");
        }
        if(block.getnHeight() <= latestConfirmed.getnHeight()){
            return Result.Error(String.format("the block height %d of %s less than latest confirmed height %d hash %s", block.getnHeight(), block.getHashHexString(), latestConfirmed.getnHeight(), latestConfirmed.getHashHexString()));
        }
        if (Math.abs(best.nHeight - block.nHeight) > orphanHeightsRange) {
            return Result.Error("the block height " + block.nHeight + " is too small or too large, current height is " + best.nHeight);
        }
        // 区块基本校验 字段值非空
        if (validator.validate(block).size() != 0) {
            return Result.Error(validator.validate(block).toArray()[0].toString());
        }
        // 区块时间戳必须在一个周期的时间内
        if (block.nTime - System.currentTimeMillis() / 1000 > blockInterval) {
            return Result.Error("the received block timestamp too large");
        }
        // 区块大小限制
        if (block.size() > Block.MAX_BLOCK_SIZE) {
            return Result.Error("block size exceed");
        }
        // 不可以接收创世区块
        if (block.nHeight == 0 || Arrays.areEqual(block.getHash(), genesis.getHash())) {
            return Result.Error("cannot write genesis block");
        }
        // 区块体不可以为空
        if (block.body == null || block.body.size() == 0) {
            return Result.Error("missing body");
        }
        // 区块版本
        if (block.nVersion != genesis.nVersion) {
            return Result.Error("version check fail");
        }
        // pow 校验
        if (BigEndian.compareUint256(Block.calculatePOWHash(block), block.nBits) >= 0) {
            return Result.Error("pow validate fail");
        }
        for (Transaction tx : block.body) {
            Result r = validateTransaction(tx);
            if (!r.isSuccess()) {
                return r;
            }
        }
        return Result.SUCCESS;
    }

    @Override
    public Result validateTransaction(Transaction transaction) {
        if (validator.validate(transaction).size() != 0 || transaction.version != Transaction.DEFAULT_TRANSACTION_VERSION) {
            return Result.Error(validator.validate(transaction).toArray()[0].toString() + "missing fields or version invalid");
        }
        // 1. deposit 事务的 amount 必须为 0
        if (transaction.type == Transaction.Type.DEPOSIT.ordinal() && transaction.amount != 0) {
            return Result.Error("the amount of deposit must be zero");
        }
        return Result.SUCCESS;
    }

    @Autowired
    public BasicRule(Block genesis, @Value("${node-character}") String character) {
        this.genesis = genesis;
    }
}
