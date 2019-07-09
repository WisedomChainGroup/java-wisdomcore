package org.wisdom.core.validate;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.consensus.pow.ConsensusConfig;
import org.wisdom.consensus.pow.TargetState;
import org.wisdom.consensus.pow.TargetStateFactory;
import org.wisdom.encoding.BigEndian;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

// 共识校验规则
// 1. 目标值符合
// 2. 不是孤块
// 3. 区块高度正确
// 4. 时间戳递增
@Component
public class ConsensusRule implements BlockRule{

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private TargetStateFactory factory;

    @Autowired
    ConsensusConfig consensusConfig;

    @Value("${wisdom.consensus.block-interval-upper-bound}")
    private int blockIntervalUpperBound;

    @Override
    public Result validateBlock(Block block) {
        Block parent = bc.getBlock(block.hashPrevBlock);
        // 不接受孤块
        if (parent == null){
            return  Result.Error("failed to find parent block");
        }
        // 父区块高度增1
        if (parent.nHeight + 1 != block.nHeight){
            return  Result.Error("block height invalid");
        }
        // 高度2以上的区块的出块者必须在一定时间内出块
        if (block.nHeight != 1 &&
                (block.nTime <= parent.nTime
                        || (block.nTime - parent.nTime) > blockIntervalUpperBound
                )
        ){
            return  Result.Error("block time invalid");
        }
        long endTime = consensusConfig.getEndTime(
                parent, block.nTime, Hex.encodeHexString(block.body.get(0).to)
        );
        // 出块者节点工作量证明不可以超时
        if(block.nTime >= endTime){
            return Result.Error("the validator propose at invalid round " + block.nHeight + " " + block.nTime + " " + endTime);
        }
        List<Block> bs = bc.getBlocks(block.nHeight, block.nHeight);
        // 此高度已接受到更优节点出的块
        for(Block b0: bs){
            if(consensusConfig.getEndTime(parent, b0.nTime, Hex.encodeHexString(b0.body.get(0).to)) <= endTime){
                return Result.Error("a better block has received in this height");
            }
        }
        // 难度值符合调整难度值
        TargetState state = factory.getInstance(block);
        if (BigEndian.decodeUint256(block.nBits).compareTo(state.getTarget()) > 0){
            return  Result.Error("block nbits invalid");
        }
        return Result.SUCCESS;
    }

    public ConsensusRule() {
    }
}
