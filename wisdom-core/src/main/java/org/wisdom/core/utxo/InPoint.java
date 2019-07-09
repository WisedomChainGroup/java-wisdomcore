package org.wisdom.core.utxo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class InPoint {
    private byte[] previousTransactionHash;
    private int outPointIndex;
    private int scriptLength;
    private byte[] script;
    private byte[] transactionHash;
    private int intPointIndex;

    @JsonIgnore
    public int getIntPointIndex() {
        return intPointIndex;
    }

    public void setIntPointIndex(int intPointIndex) {
        this.intPointIndex = intPointIndex;
    }

    @JsonIgnore
    public byte[] getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(byte[] transactionHash) {
        this.transactionHash = transactionHash;
    }

    public byte[] getPreviousTransactionHash() {
        return previousTransactionHash;
    }

    public void setPreviousTransactionHash(byte[] previousTransactionHash) {
        this.previousTransactionHash = previousTransactionHash;
    }

    public int getOutPointIndex() {
        return outPointIndex;
    }

    public void setOutPointIndex(int outPointIndex) {
        this.outPointIndex = outPointIndex;
    }

    public int getScriptLength() {
        return scriptLength;
    }

    public void setScriptLength(int scriptLength) {
        this.scriptLength = scriptLength;
    }

    public byte[] getScript() {
        return script;
    }

    public void setScript(byte[] script) {
        this.script = script;
    }

    // TODO: get transfer owner from script
    @JsonIgnore
    public String getTransferOwner() {
        return null;
    }
}
