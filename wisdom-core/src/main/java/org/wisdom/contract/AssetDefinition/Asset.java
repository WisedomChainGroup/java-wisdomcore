package org.wisdom.contract.AssetDefinition;

import lombok.*;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;
import org.wisdom.db.AccountState;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset implements AnalysisContract {

    public enum AssetRule {
        CHANGEOWNER, TRANSFER, INCREASED
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
    @RLP(6)
    private byte[] info;

    @Override
    public List<AccountState> update(List<AccountState> accountStateList) {
        return null;
    }

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        Asset asset = RLPElement.fromEncoded(payload).as(Asset.class);
        if (asset == null) {
            return false;
        }
        this.code = asset.getCode();
        this.offering = asset.getOffering();
        this.totalamount = asset.getTotalamount();
        this.createuser = asset.getCreateuser();
        this.owner = asset.getOwner();
        this.allowincrease = asset.getAllowincrease();
        this.info = asset.getInfo();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(new Asset(
                this.getCode(),
                this.getOffering(),
                this.getTotalamount(),
                this.getCreateuser(),
                this.getOwner(),
                this.getAllowincrease(),
                this.getInfo()
        ));
    }

    public Asset copy() {
        return new Asset(code, offering, totalamount, createuser, owner, allowincrease,info);
    }

    public static Asset getAsset(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(Asset.class);
    }
}
