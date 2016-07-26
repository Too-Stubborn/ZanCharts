package com.github.mikephil.charting.data.interfaces.datasets;

import android.graphics.Typeface;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.dataset.DataSet;
import com.github.mikephil.charting.data.entry.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * Modified by liangfei
 */
public interface IDataSet<T extends Entry> {

    /** returns the minimum y-value this DataSet holds */
    float getYMin();

    /** returns the maximum y-value this DataSet holds */
    float getYMax();

    float getXMin();
    float getXMax();

    /** returns the minimum x-value this DataSet holds */
    void calcMinMax();

    /**
     * Returns the first Entry object found at the given xPos with binary search. If the no Entry
     * at the specified xPos is found, this method returns the Entry at the xPos according to the
     * rounding.
     * <b>INFORMATION</b>: This method does calculations at runtime. Do not over-use in performance
     * critical situations.
     *
     * @param xPos the x value
     */
    T getEntryForXPos(float xPos, Rounding rounding);

    int getEntryCount();

    /**
     * Returns the first Entry object found at the given xPos with binary search. If no Entry is
     * found, this method returns the index at the closest xPos.
     * <b>INFORMATION</b>: This method does calculations at runtime. Do not over-use in performance
     * critical situations.
     */
    T getEntryForXPos(float xPos);

    /**
     * Returns all Entry objects found at the given xPos with binary search. An empty array if no
     * Entry object at that xPos.
     * <b>INFORMATION</b>: This method does calculations at runtime. Do
     * not over-use in performance critical situations.
     */
    List<T> getEntriesForXPos(float xPos);

    /**
     * Returns the Entry object found at the given index (NOT xIndex) in the values array.
     */
    T getEntryForIndex(int index);

    /**
     * Returns the first Entry index found at the given xPos with binary search. If the no Entry at
     * the specified xPos is found, this method returns the Entry at the closest xPos.
     *
     * <b>INFORMATION</b>: This method does calculations at runtime. Do not over-use in performance
     * critical situations.
     *
     * @param xPos the x value
     * @param rounding determine to round up/down/closest if there is no Entry matching the provided
     *                 x-index
     * @return the index
     */
    int getEntryIndex(float xPos, DataSet.Rounding rounding);

    /**
     * Returns the position of the provided entry in the DataSets Entry array. Returns -1 if doesn't
     * exist.
     */
    int getEntryIndex(T e);


    /**
     * This method returns the actual index in the Entry array of the DataSet for a given xIndex.
     * IMPORTANT: This method does calculations at runtime, do not over-use in performance critical
     * situations.
     */
    int getIndexInEntries(int xIndex);

    /**
     * Adds an Entry to the DataSet dynamically. Entries are added to the end of the list. This will
     * also recalculate the current minimum and maximum values of the DataSet and the value-sum.
     */
    boolean addEntry(T e);


    /**
     * Adds an Entry to the DataSet dynamically. Entries are added to their appropriate index in the
     * values array respective to their x-position. This will also recalculate the current minimum
     * and maximum * values of the DataSet and the value-sum.
     */
    void addEntryOrdered(T e);

    /**
     * Removes the first Entry (at index 0) of this DataSet from the entries array. Returns true if
     * successful, false if not.
     */
    boolean removeFirst();

    /**
     * Removes the last Entry (at index size-1) of this DataSet from the entries array. Returns true
     * if successful, false if not.
     */
    boolean removeLast();

    /**
     * Removes an Entry from the DataSets entries array. This will also recalculate the current
     * minimum and maximum values of the DataSet and the value-sum. Returns true if an Entry was
     * removed, false if no Entry could be removed.
     */
    boolean removeEntry(T e);

    /**
     * Removes the Entry object closest to the given xPos from the DataSet. Returns true if an Entry
     * was removed, false if no Entry could be removed.
     */
    boolean removeEntryByXPos(float xPos);

