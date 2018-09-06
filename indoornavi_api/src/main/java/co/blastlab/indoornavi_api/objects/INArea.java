package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class represents an area, creates the INArea object in iframe that communicates with frontend server and draws area.
 */
public class INArea extends INObject {

	private INMap inMap;
	private List<Point> points;
	private @ColorInt int color;
	private @FloatRange(from=0.0, to=1.0)double opacity;

	/**
	 * INArea constructor
	 *
	 * @param inMap INMap object instance
	 */
	private INArea(INMap inMap) {
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
	public void draw()
	{
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates area at given coordinates. Coordinates needs to be given as real world dimensions that map is representing. Use of this method is indispensable.
	 *
	 * @param points List of {@link Point} To be able to draw area, at least 3 points must be provided.
	 */
	public void setPoints(List<Point> points)
	{
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
	public List<Point> getPoints()
	{
		return this.points;
	}

	/**
	 * Fills Area whit given color. To apply this method it's necessary to call draw() after.
	 *
     * @param color that specifies the color.
     */
	public void setColor(@ColorInt int color)
	{
		this.color = color;
		String javaScriptString = String.format("%s.setColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return  color value represent as an Integer.
	 */
	public @ColorInt int getColor()
	{
		return this.color;
	}

	/**
	 * Sets Area opacity. To apply this method it's necessary to call draw() after.
	 *
	 * @param opacity Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	public void setOpacity(@FloatRange(from=0.0, to=1.0)double opacity)
	{
		this.opacity = opacity;
		String javaScriptString = String.format("%s.setOpacity(%s);", objectInstance, String.format(Locale.US, "%f", opacity));
		evaluate(javaScriptString, null);
	}

	/**
	 * @return opacity of the area, represented as a Float value in range 0.0 - 1.0.
	 */
	public @FloatRange(from=0.0, to=1.0)double getOpacity()
	{
		return this.opacity;
	}

	public static class INAreaBuilder  {

		private INArea inArea;

		public INAreaBuilder(INMap inMap){
			inArea = new INArea(inMap);
		}

		public INAreaBuilder setPoints(List<Point> points)
		{
			inArea.setPoints(points);
			return this;
		}

		public INAreaBuilder setColor(@ColorInt int color)
		{
			inArea.setColor(color);
			return this;
		}

		public INAreaBuilder setOpacity(@FloatRange(from=0.0, to=1.0)double opacity)
		{
			inArea.setOpacity(opacity);
			return this;
		}

		public INArea build() {
			try{
				CountDownLatch latch = new CountDownLatch(1);
				inArea.ready(data -> latch.countDown());

				latch.await();

				if(!inArea.isTimeout) {
					inArea.draw();
					return inArea;
				}
			}
			catch (Exception e) {
				Log.e("Create object exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}
