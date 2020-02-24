package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.contract.HashheightblockDefinition.HashheightblockGet;
import org.wisdom.contract.HashtimeblockDefinition.HashtimeblockGet;
import org.wisdom.core.account.Transaction;
import org.wisdom.util.ByteUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class LockgetTransferUpdater extends AbstractStateUpdater<LockTransferInfo> {

    @Override
    public Map<byte[], LockTransferInfo> getGenesisStates() {
        return Collections.emptyMap();
    }

    @Override
    public Set<byte[]> getRelatedKeys(Transaction transaction) {
        if (transaction.type != Transaction.Type.CALL_CONTRACT.ordinal() || (transaction.getMethodType() != 5 && transaction.getMethodType() != 7)) {
            return Collections.emptySet();
        }
        ByteArraySet set = new ByteArraySet();
        byte[] rplpayload=ByteUtil.bytearrayridfirst(transaction.payload);
        if(transaction.getMethodType()==5) {//获取锁定时间
            HashtimeblockGet hashtimeblockGet=HashtimeblockGet.getHashtimeblockGet(rplpayload);
            set.add(hashtimeblockGet.getTransferhash());
        }
        if(transaction.getMethodType()==7){//获取锁定高度
            HashheightblockGet hashheightblockGet=HashheightblockGet.getHashheightblockGet(rplpayload);
            set.add(hashheightblockGet.getTransferhash());
        }
        return set;
    }

    @Override
    public LockTransferInfo update(byte[] id, LockTransferInfo state, TransactionInfo info) {
        Transaction transaction = info.getTransaction();
        if (state != null) {
            throw new RuntimeException("LockTransferInfo is not a null exception");
        }
        byte[] rplpayload=ByteUtil.bytearrayridfirst(transaction.payload);
        byte[] transhash=new byte[0];
        if(transaction.getMethodType()==5) {//获取锁定时间
            HashtimeblockGet hashtimeblockGet=HashtimeblockGet.getHashtimeblockGet(rplpayload);
            transhash=hashtimeblockGet.getTransferhash();
        }
        if(transaction.getMethodType()==7){//获取锁定高度
            HashheightblockGet hashheightblockGet=HashheightblockGet.getHashheightblockGet(rplpayload);
            transhash=hashheightblockGet.getTransferhash();
        }
        if(transhash.length==0){
            throw new RuntimeException("LockTransferInf's transHash is not null exception");
        }
        return LockTransferInfo.builder().transHash(transhash).build();
    }

    @Override
    public LockTransferInfo createEmpty(byte[] id) {
        return null;
    }
}
