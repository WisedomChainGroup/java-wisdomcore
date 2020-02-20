package org.wisdom.contract.MultipleDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.encoders.Hex;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multiple implements AnalysisContract {

    @RLP(0)
    private byte[] assetHash;
    @RLP(1)
    private int max;
    @RLP(2)
    private int min;
    @RLP(3)
    private List<byte[]> pubList;//公钥
    @RLP(4)
    private long amount;
    @RLP(5)
    private List<byte[]> signatureList;//签名

    private String assetHashHex;

    private List<String> pubListHex;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        Multiple multiple = RLPElement.fromEncoded(payload).as(Multiple.class);
        if (multiple == null) {
            return false;
        }
        this.assetHash = multiple.getAssetHash();
        this.max = multiple.getMax();
        this.min = multiple.getMin();
        this.pubList = multiple.getPubList();
        this.amount = multiple.getAmount();
        this.signatureList = multiple.getSignatureList();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(Multiple.builder()
                .assetHash(this.assetHash)
                .max(this.max)
                .min(this.min)
                .pubList(this.pubList)
                .amount(this.amount)
                .signatureList(this.signatureList).build());
    }

    private String HexAssetHash(){
        return Hex.toHexString(this.assetHash);
    }

    private List<String> HexPubList(){
        List<String> list=new ArrayList<>();
        this.pubList.stream().forEach(publist->{
            list.add(Hex.toHexString(publist));
        });
        return list;
    }

    public static Multiple getMultiple(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(Multiple.class);
    }

    public static Multiple getConvertMultiple(byte[] Rlpbyte){
        Multiple multiple=getMultiple(Rlpbyte);
        multiple.setAssetHashHex(multiple.HexAssetHash());
        multiple.setPubListHex(multiple.HexPubList());
        return multiple;
    }
}
