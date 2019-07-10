/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

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