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