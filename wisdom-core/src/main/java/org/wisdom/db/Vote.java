package org.wisdom.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    @RLP(0)
    private byte[] from;
    @RLP(1)
    private long amount;
    @RLP(2)
    private long era;
}
