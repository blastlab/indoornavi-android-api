package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.webkit.ValueCallback;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.AndroidExecutionScope;

import java.util.List;

import co.blastlab.indoornavi_api.callback.GetIdCallback;
import co.blastlab.indoornavi_api.callback.GetPointsCallback;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class representing a INPolyline, creates the INPolyline in webView, communicates with indoornavi frontend server and draws INPolyline.
 *
 * @author Agata Ziółkowska <achmielewska@blastlab.co>
 */

public class INPolyline extends INObject {

	private INMap inMap;

	/**
	 * INPolyline constructor.
	 *
	 * @param inMap INMap object instance
	 */
	public INPolyline(INMap inMap)
	{
		this.inMap = inMap;
		String javaScriptString = "var poly = new INPolyline(navi);";
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Call inherit method from {@link INObject}.
	 * Method wait till INPolyline object is create.
	 * Use of this method is indispensable to operate on INPolyline object.
	 *
	 * @param doneCallback DoneCallback interface - trigger when poly is create (Promise is resolved).
	 */
	public void ready(DoneCallback<String> doneCallback)
	{
		AndroidDeferredManager dm = new AndroidDeferredManager();
		dm.when(checkReady(inMap, INPOLYLINE_OBJECT), AndroidExecutionScope.UI).done(doneCallback);
	}

	/**
	 * Call inherit method from {@link INObject}.
	 *
	 * @param getIdCallback Callback interface {@link GetIdCallback}
	 */
	public void getID(GetIdCallback getIdCallback)
	{
		getID(inMap, INPOLYLINE_OBJECT, getIdCallback);
	}

	/**
	 * Receives coordinates of the given INPolyline object.
	 *
	 * @param getPointsCallback Callback interface {@link GetPointsCallback}
	 */
	public void getPoints(final GetPointsCallback getPointsCallback)
	{
		String javaScriptString = "poly.getPoints();";
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				List<Point> points;
				points = PointsUtil.stringToPoints(s);
				getPointsCallback.onReceivePoints(points);
			}
		});
	}

	//TODO jeszcze nie zaimplementowana funkcjionalność
	/*public void isWithin(INCoordinates inCoordinates)
	{
		String javaScriptString = "poly.isWithin({x: 250, y: 480});";
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				Log.i(Constants.LOG, "onReceiveValue: " + s);
			}
		});
	}*/

	/**
	 * Place polyline on the map with all given settings.
	 * There is necessary to use points() method before place() method to indicate where polyline should to be located.
	 * Use of this method is indispensable to draw polyline with set configuration in the WebView.
	 */
	public void place()
	{
		String javaScriptString = "poly.place();";
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Locates INPolyline object at given coordinates. Coordinates needs to be given as List<Point> object.
	 * Use of this method is indispensable to draw a polyline.
	 *
	 * @param points
	 */
	public void points(List<Point> points)
	{
		String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
		inMap.evaluateJavascript(javaScriptPoints, null);
		String javaScriptString1 = String.format("poly.points(points);");
		inMap.evaluateJavascript(javaScriptString1, null);
	}

	/**
	 * Set color of points and lines in INPolyline object
	 *
	 * @param color String that specifies the color. Supports color in hex format #AABBCC and rgb format rgb(255,255,255).
	 */
	public void setLineColor(String color)
	{
		String javaScriptString = String.format("poly.setLineColor('%s');", color);
		inMap.evaluateJavascript(javaScriptString, null);
	}
}
