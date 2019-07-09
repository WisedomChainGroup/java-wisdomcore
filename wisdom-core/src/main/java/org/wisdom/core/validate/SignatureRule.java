package org.wisdom.core.validate;

import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.springframework.stereotype.Component;

// 签名校验规则
@Component
public class SignatureRule implements BlockRule, TransactionRule{
    @Override
    public Result validateBlock(Block block) {
        for(int i = 1; i < block.body.size(); i++){
            Result res = validateTransaction(block.body.get(i));
            if (!res.isSuccess()){
                return res;
            }
        }
        return Result.SUCCESS;
    }

    @Override
    public Result validateTransaction(Transaction transaction) {
        if( transaction.type == Transaction.Type.COINBASE.ordinal()){
            return Result.SUCCESS;
        }
        boolean res = new Ed25519PublicKey(transaction.from).verify(transaction.getRawForSign(), transaction.signature);
        if (!res){
            return Result.Error("signature validate fail");
        }
        return Result.SUCCESS;
    }
}
