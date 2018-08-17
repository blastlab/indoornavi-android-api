package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.List;
import java.util.Locale;

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
	public void points(List<Point> points)
	{
		if(points != null) {
			String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
			evaluate(javaScriptPoints, null);
			String javaScriptString1 = String.format("%s.points(points);", objectInstance);
			evaluate(javaScriptString1, null);
		} else {
			Log.e("NullPointerException ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Points must be provided!");
		}
	}

	/**
	 * Set color of points and lines in polyline object. To apply this method it's necessary to call draw() after.
	 *
	 * @param color String that specifies the color. Supports color in hex format #AABBCC and rgb format rgb(255,255,255).
	 */
	public void setLineColor(@ColorInt int color)
	{
		String javaScriptString = String.format("%s.setLineColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	public static class INPolylineBuilder  {

		private List<Point> points;
		private INMap inMap;
		private @ColorInt int color;

		public INPolylineBuilder(INMap inMap){
			this.inMap = inMap;
		}

		public INPolylineBuilder points(List<Point> points)
		{
			this.points = points;
			return this;
		}

		public INPolylineBuilder setLineColor(@ColorInt int color)
		{
			this.color = color;
			return this;
		}

		public INPolyline build() {
			try {
				INPolyline inPolyline = new INPolyline(inMap);
				inPolyline = new MyAsyncTask(inPolyline).execute().get();

				if(!inPolyline.isTimeout) {
					inPolyline.points(this.points);
					inPolyline.setLineColor(this.color);
					inPolyline.draw();
					return inPolyline;
				}
			}
			catch (Exception e) {
				Log.e("Create object exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}

		private static class MyAsyncTask extends AsyncTask<Void, Void, INPolyline> {
			INPolyline inPolyline;
			boolean ready = false;

			private MyAsyncTask(INPolyline inPolyline) {
				super();
				this.inPolyline = inPolyline;
				this.inPolyline.ready(data -> ready = true);
			}

			@Override
			protected INPolyline doInBackground(Void... arg0) {
				while(!ready);
				return inPolyline;
			}
		}
	}
}
