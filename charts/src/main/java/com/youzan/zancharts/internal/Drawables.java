package com.youzan.zancharts.internal;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;

/**
 * Created by liangfei on 7/22/16.
 */

public class Drawables {
    public static final int ALPHA_MASK = 0x33FFFFFF;

    public static GradientDrawable gradient(@ColorInt int startColor) {
        final int[] colors = new int[] {startColor & ALPHA_MASK, ALPHA_MASK};
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
    }
}
