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
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.springframework.stereotype.Component;

// 地址校验规则
// 除了存证事务to全部填0，其他事务的to必须是合法的公钥哈希
@Component
public class AddressRule implements TransactionRule, BlockRule{
    @Override
    public Result validateTransaction(Transaction transaction) {
        boolean isValid;
        if (transaction.type == Transaction.Type.DEPOSIT.ordinal()){
            isValid = Arrays.areEqual(transaction.to, new byte[transaction.to.length]);
        }else{
            isValid = KeystoreAction.verifyAddress(
                    KeystoreAction.pubkeyHashToAddress(transaction.to, (byte)0x00,"")
            ) == 0;
        }
        if(!isValid){
            return Result.Error("to address is no valid");
        }
        return Result.SUCCESS;
    }

    @Override
    public Result validateBlock(Block block) {
        for(int i = 0; i < block.body.size(); i++){
            Result res = validateTransaction(block.body.get(i));
            if (!res.isSuccess()){
                return res;
            }
        }
        return Result.SUCCESS;
    }
}