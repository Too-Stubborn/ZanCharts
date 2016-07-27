package com.youzan.zancharts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
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
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
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

    private static final float BAR_WIDTH_IN_DP = 8f;
    private static final float SPACE_BETWEEN_BARS_IN_DP = 15f;

    public interface OnItemSelectListener {
        void onSelected(ZanBarChart chart, ChartItem item);
    }

    private OnItemSelectListener mOnItemSelectListener;

    private List<ChartItem> mItems;
    private float mBarSpace;
    private int mMaxEntryCount;

    public ZanBarChart(Context context) {
        super(context);
    }

    public ZanBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZanBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private float mLastDragTransitionX = 0;
    private float mDragStopThreshold = Utils.dp2px(0.1f);
    private boolean mIsGestureEnding = false;

    @Override
    protected void init() {
        super.init();

        mBarSpace = Utils.dp2px(BAR_WIDTH_IN_DP + SPACE_BETWEEN_BARS_IN_DP);

        setOnChartGestureListener(new OnChartGestureListenerImp() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture gesture) {
                mIsGestureEnding = false;
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                float transition = Math.abs(dX - mLastDragTransitionX);
                if (transition >= mDragStopThreshold) {
                    mIsGestureEnding = false;
                }
                if (mIsGestureEnding) return;
                if (transition < mDragStopThreshold) {
                    mIsGestureEnding = true;
                    highlightCenterItem(true, false);
                }

                highlightCenterItem(false, false);
                mLastDragTransitionX = dX;
            }
        });

        setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                centerHighlight(e, h, 250);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // Description
        setNoDataText(null);
        setNoDataTextDescription(null);

        setDrawBarShadow(false);
        getLegend().setEnabled(false);
        setPinchZoom(false);
        setScaleYEnabled(false);
        setScaleXEnabled(false);
        setDoubleTapToZoomEnabled(false);
        setDrawGridBackground(false);
        ViewPortHandler vph = getViewPortHandler();
        vph.setMinMaxScaleY(1, 1);
        vph.setMinMaxScaleX(2, 2);

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
            BarEntry entry = new BarEntry(i + 1, value);
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

        // Calculate the bar width in value
        mMaxEntryCount = dataSet.getEntryCount();
        final float width = xAxis.mAxisRange / mMaxEntryCount / 3;
        barData.setBarWidth(width);
        setData(barData);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mMaxEntryCount > 0) {
            final ViewPortHandler port = getViewPortHandler();
            final int barCountPerPort = (int) (port.contentWidth() / mBarSpace);
            final float scale = mMaxEntryCount / (float) barCountPerPort;
            port.setMinMaxScaleX(scale, scale);
        }
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

    private float mPreviousAnimatedValue = 0f;
    private ValueAnimator mCenterHighlightAnimator = new ValueAnimator();

    private void centerHighlight(final Entry e, Highlight h, long duration) {
        if (mCenterHighlightAnimator.isRunning()) return;

        final float dx = getCenter().getX() - h.getXPx();

        BarLineChartTouchListener listener = (BarLineChartTouchListener) getOnTouchListener();
        final Matrix touchMatrix = listener.getMatrix();

        mPreviousAnimatedValue = 0f;
        mCenterHighlightAnimator.setFloatValues(mPreviousAnimatedValue, dx);
        mCenterHighlightAnimator.setDuration(duration);
        mCenterHighlightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPreviousAnimatedValue = 0f;
                onItemSelected((ChartItem) e.getData());
            }
        });
        mCenterHighlightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                touchMatrix.postTranslate(value - mPreviousAnimatedValue, 0);
                mPreviousAnimatedValue = value;
                getViewPortHandler().refresh(touchMatrix, ZanBarChart.this, true);
            }
        });

        mCenterHighlightAnimator.start();
    }

    private void onItemSelected(ChartItem item) {
        setDescription(item.title);
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onSelected(this, item);
        }
    }

    private void highlightCenterItem(boolean ending, boolean smooth) {
        Highlight highlight = getHighlightByTouchPoint(getCenter().getX(), 0);
        if (highlight == null) return;
        highlightValue(highlight);
        Entry entry = getBarData().getEntryForHighlight(highlight);

        if (ending) {
            if (smooth) {
                centerHighlight(entry, highlight, 300);
            } else {
                onItemSelected((ChartItem) entry.getData());
            }
        }
    }
}
