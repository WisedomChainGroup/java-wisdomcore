package org.wisdom.keystore.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UTCTimeUtil {
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;

    /**
     * 得到UTC时间，类型为字符串，格式为"yyyy-MM-dd HH:mm"<br />
     * 如果获取失败，返回null
     * @return
     */
    public static String getUTCTimeStr() {
        StringBuffer UTCTimeBuffer = new StringBuffer();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance() ;
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        UTCTimeBuffer.append(year).append("-").append(month).append("-").append(day) ;
        UTCTimeBuffer.append(" ").append(hour).append(":").append(minute).append(":").append(second) ;
        try{
            String date=format.format(format.parse(UTCTimeBuffer.toString()) );
            return date;
        }catch(ParseException e)
        {
            e.printStackTrace() ;
        }
        return null ;

    }

    /**
     * utc时间转成local时间戳
     * @param utcTime
     * @return
     */
    public static String utcToLocalLong(String utcTime){
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = format.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = format.format(utcDate.getTime());
        return localTime;
    }



/*    public static void main(String[] args) {
        String UTCTimeStr = getUTCTimeStr();
        String a = utcToLocalLong(UTCTimeStr);


        System.out.println(UTCTimeStr);
        System.out.println(utcToLocalLong(UTCTimeStr));
    }*/
}
