package com.jmotionsoft.towntalk.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarUtil {
    private Calendar mCalendar;
    private SimpleDateFormat dateFormatDay, dateFormatMonth;

    public CalendarUtil(){
        initCalendar();
        dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatMonth = new SimpleDateFormat("yyyy-MM");
    }

    public String getFirstDayOfWeek(){
        mCalendar.set(Calendar.DAY_OF_WEEK, mCalendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        return dateFormatDay.format(mCalendar.getTime());
    }

    public String getLastDayOfWeek(){
        mCalendar.set(Calendar.DAY_OF_WEEK, mCalendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        return dateFormatDay.format(mCalendar.getTime());
    }

    public String getFirstDayOfMonth(){
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dateFormatDay.format(mCalendar.getTime());
    }

    public String getLastDayOfMonth(){
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormatDay.format(mCalendar.getTime());
    }

    public String getFirstMonthOfYear(){
        mCalendar.set(Calendar.MONTH, mCalendar.getActualMinimum(Calendar.MONTH));
        return dateFormatMonth.format(mCalendar.getTime());
    }

    public String getLastMonthOfYear(){
        mCalendar.set(Calendar.MONTH, mCalendar.getActualMaximum(Calendar.MONTH));
        return dateFormatMonth.format(mCalendar.getTime());
    }

    public void add(int type, int addCount){
        mCalendar.add(type, addCount);
    }

    public void addWeek(int addWeek){
        mCalendar.add(Calendar.WEEK_OF_YEAR, addWeek);
    }

    public void addMonth(int addMonth){
        mCalendar.add(Calendar.MONTH, addMonth);
    }

    public void addYear(int addYear){
        mCalendar.add(Calendar.YEAR, addYear);
    }

    public void initCalendar(){
        mCalendar = Calendar.getInstance();
        mCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);
    }
}
