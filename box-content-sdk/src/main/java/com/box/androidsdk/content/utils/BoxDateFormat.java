package com.box.androidsdk.content.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains methods for parsing and formatting dates for use with the Box API.
 */
public final class BoxDateFormat {

    private static final ThreadLocal<DateFormat> THREAD_LOCAL_HEADER_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        }
    };

    private static final FastDateFormat LOCAL_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZ");

    private BoxDateFormat() { }

    /**
     * Parses a date string returned by the Box API into a {@link java.util.Date} object.
     * @param  dateString     a string containing the date.
     * @return                the parsed date.
     * @throws java.text.ParseException if the string cannot be parsed into a valid date.
     */
    public static Date parse(String dateString) throws ParseException {
        Integer year = Integer.parseInt(dateString.substring(0, 4));
        Integer month = Integer.parseInt(dateString.substring(5, 7)) -1; //months start from 0
        Integer day = Integer.parseInt(dateString.substring(8, 10));
        Integer hour = Integer.parseInt(dateString.substring(11, 13));
        Integer minute = Integer.parseInt(dateString.substring(14, 16));
        Integer second = Integer.parseInt(dateString.substring(17, 19));
        String timeZoneHourOffset = dateString.substring(19);
        Calendar calendar = GregorianCalendar.getInstance(getTimeZone(timeZoneHourOffset));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTime();
    }

    private static ConcurrentHashMap<String,TimeZone> mTimeZones = new ConcurrentHashMap<String, TimeZone>(10);
    private static final int MILLIS_PER_HOUR = 1000 * 60 * 60;
    private static final int MILLIS_PER_MINUTE = 1000 * 60;
    private static TimeZone getTimeZone(final String offset) {
        TimeZone cached = mTimeZones.get(offset);
        if (cached != null) {
            return cached;
        }
        int parseOffset = 0;
        if (offset.equals("Z")){
            TimeZone zone = TimeZone.getTimeZone("UTC");
            mTimeZones.put(offset, zone);
            return zone;
        }

        
        // Fix for devices that run on Java6, as the parseInt from Integer class cannot handle
        //  the plus sign ("+") on the beginning.
        if(offset.charAt(0) == '+') {
            parseOffset++;
        }
        Integer offsetHours = Integer.parseInt(offset.substring(parseOffset, 3));
        // Parse any minute offset as well
        Integer offsetMinutes = Integer.parseInt((offset.substring(4)));
        int offsetMiliSec = offsetHours * MILLIS_PER_HOUR;
        if (offsetHours < 0) {
            offsetMiliSec -= (offsetMinutes * MILLIS_PER_MINUTE);
        } else {
            offsetMiliSec += (offsetMinutes * MILLIS_PER_MINUTE);
        }
        TimeZone zone = new SimpleTimeZone(offsetMiliSec, offset);
        mTimeZones.put(offset, zone);
        return zone;
    }

    /**
     * Formats a date as a string that can be sent to the Box API.
     * @param  date the date to format.
     * @return      a string containing the formatted date.
     */
    public static String format(Date date) {
        String format = LOCAL_DATE_FORMAT.format(date);
        // Java 6 does not have a convenient way of having the colon in the timezone offset
        return format.substring(0,22) + ":" + format.substring(22);
    }

    /**
     * Parses a date string returned by the Box API inside headers into a {@link java.util.Date} object.
     * @param  dateString     a string containing the date.
     * @return                the parsed date.
     * @throws java.text.ParseException if the string cannot be parsed into a valid date.
     */
    public static Date parseHeaderDate(String dateString) throws ParseException {
        return THREAD_LOCAL_HEADER_DATE_FORMAT.get().parse(dateString);
    }

    /**
     * Get a String to represent a time range.
     *
     * @param fromDate can use null if don't want to specify this.
     * @param toDate can use null if don't want to specify this.
     * @return The string will be time strings separated by a comma.
     * Trailing "from_date," and leading ",to_date" are also accepted if one of the date is null.
     * Returns null if both dates are null.
     */
    public static String getTimeRangeString(Date fromDate, Date toDate) {
        if (fromDate == null && toDate == null) {
            return null;
        }

        StringBuilder sbr = new StringBuilder();
        if (fromDate != null) {
            sbr.append(format(fromDate));
        }
        sbr.append(",");
        if (toDate != null) {
            sbr.append(format(toDate));
        }
        return sbr.toString();
    }

    /**
     * Get back a from date and to date given a timeRangeString created from BoxDateForm.getTimeRangeString().
     * @param timeRangeString a timeRangeString created from BoxDateForm.getTimeRangeString()
     * @return an array of the from date and to date with Date[0] being from and Date[1] being to. Values can potentially be null.
     */
    public static Date[] getTimeRangeDates(String timeRangeString){
       if (SdkUtils.isEmptyString(timeRangeString)){
           return null;
       }
       String[] dateStrings = timeRangeString.split(",");
        Date[] dates = new Date[2];
        try {
            dates[0] = parse(dateStrings[0]);
        } catch (ParseException e){
        }catch (ArrayIndexOutOfBoundsException e){
        }
        try {
            dates[1] = parse(dateStrings[1]);
        } catch (ParseException e ){
        } catch (ArrayIndexOutOfBoundsException e){
        }
        return dates;

    }
}
