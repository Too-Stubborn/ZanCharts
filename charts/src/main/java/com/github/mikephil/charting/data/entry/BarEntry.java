package com.github.mikephil.charting.data.entry;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.highlight.Range;

/**
 * Entry class for the BarChart. (especially stacked bars)
 *
 * @author Philipp Jahoda
 * @author liangfei
 */
@SuppressLint("ParcelCreator")
public class BarEntry extends Entry {

    /** the values the stacked barchart holds */
    private float[] mYValues;

    /** the ranges for the individual stack values - automatically calculated */
    private Range[] mRanges;

    /** the sum of all negative values this entry (if stacked) contains */
    private float mNegativeSum;

    /** the sum of all positive values this entry (if stacked) contains */
    private float mPositiveSum;

    /**
     * Constructor for stacked bar entries.
     *
     * @param x the x value
     * @param yValues - the stack values, use at lest 2
     */
    public BarEntry(float x, float[] yValues) {
        super(x, calcSum(yValues));
        mYValues = yValues;
        calcRanges();
        calcPosNegSum();
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param x
     * @param y
     */
    public BarEntry(final float x, final float y) {
        super(x, y);
    }

    /**
     * Constructor for stacked bar entries.
     *
     * @param x
     * @param vals  - the stack values, use at least 2
     * @param label Additional unit label.
     */
    public BarEntry(float x, float[] vals, String label) {
        super(x, calcSum(vals), label);

        this.mYValues = vals;
        calcRanges();
        calcPosNegSum();
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param x
     * @param y
     * @param data Spot for additional data this Entry represents.
     */
    public BarEntry(float x, float y, Object data) {
        super(x, y, data);
    }

    /**
     * Returns an exact copy of the BarEntry.
     */
    public BarEntry copy() {

        BarEntry copied = new BarEntry(getX(), getY(), getData());
        copied.setVals(mYValues);
        return copied;
    }

    /**
     * Returns the stacked values this BarEntry represents, or null, if only a single value is
     * represented (then, use getY()).
     *
     * @return
     */
    public float[] getYVals() {
        return mYValues;
    }

    /**
     * Set the array of values this BarEntry should represent.
     *
     * @param vals
     */
    public void setVals(float[] vals) {
        setY(calcSum(vals));
        mYValues = vals;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Returns the value of this BarEntry. If the entry is stacked, it returns the positive sum of
     * all values.
     */
    @Override
    public float getY() {
        return super.getY();
    }

    /**
     * Returns the ranges of the individual stack-entries. Will return null if this entry is not
     * stacked.
     */
    public Range[] getRanges() {
        return mRanges;
    }

    /**
     * Returns true if this BarEntry is stacked (has a values array), false if not.
     */
    public boolean isStacked() {
        return mYValues != null;
    }

    public float getBelowSum(int stackIndex) {

        if (mYValues == null)
            return 0;

        float remainder = 0f;
        int index = mYValues.length - 1;

        while (index > stackIndex && index >= 0) {
            remainder += mYValues[index];
            index--;
        }

        return remainder;
    }

    /**
     * Returns the sum of all positive values this entry (if stacked) contains.
     */
    public float getPositiveSum() {
        return mPositiveSum;
    }

    /**
     * Returns the sum of all negative values this entry (if stacked) contains. (this is a positive
     * number)
     */
    public float getNegativeSum() {
        return mNegativeSum;
    }

    private void calcPosNegSum() {
        if (mYValues == null) {
            mNegativeSum = 0;
            mPositiveSum = 0;
            return;
        }

        float sumNeg = 0f;
        float sumPos = 0f;

        for (float f : mYValues) {
            if (f <= 0f)
                sumNeg += Math.abs(f);
            else
                sumPos += f;
        }

        mNegativeSum = sumNeg;
        mPositiveSum = sumPos;
    }

    /**
     * Calculates the sum across all values of the given stack.
     *
     * @param vals
     * @return
     */
    private static float calcSum(float[] vals) {
        if (vals == null) return 0f;

        float sum = 0f;
        for (float f : vals) {
            sum += f;
        }

        return sum;
    }

    protected void calcRanges() {

        float[] values = mYValues;
        if (values == null || values.length == 0) return;

        mRanges = new Range[values.length];

        float negRemain = -getNegativeSum();
        float posRemain = 0f;

        for (int i = 0; i < mRanges.length; i++) {
            float value = values[i];
            if (value < 0) {
                mRanges[i] = new Range(negRemain, negRemain + value);
                negRemain += Math.abs(value);
            } else {
                mRanges[i] = new Range(posRemain, posRemain + value);
                posRemain += value;
            }
        }
    }
}


