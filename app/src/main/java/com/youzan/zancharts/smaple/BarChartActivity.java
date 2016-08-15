package com.youzan.zancharts.smaple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.youzan.zancharts.ChartItem;
import com.youzan.zancharts.ZanBarChart;
import com.youzan.zancharts.smaple.test.Mocks;

import java.util.List;
import java.util.Locale;

public class BarChartActivity extends AppCompatActivity {
    private static final String TAG = "BarChart";

    public static final int MIN_ITEM_COUNT = 1;

    private ZanBarChart mBarChart;
    private int mItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        setTitle("柱状图");

        // data
        final List<ChartItem> items = Mocks.summary();
        mItemCount = items.size();

        // bar chart view
        mBarChart = (ZanBarChart) findViewById(R.id.bar_chart);

        // item count text view
        final TextView itemCountView = (TextView) findViewById(R.id.item_count);
        assert itemCountView != null;
        itemCountView.setText(String.format(Locale.CHINA, "数量: %d", mItemCount));

        // selection text view
        final TextView selectionView = (TextView) findViewById(R.id.selection);
        assert selectionView != null;

        // bar chart
        mBarChart.setItems(items);
        mBarChart.setOnItemSelectListener(new ZanBarChart.OnItemSelectListener() {
            @Override
            public void onSelected(ZanBarChart chart, ChartItem item) {
                selectionView.setText(String.format(Locale.CHINA, "选中: %d",
                        mBarChart.getSelectedIndex()));
                mBarChart.setDescription(item.title);
                Log.d(TAG, "onSelected");
            }
        });
        mBarChart.setSelectedIndex(0);

        // item count seek bar
        final SeekBar seekBar = (SeekBar) findViewById(R.id.count_seek_bar);
        assert seekBar != null;

        // selection seek bar
        final SeekBar selectionSeekBar = (SeekBar) findViewById(R.id.selection_seek_bar);
        assert selectionSeekBar != null;

        seekBar.setMax(mItemCount - MIN_ITEM_COUNT);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBarChart.clearItems();
                mItemCount = progress + MIN_ITEM_COUNT;
                mBarChart.setItems(items.subList(0, mItemCount));
                itemCountView.setText(String.format(Locale.CHINA, "数量: %d", mItemCount));
                selectionSeekBar.setMax(mItemCount - 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // selection seek bar
        selectionSeekBar.setMax(mItemCount - 1);
        selectionSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        selectionView.setText(String.format(Locale.CHINA, "选中: %d", progress));
                        mBarChart.setSelectedIndex(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
    }
}
