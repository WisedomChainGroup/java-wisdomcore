package org.wisdom.db;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.util.ByteUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class AssetCodeUpdater extends AbstractStateUpdater<Boolean> {

    @Override
    Map<byte[], Boolean> getGenesisStates() {
        return Collections.emptyMap();
    }

    @Override
    Set<byte[]> getRelatedKeys(Transaction transaction) {
        if(transaction.type!=Transaction.Type.DEPLOY_CONTRACT.ordinal() || transaction.getMethodType()!=0) Collections.emptySet();
        ByteArraySet set = new ByteArraySet();
        Asset asset=Asset.getAsset(ByteUtil.bytearrayridfirst(transaction.payload));
        set.add(asset.getCode());
        return set;
    }

    @Override
    Boolean update(byte[] id, Boolean state, Block block, Transaction transaction) {
        if(transaction.type!=Transaction.Type.DEPLOY_CONTRACT.ordinal() || transaction.getMethodType()!=0) return state;
        Asset asset=Asset.getAsset(ByteUtil.bytearrayridfirst(transaction.payload));
        if(Arrays.equals(id, asset.getCode())) return true;
        return state;
    }

    @Override
    Boolean createEmpty(byte[] id) {
        return false;
    }
}
