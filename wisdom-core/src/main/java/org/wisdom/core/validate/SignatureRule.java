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

import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.springframework.stereotype.Component;
import org.wisdom.encoding.JSONEncodeDecoder;

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