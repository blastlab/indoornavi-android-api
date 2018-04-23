package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.AndroidExecutionScope;

import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class representing an INArea, creates the area object in iframe that communicates with indoornavi frontend server and draws area.
 */
public class INArea extends INObject {

	private INMap inMap;

	/**
	 * INArea constructor
	 *
	 * @param inMap INMap object instance
	 */
	public INArea(INMap inMap)
	{
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format("area%s",this.hashCode());
		String javaScriptString = String.format("var %s = new INArea(navi);", this.objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Call inherit method from {@link INObject}.
	 * Method wait till area object is create.
	 * Use of this method is indispensable to operate on area object.
	 *
	 * @param doneCallback DoneCallback interface - trigger when area is create (Promise is resolved).
	 */
	public void ready(DoneCallback<String> doneCallback)
	{
		AndroidDeferredManager dm = new AndroidDeferredManager();
		dm.when(checkReady(), AndroidExecutionScope.UI).done(doneCallback);
	}

	/**
	 * Place area on the map with all given settings. There is necessary to use points() method before place() method to indicate where area should to be located.
	 * Use of this method is indispensable to draw area with set configuration in the IndoorNavi Map.
	 */
	public void draw()
	{
		String javaScriptString = String.format("%s.draw();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Locates area at given coordinates. Coordinates needs to be given as real world dimensions that map is representing. Use of this method is indispensable.
	 *
	 * @param points - List of {@link Point} To be able to draw area, at least 3 points must be provided.
	 */
	public void points(List<Point> points)
	{
		String javaScriptPoints = String.format("var points = %s;", PointsUtil.pointsToString(points));
		inMap.evaluateJavascript(javaScriptPoints, null);
		String javaScriptString = String.format("%s.points(points);", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/** Fills Area whit given color.
	 *
     * @param color - string that specifies the color. Supports color in hex format '#AABBCC' and rgb format 'rgb(255,255,255)';
     */
	public void setFillColor(String color)
	{
		String javaScriptString = String.format("%s.setFillColor('%s');", objectInstance, color);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Sets Area opacity.
	 *
	 * @param opacity  - Float between 1.0 and 0. Set it to 1.0 for no opacity, 0 for maximum opacity.
	 */
	public void setOpacity(float opacity)
	{
		String javaScriptString = String.format("%s.setOpacity(%s);", objectInstance, String.format(Locale.US, "%f", opacity));
		inMap.evaluateJavascript(javaScriptString, null);
	}
}
