package org.wisdom.contract.AssetDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.wisdom.contract.AnalysisContract;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetTransfer implements AnalysisContract {
    @RLP(0)
    private byte[] from;
    @RLP(1)
    private byte[] to;
    @RLP(2)
    private long value;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        AssetTransfer assetTransfer = RLPCodec.decode(payload, AssetTransfer.class);
        if (assetTransfer == null) {
            return false;
        }
        this.from = assetTransfer.getFrom();
        this.to = assetTransfer.getTo();
        this.value = assetTransfer.getValue();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(AssetTransfer.builder()
                .from(this.from)
                .to(this.to)
                .value(this.value).build());
    }

    public static AssetTransfer getAssetTransfer(byte[] Rlpbyte) {
        return RLPCodec.decode(Rlpbyte, AssetTransfer.class);
    }
}
