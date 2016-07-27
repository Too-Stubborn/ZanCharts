package com.youzan.zancharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.dataset.BarDataSet;
import com.github.mikephil.charting.data.entry.BarEntry;
import com.github.mikephil.charting.data.entry.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.youzan.zancharts.internal.OnChartGestureListenerImp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfei on 7/18/16.
 */

public class ZanBarChart extends BarChart {
    private static final int DEFAULT_BAR_COLOR = 0xFF85D2FF;
    private static final int DEFAULT_HIGHLIGHT_COLOR = Color.WHITE;

    public interface OnItemSelectListener {
        void onSelected(ZanBarChart chart, ChartItem item);
    }

    private OnItemSelectListener mOnItemSelectListener;

    private List<ChartItem> mItems;

    public ZanBarChart(Context context) {
        super(context);
    }

    public ZanBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZanBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        setOnChartGestureListener(new OnChartGestureListenerImp() {
            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture gesture) {
                if (gesture == ChartTouchListener.ChartGesture.DRAG
                        || gesture == ChartTouchListener.ChartGesture.FLING) {
                    highlightCenterItem();
                }
            }
        });

        setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                onItemSelected((ChartItem) e.getData());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        setDrawBarShadow(false);
        getLegend().setEnabled(false);
        setPinchZoom(false);
        setScaleYEnabled(false);
        setScaleXEnabled(false);
        setDoubleTapToZoomEnabled(false);
        setDrawGridBackground(false);
        ViewPortHandler vph = getViewPortHandler();
        vph.setMinMaxScaleX(2, 2);
        vph.setMinMaxScaleY(1, 1);

        // x
        XAxis xAxis = getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        // left y
        YAxis leftAxis = getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.enableGridDashedLine(10f, 5f, 0);
        leftAxis.setGridColor(Color.WHITE);
        leftAxis.setSpaceTop(30);

        // right y
        YAxis rightAxis = getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);

        // unit
        setDescriptionTextSize(14f);
        setDescriptionColor(Color.WHITE);

    }

    @Override
    protected void drawDescription(Canvas c) {
        setDescriptionPosition(getWidth() / 2 + Utils.convertDpToPixel(20f),
                getHeight() - getViewPortHandler().offsetBottom() + Utils.convertDpToPixel(5f));
        super.drawDescription(c);
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
        dataSet.setHighLightAlpha(0xFF);
        dataSet.setHighLightColor(DEFAULT_HIGHLIGHT_COLOR);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        barData.setDrawValues(false);
        barData.setBarWidth(0.4f);

        setData(barData);

        //highlightCenterItem();
    }

    public void setSelectedIndex(final int index) {
        if (mItems == null || mItems.size() == 0) return;
        if (index < 0 || index > mItems.size()) return;

        IBarDataSet set = getData().getDataSetByIndex(0);

        if (set == null) return;

        BarEntry entry = set.getEntryForIndex(index);
        highlightValue(entry.getX(), 0);
    }

    public void setSelectedItem(@NonNull final ChartItem item) {

        setSelectedIndex(mItems.indexOf(item));
    }

    public void setSelectedKey(final String key) {
        for (int i = 0, size = mItems.size(); i < size; i++) {
            if (mItems.get(i).key.equals(key)) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    private void onItemSelected(ChartItem item) {
        setDescText(item.title);
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onSelected(this, item);
        }
    }

    private void highlightCenterItem() {
        Highlight highlight = getHighlightByTouchPoint(getCenter().getX(), 0);
        highlightValue(highlight);
        Entry entry = getBarData().getEntryForHighlight(highlight);
        onItemSelected((ChartItem) entry.getData());
    }
}
