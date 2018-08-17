package co.blastlab.indoornavi_api.model;

import android.graphics.Point;

public class Path {

	public Point startPoint;
	public Point endPoint;

	public Path(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;

	}
}
