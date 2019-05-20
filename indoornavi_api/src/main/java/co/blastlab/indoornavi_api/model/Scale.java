package co.blastlab.indoornavi_api.model;

import android.graphics.Point;

/**
* Class representing a Scale.
*/
public class Scale {

	public enum Measure {CENTIMETERS, METERS}

	public Measure measure;
	public int realDistance;
	public Point start, stop;

	/**
	 * Scale constructor
	 *
	 * @param measure scale unit {@link Measure}
	 * @param realDistance actual distance between start and stop point, given in centimeters
	 * @param start {@link Point} object represent starting point of the set scale given in pixels
	 * @param stop {@link Point} object represent end point of the set scale give in pixels
	 */
	public Scale(Measure measure, int realDistance, Point start, Point stop) {
		this.measure = measure;
		this.realDistance = this.measure == Measure.METERS ? (realDistance * 100): realDistance;
		this.start = start;
		this.stop = stop;
	}
}
