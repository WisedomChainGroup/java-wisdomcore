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
public class AssetIncreased implements AnalysisContract {
    @RLP(0)
    private long amount;

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        try{
            AssetIncreased assetIncreased = RLPElement.fromEncoded(payload).as(AssetIncreased.class);
            this.amount= assetIncreased.amount;
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(AssetIncreased.builder().amount(this.amount).build());
    }
}
