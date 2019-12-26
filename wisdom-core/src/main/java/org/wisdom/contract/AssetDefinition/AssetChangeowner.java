package org.wisdom.contract.AssetDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;
import org.wisdom.db.AccountState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetChangeowner implements AnalysisContract {
    @RLP(0)
    private byte[] newowner;

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        AssetChangeowner assetChangeowner = RLPCodec.decode(payload, AssetChangeowner.class);
        if(assetChangeowner==null){
            return false;
        }
        this.newowner= assetChangeowner.getNewowner();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(AssetChangeowner.builder().newowner(this.newowner).build());
    }

    public static AssetChangeowner getAssetChangeowner(byte[] Rlpbyte){
        return RLPCodec.decode(Rlpbyte, AssetChangeowner.class);
    }
}
