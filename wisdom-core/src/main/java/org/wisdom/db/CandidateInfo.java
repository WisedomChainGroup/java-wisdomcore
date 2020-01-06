package org.wisdom.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.tdf.common.util.HexBytes;

@AllArgsConstructor
@Builder
@Getter
public class CandidateInfo {
    private HexBytes publicKeyHash;
    private long mortgage;
    private long amount;
    private long accumulated;

    public static CandidateInfo fromCandidate(Candidate candidate, long era) {
        return CandidateInfo
                .builder()
                .publicKeyHash(candidate.getPublicKeyHash())
                .mortgage(candidate.getMortgage())
                .amount(candidate.getAmount())
                .accumulated(candidate.getAccumulated(era))
                .build();
    }
}
