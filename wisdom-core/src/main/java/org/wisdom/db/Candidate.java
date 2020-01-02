package org.wisdom.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.math3.fraction.BigFraction;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDecoding;
import org.wisdom.consensus.pow.ProposersState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Candidate {

    private static final long ATTENUATION_ERAS = getenv("ATTENUATION_ERAS", 2160);

    // 投票数每次衰减 10%
    private static final BigFraction ATTENUATION_COEFFICIENT = new BigFraction(9, 10);

    private static int getenv(String key, int defaultValue) {
        String v = System.getenv(key);
        if (v == null || v.equals("")) return defaultValue;
        return Integer.parseInt(v);
    }

    public static Candidate createEmpty(byte[] publicKeyHash){
        return new Candidate(0L, new ByteArrayMap<>(), new ByteArrayMap<>(), publicKeyHash, 0L);
    }

    @RLP(0)
    private long mortgage;

    // transaction hash -> votes
    @JsonIgnore
    @RLP(1)
    @RLPDecoding(as = ByteArrayMap.class)
    private Map<byte[], Vote> receivedVotes;

    @JsonIgnore
    @RLP(2)
    @RLPDecoding(as = ByteArrayMap.class)
    // transaction hash -> count
    private Map<byte[], Long> erasCounter;

    @RLP(3)
    private byte[] publicKeyHash;

    @JsonIgnore
    @RLP(4)
    private Long votesCache;

    public Candidate(){
        receivedVotes = new HashMap<>();
        erasCounter = new HashMap<>();
        publicKeyHash = new byte[0];
        mortgage = 0;
        votesCache = 0L;
    }

    public Candidate(long mortgage, Map<byte[], Vote> receivedVotes, Map<byte[], Long> erasCounter, byte[] publicKeyHash, long votesCache) {
        this.mortgage = mortgage;
        this.receivedVotes = receivedVotes;
        this.erasCounter = erasCounter;
        this.publicKeyHash = publicKeyHash;
        this.votesCache = votesCache;
    }

    public long getMortgage() {
        return mortgage;
    }

    public void setMortgage(long mortgage) {
        this.mortgage = mortgage;
    }

    public Map<byte[], Vote> getReceivedVotes() {
        return receivedVotes;
    }

    public void setReceivedVotes(Map<byte[], Vote> receivedVotes) {
        this.receivedVotes = receivedVotes;
    }

    public Map<byte[], Long> getErasCounter() {
        return erasCounter;
    }

    public void setErasCounter(Map<byte[], Long> erasCounter) {
        this.erasCounter = erasCounter;
    }

    public byte[] getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKeyHash(byte[] publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }


    public Long getVotesCache() {
        return votesCache;
    }

    public void setVotesCache(Long votesCache) {
        this.votesCache = votesCache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidateState = (Candidate) o;
        return mortgage == candidateState.mortgage &&
                receivedVotes.equals(candidateState.receivedVotes) &&
                erasCounter.equals(candidateState.erasCounter) &&
                Arrays.equals(publicKeyHash, candidateState.publicKeyHash) &&
                votesCache.equals(candidateState.votesCache);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mortgage, receivedVotes, erasCounter);
        result = 31 * result + Arrays.hashCode(publicKeyHash);
        return result;
    }

    public Candidate copy(){
        Candidate candidate = new Candidate();
        candidate.setErasCounter(this.erasCounter);
        candidate.setMortgage(this.mortgage);
        candidate.setPublicKeyHash(this.publicKeyHash);
        candidate.setReceivedVotes(this.receivedVotes);
        candidate.setVotesCache(this.votesCache);
        return  candidate;
    }

    public long getAmount() {
        if (votesCache != null) {
            return votesCache;
        }
        this.votesCache = receivedVotes.values().stream().map(Vote::getAmount).reduce(Long::sum).orElse(0L);
        return this.votesCache;
    }

    public long getAccumulated() {
        return receivedVotes.values().stream().map(Vote::getAccumulated).reduce(Long::sum).orElse(0L);
    }

    void increaseEraCounters() {
        erasCounter.replaceAll((k, v) -> v + 1);
    }

    void attenuation() {
        clearVotesCache();
        for (byte[] k : erasCounter.keySet()) {
            if (erasCounter.get(k) < ATTENUATION_ERAS) {
                continue;
            }
            erasCounter.put(k, 0L);
            Vote v = receivedVotes.get(k);
            Vote v2 = new Vote(v.getFrom(), v.getAmount(), new BigFraction(receivedVotes.get(k).getAccumulated(), 1L)
                    .multiply(ATTENUATION_COEFFICIENT)
                    .longValue());
            receivedVotes.put(k, v2);
        }
    }

    public void clearVotesCache() {
        votesCache = null;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "mortgage=" + mortgage +
                ", receivedVotes=" + receivedVotes +
                ", erasCounter=" + erasCounter +
                ", publicKeyHash=" + Arrays.toString(publicKeyHash) +
                ", votesCache=" + votesCache +
                '}';
    }
}
