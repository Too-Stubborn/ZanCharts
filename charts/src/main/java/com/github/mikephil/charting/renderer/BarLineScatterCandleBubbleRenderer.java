package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.dataset.DataSet;
import com.github.mikephil.charting.data.entry.Entry;
import com.github.mikephil.charting.data.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.data.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.data.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 09/06/16.
 */
public abstract class BarLineScatterCandleBubbleRenderer extends DataRenderer {

    /**
     * buffer for storing the current minimum and maximum visible x
     */
    protected XBounds mXBounds = new XBounds();

    public BarLineScatterCandleBubbleRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Returns true if the DataSet values should be drawn, false if not.
     *
     * @param set
     * @return
     */
    protected boolean shouldDrawValues(IDataSet set) {
        return set.isVisible() && set.isDrawValuesEnabled();
    }

    /**
     * Checks if the provided entry object is in bounds for drawing considering the current animation phase.
     *
     * @param e
     * @param set
     * @return
     */
    protected boolean isInBoundsX(Entry e, IBarLineScatterCandleBubbleDataSet set) {

        if (e == null) return false;

        float entryIndex = set.getEntryIndex(e);

        return entryIndex < set.getEntryCount() * mAnimator.getPhaseX();
    }

    /**
     * Class representing the bounds of the current viewport in terms of indices in the values array of a DataSet.
     */
    protected class XBounds {

        /**
         * minimum visible entry index
         */
        public int minIndex;

        /**
         * maximum visible entry index
         */
        public int maxIndex;

        /**
         * indexRange of visible entry indices
         */
        public int indexRange;

        /**
         * Calculates the minimum and maximum x values as well as the indexRange between them.
         *
         * @param chart
         * @param dataSet
         */
        public void set(BarLineScatterCandleBubbleDataProvider chart, IBarLineScatterCandleBubbleDataSet dataSet) {
            float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));

            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();

            Entry entryFrom = dataSet.getEntryForXPos(low, DataSet.Rounding.DOWN);
            Entry entryTo = dataSet.getEntryForXPos(high, DataSet.Rounding.UP);

            minIndex = dataSet.getEntryIndex(entryFrom);
            maxIndex = dataSet.getEntryIndex(entryTo);
            indexRange = (int) ((maxIndex - minIndex) * phaseX);
        }
    }
}
