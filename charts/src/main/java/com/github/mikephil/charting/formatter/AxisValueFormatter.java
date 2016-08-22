package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

/**
 * Custom formatter interface that allows formatting of axis labels before they are being drawn.
 * @author Philipp Jahoda
 */
public interface AxisValueFormatter {

    /**
     * Called when a value from an axis is to be formatted before being drawn. 
     * For performance reasons, avoid excessive calculations and memory allocations inside this 
     * method.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return the formatted value
     */
    String getFormattedValue(float value, AxisBase axis);

    /**
     * Returns the number of decimal digits this formatter uses or -1, if unspecified.
     *
     * @return the decimal digits
     */
    int getDecimalDigits();
}
