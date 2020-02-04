package org.wisdom.contract.HashheightblockDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPCodec;
import org.tdf.rlp.RLPElement;
import org.wisdom.contract.AnalysisContract;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashheightblockGet implements AnalysisContract {
    @RLP(0)
    private byte[] transferhash;
    @RLP(1)
    private String origintext;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        HashheightblockGet hashheightblockGet = RLPElement.fromEncoded(payload).as(HashheightblockGet.class);
        if (hashheightblockGet == null) {
            return false;
        }
        this.transferhash = hashheightblockGet.transferhash;
        this.origintext = hashheightblockGet.origintext;
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(HashheightblockGet.builder()
                .transferhash(this.transferhash)
                .origintext(this.origintext).build());
    }

    public static HashheightblockGet getHashheightblockGet(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(HashheightblockGet.class);
    }
}
