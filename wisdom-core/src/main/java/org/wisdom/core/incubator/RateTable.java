package org.wisdom.core.incubator;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RateTable {

    public List<Rate> ratemap=new ArrayList<>();

    public class Rate{
        private int beginblock;
        private int endblock;
        private double rate120;
        private double rate365;

        public Rate(int beginblock, int endblock, double rate120, double rate365) {
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

        public double getRate120() {
            return rate120;
        }

        public void setRate120(double rate120) {
            this.rate120 = rate120;
        }

        public double getRate365() {
            return rate365;
        }

        public void setRate365(double rate365) {
            this.rate365 = rate365;
        }
    }

    public RateTable(){
        ratemap.add(new Rate(0,170000,0.0004271,0.0007119));
        ratemap.add(new Rate(170000,340000,0.0003203,0.0005339));
        ratemap.add(new Rate(340000,510000,0.0002402,0.0004004));
        ratemap.add(new Rate(510000,680000,0.0001802,0.0003003));
        ratemap.add(new Rate(680000,850000,0.0001352,0.0002252));
        ratemap.add(new Rate(850000,1020000,0.0001014,0.0001689));
        ratemap.add(new Rate(1020000,1190000,0.0000761,0.0001267));
        ratemap.add(new Rate(1190000,1360000,0.0000571,0.0000950));
        ratemap.add(new Rate(1360000,1530000,0.0000428,0.0000712));
        ratemap.add(new Rate(1530000,1700000,0.0000321,0.0000534));
        ratemap.add(new Rate(1700000,1870000,0.0000241,0.0000401));
        ratemap.add(new Rate(1870000,2040000,0.0000181,0.0000301));
        ratemap.add(new Rate(2040000,2210000,0.0000136,0.0000226));
        ratemap.add(new Rate(2210000,2380000,0.0000102,0.0000170));
        ratemap.add(new Rate(2380000,2550000,0.0000077,0.0000128));
        ratemap.add(new Rate(2550000,2720000,0.0000058,0.0000096));
    }

    public double selectrate(long height,int days){
        double rates=0;
        for(Rate rate:ratemap){
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
}
