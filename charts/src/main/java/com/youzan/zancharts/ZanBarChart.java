package com.youzan.zancharts;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.youzan.zancharts.entity.ChartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfei on 7/18/16.
 */

public class ZanBarChart extends BarChart implements OnChartValueSelectedListener {
    private static final int DEFAULT_BAR_COLOR = 0xFF85D2FF;
    private static final int DEFAULT_HIGHLIGHT_COLOR = Color.WHITE;

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onSelected(this, (ChartItem) e.getData());
        }
    }

    @Override
    public void onNothingSelected() {

    }

    public interface OnItemSelectListener {
        void onSelected(ZanBarChart chart, ChartItem item);
    }

    private OnItemSelectListener mOnItemSelectListener;

    private List<ChartItem> mItems;

    public ZanBarChart(Context context) {
        super(context);
        init();
    }

    public ZanBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZanBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void init() {
        super.init();
        setOnChartValueSelectedListener(this);
        setScaleYEnabled(false);
        setPinchZoom(false);
        setDoubleTapToZoomEnabled(false);
    }

    public void setItems(List<ChartItem> items) {
        mItems = new ArrayList<>(items);
        updateUI();
    }

    @Nullable
    public List<ChartItem> getItems() {
        return mItems;
    }

    public void addItem(@NonNull ChartItem item) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mItems.add(item);
        updateUI();
    }

    public void clearItems() {
        if (mItems != null) {
            mItems.clear();
        }
        updateUI();
    }

    public void setOnItemSelectListener(final OnItemSelectListener listener) {
        mOnItemSelectListener = listener;
    }

    public void updateUI() {
        if (mItems == null || mItems.size() == 0) return;

        final int size = mItems.size();
        XAxis xAxis = getXAxis();
        xAxis.setAxisMinValue(0);
        xAxis.setAxisMaxValue(size);

        List<BarEntry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ChartItem item = mItems.get(i);
            float value = 0f;
            try {
                value = Float.parseFloat(item.value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            BarEntry entry = new BarEntry(i+ 1, value);
            entry.setData(item);
            entries.add(entry);
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(DEFAULT_BAR_COLOR);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        barData.setDrawValues(false);
        barData.setBarWidth(0.8f);

        setData(barData);
    }
}
