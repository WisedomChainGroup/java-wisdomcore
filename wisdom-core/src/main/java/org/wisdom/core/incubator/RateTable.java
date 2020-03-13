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

package org.wisdom.core.incubator;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Setter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.protobuf.tcp.command.HatchModel;

import java.util.*;

@Component
@Setter
public class RateTable {

    public List<Rate> ratemap=new ArrayList<>();
    public List<Rate> newratemap=new ArrayList<>();

    @Value("${wisdom.block-interval-switch-era}")
    private int era;

    public class Rate{
        private int beginblock;
        private int endblock;
        private String rate120;
        private String  rate365;

        public Rate(int beginblock, int endblock, String rate120, String  rate365) {
            this.beginblock = beginblock;
            this.endblock = endblock;
            this.rate120 = rate120;
            this.rate365 = rate365;
        }

        public int getBeginblock() {
            return beginblock;
        }

        public void setBeginblock(int beginblock) {
            this.beginblock = beginblock;
        }

        public int getEndblock() {
            return endblock;
        }

        public void setEndblock(int endblock) {
            this.endblock = endblock;
        }

        public String  getRate120() {
            return rate120;
        }

        public void setRate120(String  rate120) {
            this.rate120 = rate120;
        }

        public String  getRate365() {
            return rate365;
        }

        public void setRate365(String  rate365) {
            this.rate365 = rate365;
        }
    }

    public RateTable(){
        ratemap.add(new Rate(0,170000,"0.0004271","0.0007119"));
        ratemap.add(new Rate(170000,340000,"0.0003203","0.0005339"));
        ratemap.add(new Rate(340000,510000,"0.0002402","0.0004004"));
        ratemap.add(new Rate(510000,680000,"0.0001802","0.0003003"));
        ratemap.add(new Rate(680000,850000,"0.0001352","0.0002252"));
        ratemap.add(new Rate(850000,1020000,"0.0001014","0.0001689"));
        ratemap.add(new Rate(1020000,1190000,"0.0000761","0.0001267"));
        ratemap.add(new Rate(1190000,1360000,"0.0000571","0.0000950"));
        ratemap.add(new Rate(1360000,1530000,"0.0000428","0.0000712"));
        ratemap.add(new Rate(1530000,1700000,"0.0000321","0.0000534"));
        ratemap.add(new Rate(1700000,1870000,"0.0000241","0.0000401"));
        ratemap.add(new Rate(1870000,2040000,"0.0000181","0.0000301"));
        ratemap.add(new Rate(2040000,2210000,"0.0000136","0.0000226"));
        ratemap.add(new Rate(2210000,2380000,"0.0000102","0.0000170"));
        ratemap.add(new Rate(2380000,2550000,"0.0000077","0.0000128"));
        ratemap.add(new Rate(2550000,2720000,"0.0000058","0.0000096"));

        newratemap.add(new Rate(0,510000,"0.0004271","0.0007119"));
        newratemap.add(new Rate(510000,1020000,"0.0003203","0.0005339"));
        newratemap.add(new Rate(1020000,1530000,"0.0002402","0.0004004"));
        newratemap.add(new Rate(1530000,2040000,"0.0001802","0.0003003"));
        newratemap.add(new Rate(2040000,2550000,"0.0001352","0.0002252"));
        newratemap.add(new Rate(2550000,3060000,"0.0001014","0.0001689"));
        newratemap.add(new Rate(3060000,3570000,"0.0000761","0.0001267"));
        newratemap.add(new Rate(3570000,4080000,"0.0000571","0.0000950"));
        newratemap.add(new Rate(4080000,4590000,"0.0000428","0.0000712"));
        newratemap.add(new Rate(4590000,5100000,"0.0000321","0.0000534"));
        newratemap.add(new Rate(5100000,5610000,"0.0000241","0.0000401"));
        newratemap.add(new Rate(5610000,6120000,"0.0000181","0.0000301"));
        newratemap.add(new Rate(6120000,6630000,"0.0000136","0.0000226"));
        newratemap.add(new Rate(6630000,7140000,"0.0000102","0.0000170"));
        newratemap.add(new Rate(7140000,7650000,"0.0000077","0.0000128"));
        newratemap.add(new Rate(7650000,8160000,"0.0000058","0.0000096"));
    }

    public String selectrate(long height,int days){
        String rates="";
        List<Rate> rateList=new ArrayList<>();
        if(era>=0){
            long updateheight=era*120;
            if(height>updateheight){
                rateList.addAll(newratemap);
            }else{
                rateList.addAll(ratemap);
            }
        }else{
            rateList.addAll(ratemap);
        }
        for(Rate rate:rateList){
            if(rate.getBeginblock()<=height && rate.getEndblock()>height){
                if(days==120){
                    return rate.getRate120();
                }else{
                    return rate.getRate365();
                }
            }
        }
        return rates;
    }

    public static void main(String agrs[]) throws DecoderException, InvalidProtocolBufferException {
        String s = "0a1000000000876e97e0000000000d8b0f301228373866666434636639653037363766323164646532343433613238333339333635353835313631341878";
        byte[] payload=Hex.decodeHex(s.toCharArray());
        HatchModel.Payload payloadproto = HatchModel.Payload.parseFrom(payload);
        int days = payloadproto.getType();
        System.out.println(days);
    }
}