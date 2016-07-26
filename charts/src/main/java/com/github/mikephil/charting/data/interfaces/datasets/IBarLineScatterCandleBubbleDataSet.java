package com.github.mikephil.charting.data.interfaces.datasets;

import com.github.mikephil.charting.data.entry.Entry;

/**
 * Created by philipp on 21/10/15.
 */
public interface IBarLineScatterCandleBubbleDataSet<T extends Entry> extends IDataSet<T> {

    /** Returns the color that is used for drawing the highlight indicators. */
    int getHighLightColor();
}
