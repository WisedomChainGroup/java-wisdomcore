package org.wisdom.contract.HashtimeblockDefinition;

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
public class HashtimeblockGet implements AnalysisContract {
    @RLP(0)
    private byte[] transferhash;
    @RLP(1)
    private String origintext;

    @Override
    public boolean RLPdeserialization(byte[] payload) {
        HashtimeblockGet hashtimeblockGet = RLPElement.fromEncoded(payload).as(HashtimeblockGet.class);
        if (hashtimeblockGet == null) {
            return false;
        }
        this.transferhash = hashtimeblockGet.getTransferhash();
        this.origintext = hashtimeblockGet.getOrigintext();
        return true;
    }

    @Override
    public byte[] RLPserialization() {
        return RLPCodec.encode(HashtimeblockGet.builder()
                .transferhash(this.transferhash)
                .origintext(this.origintext).build());
    }

    public static HashtimeblockGet getHashtimeblockGet(byte[] Rlpbyte) {
        return RLPElement.fromEncoded(Rlpbyte).as(HashtimeblockGet.class);
    }
}
