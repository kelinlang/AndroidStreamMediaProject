package com.lib.commonlib.utils.time;

import android.text.TextUtils;

import com.lib.commonlib.utils.bytes.ByteTools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Author: Robert
 * Date:  2016-08-29
 * Copyright (c) 2016,dudu Co.,Ltd. All rights reserved.
 * Desc: 时间转换工具类
 * <p>
 * 备注： 原TimeUtil 全部划归到本文件
 */
public class DateTimeUtils {

    /**
     * 格式定义 yyyy-MM-dd
     */
    public static String format = "yyyy-MM-dd";
    /**
     * 格式定义 yyyy-MM-dd HH:mm:ss
     */
    public static String format1 = "yyyy-MM-dd HH:mm:ss";
    /**
     * 格式定义 yyyyMMddHHmmss
     */
    public static String format2 = "yyyyMMdd";
    /**
     * 格式定义 yyyyMMddHHmmss
     */
    public static String format3 = "yyyy-MM-dd_HH:mm:ss";
    /**
     * The constant format5.
     */
    public static String format5 = "yyyyMMddHHmmss";
    /**
     * The constant format6.
     */
    public static String format6 = "yyyy-MM-dd_HH-mm-ss-SSS";

    /**
     * Obtain current time string.
     *
     * @return the string
     */
    public static String obtainCurrentTime() {
        String datetime;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        datetime = sdf.format(date);
        return datetime;
    }


    /**
     * Obtain current time string.
     *
     * @param format the format
     * @return the string
     */
    public static String obtainCurrentTime(String format) {
        String datetime;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        datetime = sdf.format(date);
        return datetime;
    }

