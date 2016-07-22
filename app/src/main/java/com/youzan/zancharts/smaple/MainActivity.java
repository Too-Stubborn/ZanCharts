package com.youzan.zancharts.smaple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.youzan.zancharts.ZanBarChart;
import com.youzan.zancharts.ChartItem;
import com.youzan.zancharts.ZanLineChart;
import com.youzan.zancharts.smaple.test.Mocks;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZanBarChart chart = (ZanBarChart) findViewById(R.id.bar_chart);

        assert chart != null;
        chart.setItems(Mocks.summary());

        chart.setOnItemSelectListener(new ZanBarChart.OnItemSelectListener() {
            @Override
            public void onSelected(ZanBarChart chart, ChartItem item) {
            }
        });

        // line chart
        ZanLineChart lineChart = (ZanLineChart) findViewById(R.id.line_chart);
        assert lineChart != null;
        lineChart.addLines(Mocks.fanLines());
    }
}
