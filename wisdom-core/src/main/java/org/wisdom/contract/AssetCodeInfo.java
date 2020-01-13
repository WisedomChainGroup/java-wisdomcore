package org.wisdom.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetCodeInfo {
    @RLP(0)
    private byte[] code;
    @RLP(1)
    private byte[] asset160hash;
}
