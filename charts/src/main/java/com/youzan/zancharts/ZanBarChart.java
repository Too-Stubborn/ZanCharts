package com.youzan.zancharts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.dataset.BarDataSet;
import com.github.mikephil.charting.data.entry.BarEntry;
import com.github.mikephil.charting.data.entry.Entry;
import com.github.mikephil.charting.data.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
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
    private static final String TAG = "ZanBarChart";

    public interface OnItemSelectListener {
        void onSelected(ZanBarChart chart, ChartItem item);
    }

    private OnItemSelectListener mOnItemSelectListener;

    private float mBarSpace;
    private int mMaxEntryCount;

    private List<ChartItem> mItems;
    private ChartItem mSelectedItem;

    private Highlight mHighlight;
    private Highlight mSelectedHighlight;

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

        mBarSpace = Utils.dp2px(BAR_WIDTH_IN_DP + SPACE_BETWEEN_BARS_IN_DP);

        setOnChartGestureListener(new OnChartGestureListenerImp() {
            @Override
            public void onDragEnd() {
                Log.d(TAG, "onDragEnd");
                mHighlight = null;
                highlightCenterItem(true, false);
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                highlightCenterItem(false, false);
            }

            @Override
            public void onScrollEnd() {
                Log.d(TAG, "onScrollEnd");
                mHighlight = null;
                highlightCenterItem(true, false);
            }
        });

        setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                centerHighlight(e, h);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // Description
        setNoDataText(null);
        setNoDataTextDescription(null);
        setDescription(null);

        setDrawBarShadow(false);
        getLegend().setEnabled(false);
        setDragEnabled(true);
        setPinchZoom(false);
        setScaleEnabled(false);
        setDoubleTapToZoomEnabled(false);
        setDrawGridBackground(false);
        // View port
        setExtraOffsets(0, 40, 0, 20);
        ViewPortHandler vph = getViewPortHandler();
        vph.setMinMaxScaleY(1, 1);
        vph.setNeedTranslateX(true);


        // x
        XAxis xAxis = getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        // left y
        YAxis leftAxis = getAxisLeft();
        leftAxis.setAxisMinValue(0);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGridColor(DEFAULT_BAR_COLOR);
        leftAxis.setZeroLineColor(DEFAULT_BAR_COLOR);
        leftAxis.enableGridDashedLine(Utils.dp2px(10f), Utils.dp2px(5f), 0);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setLabelCount(4, true);

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
        clear();
        mItems = new ArrayList<>(items);
        mSelectedItem = null;
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

    private void updateUI() {
        if (mItems == null || mItems.size() == 0) return;

        // reset touch matrix
        BarLineChartTouchListener listener = (BarLineChartTouchListener) getOnTouchListener();
        final Matrix touchMatrix = listener.getMatrix();
        touchMatrix.setTranslate(0, 0);

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
        if (mMaxEntryCount == 0) return;

        final float width = xAxis.mAxisRange / mMaxEntryCount / 3;
        barData.setBarWidth(width);
        setData(barData);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        final ViewPortHandler port = getViewPortHandler();
        float barCountPerPort = port.contentWidth() / mBarSpace;
        final float scale = mMaxEntryCount / barCountPerPort;
        port.setMinMaxScaleX(scale, scale);
    }

    public void setSelectedIndex(int index) {
        if (mItems == null || mItems.size() == 0) return;
        if (index < 0 || index > mItems.size()) return;

        resetHighlight();

        // Save the selected item.
        mSelectedItem = mItems.get(index);

        IBarDataSet set = getData().getDataSetByIndex(0);
        if (set == null) return;

        Entry middleEntry = getEntryByTouchPoint(getWidth() / 2, 0);
        ChartItem middleItem = (ChartItem) middleEntry.getData();
        final int middleIndex = mItems.indexOf(middleItem);

        final float dx = (middleIndex - index) * mBarSpace;
        translate(dx, new OnTranslateListener() {
            @Override
            public void onTranslated() {
                highlightCenterItem(true, false);
            }
        });
    }

    public int getSelectedIndex() {
        if (mItems == null || mSelectedItem == null) return -1;
        return mItems.indexOf(mSelectedItem);
    }

    public ChartItem getSelectedItem() {
        return mSelectedItem;
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

    private void centerHighlight(final Entry e, Highlight h) {
        final float dx = getCenter().getX() - h.getXPx();
        translate(dx, new OnTranslateListener() {
            @Override
            public void onTranslated() {
                onItemSelected((ChartItem) e.getData());
            }
        });
    }

    private void translate(final float dx, @NonNull final OnTranslateListener translateListener) {
        if (mCenterHighlightAnimator.isRunning()) return;

        BarLineChartTouchListener listener = (BarLineChartTouchListener) getOnTouchListener();
        final Matrix touchMatrix = listener.getMatrix();

        mPreviousAnimatedValue = 0f;
        mCenterHighlightAnimator.setFloatValues(mPreviousAnimatedValue, dx);
        mCenterHighlightAnimator.removeAllListeners();
        mCenterHighlightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPreviousAnimatedValue = 0f;
                translateListener.onTranslated();
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

    interface OnTranslateListener {
        void onTranslated();
    }

    private void onItemSelected(ChartItem item) {
        Log.d(TAG, "onItemSelected" + item.title);
        setDescription(item.title);
        mSelectedItem = item;
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onSelected(this, item);
        }
    }

    private void resetHighlight() {
        mHighlight = null;
        mSelectedHighlight = null;
    }

    private void highlightCenterItem(boolean ending, boolean smooth) {
        Highlight highlight = getHighlightByTouchPoint(getCenter().getX(), 0);

        if (highlight == null) return;
        if (highlight.equalTo(mHighlight)) return;

        mHighlight = highlight;
        highlightValue(highlight);
        Entry entry = getBarData().getEntryForHighlight(highlight);

        if (ending && !highlight.equalTo(mSelectedHighlight)) {
            mSelectedHighlight = highlight;
            if (smooth) {
                centerHighlight(entry, highlight);
            } else {
                onItemSelected((ChartItem) entry.getData());
            }
        }
    }

    @Override
    protected void drawDescription(Canvas c) {
        if (TextUtils.isEmpty(mDescText)) return;

        centerDescription();

        super.drawDescription(c);
    }

    private void centerDescription() {
        ViewPortHandler port = getViewPortHandler();
        final float textHeight = Utils.calcTextHeight(mDescPaint, mDescText);

        final float x = port.getContentCenter().x;
        final float y = port.contentBottom() + textHeight + Utils.dp2px(5);

        if (mDescPos == null) {
            mDescPos = MPPointF.getInstance(x, y);
        } else {
            mDescPos.x = x;
            mDescPos.y = y;
        }
    }
}