    /**
     * Removes the Entry object at the given index in the values array from the DataSet. Returns
     * true if an Entry was removed, false if no Entry could be removed.
     */
    boolean removeEntry(int index);

    /**
     * Checks if this DataSet contains the specified Entry. Returns true if so, false if not.
     * NOTE: Performance is pretty bad on this one, do not over-use in performance critical
     * situations.
     */
    boolean contains(T entry);

    /**
     * Removes all values from this DataSet and does all necessary recalculations.
     */
    void clear();

    /**
     * Returns the label string that describes the DataSet.
     */
    String getLabel();

    /**
     * Sets the label string that describes the DataSet.
     */
    void setLabel(String label);

    /**
     * Returns the axis this DataSet should be plotted against.
     */
    YAxis.AxisDependency getAxisDependency();

    /**
     * Set the y-axis this DataSet should be plotted against (either LEFT or RIGHT). Default: LEFT
     */
    void setAxisDependency(YAxis.AxisDependency dependency);

    /**
     * Returns all the colors that are set for this DataSet
     */
    List<Integer> getColors();

    /**
     * Returns the first color (index 0) of the colors-array this DataSet contains. This is only
     * used for performance reasons when only one color is in the colors array (size == 1)
     */
    int getColor();

    /**
     * Returns the color at the given index of the DataSet's color array. Performs a
     * IndexOutOfBounds check by modulus.
     */
    int getColor(int index);

    /**
     * Returns true if highlighting of values is enabled, false if not
     */
    boolean isHighlightEnabled();

    /**
     * If set to true, value highlighting is enabled which means that values can
     * be highlighted programmatically or by touch gesture.
     */
    void setHighlightEnabled(boolean enabled);

    /**
     * Sets the formatter to be used for drawing the values inside the chart. If no formatter is
     * set, the chart will automatically determine a reasonable formatting (concerning decimals) for
     * all the values that are drawn inside the chart. Use chart.getDefaultValueFormatter() to use
     * the formatter calculated by the chart.
     */
    void setValueFormatter(ValueFormatter f);

    /**
     * Returns the formatter used for drawing the values inside the chart.
     */
    ValueFormatter getValueFormatter();

    /**
     * Returns true if the valueFormatter object of this DataSet is null.
     */
    boolean needsFormatter();

    /**
     * Sets the color the value-labels of this DataSet should have.
     */
    void setValueTextColor(int color);

    /**
     * Sets a list of colors to be used as the colors for the drawn values.
     */
    void setValueTextColors(List<Integer> colors);

    /**
     * Sets a Typeface for the value-labels of this DataSet.
     */
    void setValueTypeface(Typeface tf);

    /**
     * Sets the text-size of the value-labels of this DataSet in dp.
     */
    void setValueTextSize(float size);

    /**
     * Returns only the first color of all colors that are set to be used for the values.
     */
    int getValueTextColor();

    /**
     * Returns the color at the specified index that is used for drawing the values inside the
     * chart. Uses modulus internally.
     */
    int getValueTextColor(int index);

    /**
     * Returns the typeface that is used for drawing the values inside the chart
     */
    Typeface getValueTypeface();

    /**
     * Returns the text size that is used for drawing the values inside the chart
     */
    float getValueTextSize();

    /**
     * Set this to true to draw y-values on the chart NOTE (for bar and linechart):
     * if "maxvisiblecount" is reached, no values will be drawn even
     * if this is enabled
     */
    void setDrawValues(boolean enabled);

    /** Returns true if y-value drawing is enabled, otherwise false */
    boolean isDrawValuesEnabled();

    /** Set the data set is visible or not */
    void setVisible(boolean visible);

    /** Is visible */
    boolean isVisible();

    /**
     * Determines how to round DataSet index values for
     * {@link DataSet#getEntryIndex(float, Rounding)} DataSet.getEntryIndex()} when an exact x-index
     * is not found.
     */
    enum Rounding {
        UP,
        DOWN,
        CLOSEST,
    }
}
