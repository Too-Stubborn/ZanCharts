package com.youzan.zancharts.smaple;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.youzan.zancharts.PieChartItem;
import com.youzan.zancharts.ZanPieChart;

import java.util.ArrayList;

public class PieChartActivity extends AppCompatActivity {
    private int mFemaleCount = 0;
    private int mMaleCount = 10;
    private int mUnknownCount = 20;

    private ZanPieChart mPieChart;

    public static abstract class OnSeekBarChangeListenerImp implements SeekBar.OnSeekBarChangeListener {

        @Override
        public abstract void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        setTitle("饼状图");

        mPieChart = (ZanPieChart) findViewById(R.id.pie_chart);

        fillData();

        final SeekBar femaleSeekBar = (SeekBar) findViewById(R.id.female_range);
        final SeekBar maleSeekBar = (SeekBar) findViewById(R.id.male_range);
        final SeekBar unknownSeekBar = (SeekBar) findViewById(R.id.unknown_range);

        assert femaleSeekBar != null;
        femaleSeekBar.setMax(100);

        assert maleSeekBar != null;
        maleSeekBar.setMax(100);

        assert unknownSeekBar != null;
        unknownSeekBar.setMax(100);

        femaleSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFemaleCount = progress;
                fillData();
            }
        });

        maleSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMaleCount = progress;
                fillData();
            }
        });

        unknownSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mUnknownCount = progress;
                fillData();
            }
        });
    }

    private void fillData() {
        final PieChartItem female = new PieChartItem("female", "男", String.valueOf(mFemaleCount),
                Color.RED, "人");
        final PieChartItem male = new PieChartItem("male", "女", String.valueOf(mMaleCount),
                Color.GREEN, "人");
        final PieChartItem unknown = new PieChartItem("unknown", "未知", String.valueOf(mUnknownCount),
                Color.BLUE, "人");

        mPieChart.clear();
        mPieChart.setItems(new ArrayList<PieChartItem>() {
            {
                add(female);
                add(male);
                add(unknown);
            }
        });
    }
}
