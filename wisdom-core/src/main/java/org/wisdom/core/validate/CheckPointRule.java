package org.wisdom.core.validate;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Setter
public class CheckPointRule implements BlockRule {

    private boolean openCheckPoint;

    private Map<Long, String> confirms;

    @Autowired
    private AccountDB accountDB;

    @Autowired
    private WisdomBlockChain wisdomBlockChain;

    public CheckPointRule(@Value("${wisdom.open-check-point}") boolean openCheckPoint) {
        this.openCheckPoint = openCheckPoint;
        this.confirms = new HashMap<Long, String>() {
            {
                put(20000L, "f24b52d9d918c2a0948cd1efa2881473981d9b5ef7cf4d5567ab1948423782a8");
                put(50000L, "77c77fda2967cbe861cbd7a1f70bc5349c2dd684bb3c5798f1c92516a21f44fb");
                put(70000L, "59756f599e67c56ee6bca2f6e85c8b95f23de982a7f1ac6b581433e8bae11f3d");
                put(100000L, "43a1545c84e7e2fa25cfda1d389a5c1923f9f9d1f3418628aa7b543703d667e3");
                put(130000L, "d9bfe00c89c893ee6ab16a6a19f562e6edeef4eb8308e9d2d0bbc86123a5a004");
                put(160000L, "133aa371ee687ed6f18a72584ba91df5eebf985303e9a1e8adfede0ea9060770");
                put(200000L, "36c450ad56b8978628b97e4fb5b020eb479dd2e5b5867e05db7b8abe498b77f0");
                put(250000L, "31a835c68c296741793b1517e35359dd2565ef8d711ca95db8b9dc1773a67720");
                put(300000L, "5387ed5c7dd4edea1247479d562ae89b43f6faf2a8e9be8ce1dade5f89674761");
                put(400000L, "78a7d913a7c9bcf335a2003c203f0f347c92edee9496f337772f9a8d2ce36e8f");
                put(500000L, "10ecba1383be2a4aa55f6b7e32bf181dc99d36ec1f7da94fbdb3bbb56266764e");
                put(600000L, "7d736040823abfc081f5e7981503d3f48dd3c1d5ff422575edd687146da70f1e");
                put(700000L, "db51d99385a6c81079d8fdf108cec5fd546923faa7895bdc31f673618d54fbe1");
                put(800000L, "c65ee3dc3ab338edb31511331bc451169797adaa7369d936db9ab4b5eed66364");
            }
        };
    }

    @Override
    public Result validateBlock(Block block) {
        if (!openCheckPoint) {
            return Result.SUCCESS;
        }
        if (!confirms.containsKey(block.nHeight)) {
            return Result.SUCCESS;
        }
        if (block.getHashHexString().equals("")) {
            return Result.Error("block hash is empty");
        }
        String blockHash = confirms.get(block.nHeight);
        if (!block.getHashHexString().equals(blockHash)) {
            return Result.Error("check point : block hash is incorrect, height is " + block.nHeight);
        }
        return Result.SUCCESS;
    }

    // TODO: reduce this io !!!!
    // validateDBBlock 校验数据库中的检查点
    public Result validateDBCheckPoint() {
        if (!openCheckPoint) {
            return Result.SUCCESS;
        }
        long height = accountDB.getBestHeight();
        List<Long> heights = confirms.keySet().stream().filter(h -> h <= height).collect(Collectors.toList());
        for (long h : heights) {
            Block b = wisdomBlockChain.getBlockByHeight(h);
            String blockHash = confirms.get(h);
            if (!b.getHashHexString().equals(blockHash)) {
                return Result.Error("db has been written to forking blocks");
            }
        }
        return Result.SUCCESS;
    }

}
