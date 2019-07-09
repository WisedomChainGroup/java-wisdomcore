package org.wisdom.core.validate;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.Arrays;
import org.wisdom.encoding.BigEndian;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.Block;
import org.wisdom.core.account.Account;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Validation;
import java.util.List;
import java.util.Map;

// 基本规则校验 校验区块版本号，字段类型, pow，交易 merkle root
@Component
public class BasicRule implements BlockRule, TransactionRule{
    @Autowired
    MerkleRule merkleRule;

    private Block genesis;
    private static javax.validation.Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory().getValidator();
    private static final Logger logger = LoggerFactory.getLogger(BasicRule.class);
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();

    @Override
    public Result validateBlock(Block block) {
        if (block == null){
            return Result.Error("null block");
        }
        // 区块基本校验
        if (validator.validate(block).size() != 0){
            return Result.Error(validator.validate(block).toArray()[0].toString());
        }
        // 不可以接收创世区块
        if(block.nHeight == 0 || Arrays.areEqual(block.getHash(), genesis.getHash())){
            return Result.Error("cannot write genesis block");
        }
        // 区块体不可以为空
        if (block.body == null || block.body.size() == 0){
            return Result.Error("missing body");
        }
        // 区块版本
        if (block.nVersion != genesis.nVersion){
            return Result.Error("version check fail");
        }
        // pow 校验
        if (BigEndian.compareUint256(Block.calculatePOWHash(block), block.nBits) >= 0){
            return Result.Error("pow validate fail");
        }
        // 梅克尔根校验
        if (!Arrays.areEqual(block.hashMerkleRoot, Block.calculateMerkleRoot(block.body))){
            return Result.Error("merkle root validate fail " + new String(codec.encodeBlock(block)) + " " + Hex.encodeHexString(block.hashMerkleRoot) + " " + Hex.encodeHexString(Block.calculateMerkleRoot(block.body)));
        }
        try {
            Map<String,Object> merklemap = merkleRule.validateMerkle(block.body,block.nHeight);
            List<Account> accountList= (List<Account>) merklemap.get("account");
            List<Incubator> incubatorList= (List<Incubator>) merklemap.get("incubator");
            if(!Arrays.areEqual(block.hashMerkleState,Block.calculateMerkleState(accountList))){
                return Result.Error("merkle state validate fail " + new String(codec.encodeBlock(block)) + " " + Hex.encodeHexString(block.hashMerkleState) + " " + Hex.encodeHexString(Block.calculateMerkleState(accountList)));
            }
            if(!Arrays.areEqual(block.hashMerkleIncubate,Block.calculateMerkleIncubate(incubatorList))){
                return Result.Error("merkle incubate validate fail " + new String(codec.encodeBlock(block)) + " " + Hex.encodeHexString(block.hashMerkleIncubate) + " " + Hex.encodeHexString(Block.calculateMerkleIncubate(incubatorList)));
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        for(Transaction tx: block.body){
            Result r = validateTransaction(tx);
            if (!r.isSuccess()){
                return r;
            }
        }
        return Result.SUCCESS;
    }

    @Override
    public Result validateTransaction(Transaction transaction) {
        if (validator.validate(transaction).size() != 0 || transaction.version != Transaction.DEFAULT_TRANSACTION_VERSION){
            return Result.Error(validator.validate(transaction).toArray()[0].toString() + "missing fields or version invalid");
        }
        // 1. deposit 事务的 amount 必须为 0
        if (transaction.type == Transaction.Type.DEPOSIT.ordinal() && transaction.amount != 0){
            return Result.Error("the amount of deposit must be zero");
        }
        return Result.SUCCESS;
    }

    @Autowired
    public BasicRule(Block genesis) {
        this.genesis = genesis;
    }
}
