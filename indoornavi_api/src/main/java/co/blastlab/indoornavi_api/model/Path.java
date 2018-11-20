package co.blastlab.indoornavi_api.model;

import android.graphics.Point;

/**
 * Class representing a Path.
 */
public class Path {

	public Point startPoint;
	public Point endPoint;

	/**
	 * Path object
	 *
	 * @param startPoint path starting point
	 * @param endPoint path ending point
	 */
	public Path(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;

	}
}
