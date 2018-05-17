package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;

import java.util.Locale;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.documentation.DocINMarker;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class representing a marker, creates the INMarker object in iframe that communicates with frontend server and places a marker on the map.
 */
public class INMarker extends INObject implements DocINMarker {

	private INMap inMap;
	private int callbackId;

	/**
	 * INMArker constructor.
	 *
	 * @param inMap INMap object instance
	 */
	public INMarker(INMap inMap) {
		super(inMap);
		this.objectInstance = String.format(Locale.US, "marker%d",this.hashCode());
		this.inMap = inMap;

		String javaScriptString = String.format("var %s = new INMarker(navi);", this.objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Register a callback to be invoked when marker is clicked.
	 *
	 * @param onMarkerClickListener interface - invoked when event occurs.
	 */
	public void addEventListener(OnMarkerClickListener onMarkerClickListener) {

		callbackId = onMarkerClickListener.hashCode();
		Controller.markerClickListenerMap.put(callbackId, onMarkerClickListener);

		String javaScriptString = String.format(Locale.US, "%s.addEventListener(Event.MOUSE.CLICK, () => inMarkerInterface.onClick(%d))", objectInstance, callbackId);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Removes listener if exists.
	 */
	public void removeEventListener() {

		Controller.markerClickListenerMap.remove(callbackId);
		String javaScriptString = String.format("%s.removeEventListener(Event.MOUSE.CLICK)", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Place marker on the map with all given settings. There is necessary to use point() method before draw() method to indicate where marker should to be located.
	 * Use of this method is indispensable to draw marker with set configuration on the map.
	 */
	public void draw()
	{
		String javaScriptString = String.format("%s.draw();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Locates marker at given coordinates. Use of this method is indispensable.
	 *
	 * @param point {@link Point} Position will be clipped to the point in the bottom center of marker icon.
	 */
	public void point(Point point)
	{
		String javaScriptString = String.format("%s.point(%s);", objectInstance, PointsUtil.pointToString(point));
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Sets marker label.
	 *
	 * @param label string that will be used as a marker label. If label method isn't used than no label is going to be displayed.
	 * To reset label to a new string call this method again passing new label as a string and call draw() method again.
	 */
	public void setLabel(String label)
	{
		String javaScriptString = String.format("%s.setLabel('%s');", objectInstance, label);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Remove marker label. To remove label it is indispensable to call draw() method again.
	 */
	public void removeLabel()
	{
		String javaScriptString = String.format("%s.removeLabel();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Set marker icon. To apply this method it's necessary to call draw() after.
	 *
	 * @param path String url path to your icon;
	 */
	public void setIcon(String path)
	{
		String javaScriptString = String.format("%s.setIcon('%s');", objectInstance, path);
		inMap.evaluateJavascript(javaScriptString, null);
	}
}
