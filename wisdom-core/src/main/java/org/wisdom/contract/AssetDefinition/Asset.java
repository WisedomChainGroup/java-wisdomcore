package org.wisdom.contract.AssetDefinition;

import lombok.*;
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
public class Asset implements AnalysisContract {

    public enum AssetRule{
        changeowner,transfer,increased
    }

    @RLP(0)
    private String code;
    @RLP(1)
    private long offering;
    @RLP(2)
    private long totalamount;
    @RLP(3)
    private byte[] createuser;
    @RLP(4)
    private byte[] owner;
    @RLP(5)
    private int allowincrease;

    @Override
    public APIResult FormatCheck(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        try{
            Asset asset= RLPDeserializer.deserialize(payload,Asset.class);
            this.code=asset.getCode();
            this.offering=asset.getOffering();
            this.totalamount=asset.getTotalamount();
            this.createuser=asset.getCreateuser();
            this.owner=asset.getOwner();
            this.allowincrease=asset.getAllowincrease();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPElement.encode(new Asset(
                                        this.getCode(),
                                        this.getOffering(),
                                        this.getTotalamount(),
                                        this.getCreateuser(),
                                        this.getOwner(),
                                        this.getAllowincrease()
                                )).getEncoded();
    }

    public Asset copy(){
        return new Asset(code,offering,totalamount,createuser,owner,allowincrease);
    }
}
