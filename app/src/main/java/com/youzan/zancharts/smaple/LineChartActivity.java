package com.youzan.zancharts.smaple;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.components.Legend;
import com.youzan.zancharts.ZanLineChart;
import com.youzan.zancharts.smaple.test.Mocks;

public class LineChartActivity extends AppCompatActivity {
    private ZanLineChart mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        setTitle("线状图");

        mLineChart = (ZanLineChart) findViewById(R.id.line_chart);
        assert mLineChart != null;


        final Legend legend = mLineChart.getLegend();
        final int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setTitle("线状图(旋转可查看横屏效果)");
            legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
            legend.setXOffset(0);
            legend.setYOffset(15);
            mLineChart.getXAxis().setLabelCount(6, true);
            mLineChart.setExtraOffsets(0, 15, 0, 15);
        } else {
            setTitle("线状图(旋转可查看竖屏效果)");
            legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
            legend.setTextSize(16f);
            legend.setFormSize(16f);
            mLineChart.setDescriptionTextSize(16f);
            mLineChart.setHighlightEnabled(true);
            mLineChart.getXAxis().setLabelCount(10, true);
            mLineChart.setExtraOffsets(0, 15, 0, 0);
        }

        mLineChart.addLines(Mocks.fanLines());
    }
}
