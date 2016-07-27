package com.youzan.zancharts;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.entry.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.dataset.LineDataSet;
import com.github.mikephil.charting.data.interfaces.datasets.ILineDataSet;
import com.youzan.zancharts.internal.Drawables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liangfei on 7/22/16.
 */

public class ZanLineChart extends LineChart {
    private List<Line> mLines;
    public ZanLineChart(Context context) {
        super(context);
    }

    public ZanLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZanLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        // gestures
        setTouchEnabled(false);
        setDragEnabled(false);
        setDoubleTapToZoomEnabled(false);

        // borders
        setDrawBorders(false);

        // unit
        setDescText("");

        // x axis
        XAxis xAxis = getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // right axis
        YAxis rightAxis = getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);

        // left axis
        YAxis leftAxis = getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setZeroLineColor(Color.BLACK);
        leftAxis.setGridColor(0xFFCCCCCC);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(0xFFCCCCCC);
        leftAxis.setTextSize(12.f);
        leftAxis.setYOffset(-10.f);

        // legend
        Legend legend = getLegend();
        legend.setTextSize(12.f);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setYOffset(20f);
    }

    public void addLines(List<Line> lines) {
        mLines = new ArrayList<>(lines);
        updateUI();
    }

    public void addLines(Line... lines) {
        if (lines == null) return;
        addLines(Arrays.asList(lines));
    }

    public void addLine(Line line) {
        if (mLines == null) {
            mLines = new ArrayList<>();
        }
        mLines.add(line);
        updateUI();
    }

    private void updateUI() {
        if (mLines == null || mLines.size() == 0) return;

        List<ILineDataSet> sets = new ArrayList<>();

        for (int i = 0, size = mLines.size(); i < size; i++) {
            Line line = mLines.get(i);
            int itemCount = line.items.size();
            List<Entry> entries = new ArrayList<>(itemCount);
            for (int x = 0; x < itemCount; x++) {
                ChartItem item = line.items.get(x);
                float y = 0f;
                try {
                    y = Float.parseFloat(item.value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                Entry entry = new Entry(x, y);
                entry.setData(item);
                entries.add(entry);
            }

            LineDataSet dataSet = new LineDataSet(entries, line.label);
            dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSet.setDrawValues(false);
            dataSet.setColor(line.color);
            dataSet.setLineWidth(1f);
            dataSet.setDrawCircles(false);
            dataSet.setDrawCircleHole(false);
            dataSet.setValueTextSize(9f);
            dataSet.setDrawFilled(true);
            dataSet.setFillDrawable(Drawables.gradient(line.color));

            sets.add(dataSet);
        }

        LineData data = new LineData(sets);

        setData(data);
    }
}
