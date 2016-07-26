package com.github.mikephil.charting.data.interfaces.dataprovider;

import com.github.mikephil.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
