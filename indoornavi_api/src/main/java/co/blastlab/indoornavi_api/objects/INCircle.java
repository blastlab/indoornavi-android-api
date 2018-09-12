package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.model.Border;

public class INCircle extends INObject {

	private INMap inMap;
	private Point position;
	private int radius;
	private @ColorInt int color;
	private @FloatRange(from = 0.0, to = 1.0) double opacity;
	private Border border;

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
	public void draw() {
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates circle at given coordinates. Coordinates needs to be given as real world dimensions that map is representing. Use of this method is indispensable.
	 *
	 * @param position position where the center of the circle will be located.
	 */
	public void setPosition(Point position) {
		if (position != null) {
			this.position = position;
			String javaScriptString = String.format(Locale.ENGLISH, "%s.setPosition(new Point(%d, %d));", objectInstance, position.x, position.y);
			evaluate(javaScriptString, null);
		} else {
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Invalid point given");
		}
	}

	/**
	 * @return position of the circle. Return {@link Point} object.
	 */
	public Point getPosition() {
		return this.position;
	}

	/**
	 * Sets radius of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param radius radius of the circle
	 */
	public void setRadius(int radius) {
		this.radius = radius;
		String javaScriptString = String.format(Locale.ENGLISH, "%s.setRadius(%d);", objectInstance, radius);
		evaluate(javaScriptString, null);
	}

	/**
	 * @return radius of the circle. Return Integer value.
	 */
	public int getRadius() {
		return this.radius;
	}

	/**
	 * Sets color of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param color that specifies the color.
	 */
	public void setColor(@ColorInt int color) {
		this.color = color;
		String javaScriptString = String.format("%s.setColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return color of the circle represent as an Integer.
	 */
	public @ColorInt int getColor() {
		return this.color;
	}

	/**
	 * Sets opacity of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param opacity Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	public void setOpacity(@FloatRange(from = 0.0, to = 1.0) double opacity) {
		this.opacity = opacity;
		String javaScriptString = String.format("%s.setOpacity(%s);", objectInstance, String.format(Locale.US, "%f", opacity));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return opacity of the circle. Return Float value.
	 */
	@FloatRange(from = 0.0, to = 1.0) public  double getOpacity() {
		return this.opacity;
	}

	/**
	 * Sets opacity of the circle. To apply this method it's necessary to call draw() after.
	 *
	 * @param border Border object, define border parameters of the circle.
	 */
	public void setBorder(Border border) {
		this.border = border;
		String javaScriptString = String.format(Locale.ENGLISH, "%s.setBorder(new Border(%d, '%s'));", objectInstance, border.width, String.format("#%06X", (0xFFFFFF & border.color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * Gets border of the circle.
	 */
	public Border getBorder() {
		return this.border;
	}


	/**
	 * Erase object and its instance from frontend server, but do not destroys object class instance in your app.
	 */
	public void erase() {
		super.erase();
		this.inMap = null;
		this.position = null;
		this.border = null;
		this.radius = 0;
		this.color = 0;
		this.opacity = 0;
	}

	public static class INCircleBuilder {

		private INCircle inCircle;

		public INCircleBuilder(INMap inMap) {
			inCircle = new INCircle(inMap);
		}

		public INCircle.INCircleBuilder setPosition(Point position) {
			inCircle.setPosition(position);
			return this;
		}

		public INCircle.INCircleBuilder setRadius(int radius) {
			inCircle.setRadius(radius);
			return this;
		}

		public INCircle.INCircleBuilder setColor(@ColorInt int color) {
			inCircle.setColor(color);
			return this;
		}

		public INCircle.INCircleBuilder setOpacity(@FloatRange(from = 0.0, to = 1.0) double opacity) {
			inCircle.setOpacity(opacity);
			return this;
		}

		public INCircle.INCircleBuilder setBorder(Border border) {
			inCircle.setBorder(border);
			return this;
		}

		public INCircle build() {
			try {
				CountDownLatch latch = new CountDownLatch(1);
				inCircle.ready(data -> latch.countDown());

				latch.await();

				if (!inCircle.isTimeout) {
					inCircle.draw();
					return inCircle;
				}
			} catch (Exception e) {
				Log.e("Create object exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}

