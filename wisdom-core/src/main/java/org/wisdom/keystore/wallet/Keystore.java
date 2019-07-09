package org.wisdom.keystore.wallet;

public class Keystore {
    public String address;
    public Crypto crypto;
    public Kdfparams kdfparams;
    public String id;
    public String version;
    public String mac;
    public String kdf;
    private static final int saltLength = 32;
    private static final int ivLength = 16;
    private static final String defaultVersion = "1";



    public Keystore(String address, Crypto crypto, String id, String version, String mac, String kdf,Kdfparams kdfparams) {
        this.address = address;
        this.crypto = crypto;
        this.id = id;
        this.version = version;
        this.mac = mac;
        this.kdf = kdf;
        this.kdfparams = kdfparams;

    }
    public Keystore() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getKdf() {
        return kdf;
    }

    public void setKdf(String kdf) {
        this.kdf = kdf;
    }

    public Kdfparams getKdfparams() {
        return kdfparams;
    }

    public void setKdfparams(Kdfparams kdfparams) {
        this.kdfparams = kdfparams;
    }
}









