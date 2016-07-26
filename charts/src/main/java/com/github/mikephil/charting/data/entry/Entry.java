
package com.github.mikephil.charting.data.entry;

/**
 * Class representing one entry in the chart. Might contain multiple values. Might only contain a
 * single value depending on the used constructor.
 * 
 * @author Philipp Jahoda
 * @author liangfei
 */
public class Entry {

    private float x = 0f;
    private float y = 0f;
    private Object data;

    public Entry() {

    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the x value
     * @param y the y value (the actual value of the entry)
     */
    public Entry(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the x value
     * @param y the y value (the actual value of the entry)
     * @param data Spot for additional data this Entry represents.
     */
    public Entry(float x, float y, Object data) {
        this.x = x;
        this.y = y;
        this.data = data;
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(final float y) {
        this.y = y;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    /**
     * returns an exact copy of the entry
     */
    public Entry copy() {
        return new Entry(x, y, data);
    }

    /**
     * Compares value, xIndex and data of the entries. Returns true if entries are equal in those
     * points, false if not. Does not check by hash-code like it's done by the "equals" method.
     */
    public boolean equalTo(Entry e) {
        return e != null && e.data == data && Math.abs(e.x - this.x) <= 0.000001f
                && Math.abs(e.getY() - this.getY()) <= 0.000001f;

    }

    /**
     * returns a string representation of the entry containing x-index and value
     */
    @Override
    public String toString() {
        return "Entry, x: " + x + " y (sum): " + getY();
    }
}
