package com.youzan.zancharts.smaple;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.youzan.zancharts.ChartItem;
import com.youzan.zancharts.ZanBarChart;
import com.youzan.zancharts.ZanLineChart;
import com.youzan.zancharts.ZanPieChart;
import com.youzan.zancharts.smaple.test.Mocks;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void barChart(View view) {
        startActivity(new Intent(this, BarChartActivity.class));
    }

    public void lineChart(View view) {
        startActivity(new Intent(this, LineChartActivity.class));
    }

    public void pieChart(View view) {
        startActivity(new Intent(this, PieChartActivity.class));
    }
}
