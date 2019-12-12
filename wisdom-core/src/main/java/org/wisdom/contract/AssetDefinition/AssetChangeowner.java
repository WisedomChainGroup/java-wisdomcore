package org.wisdom.contract.AssetDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDeserializer;
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
        try{
            AssetChangeowner assetChangeowner = RLPDeserializer.deserialize(payload, AssetChangeowner.class);
            this.newowner= assetChangeowner.getNewowner();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPElement.encode(AssetChangeowner.builder().newowner(this.newowner).build()).getEncoded();
    }
}
