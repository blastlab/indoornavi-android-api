package co.blastlab.indoornavi_api.callback;

import android.graphics.Point;

import java.util.List;
/**
 * A callback interface used to provide List of points asynchronously.
 */
public interface GetPointsCallback {
	/**
	 * Invoked when coordinates are available.
	 * @param points List of coordinates.
	 */
	void onReceivePoints(List<Point> points);
}
