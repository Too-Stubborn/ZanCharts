package com.github.mikephil.charting.highlight;

/**
 * Class that represents the indexRange of one value in a stacked bar entry. e.g.
 * stack values are -10, 5, 20 -> then ranges are (-10 - 0, 0 - 5, 5 - 25).
 *
 * Created by Philipp Jahoda on 24/07/15.
 * Modified by liangfei
 */
public final class Range {

	public float from;
	public float to;

	public Range(float from, float to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Returns true if this indexRange contains (if the value is in between) the given value, false if not.
	 */
	public boolean contains(float value) {
        return value > from && value <= to;
	}

	public boolean isLargerThan(float value) {
		return value > to;
	}

	public boolean isSmallerThan(float value) {
		return value < from;
	}
}