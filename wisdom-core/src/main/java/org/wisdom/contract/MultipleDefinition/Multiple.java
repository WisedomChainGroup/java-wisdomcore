package org.wisdom.contract.MultipleDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDeserializer;
import org.tdf.rlp.RLPElement;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.contract.AnalysisContract;
import org.wisdom.db.AccountState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multiple implements AnalysisContract {

    @RLP(0)
    private byte[] assetHash;
    @RLP(1)
    private int min;
    @RLP(2)
    private int max;
    @RLP(3)
    private List<byte[]> pubList;//公钥hash
    @RLP(4)
    private long amount;

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        try{
            Multiple multiple= RLPDeserializer.deserialize(payload,Multiple.class);
            this.assetHash=multiple.getAssetHash();
            this.min=multiple.getMin();
            this.max=multiple.getMax();
            this.pubList=multiple.getPubList();
            this.amount=multiple.getAmount();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPElement.encode(Multiple.builder()
                            .assetHash(this.assetHash)
                            .min(this.min)
                            .max(this.max)
                            .pubList(this.pubList)
                            .amount(this.amount).build()).getEncoded();
    }
}
