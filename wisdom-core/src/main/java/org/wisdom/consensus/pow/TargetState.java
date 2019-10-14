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

package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.math3.fraction.BigFraction;
import org.wisdom.encoding.BigEndian;
import org.wisdom.core.Block;
import org.wisdom.core.state.State;
import org.wisdom.core.account.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author sal 1564319846@qq.com
 * adjust difficulty per era
 */
@Component
public class TargetState implements State {
    private int blockInterval;
    private static final Logger logger = LoggerFactory.getLogger(TargetState.class);
    private static final long MAX_ADJUST_RATE = 16;

    private BigInteger target;

    public TargetState() {
    }

    @Autowired
    public TargetState(Block genesis, @Value("${wisdom.consensus.block-interval}") int blockInterval) {
        this.target = BigEndian.decodeUint256(genesis.nBits);
        this.blockInterval = blockInterval;
    }

    @Override
    public State updateBlock(Block block) {
        return this;
    }

    /**
     * Calculate new target difficulty as:
     * currentDifficulty * (adjustedTimespan / targetTimespan)
     * The result uses integer division which means it will be slightly
     * rounded down.  Bitcoind also uses integer division to calculate this
     * result.
     *
     * @param blocks
     * @return
     */
    @Override
    public State updateBlocks(List<Block> blocks) {
        long actualTimeSpent = blocks.get(blocks.size() - 1).nTime - blocks.get(0).nTime;
        if (actualTimeSpent <= 0) {
            target = target.divide(BigInteger.valueOf(MAX_ADJUST_RATE));
            return this;
        }
        BigFraction rate = new BigFraction(actualTimeSpent, (long) blockInterval * (blocks.size() - 1));
        if (rate.compareTo(new BigFraction(MAX_ADJUST_RATE)) > 0) {
            rate = new BigFraction(MAX_ADJUST_RATE);
        }
        if (rate.compareTo(new BigFraction(1, MAX_ADJUST_RATE)) < 0) {
            rate = new BigFraction(1, MAX_ADJUST_RATE);
        }
        target = safeTyMul(target, rate);
        logger.info("update blocks start from " + blocks.get(0).nHeight + " stop at " + blocks.get(blocks.size() - 1).nHeight + " target = " + Hex.encodeHexString(BigEndian.encodeUint256(target)));
        // TODO: remove assertion codes
        assert Arrays.equals(BigEndian.encodeUint256(target), blocks.get(0).nBits);
        return this;
    }

    private BigInteger safeTyMul(BigInteger target, BigFraction f){
        target = target.multiply(f.getNumerator());
        if (target.compareTo(BigEndian.MAX_UINT_256) > 0){
            target = BigEndian.MAX_UINT_256;
        }
        return target.divide(f.getDenominator());
    }

    @Override
    public State updateTransaction(Transaction transaction) {
        return this;
    }

    @Override
    public State copy() {
        TargetState d = new TargetState();
        d.target = target;
        d.blockInterval = blockInterval;
        return d;
    }

    public BigInteger getTarget() {
        return target;
    }
}
