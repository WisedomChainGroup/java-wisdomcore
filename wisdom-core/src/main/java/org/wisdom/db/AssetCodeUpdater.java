package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArraySet;
import org.wisdom.contract.AssetDefinition.Asset;
import org.wisdom.contract.AssetcodeInfo;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.util.ByteUtil;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class AssetCodeUpdater extends AbstractStateUpdater<AssetcodeInfo> {

    @Override
    Map<byte[], AssetcodeInfo> getGenesisStates() {
        return Collections.emptyMap();
    }

    @Override
    Set<byte[]> getRelatedKeys(Transaction transaction) {
        if (transaction.type != Transaction.Type.DEPLOY_CONTRACT.ordinal() || transaction.getMethodType() != 0)
            return Collections.emptySet();
        ByteArraySet set = new ByteArraySet();
        Asset asset = Asset.getAsset(ByteUtil.bytearrayridfirst(transaction.payload));
        set.add(asset.getCode().getBytes(StandardCharsets.UTF_8));
        return set;
    }

    @Override
    AssetcodeInfo update(byte[] id, AssetcodeInfo state, Block block, Transaction transaction) {
        if (transaction.type != Transaction.Type.DEPLOY_CONTRACT.ordinal() || transaction.contractType != 0)
            return state;
        Asset asset = Asset.getAsset(ByteUtil.bytearrayridfirst(transaction.payload));
        if (asset == null) {
            return state;
        }
        if (Arrays.equals(asset.getCode().getBytes(StandardCharsets.UTF_8), state.getCode()) &&
                Arrays.equals(RipemdUtility.ripemd160(transaction.getHash()), state.getAsset160hash())) {
            return state;
        }
        return AssetcodeInfo.builder()
                .code(asset.getCode().getBytes(StandardCharsets.UTF_8))
                .asset160hash(RipemdUtility.ripemd160(transaction.getHash())).build();
    }

    @Override
    AssetcodeInfo createEmpty(byte[] id) {
        return AssetcodeInfo.builder().build();
    }
}
