package org.wisdom;

public class Env {

    public String genesis;

    public String validatorsFile;

    public Env() {
    }

    public void setGenesis(String genesis) {
        this.genesis = genesis;
    }

    public void setValidatorsFile(String validatorsFile) {
        this.validatorsFile = validatorsFile;
    }

    public Env(String genesis, String validatorsFile) {
        this.genesis = genesis;
        this.validatorsFile = validatorsFile;
    }

    @Override
    public String toString() {
        return "Env{" +
                "genesis='" + genesis + '\'' +
                ", validatorsFile='" + validatorsFile + '\'' +
                '}';
    }
}
