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
        AssetIncreased assetIncreased = RLPElement.fromEncoded(payload).as(AssetIncreased.class);
        if (assetIncreased == null) {
            return false;
        }
        this.amount = assetIncreased.amount;
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(AssetIncreased.builder().amount(this.amount).build());
    }

    public static AssetIncreased getAssetIncreased(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(AssetIncreased.class);
    }
}
