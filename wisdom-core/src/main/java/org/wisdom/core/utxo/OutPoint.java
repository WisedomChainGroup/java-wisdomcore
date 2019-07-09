package org.wisdom.core.utxo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OutPoint {
    private long amount;
    private int scriptLength;
    private byte[] script;
    private int index;
    private int dataScriptLength;
    private byte[] dataScript;

    private String address;

    public int getDataScriptLength() {
        return dataScriptLength;
    }

    public void setDataScriptLength(int dataScriptLength) {
        this.dataScriptLength = dataScriptLength;
    }

    public byte[] getDataScript() {
        return dataScript;
    }

    public void setDataScript(byte[] dataScript) {
        this.dataScript = dataScript;
    }

    private byte[] transactionHash;

    @JsonIgnore
    public byte[] getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(byte[] transactionHash) {
        this.transactionHash = transactionHash;
    }

    @JsonIgnore
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // TODO: get target from script
    @JsonIgnore
    public String getTransferTarget() {
        return null;
    }
}
