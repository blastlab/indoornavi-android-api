package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class represents an area, creates the INArea object in iframe that communicates with frontend server and draws area.
 */
public class INArea extends INObject {

	private INMap inMap;

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
	public void points(List<Point> points)
	{
		if (points != null && points.size() > 2) {
			String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
			evaluate(javaScriptPoints, null);
			String javaScriptString = String.format("%s.points(points);", objectInstance);
			evaluate(javaScriptString, null);
		} else {
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): At least 3 points must be provided!");
		}
	}

	/**
	 * Fills Area whit given color. To apply this method it's necessary to call draw() after.
	 *
     * @param color that specifies the color.
     */
	public void setFillColor(@ColorInt int color)
	{
		String javaScriptString = String.format("%s.setFillColor('%s');", objectInstance, String.format("#%06X", (0xFFFFFF & color)));
		evaluate(javaScriptString, null);
	}

	/**
	 * Sets Area opacity. To apply this method it's necessary to call draw() after.
	 *
	 * @param opacity Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	public void setOpacity(@FloatRange(from=0.0, to=1.0)double opacity)
	{
		String javaScriptString = String.format("%s.setOpacity(%s);", objectInstance, String.format(Locale.US, "%f", opacity));
		evaluate(javaScriptString, null);
	}

	public static class INAreaBuilder  {

		private List<Point> points;
		private INMap inMap;
		private @ColorInt int color;
		private @FloatRange(from=0.0, to=1.0)double opacity;

		public INAreaBuilder(INMap inMap){
			this.inMap = inMap;
		}

		public INAreaBuilder points(List<Point> points)
		{
			this.points = points;
			return this;
		}

		public INAreaBuilder setFillColor(@ColorInt int color)
		{
			this.color = color;
			return this;
		}

		public INAreaBuilder setOpacity(@FloatRange(from=0.0, to=1.0)double opacity)
		{
			this.opacity = opacity;
			return this;
		}

		public INArea build() {
			try{
				CountDownLatch latch = new CountDownLatch(1);

				INArea inArea = new INArea(inMap);
				inArea.ready(data -> latch.countDown());

				latch.await();

				if(!inArea.isTimeout) {
					inArea.points(this.points);
					inArea.setFillColor(this.color);
					inArea.setOpacity(this.opacity);
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
