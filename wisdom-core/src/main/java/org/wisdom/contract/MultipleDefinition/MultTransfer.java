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
public class MultTransfer implements AnalysisContract {
    @RLP(0)
    private int origin;//0是普通账户地址，1是多签地址
    @RLP(1)
    private int dest;
    @RLP(2)
    private byte[] pubhash;
    @RLP(3)
    private List<byte[]> signaturesList;
    @RLP(4)
    private byte[] to;
    @RLP(5)
    private long value;

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
            MultTransfer multTransfer= RLPDeserializer.deserialize(payload,MultTransfer.class);
            this.origin=multTransfer.getOrigin();
            this.dest=multTransfer.getDest();
            this.pubhash=multTransfer.getPubhash();
            this.signaturesList=multTransfer.getSignaturesList();
            this.to=multTransfer.getTo();
            this.value=multTransfer.getValue();
        }catch (Exception e){
            return false;
        }
        return false;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPElement.encode(MultTransfer.builder()
                .origin(this.origin)
                .dest(this.dest)
                .pubhash(this.pubhash)
                .signaturesList(this.signaturesList)
                .to(this.to)
                .value(this.value).build()).getEncoded();
    }
}
