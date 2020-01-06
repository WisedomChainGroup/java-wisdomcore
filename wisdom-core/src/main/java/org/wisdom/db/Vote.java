package org.wisdom.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tdf.rlp.RLP;

import static org.wisdom.db.Candidate.ATTENUATION_COEFFICIENT;
import static org.wisdom.db.Candidate.ATTENUATION_ERAS;

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

    public long getAccumulated(long era) {
        if (era <= getEra()) return 0L;
        long count = era - getEra() - 1;
        long accumulated = getAmount();
        for (long i = 0; i < count / ATTENUATION_ERAS; i++) {
            accumulated = ATTENUATION_COEFFICIENT.multiply(accumulated).longValue();
        }
        return accumulated;
    }
}
