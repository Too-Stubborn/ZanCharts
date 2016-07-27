
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.entry.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.data.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.data.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class BarChartRenderer extends BarLineScatterCandleBubbleRenderer {
    public static final float DEFAULT_BAR_RADIUS = 9.5f;
    public static final int DEFAULT_HIGHLIGHT_COLOR = 0x78000000;
    public static final float DEFAULT_ZERO_HEIGHT_IN_DP = 5.f;

    protected BarDataProvider mDataProvider;

    /**
     * the rect object that is used for drawing the bars
     */
    protected RectF mBarRect = new RectF();

    protected BarBuffer[] mBarFlatBuffers;

    protected Paint mShadowPaint;
    protected Paint mBarBorderPaint;

    private float mHeightOfZero = Utils.dp2px(DEFAULT_ZERO_HEIGHT_IN_DP);
    private float mBarRadius = Utils.dp2px(DEFAULT_BAR_RADIUS);

    public BarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);

        mDataProvider = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(DEFAULT_HIGHLIGHT_COLOR);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mBarBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void initBuffers() {
        BarData barData = mDataProvider.getBarData();
        mBarFlatBuffers = new BarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarFlatBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);

            final int bufferSize = set.getEntryCount() * 4
                    * (set.isStacked() ? set.getStackSize() : 1);

            mBarFlatBuffers[i] = new BarBuffer(bufferSize, barData.getDataSetCount(),
                    set.isStacked());
        }
    }

    @Override
    public void drawData(Canvas c) {
        BarData barData = mDataProvider.getBarData();

        for (int i = 0, size = barData.getDataSetCount(); i < size; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer transformer = mDataProvider.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.dp2px(dataSet.getBarBorderWidth()));

        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // initialize the buffer
        BarBuffer buffer = mBarFlatBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mDataProvider.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mDataProvider.getBarData().getBarWidth());
        buffer.feed(dataSet);

        transformer.pointValuesToPixel(buffer.buffer);

        // draw the bar shadow before the values
        if (mDataProvider.isDrawBarShadowEnabled()) {
            for (int j = 0; j < buffer.size(); j += 4) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) continue;
                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break;
                c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(), buffer.buffer[j + 2],
                        mViewPortHandler.contentBottom(), mShadowPaint);
            }
        }

        // if multiple colors
        boolean multiColors = dataSet.getColors().size() > 1;

        for (int j = 0, size = buffer.size(); j < size; j += 4) {
            float left = buffer.buffer[j];
            float top = buffer.buffer[j + 1];
            float right = buffer.buffer[j + 2];
            float bottom = buffer.buffer[j + 3];

            if (!mViewPortHandler.isInBoundsLeft(right)) continue;
            if (!mViewPortHandler.isInBoundsRight(left)) break;

            if (multiColors) {
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            } else {
                mRenderPaint.setColor(dataSet.getColor());
            }
            drawBarRect(c, left, top, right, bottom, drawBorder);
        }
    }

    private void drawBarRect(Canvas canvas, float left, float top, float right, float bottom,
                             boolean withBorder) {
        mBarRect.set(left, top - mHeightOfZero, right, bottom);
        canvas.drawRoundRect(mBarRect, mBarRadius, mBarRadius, mRenderPaint);
        if (withBorder) {
            canvas.drawRoundRect(mBarRect, mBarRadius, mBarRadius, mBarBorderPaint);
        }
    }

    protected void transformHighlightRect(float x, float y1, float y2, float halfBarWidth,
                                          Transformer transformer) {
        mBarRect.set(x - halfBarWidth, y1, x + halfBarWidth, y2);
        transformer.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
        mBarRect.top = mBarRect.top - mHeightOfZero;
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (isDrawingValuesAllowed(mDataProvider)) {

            List<IBarDataSet> dataSets = mDataProvider.getBarData().getDataSets();

            final float valueOffsetPlus = Utils.dp2px(4.5f);
            float posOffset = 0f;
            float negOffset = 0f;
            boolean drawValueAboveBar = mDataProvider.isDrawValueAboveBarEnabled();

            for (int i = 0; i < mDataProvider.getBarData().getDataSetCount(); i++) {

                IBarDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                boolean isInverted = mDataProvider.isInverted(dataSet.getAxisDependency());

                // calculate the correct offset depending on the draw position of
                // the value
                float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
                posOffset = (drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus);
                negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus);

                if (isInverted) {
                    posOffset = -posOffset - valueTextHeight;
                    negOffset = -negOffset - valueTextHeight;
                }

                // get the buffer
                BarBuffer buffer = mBarFlatBuffers[i];

                // if only single values are drawn (sum)
                if (!dataSet.isStacked()) {

                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                        float x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2f;

                        if (!mViewPortHandler.isInBoundsRight(x))
                            break;

                        if (!mViewPortHandler.isInBoundsY(buffer.buffer[j + 1])
                                || !mViewPortHandler.isInBoundsLeft(x))
                            continue;

                        BarEntry entry = dataSet.getEntryForIndex(j / 4);
                        float val = entry.getY();

                        drawValue(c, dataSet.getValueFormatter(), val, entry, i, x,
                                val >= 0 ? (buffer.buffer[j + 1] + posOffset) : (buffer.buffer[j + 3] + negOffset),
                                dataSet.getValueTextColor(j / 4));
                    }

                    // if we have stacks
                } else {

                    Transformer trans = mDataProvider.getTransformer(dataSet.getAxisDependency());

                    int bufferIndex = 0;
                    int index = 0;

                    while (index < dataSet.getEntryCount() * mAnimator.getPhaseX()) {

                        BarEntry entry = dataSet.getEntryForIndex(index);

                        float[] vals = entry.getYVals();
                        float x = (buffer.buffer[bufferIndex] + buffer.buffer[bufferIndex + 2]) / 2f;

                        int color = dataSet.getValueTextColor(index);

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {

                            if (!mViewPortHandler.isInBoundsRight(x))
                                break;

                            if (!mViewPortHandler.isInBoundsY(buffer.buffer[bufferIndex + 1])
                                    || !mViewPortHandler.isInBoundsLeft(x))
                                continue;

                            drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, x,
                                    buffer.buffer[bufferIndex + 1] + (entry.getY() >= 0 ? posOffset : negOffset),
                                    color);

                            // draw stack values
                        } else {

                            float[] transformed = new float[vals.length * 2];

                            float posY = 0f;
                            float negY = -entry.getNegativeSum();

                            for (int k = 0, idx = 0; k < transformed.length; k += 2, idx++) {

                                float value = vals[idx];
                                float y;

                                if (value >= 0f) {
                                    posY += value;
                                    y = posY;
                                } else {
                                    y = negY;
                                    negY -= value;
                                }

                                transformed[k + 1] = y * mAnimator.getPhaseY();
                            }

                            trans.pointValuesToPixel(transformed);

                            for (int k = 0; k < transformed.length; k += 2) {

                                float y = transformed[k + 1]
                                        + (vals[k / 2] >= 0 ? posOffset : negOffset);

                                if (!mViewPortHandler.isInBoundsRight(x))
                                    break;

                                if (!mViewPortHandler.isInBoundsY(y)
                                        || !mViewPortHandler.isInBoundsLeft(x))
                                    continue;

                                drawValue(c, dataSet.getValueFormatter(), vals[k / 2], entry, i, x, y, color);
                            }
                        }

                        bufferIndex = vals == null ? bufferIndex + 4 : bufferIndex + 4 * vals.length;
                        index++;
                    }
                }
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] highlights) {
        BarData barData = mDataProvider.getBarData();

        for (Highlight highlight : highlights) {
            IBarDataSet set = barData.getDataSetByIndex(highlight.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled()) continue;

            Transformer transformer = mDataProvider.getTransformer(set.getAxisDependency());

            BarEntry e = set.getEntryForXPos(highlight.getX());

            if (!isInBoundsX(e, set)) continue;

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());

            boolean isStack = e.isStacked() && highlight.getStackIndex() >= 0;

            final float y1;
            final float y2;

            if (isStack) {
                if (mDataProvider.isHighlightFullBarEnabled()) {
                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();
                } else {
                    Range range = e.getRanges()[highlight.getStackIndex()];
                    y1 = range.from;
                    y2 = range.to;
                }
            } else {
                y1 = e.getY();
                y2 = 0.f;
            }

            transformHighlightRect(e.getX(), y1, y2, barData.getBarWidth() / 2f, transformer);

            setHighlightDrawPos(highlight, mBarRect);

            c.drawRoundRect(mBarRect, mBarRadius, mBarRadius, mHighlightPaint);
        }
    }

    /**
     * Sets the drawing position of the highlight object based on the riven bar-rect.
     *
     * @param high
     */
    protected void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerX(), bar.top);
    }

    @Override
    public void drawExtras(Canvas c) {
    }
}
