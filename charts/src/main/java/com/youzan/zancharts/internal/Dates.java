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
    public static DateFormat sSimpleDateFormat = new SimpleDateFormat("MM/dd", Locale.US);

    public static String simplify(final String dateStr) {
        try {
            Date date = sCanonicalNDateFormat.parse(dateStr);
            return sSimpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }
}