    /**
     * Transform date date.
     *
     * @param dateString the date string
     * @return the date
     */
    public static Date transformDate(String dateString) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.getMessage();
        }
        return date;
    }

    /**
     * Transform date date.
     *
     * @param dateString the date string
     * @param format     the format
     * @return the date
     */
    public static Date transformDate(String dateString, String format) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.getMessage();
        }
        return date;
    }


    /**
     * 获得当前时间的格式化输出
     *
     * @param format 格式
     * @return 返回日期字符串 string
     */
    public static String format(String format) {
        String str = null;
        ;
        try {
            if (format != null) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                str = sdf.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 将毫秒数转换为hh小时mm分格式 ,分钟四舍五入
     *
     * @param time the time
     * @return houe and minute
     */
    public static String getHoueAndMinute(long time) {
        if (time == 0) {
            return 0 + "min";
        }
        String str = "";
        int second = (int) (time / (1000) % 60);
        int minute = (int) (time / (1000 * 60));
        int hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        minute += (int) Math.rint(((float) second) / 60);
        if (hour != 0)
            str = hour + "h";
        if (minute != 0)
            str += minute + "min";
        return str;
    }

    /**
     * String tolong long.
     *
     * @param time the time
     * @return the long
     */
    public static long stringTolong(String time) {
        if (TextUtils.isEmpty(time))
            return 0;
        long t = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(format1);
        try {
            t = sdf.parse(time).getTime();
        } catch (ParseException e) {
            // TODO: handle exception
        }
        return t;
    }

    /**
     * Gets h hmm.
     *
     * @param time the time
     * @return the h hmm
     */
    public static String getHHmm(String time) {
        String result = "";
        if (TextUtils.isEmpty(time))
            return result;
        long ltime = stringTolong(time);
        if (ltime == 0)
            return result;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ltime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        if (mm < 10) {
            result += hour + ":0" + mm;
        } else {
            result += hour + ":" + mm;
        }
        return result;
    }

    /**
     * 时间转换类
     *
     * @param dateStr   the date str
     * @param formatStr the format str
     * @return string string
     */
    public static String dataFormatMMdd(String dateStr, String formatStr) {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }
        String result = "";
        SimpleDateFormat sdf;
        if (TextUtils.isEmpty(formatStr)) {
            sdf = new SimpleDateFormat(format1);
        } else {
            sdf = new SimpleDateFormat(formatStr);
        }
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            if (1 == month.length()) {
                month = "0" + month;
            }
            String day = String.valueOf(calendar.get(Calendar.DATE));
            if (1 == day.length()) {
                day = "0" + day;
            }
            result += month + "月" + day + "日";
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("dataFormatMMdd--------时间转换错误");
        }
        return result;
    }

    /**
     * 将long类型的时间转换为string
     *
     * @param time      the time
     * @param formatStr the format str
     * @return string string
     */
    public static String dateLongFormatString(long time, String formatStr) {
        // TODO Auto-generated method stub
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat sdf;
        if (TextUtils.isEmpty(formatStr)) {
            sdf = new SimpleDateFormat(format1, Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat(formatStr, Locale.getDefault());
        }
        return sdf.format(calendar.getTime());
    }

    /**
     * 将"yyyy-MM-dd HH:mm:ss"格式的字符串转为UTC时间的"yyyy-MM-dd HH:mm:ss"格式的字符串
     *
     * @param time the time
     * @return string string
     */
    public static String parseToUTC(String time) {
        if (TextUtils.isEmpty(time))
            return "";
        String newTime = new String();
        try {
            SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));// 设置
            newTime = sdfUTC.format(sdfLocal.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }

    /**
     * Parse to local string.
     *
     * @param time the time
     * @return the string
     */
    public static String parseToLocal(String time) {
        String newTime = "";
        SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));// 设置
        sdfLocal.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdfUTC.parse(time);
            newTime = sdfLocal.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newTime;
    }

    /**
     * 将时间类型1的转换为时间类型2
     *
     * @param format1 时间类型1
     * @param format2 时间类型2
     * @param oldDate 时间
     * @return string string
     */
    public static String parseTime(String format1, String format2, String oldDate) {
        String result = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(format1, Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat(format2, Locale.getDefault());
        Date d = null;
        try {
            d = sdf1.parse(oldDate);
            result = sdf2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 计算两个日期之间相差的天数   字符串的日期格式的计算
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数 int
     * @throws ParseException the parse exception
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 将毫秒数转化为hh:mm:ss格式
     *
     * @param milliseconds the milliseconds
     * @return string string
     */
    public static String formatLongToTimeStr(long milliseconds) {
        if (milliseconds == 0)
            return "00:00";
        StringBuilder result = new StringBuilder();
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = (int) (milliseconds / 1000);
        if (second >= 3600) {
            hour = second / 3600;
            if (hour < 10)
                result.append("0");
            result.append(hour + ":");
            second -= hour * 3600;
        }
        if (second > 60) {
            minute = second / 60;
            second %= 60;
        }
        if (minute < 10)
            result.append("0");
        result.append(minute + ":");

        if (second < 10) {
            result.append("0");
        }
        result.append(second);
        return result.toString();

    }

    /**
     * Gets date string.
     *
     * @param formatString the format string
     * @return the date string
     */
    public static String getDateString(String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }


    /**
     * Is same day boolean.
     * 日期格式必须 format2 = "yyyyMMdd";
     * @param date1 the date 1
     * @param date2 the date 2
     * @return the boolean
     */
    public static boolean isSameDay(String date1, String date2){
        return date1.equals(date2);
    }


    /**
     * 6字节格式BCD时间
     * @return yyMMddHHmmss格式时间的字节组数
     */
    public static byte[] getDateBCDTimeArray(){
        return ByteTools.str2Bcd(getDateBCDTime());
    }

    /**
     *
     * @return yyMMddHHmmss格式时间
     */
    public static String getDateBCDTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date.substring(2);
    }

    /**
     * 将格式（yyyyMMddHHmmss）字符串转为时间戳字符串
     * @param user_time
     * @return
     */
    public static String dateToTimestamp(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    /**
     * 6字节BCD时间转换成UNIX时间戳
     * @param bcdTimeArray
     * @return
     */
    public static long bcdTimeArrayToTimestamp(byte[] bcdTimeArray){
        String time = "20" + ByteTools.bytesToHexString(bcdTimeArray);
        return Long.valueOf(dateToTimestamp(time));
    }
}
