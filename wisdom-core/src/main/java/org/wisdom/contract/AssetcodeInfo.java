package org.wisdom.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetcodeInfo {
    private byte[] code;
    private byte[] asset160hash;

    public boolean isEmpty() {
        if (this.code == null || this.asset160hash == null) {
            return false;
        }
        return true;
    }
}
