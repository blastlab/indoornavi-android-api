package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.util.Log;
import android.webkit.ValueCallback;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnINObjectClickListener;
import co.blastlab.indoornavi_api.model.Border;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.utils.CoordinatesUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class represents an area, creates the INArea object in iframe that communicates with frontend server and draws area.
 */
public class INArea extends INObject {

	private INMap inMap;
	private List<Point> points;
	private @ColorInt
	int color;
	private @FloatRange(from = 0.0, to = 1.0)
	double opacity;
	private String name;
	private int callbackId;
	private int databaseId = -1;
	private Border border;

	/**
	 * INArea constructor
	 *
	 * @param inMap INMap object instance
	 */
	protected INArea(INMap inMap) {
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format("area%s", this.hashCode());
		String javaScriptString = String.format("var %s = new INArea(navi);", this.objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Place area on the map with all given settings. There is necessity to use points() method before draw() method to indicate where area should to be located.
	 * Using of this method is indispensable to draw area with set configuration in the IndoorNavi Map.
	 */
	public void draw() {
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates area at given coordinates. Coordinates needs to be given as real world dimensions that map is representing. Use of this method is indispensable.
	 *
	 * @param points List of {@link Point} To be able to draw area, at least 3 points must be provided.
	 */
	public void setPoints(List<Point> points) {
		if (points != null && points.size() > 2) {
			this.points = points;
			String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
			evaluate(javaScriptPoints, null);
			String javaScriptString = String.format("%s.setPoints(points);", objectInstance);
			evaluate(javaScriptString, null);
		} else {
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): At least 3 points must be provided!");
		}
	}

	/**
	 * @return list of {@link Point} object.
	 */
	public List<Point> getPoints() {
		return this.points;
	}

	/**
	 * Fills Area whit given color. To apply this method it's necessary to call draw() after.
	 *
	 * @param color that specifies the color.
	 */
	public void setColor(@ColorInt int color) {
		this.color = color;
		String javaScriptString = String.format("%s.setColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return color value represent as an Integer.
	 */
	public @ColorInt
	int getColor() {
		return this.color;
	}

	/**
	 * Sets Area opacity. To apply this method it's necessary to call draw() after.
	 *
	 * @param opacity Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	public void setOpacity(@FloatRange(from = 0.0, to = 1.0) double opacity) {
		this.opacity = opacity;
		String javaScriptString = String.format("%s.setOpacity(%s);", objectInstance, String.format(Locale.US, "%f", opacity));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return opacity of the area, represented as a Float value in range 0.0 - 1.0.
	 */
	public @FloatRange(from = 0.0, to = 1.0)
	double getOpacity() {
		return this.opacity;
	}

	/**
	 * Sets border of the area. To apply this method it's necessary to call draw() after.
	 *
	 * @param border Border object, define border parameters of the circle.
	 */
	public void setBorder(Border border) {
		this.border = border;
		String javaScriptString = String.format(Locale.ENGLISH, "%s.setBorder(new Border(%d, '%s'));", objectInstance, border.width, String.format("#%06X", (0xFFFFFF & border.color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return Gets border of the area.
	 */
	public Border getBorder() {
		return this.border;
	}


	/**
	 * Sets name of the area
	 *
	 * @param name name of area
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Gets name of the area.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets database id of the area (it is not the id of the object, it identifies the object in the database)
	 * @param id set id
	 */
	public void setDatabaseId(int id) {
		this.databaseId = id;
	}

	/**
	 * @return area database id
	 */
	public int getDatabaseId() {
		return this.databaseId;
	}

	/**
	 * Checks if point of given coordinates is inside of the area.
	 *
	 * @param coordinates   checking coordinates
	 * @param valueCallback interface - invoked when boolean value is available.
	 */
	public void isWithin(Coordinates coordinates, final ValueCallback<Boolean> valueCallback) {
		String javaScriptString = String.format("%s.isWithin(%s);", objectInstance, CoordinatesUtil.coordsToString(coordinates));
		evaluate(javaScriptString, stringIsWithin -> {
			if (!stringIsWithin.equals("null")) {
				valueCallback.onReceiveValue(Boolean.valueOf(stringIsWithin));
			} else {
				Log.e("Null pointer Exception", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): The value can't be determined! ");
				valueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Register a callback to be invoked when marker is clicked.
	 *
	 * @param onINObjectClickListener interface - invoked when event occurs.
	 */
	public void addEventListener(OnINObjectClickListener onINObjectClickListener) {

		callbackId = onINObjectClickListener.hashCode();
		Controller.inObjectClickListenerMap.put(callbackId, onINObjectClickListener);

		String javaScriptString = String.format(Locale.US, "%s.addEventListener(Event.MOUSE.CLICK, () => inObjectEventInterface.onClick(%d))", objectInstance, callbackId);
		evaluate(javaScriptString, null);

		draw();
	}

	/**
	 * Removes listener if exists.
	 */
	public void removeEventListener() {

		Controller.inObjectClickListenerMap.remove(callbackId);
		String javaScriptString = String.format("%s.removeEventListener(Event.MOUSE.CLICK)", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Calculates center of given area.
	 *
	 * @return center {@link Point}
	 */
	public Point getCenterPoint() {
		if (this.points == null) return null;

		int avgX = 0;
		int avgY = 0;

		for (Point point : this.points) {
			avgX += point.x;
			avgY += point.y;
		}
		return new Point(Math.round(avgX / this.points.size()), Math.round(avgY / this.points.size()));
	}

	public static INArea createDefault(INMap inMap) {
		return new INArea(inMap);
	}

	/**
	 * Erase object and its instance from frontend server, but do not destroys object class instance in your app.
	 */
	public void erase() {
		super.erase();
		this.inMap = null;
		this.points = null;
		this.color = 0;
		this.opacity = 0;
	}


	public static class INAreaBuilder {

		private INArea inArea;

		public INAreaBuilder(INMap inMap) {
			inArea = new INArea(inMap);
		}

		public INAreaBuilder setPoints(List<Point> points) {
			inArea.setPoints(points);
			return this;
		}

		public INAreaBuilder setColor(@ColorInt int color) {
			inArea.setColor(color);
			return this;
		}

		public INAreaBuilder setOpacity(@FloatRange(from = 0.0, to = 1.0) double opacity) {
			inArea.setOpacity(opacity);
			return this;
		}

		public INAreaBuilder setName(String name) {
			inArea.setName(name);
			return this;
		}

		public INAreaBuilder setBorder(Border border) {
			inArea.setBorder(border);
			return this;
		}

		public INArea build() throws Exception {
			CountDownLatch latch = new CountDownLatch(1);
			inArea.ready(data -> latch.countDown());

			latch.await();

			if (!inArea.isTimeout) {
				inArea.draw();
				return inArea;
			}
			return null;
		}
	}
}
