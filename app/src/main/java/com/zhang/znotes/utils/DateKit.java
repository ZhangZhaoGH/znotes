package com.zhang.znotes.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateKit {

    public static String[] WEEK = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
    public static final int WEEKDAYS = 7;

    /**
     * 日期格式
     */
    private final static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 时间格式
     */
    private final static ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };

    /**
     * 时间格式
     */
    private final static ThreadLocal<SimpleDateFormat> minutesFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm");
        }
    };

    /**
     * 获取当前时间:Date
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 获取当前时间:Calendar
     */
    public static Calendar getCal() {
        return Calendar.getInstance();
    }

    /**
     * 日期转换为字符串:yyyy-MM-dd
     */
    public static String dateToStr(Date date) {
        if (date != null)
            return dateFormat.get().format(date);
        return null;
    }

    /**
     * 时间转换为字符串:yyyy-MM-dd HH:mm
     */
    public static String timeToStr(Date date) {
        if (date != null)
            return timeFormat.get().format(date);
        return null;
    }

    /**
     * 字符串转换为日期:yyyy-MM-dd
     */
    public static Date strToDate(String str) {
        Date date = null;
        try {
            date = dateFormat.get().parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *
     * 字符串转换为日期:HH:mm
     */
    public static String getTimeToTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }


    /**
     * 字符串转换为时间:yyyy-MM-dd HH:mm:ss
     */
    public static Date strToTime(String str) {
        Date date = null;
        try {
            date = timeFormat.get().parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 字符串转换为时间:HH:mm:
     */
    public static Date strToMinutesTime(String str) {
        Date date = null;
        try {
            date = minutesFormat.get().parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 友好的方式显示时间
     */
    public static String friendlyFormat(String str) {
        Date date = strToTime(str);
        if (date == null)
            return ":)";
        Calendar now = getCal();

        // 第一种情况，日期在同一天
        String curDate = dateFormat.get().format(now.getTime());
        String paramDate = dateFormat.get().format(date);
        if (curDate.equals(paramDate)) {
            int minute = (int) ((now.getTimeInMillis() - date.getTime()) / 60000);
            if (minute <= 2)
                return "刚刚";
            if (minute >= 2 && minute < 60)
                return minute + "分钟前";
            int hour = (int) ((now.getTimeInMillis() - date.getTime()) / 3600000);
            if (hour >= 1)
                return hour + "小时前";

            return "";
        }

        // 第二种情况，不在同一天
        int days = (int) ((getBegin(getDate()).getTime() - getBegin(date).getTime()) / 86400000);
        if (days == 1)
            return "昨天";
        if (days == 2)
            return "前天";
        if (days >= 3 && days <= 30) {
            return days + "天前";
        }

        // 第二种情况，不在同一月
        int months = (int) ((getBegin(getDate()).getTime() - getBegin(date).getTime()) / 86400000 / 30);
        if (months >= 1 && months <= 12)
            return months + "月前";

        // 第二种情况，不在同一年
        int years = (int) ((getBegin(getDate()).getTime() - getBegin(date).getTime()) / 86400000 / 12);
        if (years >= 1)
            return years + "年前";

        return timeToStr(date);
    }

    public static String showTime(Date date) {
        if (date == null)
            return "";
        Calendar now = getCal();

        // 第一种情况，日期在同一天
        String curDate = dateFormat.get().format(now.getTime());
        String paramDate = dateFormat.get().format(date);
        if (curDate.equals(paramDate)) {
            return getTimeToTime(date);
        }else {
            return    new SimpleDateFormat("MM/dd").format(date);
        }

    }

    /**
     * 格式化时间 今天昨天
     *
     * @param str
     * @return zz
     */
    public static String parseTime(String str) {
        Date date = strToTime(str);
        if (date == null)
            return "";
        Calendar now = Calendar.getInstance();
        //判断的时间
        Calendar other = Calendar.getInstance();
        other.setTime(date);
        //判断是不是当天
        if (now.get(Calendar.YEAR) == other.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) ==
                other.get(Calendar.DAY_OF_YEAR)) {
            return "今天";
        }
        //判断是不是昨天
        now.add(Calendar.DAY_OF_YEAR, -1);
        if (now.get(Calendar.YEAR) == other.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) ==
                other.get(Calendar.DAY_OF_YEAR)) {
            return "昨天";
        }
        String time=timeToStr(date).substring(5,10);
        String times=time.replace("-","月");

        return times;
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        String mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mMINUTE = String.valueOf(c.get(Calendar.MINUTE));
        int num=Integer.parseInt(mMINUTE);
        if (num<10){
            mMINUTE="0"+mMINUTE;
        }
        int month=Integer.parseInt(mMonth);
        if (month<10){
            mMonth="0"+mMonth;
        }
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        //2017-03-26 14:53:13
        return mYear + "-" + mMonth + "-" + mDay+"  星期"+mWay+"  "+mHour+":"+mMINUTE;
    }

    public static long strToMm(String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }

        return calendar.getTimeInMillis();
    }

    public static String strToWeek(String time) {
        Date date = strToDate(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }

        return WEEK[dayIndex - 1];
    }


    /**
     * 返回日期的0点:2012-07-07 20:20:20 --> 2012-07-07 00:00:00
     */
    public static Date getBegin(Date date) {
        return strToTime(dateToStr(date) + " 00:00:00");
    }
}
