package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.documentation.DocINMarker;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class represents a marker, creates the INMarker object in iframe that communicates with frontend server and places a marker on the map.
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
		evaluate(javaScriptString, null);
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
		evaluate(javaScriptString, null);

		draw();
	}

	/**
	 * Removes listener if exists.
	 */
	public void removeEventListener() {

		Controller.markerClickListenerMap.remove(callbackId);
		String javaScriptString = String.format("%s.removeEventListener(Event.MOUSE.CLICK)", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Place marker on the map with all given settings. There is necessity to use point() method before draw() method to indicate where marker should be located.
	 * Using this method is indispensable to draw marker with set configuration on the map.
	 */
	public void draw()
	{
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates marker at given coordinates. Using this method is indispensable.
	 *
	 * @param point {@link Point} Position will be clipped to the point in the bottom center of marker icon.
	 */
	public void point(Point point)
	{
		if(point != null) {
			String javaScriptString = String.format("%s.point(%s);", objectInstance, PointsUtil.pointToString(point));
			evaluate(javaScriptString, null);
		} else {
			Log.e("NullPointerException ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Point must be provided!");
		}
	}

	/**
	 * Sets marker label.
	 *
	 * @param label string that will be used as a marker label. If label method isn't used then no label is going to be displayed.
	 * To reset label to a new string call this method again passing new label as a string and call draw() method again.
	 */
	public void setLabel(String label)
	{
		String javaScriptString = String.format("%s.setLabel('%s');", objectInstance, label);
		evaluate(javaScriptString, null);
	}

	/**
	 * Remove marker label. To remove label it is indispensable to call draw() method again.
	 */
	public void removeLabel()
	{
		String javaScriptString = String.format("%s.removeLabel();", objectInstance);
		evaluate(javaScriptString, null);
	}

	public void addInfoWindow(INInfoWindow inInfoWindow) {
		if(inInfoWindow != null) {
			String javaScriptString = String.format(Locale.US, "%s.open(%s);", inInfoWindow.objectInstance, objectInstance);
			evaluate(javaScriptString, null);
		}
		else {
			Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): InfoWindow not created");
		}
	}

	/**
	 * Set marker icon. To apply this method it's necessary to call draw() after.
	 *
	 * @param path String url path to your icon;
	 */
	public void setIcon(String path)
	{
		String javaScriptString = String.format("%s.setIcon('%s');", objectInstance, path);
		evaluate(javaScriptString, null);
	}

	public static class INMarkerBuilder  {

		private Point point;
		private INMap inMap;
		private String label = "",  icon = "";

		public INMarkerBuilder(INMap inMap){
			this.inMap = inMap;
		}

		public INMarkerBuilder point(Point point)
		{
			this.point = point;
			return this;
		}

		public INMarkerBuilder setLabel(String label)
		{
			this.label = label;
			return this;
		}

		public INMarkerBuilder setIcon(String icon)
		{
			this.icon = icon;
			return this;
		}

		public INMarker build() {
			final CountDownLatch latch = new CountDownLatch(1);
			INMarker inMarker = new INMarker(inMap);
			inMarker.ready(object -> latch.countDown());

			try{
				latch.await();

				inMarker.point(this.point);
				inMarker.setLabel(this.label);
				inMarker.setIcon(this.icon);
				inMarker.draw();
				return inMarker;
			}
			catch (Exception e) {
				Log.e("Create object exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;

		}
	}
}
