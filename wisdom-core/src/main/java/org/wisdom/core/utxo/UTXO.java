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
