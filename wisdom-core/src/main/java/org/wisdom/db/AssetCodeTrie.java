package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.contract.AssetCodeInfo;
import org.wisdom.core.Block;

@Component
public class AssetCodeTrie extends AbstractStateTrie<AssetCodeInfo>  {


    public AssetCodeTrie(Block genesis, DatabaseStoreFactory factory) {
        super(AssetCodeInfo.class, new AssetCodeUpdater(), genesis, factory, false, false);
    }

    @Override
    protected String getPrefix() {
        return "asset-code";
    }
}
