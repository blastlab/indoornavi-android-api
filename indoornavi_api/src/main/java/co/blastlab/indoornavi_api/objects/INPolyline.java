package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;

import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class representing a INPolyline, creates the INPolyline in webView, communicates with frontend server and draws INPolyline.
 */

public class INPolyline extends INObject {

	private INMap inMap;

	/**
	 * INPolyline constructor.
	 *
	 * @param inMap INMap object instance
	 */
	public INPolyline(INMap inMap) {
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format(Locale.US, "poly%d", this.hashCode());

		String javaScriptString = String.format("var %s = new INPolyline(navi);", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Place polyline on the map with all given settings.
	 * There is necessary to use points() method before draw() method to indicate where polyline should to be located.
	 * Use of this method is indispensable to draw polyline with set configuration in the WebView.
	 */
	public void draw()
	{
		String javaScriptString = String.format("%s.draw();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Locates polyline object at given coordinates. Coordinates needs to be given as List<Point> object.
	 * Use of this method is indispensable to draw a polyline.
	 *
	 * @param points List of points
	 */
	public void points(List<Point> points)
	{
		String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
		inMap.evaluateJavascript(javaScriptPoints, null);
		String javaScriptString1 = String.format("%s.points(points);", objectInstance);
		inMap.evaluateJavascript(javaScriptString1, null);
	}

	/**
	 * Set color of points and lines in polyline object. To apply this method it's necessary to call draw() after.
	 *
	 * @param color String that specifies the color. Supports color in hex format #AABBCC and rgb format rgb(255,255,255).
	 */
	public void setLineColor(String color)
	{
		String javaScriptString = String.format("%s.setLineColor('%s');", objectInstance, color);
		inMap.evaluateJavascript(javaScriptString, null);
	}
}
