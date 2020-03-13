package org.wisdom.contract.MultipleDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;

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
    private List<byte[]> from;
    @RLP(3)
    private List<byte[]> signatures;
    @RLP(4)
    private byte[] to;
    @RLP(5)
    private long value;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        MultTransfer multTransfer= RLPElement.fromEncoded(payload).as(MultTransfer.class);
        if(multTransfer==null){
            return false;
        }
        this.origin=multTransfer.getOrigin();
        this.dest=multTransfer.getDest();
        this.from=multTransfer.getFrom();
        this.signatures=multTransfer.getSignatures();
        this.to=multTransfer.getTo();
        this.value=multTransfer.getValue();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(MultTransfer.builder()
                .origin(this.origin)
                .dest(this.dest)
                .from(this.from)
                .signatures(this.signatures)
                .to(this.to)
                .value(this.value).build());
    }

    public static MultTransfer getMultTransfer(byte[] Rlpbyte){
        return RLPElement.fromEncoded(Rlpbyte).as(MultTransfer.class);
    }
}
