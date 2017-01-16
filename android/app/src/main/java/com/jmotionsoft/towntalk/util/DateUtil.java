package com.jmotionsoft.towntalk.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    public final static String TAG = DateUtil.class.getSimpleName();

    public final static String FORMAT_YEAR = "yyyy";
    public final static String FORMAT_MONTH = "yyyy-MM";
    public final static String FORMAT_DAY = "yyyy-MM-dd";
    public final static String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_DATETIME_MIN = "MM.dd HH:mm";
    public final static String FORMAT_HOUR = "HH";

    public final static String SEARCH_DAY = "day";
    public final static String SEARCH_MONTH = "month";
    public final static String SEARCH_YEAR = "year";
    public final static String SEARCH_HOUR = "hour";

    public static String getCurrentYear(){
        return getCurrentDateFormat(FORMAT_YEAR);
    }

    public static String getCurrentMonth(){
        return getCurrentDateFormat(FORMAT_MONTH);
    }

    public static String getCurrentDay(){
        return getCurrentDateFormat(FORMAT_DAY);
    }

    public static String getCurrentDatetime(){
        return getCurrentDateFormat(FORMAT_DATETIME);
    }

    public static String getCurrentDateFormat(String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String dateToString(Date date, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getCalDateFormant(String format, int dateField, int calDay){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        Calendar calendar = Calendar.getInstance();
        calendar.add(dateField, calDay);

        return dateFormat.format(calendar.getTime());
    }

    public static String toDateFromatFromCalendar(String format, Calendar calendar){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(calendar.getTime());
    }

    public static int getDeviceDefaultSeconds(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATETIME);

        Calendar defaultCal = Calendar.getInstance();
        defaultCal.clear();
        defaultCal.set(Calendar.YEAR, 2015);

        Calendar todayCal = Calendar.getInstance();

        long defaultMillis = todayCal.getTimeInMillis() - defaultCal.getTimeInMillis();
        int defaultSeconds = (int) (defaultMillis / 1000);

        CLog.d(TAG, "send server time: " + dateFormat.format(todayCal.getTime()) + " - " + dateFormat.format(defaultCal.getTime()) + ": " + defaultSeconds);
        return defaultSeconds;
    }

    public static long getSecondsInterval(String format, String orgDateTime, String newDateTime)
            throws Exception{

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        Calendar oldCal = Calendar.getInstance();
        oldCal.setTime(dateFormat.parse(orgDateTime));

        Calendar newCal = Calendar.getInstance();
        newCal.setTime(dateFormat.parse(newDateTime));

        return (oldCal.getTimeInMillis() - newCal.getTimeInMillis()) / 1000;
    }

    public static long getSecondsInterval(String format, String orgDateTime)
            throws Exception{

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        Calendar oldCal = Calendar.getInstance();
        oldCal.setTime(dateFormat.parse(orgDateTime));

        Calendar newCal = Calendar.getInstance();

        return (oldCal.getTimeInMillis() - newCal.getTimeInMillis()) / 1000;
    }

    public static long getDayInterval(String format, String dateTime) throws Exception{
        long interval_second = getSecondsInterval(format, dateTime);
        return interval_second / (60 * 60 * 24);
    }

    public static Calendar getDeviceSyncCalendar(int syncSeconds){
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATETIME);

        Calendar deviceCal = Calendar.getInstance();
        deviceCal.clear();
        deviceCal.set(Calendar.YEAR, 2015);

        deviceCal.add(Calendar.SECOND, syncSeconds);

        CLog.d(TAG, "get server sync time: " + dateFormat.format(deviceCal.getTime()));
        return deviceCal;
    }

    public static boolean checkExpirationTimeForPhoneId(String compareDate) throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATETIME);

        Calendar today = Calendar.getInstance();

        Calendar compareDay = Calendar.getInstance();
        compareDay.setTime(dateFormat.parse(compareDate));
        compareDay.add(Calendar.DAY_OF_MONTH, 90);

        int compareResult = today.compareTo(compareDay);
        if(compareResult == -1){
            return true;
        }else{
            return false;
        }
    }

    public static String getDateByTimeZone(String _millisecond){
        long millisecond;
        try{
            millisecond = Long.parseLong(_millisecond);
        }catch (Exception e){
            CLog.e(TAG, e);
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        //int gmtOffset = calendar.getTimeZone().getRawOffset();
        //CLog.i(TAG, "offset: "+gmtOffset);
        calendar.setTimeInMillis(millisecond);
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_DATETIME_MIN);

        return df.format(calendar.getTime());
    }
}
