package org.wisdom.genesis;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.wisdom.encoding.JSONEncodeDecoder;

import java.math.BigDecimal;
import java.util.List;

public class Genesis {
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();

    public static class Config{
        @JsonProperty("chainId")
        public int chainID;

        @JsonProperty("Block")
        public int block;
    }

    public static class Alloc{
        public List<IncubateAmount> incubateAmount;

        public List<InitAmount> initAmount;

        public List<UserIncubateAmount> userIncubateAmount;
    }

    public static class IncubateAmount{
        @JsonProperty("addr")
        public String address;

        public int balance;
    }

    public static class InitAmount{
        @JsonProperty("addr")
        public String address;

        public BigDecimal balance;
    }

    public static class UserIncubateAmount{
        @JsonProperty("addr")
        public String address; // to

        public BigDecimal balance; //

        public int days;

        @JsonProperty("remaindays")
        public int remainDays;

        @JsonProperty("shareaddr")
        public String shareAddress;

        public BigDecimal interest;

        public BigDecimal share;
    }


    public Config config;

    public String  coinbase;

    @JsonProperty("nBits")
    public String nBits;

    public String nonce;

    @JsonProperty("extraData")
    public String extraData;

    @JsonProperty("hashBlock")
    public String hashBlock;

    @JsonProperty("parentHash")
    public String parentHash;

    public long timestamp;

    public Alloc alloc;

    public Genesis(){

    }

}
