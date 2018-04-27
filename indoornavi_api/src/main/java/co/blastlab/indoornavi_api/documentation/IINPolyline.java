package co.blastlab.indoornavi_api.documentation;

import android.graphics.Point;

import java.util.List;

/**
 * Class representing a INPolyline, creates the INPolyline in webView, communicates with frontend server and draws INPolyline.
 */
public interface IINPolyline {
	/**
	 * Place polyline on the map with all given settings.
	 * There is necessary to use points() method before draw() method to indicate where polyline should to be located.
	 * Use of this method is indispensable to draw polyline with set configuration in the WebView.
	 */
	void draw();

	/**
	 * Locates polyline object at given coordinates. Coordinates needs to be given as List<Point> object.
	 * Use of this method is indispensable to draw a polyline.
	 *
	 * @param points List of points
	 */
	void points(List<Point> points);

	/**
	 * Set color of points and lines in polyline object. To apply this method it's necessary to call draw() after.
	 *
	 * @param color String that specifies the color. Supports color in hex format #AABBCC and rgb format rgb(255,255,255).
	 */
	void setLineColor(String color);
}
