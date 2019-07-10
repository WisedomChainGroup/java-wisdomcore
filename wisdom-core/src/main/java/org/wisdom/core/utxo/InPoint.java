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