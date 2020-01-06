package org.wisdom.db;

import org.springframework.stereotype.Component;
import org.wisdom.core.Block;

@Component
public class AssetCodeTrie extends AbstractStateTrie<Boolean>  {


    public AssetCodeTrie(Block genesis, DatabaseStoreFactory factory) {
        super(Boolean.class, new AssetCodeUpdater(), genesis, factory, false, false);
    }

    @Override
    protected String getPrefix() {
        return "asset-code";
    }
}
