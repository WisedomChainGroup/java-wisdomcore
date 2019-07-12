package org.wisdom.consensus.pow;

public class Proposer {
    public String pubkeyHash;
    public long startTimeStamp;
    public long endTimeStamp;

    public Proposer(String pubkeyHash, long startTimeStamp, long endTimeStamp) {
        this.pubkeyHash = pubkeyHash;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
    }
}
