package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class represents a polyline, creates the INPolyline in iframe, communicates with frontend server and draws polyline.
 */
public class INPolyline extends INObject {

	private INMap inMap;
	private List<Point> points;
	private @ColorInt int color;

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
	public void draw() {
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates polyline object at given coordinates. Coordinates needs to be given as list of {@link Point} object.
	 * Using this method is indispensable to draw a polyline.
	 *
	 * @param points List of points
	 */
	public void setPoints(List<Point> points) {
		if (points != null) {
			this.points = points;
			String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
			evaluate(javaScriptPoints, null);
			String javaScriptString1 = String.format("%s.setPoints(points);", objectInstance);
			evaluate(javaScriptString1, null);
		} else {
			Log.e("NullPointerException ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Points must be provided!");
		}
	}

	/**
	 * @return coordinates of the polyline as a list of {@link Point} object.
	 */
	public List<Point> getPoints() {
		return this.points;
	}

	/**
	 * Set color of points and lines in polyline object. To apply this method it's necessary to call draw() after.
	 *
	 * @param color String that specifies the color. Supports color in hex format #AABBCC and rgb format rgb(255,255,255).
	 */
	public void setColor(@ColorInt int color) {
		this.color = color;
		String javaScriptString = String.format("%s.setColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return color of the polyline. Return color value represent as an Integer.
	 */
	public @ColorInt int getColor() {
		return this.color;
	}

	/**
	 * Erase object and its instance from frontend server, but do not destroys object class instance in your app.
	 */
	public void erase() {
		super.erase();
		this.inMap = null;
		this.points = null;
		this.color = 0;
	}

	public static class INPolylineBuilder {

		private INPolyline inPolyline;

		public INPolylineBuilder(INMap inMap) {
			inPolyline = new INPolyline(inMap);
		}

		public INPolylineBuilder setPoints(List<Point> points) {
			inPolyline.setPoints(points);
			return this;
		}

		public INPolylineBuilder setColor(@ColorInt int color) {
			inPolyline.setColor(color);
			return this;
		}

		public INPolyline build() {
			try {
				CountDownLatch latch = new CountDownLatch(1);
				inPolyline.ready(data -> latch.countDown());

				latch.await();

				if (!inPolyline.isTimeout) {

					inPolyline.draw();
					return inPolyline;
				}
			} catch (Exception e) {
				Log.e("Create object exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}
