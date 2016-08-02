package com.youzan.zancharts.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liangfei on 7/27/16.
 */

public class Dates {
    public static DateFormat sCanonicalNDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static DateFormat sChineseDateFormat = new SimpleDateFormat("MM月dd日", Locale.CHINESE);
    public static DateFormat sSimpleDateFormat = new SimpleDateFormat("MM/dd", Locale.US);

    public static String simplify(final String dateString) {
        try {
            Date date = sCanonicalNDateFormat.parse(dateString);
            return sSimpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String toChinese(final String dateString) {
        try {
            Date date = sSimpleDateFormat.parse(dateString);
            return sChineseDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }
}
