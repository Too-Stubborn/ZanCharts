package com.youzan.zancharts.smaple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.youzan.zancharts.ZanBarChart;
import com.youzan.zancharts.ChartItem;
import com.youzan.zancharts.smaple.test.Mocks;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZanBarChart chart = (ZanBarChart) findViewById(R.id.bar_chart);
        final TextView selectedValueView = (TextView) findViewById(R.id.selected_value);

        assert chart != null;
        chart.setItems(Mocks.summary());

        chart.setOnItemSelectListener(new ZanBarChart.OnItemSelectListener() {
            @Override
            public void onSelected(ZanBarChart chart, ChartItem item) {
                assert selectedValueView != null;
                selectedValueView.setText(item.key);
            }
        });
    }
}
