package org.wisdom.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetCodeInfo {
    private byte[] code;
    private byte[] asset160hash;
}
