package org.wisdom.db;

import lombok.*;
import org.tdf.rlp.RLP;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockTransferInfo {
    @RLP(0)
    private byte[] transHash;
    @RLP(1)
    private List<byte[]> lists;
}
