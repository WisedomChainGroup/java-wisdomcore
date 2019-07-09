package org.wisdom.keystore.wallet;

public class Kdfparams {

    public Kdfparams(int memoryCost,int timeCost,int parallelism,String salt){
        this.memoryCost = memoryCost;
        this.timeCost = timeCost;
        this.parallelism = parallelism;
        this.salt = salt;
    }

    public int memoryCost;
    public int timeCost;
    public int parallelism;
    public String salt;

    public int getMemoryCost() {
        return memoryCost;
    }

    public void setMemoryCost(int memoryCost) {
        this.memoryCost = memoryCost;
    }

    public int getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(int timeCost) {
        this.timeCost = timeCost;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
