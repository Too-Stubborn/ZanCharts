
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.data.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.data.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.data.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegendRenderer extends Renderer {

    protected Paint mLabelPaint;
    protected Paint mFormPaint;

    protected Legend mLegend;

    public LegendRenderer(ViewPortHandler viewPortHandler, Legend legend) {
        super(viewPortHandler);

        mLegend = legend;

        mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelPaint.setTextSize(Utils.dp2px(9f));
        mLabelPaint.setTextAlign(Align.LEFT);

        mFormPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFormPaint.setStyle(Paint.Style.FILL);
        mFormPaint.setStrokeWidth(3f);
    }

    /**
     * Returns the Paint object used for drawing the Legend labels.
     */
    public Paint getLabelPaint() {
        return mLabelPaint;
    }

    /**
     * Returns the Paint object used for drawing the Legend forms.
     */
    public Paint getFormPaint() {
        return mFormPaint;
    }


    protected List<String> mLabels = new ArrayList<>(16);
    protected List<Integer> mColors = new ArrayList<>(16);

    /**
     * Prepares the legend and calculates all needed forms, labels and colors.
     *
     * Called from {@link BarLineChartBase#notifyDataSetChanged()}
     */
    public void computeLegend(ChartData<?> data) {
        if (!mLegend.isLegendCustom()) {
            mLabels.clear();
            mColors.clear();

            // loop for building up the colors and labels used in the legend
            for (int i = 0, size = data.getDataSetCount(); i < size; i++) {
                IDataSet<?> dataSet = data.getDataSetByIndex(i);
                int entryCount = dataSet.getEntryCount();

                List<Integer> colors = dataSet.getColors();

                // if we have a barchart with stacked bars
                if (dataSet instanceof IBarDataSet && ((IBarDataSet) dataSet).isStacked()) {

                    IBarDataSet bds = (IBarDataSet) dataSet;
                    String[] sLabels = bds.getStackLabels();

                    for (int j = 0; j < colors.size() && j < bds.getStackSize(); j++) {

                        mLabels.add(sLabels[j % sLabels.length]);
                        mColors.add(colors.get(j));
                    }

                    if (bds.getLabel() != null) {
                        // add the legend unit label
                        mColors.add(ColorTemplate.COLOR_SKIP);
                        mLabels.add(bds.getLabel());
                    }

                } else if (dataSet instanceof IPieDataSet) {
                    IPieDataSet pds = (IPieDataSet) dataSet;
                    for (int j = 0; j < colors.size() && j < entryCount; j++) {
                        mLabels.add(pds.getEntryForIndex(j).getLabel());
                        mColors.add(colors.get(j));
                    }

                    if (pds.getLabel() != null) {
                        // add the legend unit label
                        mColors.add(ColorTemplate.COLOR_SKIP);
                        mLabels.add(pds.getLabel());
                    }

                } else if (dataSet instanceof ICandleDataSet && ((ICandleDataSet) dataSet).getDecreasingColor() !=
                        ColorTemplate.COLOR_NONE) {

                    int decreasingColor = ((ICandleDataSet) dataSet).getDecreasingColor();
                    mColors.add(decreasingColor);

                    int increasingColor = ((ICandleDataSet) dataSet).getIncreasingColor();
                    mColors.add(increasingColor);

                    mLabels.add(null);
                    mLabels.add(dataSet.getLabel());

                } else { // all others
                    for (int j = 0; j < colors.size() && j < entryCount; j++) {
                        // if multiple colors are set for a DataSet, group them
                        if (j < colors.size() - 1 && j < entryCount - 1) {
                            mLabels.add(null);
                        } else { // add label to the last entry

                            String label = data.getDataSetByIndex(i).getLabel();
                            mLabels.add(label);
                        }

                        mColors.add(colors.get(j));
                    }
                }
            }

            if (mLegend.getExtraColors() != null && mLegend.getExtraLabels() != null) {
                for (int color : mLegend.getExtraColors())
                    mColors.add(color);
                Collections.addAll(mLabels, mLegend.getExtraLabels());
            }

            mLegend.setComputedColors(mColors);
            mLegend.setComputedLabels(mLabels);
        }

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLabelPaint.setTypeface(tf);

        mLabelPaint.setTextSize(mLegend.getTextSize());
        mLabelPaint.setColor(mLegend.getTextColor());

        // calculate all dimensions of the mLegend
        mLegend.calculateDimensions(mLabelPaint, mViewPortHandler);
    }

    protected Paint.FontMetrics legendFontMetrics = new Paint.FontMetrics();

    public void renderLegend(Canvas c) {
        if (!mLegend.isEnabled()) return;

        Typeface tf = mLegend.getTypeface();
        if (tf != null) {
            mLabelPaint.setTypeface(tf);
        }
        mLabelPaint.setTextSize(mLegend.getTextSize());
        mLabelPaint.setColor(mLegend.getTextColor());

        float labelLineHeight = Utils.getLineHeight(mLabelPaint, legendFontMetrics);
        float labelLineSpacing = Utils.getLineSpacing(mLabelPaint, legendFontMetrics)
                + mLegend.getYEntrySpace();
        float formYOffset = labelLineHeight - Utils.calcTextHeight(mLabelPaint, "ABC") / 2.f;

        String[] labels = mLegend.getLabels();
        int[] colors = mLegend.getColors();

        float formToTextSpace = mLegend.getFormToTextSpace();
        float xEntrySpace = mLegend.getXEntrySpace();
        Legend.LegendOrientation orientation = mLegend.getOrientation();
        Legend.LegendHorizontalAlignment horizontalAlignment = mLegend.getHorizontalAlignment();
        Legend.LegendVerticalAlignment verticalAlignment = mLegend.getVerticalAlignment();
        Legend.LegendDirection direction = mLegend.getDirection();
        float formSize = mLegend.getFormSize();

        // space between the entries
        float stackSpace = mLegend.getStackSpace();

        float yoffset = mLegend.getYOffset();
        float xoffset = mLegend.getXOffset();
        float originPosX = 0.f;

        switch (horizontalAlignment) {
            case LEFT:

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = xoffset;
                else
                    originPosX = mViewPortHandler.contentLeft() + xoffset;

                if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                    originPosX += mLegend.mNeededWidth;

                break;

            case RIGHT:

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = mViewPortHandler.getChartWidth() - xoffset;
                else
                    originPosX = mViewPortHandler.contentRight() - xoffset;

                if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                    originPosX -= mLegend.mNeededWidth;

                break;

            case CENTER:

                if (orientation == Legend.LegendOrientation.VERTICAL)
                    originPosX = mViewPortHandler.getChartWidth() / 2.f;
                else
                    originPosX = mViewPortHandler.contentLeft()
                            + mViewPortHandler.contentWidth() / 2.f;

                originPosX += (direction == Legend.LegendDirection.LEFT_TO_RIGHT
                        ? +xoffset
                        : -xoffset);

                // Horizontally layed out legends do the center offset on a line basis,
                // So here we offset the vertical ones only.
                if (orientation == Legend.LegendOrientation.VERTICAL) {
                    originPosX += (direction == Legend.LegendDirection.LEFT_TO_RIGHT
                            ? -mLegend.mNeededWidth / 2.0 + xoffset
                            : mLegend.mNeededWidth / 2.0 - xoffset);
                }

                break;
        }

        switch (orientation) {
            case HORIZONTAL: {

                List<FSize> calculatedLineSizes = mLegend.getCalculatedLineSizes();
                List<FSize> calculatedLabelSizes = mLegend.getCalculatedLabelSizes();
                List<Boolean> calculatedLabelBreakPoints = mLegend.getCalculatedLabelBreakPoints();

                float posX = originPosX + xoffset;
                float posY = 0.f;

                switch (verticalAlignment) {
                    case TOP:
                        posY = yoffset;
                        break;

                    case BOTTOM:
                        posY = mViewPortHandler.getChartHeight() - yoffset - mLegend.mNeededHeight;
                        break;

                    case CENTER:
                        posY = (mViewPortHandler.getChartHeight() - mLegend.mNeededHeight) / 2.f + yoffset;
                        break;
                }

                int lineIndex = 0;
                for (int i = 0, count = labels.length; i < count; i++) {
                    if (i < calculatedLabelBreakPoints.size() && calculatedLabelBreakPoints.get(i)) {
                        posX = originPosX;
                        posY += labelLineHeight + labelLineSpacing;
                    }

                    if (posX == originPosX &&
                            horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER &&
                            lineIndex < calculatedLineSizes.size()) {
                        posX += (direction == Legend.LegendDirection.RIGHT_TO_LEFT
                                ? calculatedLineSizes.get(lineIndex).width
                                : -calculatedLineSizes.get(lineIndex).width) / 2.f;
                        lineIndex++;
                    }

                    boolean drawingForm = colors[i] != ColorTemplate.COLOR_SKIP;
                    boolean isStacked = labels[i] == null; // grouped forms have null labels

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= formSize;

                        drawForm(c, posX, posY + formYOffset, i, mLegend);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += formSize;
                    }

                    if (!isStacked) {
                        if (drawingForm)
                            posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -formToTextSpace :
                                    formToTextSpace;

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= calculatedLabelSizes.get(i).width;

                        drawLabel(c, posX, posY + labelLineHeight, labels[i], i);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += calculatedLabelSizes.get(i).width;

                        posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -xEntrySpace : xEntrySpace;
                    } else
                        posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -stackSpace : stackSpace;
                }

                break;
            }

            case VERTICAL: {
                // contains the stacked legend size in pixels
                float stack = 0f;
                boolean wasStacked = false;
                float posY = 0.f;

                switch (verticalAlignment) {
                    case TOP:
                        posY = (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER
                                ? 0.f
                                : mViewPortHandler.contentTop());
                        posY += yoffset;
                        break;

                    case BOTTOM:
                        posY = (horizontalAlignment == Legend.LegendHorizontalAlignment.CENTER
                                ? mViewPortHandler.getChartHeight()
                                : mViewPortHandler.contentBottom());
                        posY -= mLegend.mNeededHeight + yoffset;
                        break;

                    case CENTER:
                        posY = mViewPortHandler.getChartHeight() / 2.f
                                - mLegend.mNeededHeight / 2.f
                                + mLegend.getYOffset();
                        break;
                }

                for (int i = 0; i < labels.length; i++) {

                    Boolean drawingForm = colors[i] != ColorTemplate.COLOR_SKIP;
                    float posX = originPosX + xoffset;

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += stack;
                        else
                            posX -= formSize - stack;

                        drawForm(c, posX, posY + formYOffset, i, mLegend);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += formSize;
                    }

                    if (labels[i] != null) {

                        if (drawingForm && !wasStacked)
                            posX += direction == Legend.LegendDirection.LEFT_TO_RIGHT ? formToTextSpace
                                    : -formToTextSpace;
                        else if (wasStacked)
                            posX = originPosX;

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= Utils.calcTextWidth(mLabelPaint, labels[i]);

                        if (!wasStacked) {
                            drawLabel(c, posX, posY + labelLineHeight, labels[i]);
                        } else {
                            posY += labelLineHeight + labelLineSpacing;
                            drawLabel(c, posX, posY + labelLineHeight, labels[i]);
                        }

                        // make a step down
                        posY += labelLineHeight + labelLineSpacing;
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }

                break;

            }
        }
    }

    /**
     * Draws the Legend-form at the given position with the color at the given index.
     *
     * @param c     canvas to draw with
     * @param x     position
     * @param y     position
     * @param index the index of the color to use (in the colors array)
     */
    protected void drawForm(Canvas c, float x, float y, int index, Legend legend) {

        int[] colors = legend.getColors();

        if (colors[index] == ColorTemplate.COLOR_SKIP) return;
        mFormPaint.setColor(colors[index]);

        float formSize = legend.getFormSize();
        float half = formSize / 2f;

        switch (legend.getFormType()) {
            case CIRCLE:
                c.drawCircle(x + half, y, half, mFormPaint);
                break;
            case SQUARE:
                c.drawRect(x, y - half, x + formSize, y + half, mFormPaint);
                break;
            case LINE:
                c.drawLine(x, y, x + formSize, y, mFormPaint);
                break;
        }
    }

    /**
     * Draws the provided label at the given position.
     */
    protected void drawLabel(Canvas c, float x, float y, String label) {
        c.drawText(label, x, y, mLabelPaint);
    }

    protected void drawLabel(Canvas c, float x, float y, String label, int index) {
        final int colonIndex = label.indexOf(':');
        if (colonIndex != -1) {
            String value = label.substring(colonIndex + 2);
            label = label.substring(0, colonIndex + 2);
            final int color = mLabelPaint.getColor();
            mLabelPaint.setColor(mLegend.getColors()[index]);
            c.drawText(value, x + Utils.calcTextWidth(mLabelPaint, label), y, mLabelPaint);
            mLabelPaint.setColor(color);
        }

        c.drawText(label, x, y, mLabelPaint);
    }
}
