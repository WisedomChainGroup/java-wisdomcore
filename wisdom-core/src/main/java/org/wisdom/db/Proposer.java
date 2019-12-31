package org.wisdom.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.rlp.RLP;
import org.tdf.rlp.RLPDecoding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Proposer {

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

    public Proposer(){
        receivedVotes = new HashMap<>();
        erasCounter = new HashMap<>();
        publicKeyHash = new byte[0];
        mortgage = 0;
    }

    public Proposer(long mortgage, Map<byte[], Vote> receivedVotes, Map<byte[], Long> erasCounter, byte[] publicKeyHash) {
        this.mortgage = mortgage;
        this.receivedVotes = receivedVotes;
        this.erasCounter = erasCounter;
        this.publicKeyHash = publicKeyHash;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposer proposerState = (Proposer) o;
        return mortgage == proposerState.mortgage &&
                receivedVotes.equals(proposerState.receivedVotes) &&
                erasCounter.equals(proposerState.erasCounter) &&
                Arrays.equals(publicKeyHash, proposerState.publicKeyHash);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mortgage, receivedVotes, erasCounter);
        result = 31 * result + Arrays.hashCode(publicKeyHash);
        return result;
    }

    @Override
    public String toString() {
        return "Proposer{" +
                "mortgage=" + mortgage +
                ", receivedVotes=" + receivedVotes +
                ", erasCounter=" + erasCounter +
                ", publicKeyHash=" + Arrays.toString(publicKeyHash) +
                '}';
    }
}
