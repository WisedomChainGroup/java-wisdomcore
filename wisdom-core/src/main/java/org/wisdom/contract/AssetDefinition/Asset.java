package org.wisdom.contract.AssetDefinition;

import lombok.*;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;
import org.wisdom.keystore.wallet.KeystoreAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset implements AnalysisContract {

    @RLP(0)
    private String code;
    @RLP(1)
    private long offering;
    @RLP(2)
    private long totalamount;
    @RLP(3)
    private byte[] createuser;  //公钥
    @RLP(4)
    private byte[] owner;   //公钥哈希
    @RLP(5)
    private int allowincrease;
    @RLP(6)
    private String info;

    private String createuserAddress;

    private String ownerAddress;

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
        return RLPCodec.encode(Asset.builder()
                .code(this.code)
                .offering(this.offering)
                .totalamount(this.totalamount)
                .createuser(this.createuser)
                .owner(this.owner)
                .allowincrease(this.allowincrease)
                .info(this.info).build());
    }

    private String HexCreateuserAddress(){
        return KeystoreAction.pubkeyToAddress(this.createuser,(byte)0x00,"WX");
    }

    private String HexOwnerAddress(){ return KeystoreAction.pubkeyHashToAddress(this.owner,(byte)0x00,"WX"); }

    public static Asset getAsset(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(Asset.class);
    }

    public static Asset getConvertAsset(byte[] Rlpbyte){
        Asset asset=getAsset(Rlpbyte);
        asset.setCreateuserAddress(asset.HexCreateuserAddress());
        asset.setOwnerAddress(asset.HexOwnerAddress());
        return asset;
    }
}
