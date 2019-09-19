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

import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.ConsensusConfig;
import org.wisdom.consensus.pow.Proposer;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.db.StateDB;
import org.wisdom.encoding.BigEndian;
import org.wisdom.core.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

// 共识校验规则
// 1. 目标值符合
// 2. 不是孤块
// 3. 区块高度正确
// 4. 时间戳递增
@Component
public class ConsensusRule implements BlockRule {
    private EraLinkedStateFactory targetStateFactory;

    @Autowired
    ConsensusConfig consensusConfig;

    private StateDB stateDB;

    @Autowired
    public ConsensusRule(StateDB stateDB) {
        this.stateDB = stateDB;
        this.targetStateFactory = stateDB.getTargetStateFactory();
    }

    @Override
    public Result validateBlock(Block block) {
        Block parent = stateDB.getBlock(block.hashPrevBlock);
        // 不接受孤块
        if (parent == null) {
            return Result.Error("failed to find parent block");
        }
        // 父区块高度增1
        if (parent.nHeight + 1 != block.nHeight) {
            return Result.Error("block height invalid");
        }
        // 出块在是否在合理时间内出块
        Optional<Proposer> p = consensusConfig.getProposer(parent, block.nTime);
        if (!p.
                map(x -> x.pubkeyHash.equals(Hex.encodeHexString(block.body.get(0).to)))
                .orElse(false)) {
            return Result.Error("the proposer cannot propose this block");
        }
        // 难度值符合调整难度值
        TargetState state = (TargetState) targetStateFactory.getInstance(block);
        if (BigEndian.decodeUint256(block.nBits).compareTo(state.getTarget()) > 0) {
            return Result.Error("block nbits invalid");
        }
        return Result.SUCCESS;
    }

    public ConsensusRule() {
    }
}
