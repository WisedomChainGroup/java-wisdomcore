package org.wisdom.consensus.pow;

public class Proposer {
    public byte[] pubkeyHash;
    public long startTimeStamp;
    public long endTimeStamp;

    public Proposer(byte[] pubkeyHash, long startTimeStamp, long endTimeStamp) {
        this.pubkeyHash = pubkeyHash;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
    }
}
