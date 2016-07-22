package com.youzan.zancharts;

import android.support.annotation.ColorInt;

/**
 * Created by liangfei on 7/22/16.
 */

public class PieChartItem extends ChartItem {
    @ColorInt public int color;
    public String unit;

    public PieChartItem(String key, String title, String value,
                        @ColorInt int color, String unit) {
        super(key, title, value);
        this.color = color;
        this.unit = unit;
    }
}
