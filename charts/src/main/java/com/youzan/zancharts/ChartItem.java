package com.youzan.zancharts;

import android.support.annotation.Keep;

/**
 * Created by liangfei on 7/18/16.
 */

@Keep
public class ChartItem {
    public String key;
    public String title;
    public String value;

    public ChartItem(String key, String title, String value) {
        this.key = key;
        this.title = title;
        this.value = value;
    }
}
