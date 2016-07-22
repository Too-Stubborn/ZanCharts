package com.youzan.zancharts;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by liangfei on 7/22/16.
 */

public class ZanPieChart extends PieChart implements OnChartValueSelectedListener {
    private List<PieChartItem> mItems;

    public ZanPieChart(Context context) {
        super(context);
    }

    public ZanPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZanPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        Legend legend = getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        setDescription("");
        setExtraOffsets(5, 10, 5, 5);
        setDragDecelerationFrictionCoef(0.95f);
        setCenterTextColor(Color.BLACK);
        setDrawHoleEnabled(true);
        setHoleColor(Color.WHITE);
        setTransparentCircleColor(Color.WHITE);
        setTransparentCircleAlpha(110);
        setHoleRadius(58f);
        setTransparentCircleRadius(61f);
        setDrawCenterText(true);
        setRotationAngle(0);
        setRotationEnabled(true);
        setHighlightPerTapEnabled(true);
        setOnChartValueSelectedListener(this);
        setDrawEntryLabels(false);
        setEntryLabelColor(Color.WHITE);
    }

    public void setItems(List<PieChartItem> items) {
        mItems = items;
        updateUI();
    }

    private void updateUI() {
        final int size = mItems.size();
        List<PieEntry> entries = new ArrayList<>(size);

        final int[] colors = new int[size];
        for (int i = 0; i < size; i++) {
            PieChartItem item = mItems.get(i);
            colors[i] = item.color;
            float value = 0f;
            try {
                value = Float.parseFloat(item.value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            PieEntry entry = new PieEntry(value, item.title, item);
            entries.add(entry);
        }

        PieDataSet set = new PieDataSet(entries, "");
        set.setDrawValues(false);
        set.setColors(colors);
        //set.setSliceSpace(3f);
        set.setSelectionShift(5f);

        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());

        setData(data);

        highlightValue(entries.get(0).getX(), 0);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        setCenterText(beautifyCenterText(e));
    }

    @Override
    public void onNothingSelected() {

    }

    private SpannableString beautifyCenterText(Entry e) {
        float ySum = getData().getYValueSum();
        String percent = String.format(Locale.UK, "%.1f", e.getY() / ySum * 100f);

        PieChartItem item = (PieChartItem) e.getData();

        String display = item.title + " " + percent + "%\n" + item.unit + ": " + item.value;

        int carriageIndex = display.indexOf("%");
        int lastIndex = display.length();

        SpannableString s = new SpannableString(display);
        s.setSpan(new RelativeSizeSpan(1.6f), 0, carriageIndex + 1, 0);
        s.setSpan(new ForegroundColorSpan(item.color), 0, carriageIndex + 1, 0);

        s.setSpan(new RelativeSizeSpan(1.2f), carriageIndex + 2, lastIndex, 0);
        s.setSpan(new ForegroundColorSpan(0xFF666666), carriageIndex + 2, lastIndex, 0);

        return s;
    }
}
