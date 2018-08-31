package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.util.Log;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;;import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Border;
import co.blastlab.indoornavi_api.utils.PointsUtil;

public class INCircle extends INObject {

	private INMap inMap;

	/**
	 * INCircle constructor
	 *
	 * @param inMap INMap object instance
	 */
	private INCircle(INMap inMap) {
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format("circle%s", this.hashCode());
		String javaScriptString = String.format("var %s = new INCircle(navi);", this.objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Place circle on the map with all given settings. There is necessity to use setPosition() method before draw() method to indicate where area should to be located.
	 * Using of this method is indispensable to draw circle with set configuration in the IndoorNavi Map.
	 */
	public void draw()
	{
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates circle at given coordinates. Coordinates needs to be given as real world dimensions that map is representing. Use of this method is indispensable.
	 *
	 * @param position position where the center of the circle will be located.
	 */
	public void setPosition(Point position)
	{
		if (position != null) {
			String javaScriptString = String.format(Locale.ENGLISH, "%s.setPosition(new Point(%d, %d));", objectInstance, position.x, position.y);
			evaluate(javaScriptString, null);
		} else {
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Invalid point given");
		}
	}

	/**
	 * Gets position of the circle
	 *
	 * @param onReceiveValueCallback interface - invoked when circle position is available. Return {@link Point} object.
	 */
	public void getPosition( final OnReceiveValueCallback<Point> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getPosition();", objectInstance);
		evaluate(javaScriptString, stringPosition -> {
			if(!stringPosition.equals("null")) {
				onReceiveValueCallback.onReceiveValue(PointsUtil.stringToPoint(stringPosition));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Sets radius of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param radius radius of the circle
	 */
	public void setRadius(int radius)
	{
		String javaScriptString = String.format(Locale.ENGLISH, "%s.setRadius(%d);", objectInstance, radius);
		evaluate(javaScriptString, null);
	}

	/**
	 * Gets radius of the circle
	 *
	 * @param onReceiveValueCallback interface - invoked when circle radius is available. Return Integer value.
	 */
	public void getRadius(final OnReceiveValueCallback<Integer> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getRadius();", objectInstance);
		evaluate(javaScriptString, stringRadius-> {
			if(!stringRadius.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Integer.parseInt(stringRadius));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Sets color of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param color that specifies the color.
	 */
	public void setColor(@ColorInt int color)
	{
		String javaScriptString = String.format("%s.setColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * Gets color of the circle
	 *
	 * @param onReceiveValueCallback interface - invoked when circle color is available. Return color value represent as an Integer..
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

	/**
	 * Sets opacity of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param opacity Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	public void setOpacity(@FloatRange(from=0.0, to=1.0)double opacity)
	{
		String javaScriptString = String.format("%s.setOpacity(%s);", objectInstance, String.format(Locale.US, "%f", opacity));
		evaluate(javaScriptString, null);
	}

	/**
	 * Gets opacity of the circle
	 *
	 * @param onReceiveValueCallback interface - invoked when circle opacity is available. Return Float value.
	 */
	public void getOpacity(final OnReceiveValueCallback<Float> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getOpacity();", objectInstance);
		evaluate(javaScriptString, stringOpacity-> {
			if(!stringOpacity.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Float.parseFloat(stringOpacity));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Sets opacity of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param border Border object, define border parameters of the circle.
	 */
	public void setBorder(Border border)
	{
		String javaScriptString = String.format(Locale.ENGLISH, "%s.setBorder(new Border(%d, '%s'));", objectInstance, border.width, String.format("#%06X", (0xFFFFFF & border.color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * Gets border of the circle.
	 *
	 * @param onReceiveValueCallback interface - invoked when circle border is available. Return {@link Border} object.
	 */
	public void getBorder(final OnReceiveValueCallback<Border> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getBorder();", objectInstance);
		evaluate(javaScriptString, stringOpacity-> {
			if(!stringOpacity.equals("null")) {
				try {
					JSONObject jsonObject = new JSONObject(stringOpacity);
					onReceiveValueCallback.onReceiveValue(new Border(jsonObject.getInt("width"), Integer.parseInt(jsonObject.getString("color").replaceFirst("#", ""), 16)));
				} catch (Exception e) {

				}
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	public static class INCircleBuilder  {

		private Point position;
		private INMap inMap;
		private int radius;
		private @ColorInt int color;
		private @FloatRange(from=0.0, to=1.0)double opacity;
		private Border border;

		public INCircleBuilder(INMap inMap){
			this.inMap = inMap;
		}

		public INCircle.INCircleBuilder setPosition(Point position)
		{
			this.position = position;
			return this;
		}

		public INCircle.INCircleBuilder setRadius(int radius)
		{
			this.radius = radius;
			return this;
		}

		public INCircle.INCircleBuilder setColor(@ColorInt int color)
		{
			this.color = color;
			return this;
		}

		public INCircle.INCircleBuilder setOpacity(@FloatRange(from=0.0, to=1.0)double opacity)
		{
			this.opacity = opacity;
			return this;
		}

		public INCircle.INCircleBuilder setBorder(Border border)
		{
			this.border = border;
			return this;
		}

		public INCircle build() {
			try{
				CountDownLatch latch = new CountDownLatch(1);

				INCircle inCircle = new INCircle(inMap);
				inCircle.ready(data -> latch.countDown());

				latch.await();

				if(!inCircle.isTimeout) {
					inCircle.setPosition(this.position);
					inCircle.setRadius(this.radius);
					inCircle.setColor(this.color);
					inCircle.setOpacity(this.opacity);
					inCircle.setBorder(this.border);
					inCircle.draw();
					return inCircle;
				}
			}
			catch (Exception e) {
				Log.e("Create object exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}

