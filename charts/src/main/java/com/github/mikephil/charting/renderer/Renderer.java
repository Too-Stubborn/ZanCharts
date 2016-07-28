
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Renderer base class.
 * 
 * @author Philipp Jahoda
 * @author liangfei
 */
public abstract class Renderer {

    /** The drawing area of the chart */
    protected ViewPortHandler mViewPortHandler;

    public Renderer(ViewPortHandler viewPortHandler) {
        mViewPortHandler = viewPortHandler;
    }
}
