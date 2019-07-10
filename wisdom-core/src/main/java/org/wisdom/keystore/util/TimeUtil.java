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

package org.wisdom.keystore.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /**
     * @return get current timestamp,precision to second
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }


    /**
     * @return get the current timezone string
     */
    public static String getCurrentTimezone() {
        String timezone = System.getProperty("user.timezone");
        return timezone;
    }


    /**
     * @param timestamp precision to second,10 digits
     * @return date string
     */
    public static String convertTimestampToDateString(long timestamp) {
        String rs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp * 1000));
        return rs;
    }


    /**
     * @param strDate
     * @return timestamp precision to second,10 digits
     */
    public static long convertDateStringToTimestamp(String strDate) {
        long timestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .parse(strDate, new ParsePosition(0)).getTime() / 1000;
        return timestamp;
    }


    /**
     * @param strDate
     * @return date string to date
     * @throws ParseException
     */
    public static Date convertDateStringToDate(String strDate) throws ParseException {

        Date dtParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
        return dtParse;

    }


    public static Date addDate(Date paramDate, EnumTimeType ett, int adds) {
        Date myDate;
        switch (ett) {

            case Year:
                myDate= DateUtils.addYears(paramDate, adds);
                break;

            case Month:
                myDate= DateUtils.addMonths(paramDate, adds);
                break;

            case Day:
                myDate= DateUtils.addDays(paramDate, adds);
                break;

            case Hour:
                myDate= DateUtils.addHours(paramDate, adds);
                break;

            case Minute:
                myDate= DateUtils.addMinutes(paramDate, adds);
                break;

            case Second:
                myDate= DateUtils.addSeconds(paramDate, adds);
                break;

            default:
                myDate= DateUtils.addDays(paramDate, adds);
        }
        return myDate;
    }

}