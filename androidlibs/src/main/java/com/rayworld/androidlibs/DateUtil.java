package com.rayworld.androidlibs;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by 이광열 on 2016-07-12.
 */
public class DateUtil {

    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static boolean isWeekend() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }

}
