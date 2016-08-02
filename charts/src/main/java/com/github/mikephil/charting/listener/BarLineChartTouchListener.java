package com.github.mikephil.charting.listener;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.entry.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.data.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * TouchListener for Bar-, Line-, Scatter- and CandleStickChart with handles all
 * touch interaction. Longpress == Zoom out. Double-Tap == Zoom in.
 *
 * @author Philipp Jahoda
 */
public class BarLineChartTouchListener extends ChartTouchListener<BarLineChartBase<?
        extends BarLineScatterCandleBubbleData<?
        extends IBarLineScatterCandleBubbleDataSet<? extends Entry>>>> {

    /**
     * the original touch-matrix from the chart
     */
    private Matrix mTouchMatrix = new Matrix();

    /**
     * matrix for saving the original matrix state
     */
    private Matrix mSavedMatrix = new Matrix();

    /**
     * point where the touch action started
     */
    private MPPointF mTouchStartPoint = MPPointF.getInstance(0, 0);

    /**
     * center between two pointers (fingers on the display)
     */
    private MPPointF mTouchPointCenter = MPPointF.getInstance(0, 0);

    private float mSavedXDist = 1f;
    private float mSavedYDist = 1f;
    private float mSavedDist = 1f;

    private IDataSet mClosestDataSetToTouch;

    /**
     * used for tracking velocity of dragging
     */
    private VelocityTracker mVelocityTracker;

    private long mDecelerationLastTime = 0;
    private MPPointF mDecelerationCurrentPoint = MPPointF.getInstance(0, 0);
    private MPPointF mDecelerationVelocity = MPPointF.getInstance(0, 0);

    /**
     * the distance of movement that will be counted as a drag
     */
    private float mDragTriggerDist;

    /**
     * the minimum distance between the pointers that will trigger a zoom gesture
     */
    private float mMinScalePointerDistance;

    /**
     * Constructor with initialization parameters.
     *
     * @param chart               instance of the chart
     * @param touchMatrix         the touch-matrix of the chart
     * @param dragTriggerDistance the minimum movement distance that will be interpreted as a "drag" gesture in dp (3dp equals
     *                            to about 9 pixels on a 5.5" FHD screen)
     */
    public BarLineChartTouchListener(BarLineChartBase<? extends BarLineScatterCandleBubbleData<? extends
            IBarLineScatterCandleBubbleDataSet<? extends Entry>>> chart, Matrix touchMatrix, float dragTriggerDistance) {
        super(chart);

        mTouchMatrix = touchMatrix;
        mDragTriggerDist = Utils.dp2px(dragTriggerDistance);
        mMinScalePointerDistance = Utils.dp2px(3.5f);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            if (mVelocityTracker != null) {
                Log.d("fling", "action cancel");
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
        }

        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }

        if (!mChart.isDragEnabled() && (!mChart.isScaleXEnabled() && !mChart.isScaleYEnabled()))
            return true;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                startAction(event);
                stopDeceleration();
                saveTouchStart(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    mChart.disableScroll();
                    saveTouchStart(event);

                    // get the distance between the pointers on the x-axis
                    mSavedXDist = getXDist(event);

                    // get the distance between the pointers on the y-axis
                    mSavedYDist = getYDist(event);

                    // get the total distance between the pointers
                    mSavedDist = spacing(event);

                    if (mSavedDist > 10f) {
                        if (mChart.isPinchZoomEnabled()) {
                            mTouchMode = PINCH_ZOOM;
                        } else {
                            if (mSavedXDist > mSavedYDist)
                                mTouchMode = X_ZOOM;
                            else
                                mTouchMode = Y_ZOOM;
                        }
                    }

                    // determine the touch-pointer center
                    midPoint(mTouchPointCenter, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (mTouchMode == DRAG) {
                    mChart.disableScroll();
                    performDrag(event);
                } else if (mTouchMode == X_ZOOM || mTouchMode == Y_ZOOM || mTouchMode == PINCH_ZOOM) {
                    mChart.disableScroll();
                    if (mChart.isScaleXEnabled() || mChart.isScaleYEnabled()) {
                        performZoom(event);
                    }
                } else if (mTouchMode == NONE
                        && Math.abs(distance(event.getX(), mTouchStartPoint.x, event.getY(),
                        mTouchStartPoint.y)) > mDragTriggerDist) {

                    if (mChart.hasNoDragOffset()) {
                        if (!mChart.isFullyZoomedOut() && mChart.isDragEnabled()) {
                            mTouchMode = DRAG;
                        } else {
                            mLastGesture = ChartGesture.DRAG;
                            if (mChart.isHighlightPerDragEnabled()) {
                                performHighlightDrag(event);
                            }
                        }

                    } else if (mChart.isDragEnabled()) {
                        mLastGesture = ChartGesture.DRAG;
                        mTouchMode = DRAG;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

                final VelocityTracker velocityTracker = mVelocityTracker;
                final int pointerId = event.getPointerId(0);
                velocityTracker.computeCurrentVelocity(1000, Utils.getMaximumFlingVelocity());
                final float velocityY = velocityTracker.getYVelocity(pointerId);
                final float velocityX = velocityTracker.getXVelocity(pointerId);

                if (Math.abs(velocityX) > Utils.getMinimumFlingVelocity() ||
                        Math.abs(velocityY) > Utils.getMinimumFlingVelocity()) {

                    if (mTouchMode == DRAG && mChart.isDragDecelerationEnabled()) {

                        stopDeceleration();

                        mDecelerationLastTime = AnimationUtils.currentAnimationTimeMillis();

                        mDecelerationCurrentPoint.x = event.getX();
                        mDecelerationCurrentPoint.y = event.getY();

                        mDecelerationVelocity.x = velocityX;
                        mDecelerationVelocity.y = velocityY;

                        // This causes computeScroll to fire, recommended for this by google
                        Utils.postInvalidateOnAnimation(mChart);
                    }
                } else if (mTouchMode == DRAG) {
                    // drag ended
                    OnChartGestureListener listener = mChart.getOnChartGestureListener();
                    if (listener != null) {
                        listener.onDragEnd();
                    }
                }

                if (mTouchMode == X_ZOOM ||
                        mTouchMode == Y_ZOOM ||
                        mTouchMode == PINCH_ZOOM ||
                        mTouchMode == POST_ZOOM) {

                    // Range might have changed, which means that Y-axis labels
                    // could have changed in size, affecting Y-axis size.
                    // So we need to recalculate offsets.
                    mChart.calculateOffsets();
                    mChart.postInvalidate();
                }

                mTouchMode = NONE;
                mChart.enableScroll();

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                endAction(event);

                break;
            case MotionEvent.ACTION_POINTER_UP:
                Utils.velocityTrackerPointerUpCleanUpIfNecessary(event, mVelocityTracker);

                mTouchMode = POST_ZOOM;
                break;

            case MotionEvent.ACTION_CANCEL:

                mTouchMode = NONE;
                endAction(event);
                break;
        }

        // perform the transformation, update the chart
        mTouchMatrix = mChart.getViewPortHandler().refresh(mTouchMatrix, mChart, true);

        return true; // indicate event was handled
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW CODE PERFORMS THE ACTUAL TOUCH ACTIONS */

    /**
     * Saves the current Matrix state and the touch-start point.
     *
     * @param event
     */
    private void saveTouchStart(MotionEvent event) {

        mSavedMatrix.set(mTouchMatrix);
        mTouchStartPoint.x = event.getX();
        mTouchStartPoint.y = event.getY();

        mClosestDataSetToTouch = mChart.getDataSetByTouchPoint(event.getX(), event.getY());
    }

    /**
     * Performs all necessary operations needed for dragging.
     */
    private void performDrag(MotionEvent event) {
        mLastGesture = ChartGesture.DRAG;
        mTouchMatrix.set(mSavedMatrix);

        float dX = event.getX() - mTouchStartPoint.x;
        float dY = event.getY() - mTouchStartPoint.y;

        // moving to right
        mTouchMatrix.postTranslate(dX, dY);

        OnChartGestureListener listener = mChart.getOnChartGestureListener();
        if (listener != null) {
            listener.onChartTranslate(event, dX, dY);
        }
    }

    /**
     * Performs the all operations necessary for pinch and axis zoom.
     *
     * @param event
     */
    private void performZoom(MotionEvent event) {

        if (event.getPointerCount() >= 2) { // two finger zoom
            OnChartGestureListener l = mChart.getOnChartGestureListener();

            // get the distance between the pointers of the touch event
            float totalDist = spacing(event);

            if (totalDist > mMinScalePointerDistance) {

                // get the translation
                MPPointF t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y);
                ViewPortHandler h = mChart.getViewPortHandler();

                // take actions depending on the activated touch mode
                if (mTouchMode == PINCH_ZOOM) {

                    mLastGesture = ChartGesture.PINCH_ZOOM;

                    float scale = totalDist / mSavedDist; // total scale

                    boolean isZoomingOut = (scale < 1);

                    boolean canZoomMoreX = isZoomingOut ?
                            h.canZoomOutMoreX() :
                            h.canZoomInMoreX();

                    boolean canZoomMoreY = isZoomingOut ?
                            h.canZoomOutMoreY() :
                            h.canZoomInMoreY();

                    float scaleX = (mChart.isScaleXEnabled()) ? scale : 1f;
                    float scaleY = (mChart.isScaleYEnabled()) ? scale : 1f;

                    if (canZoomMoreY || canZoomMoreX) {

                        mTouchMatrix.set(mSavedMatrix);
                        mTouchMatrix.postScale(scaleX, scaleY, t.x, t.y);

                        if (l != null)
                            l.onChartScale(event, scaleX, scaleY);
                    }

                } else if (mTouchMode == X_ZOOM && mChart.isScaleXEnabled()) {

                    mLastGesture = ChartGesture.X_ZOOM;

                    float xDist = getXDist(event);
                    float scaleX = xDist / mSavedXDist; // x-axis scale

                    boolean isZoomingOut = (scaleX < 1);
                    boolean canZoomMoreX = isZoomingOut ? h.canZoomOutMoreX() : h.canZoomInMoreX();

                    if (canZoomMoreX) {

                        mTouchMatrix.set(mSavedMatrix);
                        mTouchMatrix.postScale(scaleX, 1f, t.x, t.y);

                        if (l != null)
                            l.onChartScale(event, scaleX, 1f);
                    }

                } else if (mTouchMode == Y_ZOOM && mChart.isScaleYEnabled()) {

                    mLastGesture = ChartGesture.Y_ZOOM;

                    float yDist = getYDist(event);
                    float scaleY = yDist / mSavedYDist; // y-axis scale

                    boolean isZoomingOut = (scaleY < 1);
                    boolean canZoomMoreY = isZoomingOut ?
                            h.canZoomOutMoreY() :
                            h.canZoomInMoreY();

                    if (canZoomMoreY) {

                        mTouchMatrix.set(mSavedMatrix);
                        mTouchMatrix.postScale(1f, scaleY, t.x, t.y);

                        if (l != null)
                            l.onChartScale(event, 1f, scaleY);
                    }
                }

                MPPointF.recycleInstance(t);
            }
        }
    }

    /**
     * Highlights upon dragging, generates callbacks for the selection-listener.
     *
     * @param e
     */
    private void performHighlightDrag(MotionEvent e) {

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());

        if (h != null && !h.equalTo(mLastHighlighted)) {
            mLastHighlighted = h;
            mChart.highlightValue(h, true);
        }
    }

    /**
     * ################ ################ ################ ################
     */
    /** DOING THE MATH BELOW ;-) */


    /**
     * Determines the center point between two pointer touch points.
     *
     * @param point
     * @param event
     */
    private static void midPoint(MPPointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.x = (x / 2f);
        point.y = (y / 2f);
    }

    /**
     * returns the distance between two pointer touch points
     *
     * @param event
     * @return
     */
    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * calculates the distance on the x-axis between two pointers (fingers on
     * the display)
     *
     * @param e
     * @return
     */
    private static float getXDist(MotionEvent e) {
        return Math.abs(e.getX(0) - e.getX(1));
    }

    /**
     * calculates the distance on the y-axis between two pointers (fingers on
     * the display)
     *
     * @param e
     * @return
     */
    private static float getYDist(MotionEvent e) {
        float y = Math.abs(e.getY(0) - e.getY(1));
        return y;
    }

    /**
     * Returns a recyclable MPPointF instance.
     * returns the correct translation depending on the provided x and y touch
     * points
     *
     * @param x
     * @param y
     * @return
     */
    public MPPointF getTrans(float x, float y) {

        ViewPortHandler vph = mChart.getViewPortHandler();

        float xTrans = x - vph.offsetLeft();
        float yTrans = 0f;

        // check if axis is inverted
        if (inverted()) {
            yTrans = -(y - vph.offsetTop());
        } else {
            yTrans = -(mChart.getMeasuredHeight() - y - vph.offsetBottom());
        }

        return MPPointF.getInstance(xTrans, yTrans);
    }

    /**
     * Returns true if the current touch situation should be interpreted as inverted, false if not.
     *
     * @return
     */
    private boolean inverted() {
        return (mClosestDataSetToTouch == null && mChart.isAnyAxisInverted()) || (mClosestDataSetToTouch != null
                && mChart.isInverted(mClosestDataSetToTouch.getAxisDependency()));
    }

    /**
     * ################ ################ ################ ################
     */
    /** GETTERS AND GESTURE RECOGNITION BELOW */

    /**
     * returns the matrix object the listener holds
     *
     * @return
     */
    public Matrix getMatrix() {
        return mTouchMatrix;
    }

    /**
     * Sets the minimum distance that will be interpreted as a "drag" by the chart in dp.
     * Default: 3dp
     *
     * @param dragTriggerDistance
     */
    public void setDragTriggerDist(float dragTriggerDistance) {
        this.mDragTriggerDist = Utils.dp2px(dragTriggerDistance);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        mLastGesture = ChartGesture.DOUBLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
        }

        // check if double-tap zooming is enabled
        if (mChart.isDoubleTapToZoomEnabled() && mChart.getData().getEntryCount() > 0) {

            MPPointF trans = getTrans(e.getX(), e.getY());

            mChart.zoom(mChart.isScaleXEnabled() ? 1.4f : 1f, mChart.isScaleYEnabled() ? 1.4f : 1f, trans.x, trans.y);

            if (mChart.isLogEnabled())
                Log.i("BarlineChartTouch", "Double-Tap, Zooming In, x: " + trans.x + ", y: "
                        + trans.y);

            MPPointF.recycleInstance(trans);
        }

        return super.onDoubleTap(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {

        mLastGesture = ChartGesture.LONG_PRESS;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {

            l.onChartLongPressed(e);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        mLastGesture = ChartGesture.SINGLE_TAP;

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        if (!mChart.isHighlightPerTapEnabled()) {
            return false;
        }

        Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
        performHighlight(h, e);

        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        mLastGesture = ChartGesture.FLING;

        OnChartGestureListener l = mChart.getOnChartGestureListener();
        if (l != null) {
            l.onChartFling(e1, e2, velocityX, velocityY);
        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public void stopDeceleration() {
        mDecelerationVelocity.x = 0;
        mDecelerationVelocity.y = 0;
    }

    public void computeScroll() {

        // There's no deceleration in progress
        if (mDecelerationVelocity.x == 0.f && mDecelerationVelocity.y == 0.f) return;

        final long currentTime = AnimationUtils.currentAnimationTimeMillis();

        mDecelerationVelocity.x *= mChart.getDragDecelerationFrictionCoef();
        mDecelerationVelocity.y *= mChart.getDragDecelerationFrictionCoef();

        final float timeInterval = (float) (currentTime - mDecelerationLastTime) / 1000.f;

        float distanceX = mDecelerationVelocity.x * timeInterval;
        float distanceY = mDecelerationVelocity.y * timeInterval;

        mDecelerationCurrentPoint.x += distanceX;
        mDecelerationCurrentPoint.y += distanceY;

        MotionEvent event = MotionEvent.obtain(currentTime, currentTime, MotionEvent.ACTION_MOVE,
                mDecelerationCurrentPoint.x, mDecelerationCurrentPoint.y, 0);

        performDrag(event);

        event.recycle();

        mTouchMatrix = mChart.getViewPortHandler().refresh(mTouchMatrix, mChart, false);
        mDecelerationLastTime = currentTime;

        OnChartGestureListener listener = mChart.getOnChartGestureListener();
        if (Math.abs(mDecelerationVelocity.x) >= 1 || Math.abs(mDecelerationVelocity.y) >= 1) {
            // This causes computeScroll to fire, recommended for this by Google
            Utils.postInvalidateOnAnimation(mChart);
        } else {
            // Range might have changed, which means that Y-axis labels could have changed in size,
            // affecting Y-axis size. So we need to recalculate offsets.
            mChart.calculateOffsets();
            mChart.postInvalidate();

            stopDeceleration();

            if (listener != null) {
                listener.onScrollEnd();
            }
        }
    }
}
