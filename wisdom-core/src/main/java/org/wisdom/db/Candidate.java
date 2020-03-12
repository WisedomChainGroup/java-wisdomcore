package org.wisdom.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.fraction.BigFraction;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.HexBytes;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDecoding;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {

    public static long ATTENUATION_ERAS = 2160;

    // 投票数每次衰减 10%
    static final BigFraction ATTENUATION_COEFFICIENT = new BigFraction(9, 10);

    private static int getenv(String key, int defaultValue) {
        String v = System.getenv(key);
        if (v == null || v.equals("")) return defaultValue;
        return Integer.parseInt(v);
    }

    public static Candidate createEmpty(byte[] publicKeyHash) {
        Candidate candidate = new Candidate();
        candidate.setPublicKeyHash(HexBytes.fromBytes(publicKeyHash));
        return candidate;
    }

    @RLP(0)
    private HexBytes publicKeyHash;

    @RLP(1)
    private long mortgage;

    // transaction hash -> votes
    @JsonIgnore
    @RLP(2)
    @RLPDecoding(as = ByteArrayMap.class)
    private Map<byte[], Vote> receivedVotes = new ByteArrayMap<>();

    // has been blocked
    @RLP(3)
    private boolean blocked;

    @JsonIgnore
    private Long votesCache;

    @JsonIgnore
    private HashMap<Long, Long> cache = new HashMap<>();

    public long getAmount() {
        if (votesCache != null) {
            return votesCache;
        }
        this.votesCache = receivedVotes.values().stream().map(Vote::getAmount).reduce(Long::sum).orElse(0L);
        return this.votesCache;
    }

    public long getAccumulated(long era) {
        Long ret = cache.get(era);
        if (ret != null) return ret;
        ret = receivedVotes.values().stream()
                .map(v -> v.getAccumulated(era))
                .reduce(Long::sum)
                .orElse(0L);
        cache.put(era, ret);
        return ret;
    }

    public Candidate copy() {
        return new Candidate(publicKeyHash, mortgage, new ByteArrayMap<>(receivedVotes), blocked, null, null);
    }
}
