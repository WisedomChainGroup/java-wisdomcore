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

public class UTXO {

    private byte txtype;
    private byte[] hash;
    private int index;
    private long amount;
    private long height;
    private String address;
    private byte[] outscript;
    private byte[] datascript;
    private boolean is_reference;
    private boolean is_confirm;

    public UTXO(){

    }

    public byte getTxtype() {
        return txtype;
    }

    public void setTxtype(byte txtype) {
        this.txtype = txtype;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

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

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getOutscript() {
        return outscript;
    }

    public void setOutscript(byte[] outscript) {
        this.outscript = outscript;
    }

    public byte[] getDatascript() {
        return datascript;
    }

    public void setDatascript(byte[] datascript) {
        this.datascript = datascript;
    }

    public boolean isIs_reference() {
        return is_reference;
    }

    public void setIs_reference(boolean is_reference) {
        this.is_reference = is_reference;
    }

    public boolean isIs_confirm() {
        return is_confirm;
    }

    public void setIs_confirm(boolean is_confirm) {
        this.is_confirm = is_confirm;
    }

}