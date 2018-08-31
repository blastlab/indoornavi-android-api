package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class represents a polyline, creates the INPolyline in iframe, communicates with frontend server and draws polyline.
 */
public class INPolyline extends INObject {

	private INMap inMap;

	/**
	 * INPolyline constructor.
	 *
	 * @param inMap INMap object instance
	 */
	private INPolyline(INMap inMap) {
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format(Locale.US, "poly%d", this.hashCode());

		String javaScriptString = String.format("var %s = new INPolyline(navi);", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Place polyline on the map with all given settings.
	 * There is necessity to use points() method before draw() method to indicate where polyline should be located.
	 * Using this method is indispensable to draw polyline with set configuration in the WebView.
	 */
	public void draw()
	{
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates polyline object at given coordinates. Coordinates needs to be given as list of {@link Point} object.
	 * Using this method is indispensable to draw a polyline.
	 *
	 * @param points List of points
	 */
	public void setPoints(List<Point> points)
	{
		if(points != null) {
			String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
			evaluate(javaScriptPoints, null);
			String javaScriptString1 = String.format("%s.setPoints(points);", objectInstance);
			evaluate(javaScriptString1, null);
		} else {
			Log.e("NullPointerException ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Points must be provided!");
		}
	}

	/**
	 * Receives coordinates of the polyline.
	 *
	 * @param onReceiveValueCallback interface - invoked when list of points is available. Return list of {@link Point} object.
	 */
	public void getPoints(final OnReceiveValueCallback<List<Point>> onReceiveValueCallback)
	{
		String javaScriptString = String.format("%s.getPoints();", objectInstance);
		evaluate(javaScriptString, stringPoints -> {
			if(!stringPoints.equals("null")) {
				onReceiveValueCallback.onReceiveValue(PointsUtil.stringToPoints(stringPoints));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): points not set yet! ");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Set color of points and lines in polyline object. To apply this method it's necessary to call draw() after.
	 *
	 * @param color String that specifies the color. Supports color in hex format #AABBCC and rgb format rgb(255,255,255).
	 */
	public void setColor(@ColorInt int color)
	{
		String javaScriptString = String.format("%s.setColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * Gets color of the polyline. Return color value represent as an Integer.
	 *
	 * @param onReceiveValueCallback interface - invoked when polyline color is available. Return Integer value.
	 */
	public void getColor(final OnReceiveValueCallback<Integer> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getColor();", objectInstance);
		evaluate(javaScriptString, stringColor-> {
			if(!stringColor.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Integer.parseInt(stringColor.replaceFirst("#", "").replaceAll("\"", ""), 16));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	public static class INPolylineBuilder  {

		private List<Point> points;
		private INMap inMap;
		private @ColorInt int color;

		public INPolylineBuilder(INMap inMap){
			this.inMap = inMap;
		}

		public INPolylineBuilder setPoints(List<Point> points)
		{
			this.points = points;
			return this;
		}

		public INPolylineBuilder setColor(@ColorInt int color)
		{
			this.color = color;
			return this;
		}

		public INPolyline build() {
			try {
				CountDownLatch latch = new CountDownLatch(1);

				INPolyline inPolyline = new INPolyline(inMap);
				inPolyline.ready(data -> latch.countDown());

				latch.await();

				if(!inPolyline.isTimeout) {
					inPolyline.setPoints(this.points);
					inPolyline.setColor(this.color);
					inPolyline.draw();
					return inPolyline;
				}
			}
			catch (Exception e) {
				Log.e("Create object exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}
