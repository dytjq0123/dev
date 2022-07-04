package kr.or.dev.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Format {
    public Format() {
    }

    public static String dateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String timeFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return sdf.format(date);
    }

    public static String monthFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        return sdf.format(date);
    }

    public static String notificationFormatter(String date, int duty) throws ParseException {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        now.setTime(sdf.parse(date));
        now.set(now.get(1), now.get(2), now.get(5), now.get(11), now.get(12) + duty);
        int year = now.get(1);
        int month = now.get(2) + 1;
        int day = now.get(5);
        int hour = now.get(11);
        int minute = now.get(12);
        String reDate = year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day + " " + (hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "") + minute;
        return reDate;
    }

    public static String dateCal(String start_time, String alert_time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        Date start = sdf.parse(start_time);
        Date alert = sdf.parse(alert_time);
        long diff = start.getTime() - alert.getTime();
        long stringFormatMinute = diff / 60000L;
        String date = "";
        if (stringFormatMinute == 0L) {
            date = null;
        } else if (stringFormatMinute == 10L) {
            date = "10 분";
        } else if (stringFormatMinute == 30L) {
            date = "30분";
        } else if (stringFormatMinute == 60L) {
            date = "1시간";
        } else if (stringFormatMinute == 120L) {
            date = "2시간";
        } else if (stringFormatMinute == 180L) {
            date = "3시간";
        } else if (stringFormatMinute == 1440L) {
            date = "1일";
        } else if (stringFormatMinute == 2880L) {
            date = "2일";
        } else if (stringFormatMinute == 10080L) {
            date = "7일";
        } else {
            System.out.println("에러");
        }

        return date;
    }
}
