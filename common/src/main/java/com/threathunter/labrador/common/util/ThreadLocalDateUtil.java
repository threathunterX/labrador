package com.threathunter.labrador.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 */
public class ThreadLocalDateUtil {
    private static final Object localObj = new Object();
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String HOUR_FORMAT = "HH";
    private static final String DATE_FORMAT_HH = "yyyyMMddHH";

    private static Map<String, ThreadLocal<SimpleDateFormat>> locals = new HashMap<>();

    //线程安全的获得日是期处理类
    private static DateFormat getDateFormat(String pattern) {
        ThreadLocal<SimpleDateFormat> local = locals.get(pattern);
        if (local == null) {
            synchronized (localObj) {
                local = locals.get(pattern);
                if (null == local) {
                    local = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    locals.put(pattern, local);
                }
            }
        }
        return local.get();
    }

    public static long parseTimestatmp(String day) {
        DateFormat dateFormat = getDateFormat(DATE_FORMAT);
        try {
            Date date = dateFormat.parse(day);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String formatDay(Date date) {
        DateFormat dateFormat = getDateFormat(DATE_FORMAT);
        return dateFormat.format(date);
    }

    public static String formatDayByTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        return getDateFormat(DATE_FORMAT).format(date);
    }

    public static String formatDayHourByTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        return getDateFormat(DATE_FORMAT_HH).format(date);
    }

    public static List<String> getLast30Day() {
        Calendar currentDay = Calendar.getInstance();
        currentDay.setTime(new Date());
        List<String> days = new ArrayList<>();
        days.add(ThreadLocalDateUtil.formatDay(currentDay.getTime()));
        for (int i = 0; i < 30; i++) {
            currentDay.add(currentDay.DATE, -1);
            days.add(ThreadLocalDateUtil.formatDay(currentDay.getTime()));
        }
        return days;
    }

    public static String formatDayHour(Date date) {
        DateFormat format = getDateFormat(DATE_FORMAT_HH);
        return format.format(date);
    }

    public static String formatHour(Date date) {
        DateFormat dateFormat = getDateFormat(HOUR_FORMAT);
        return dateFormat.format(date);
    }


    public static List<String> getDays(long startTs, Long endTs) {
        List<String> results = new ArrayList<>();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date(startTs));
        endCalendar.setTime(new Date(endTs));
        while (true) {
            results.add(getDateFormat(DATE_FORMAT).format(startCalendar.getTime()));
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            if (startCalendar.getTimeInMillis() > endCalendar.getTimeInMillis()) {
                break;
            }
        }
        return results;
    }

    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }


    public static List<Date> getDaysBetweenHours(Date startdate, Date enddate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.HOUR, 1);
        }
        return dates;
    }


    public static void main(String[] args) {
        System.out.println(ThreadLocalDateUtil.formatDayHourByTimestamp(1512456070845L));
    }
}
