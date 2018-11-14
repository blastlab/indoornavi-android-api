package co.blastlab.indoornavi_api.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnINObjectClickListener;
import co.blastlab.indoornavi_api.utils.MapUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class represents a marker, creates the INMarker object in iframe that communicates with frontend server and places a marker on the map.
 */
public class INMarker extends INObject {

	private INMap inMap;
	private int callbackId;
	private Point point;
	private String label = "";
	private String icon = "";
	private int iconDrawable = -1;

	/**
	 * INMArker constructor.
	 *
	 * @param inMap INMap object instance
	 */
	private INMarker(INMap inMap) {
		super(inMap);
		this.objectInstance = String.format(Locale.US, "marker%d", this.hashCode());
		this.inMap = inMap;

		String javaScriptString = String.format("var %s = new INMarker(navi);", this.objectInstance);
		evaluate(javaScriptString, null);
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
	 * Place marker on the map with all given settings. There is necessity to use point() method before draw() method to indicate where marker should be located.
	 * Using this method is indispensable to draw marker with set configuration on the map.
	 */
	public void draw() {
		String javaScriptString = String.format("%s.draw();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Locates marker at given coordinates. Using this method is indispensable.
	 *
	 * @param point {@link Point} Position will be clipped to the point in the bottom center of marker icon.
	 */
	public void setPosition(Point point) {
		if (point != null) {
			this.point = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), point);
			String javaScriptString = String.format("%s.setPosition(%s);", objectInstance, PointsUtil.pointToString(point));
			evaluate(javaScriptString, null);
		} else {
			Log.e("NullPointerException ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Point must be provided!");
		}
	}

	/**
	 * @return position of the marker as a {@link Point} object.
	 */
	public Point getPosition() {
		return this.point;
	}

	/**
	 * Sets marker label.
	 *
	 * @param label string that will be used as a marker label. If label method isn't used then no label is going to be displayed.
	 *              To reset label to a new string call this method again passing new label as a string and call draw() method again.
	 */
	public void setLabel(String label) {
		this.label = label;
		String javaScriptString = String.format("%s.setLabel('%s');", objectInstance, label);
		evaluate(javaScriptString, null);
	}

	/**
	 * @return label placed on the marker.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Remove marker label. To remove label it is indispensable to call draw() method again.
	 */
	public void removeLabel() {
		String javaScriptString = String.format("%s.removeLabel();", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Displays {@link INInfoWindow} object on marker.
	 *
	 * @param inInfoWindow - info window object.
	 */
	public void addInfoWindow(INInfoWindow inInfoWindow) {
		if (inInfoWindow != null) {
			String javaScriptString = String.format(Locale.US, "%s.open(%s);", inInfoWindow.objectInstance, objectInstance);
			evaluate(javaScriptString, null);
		} else {
			Log.e("Null pointer Exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): InfoWindow not created");
		}
	}

	/**
	 * Set marker icon. To apply this method it's necessary to call draw() after.
	 *
	 * @param icon String url path to your icon;
	 */
	public void setIcon(String icon) {
		this.icon = icon;
		String javaScriptString = String.format("%s.setIconUrl(`%s`);", objectInstance, icon);
		evaluate(javaScriptString, null);
	}

	/**
	 * Set marker icon. To apply this method it's necessary to call draw() after.
	 *
	 * @param iconDrawable String url path to your icon;
	 */
	public void setIcon(int iconDrawable) {
		this.iconDrawable = iconDrawable;

		Bitmap bmp = BitmapFactory.decodeResource(inMap.getResources(), iconDrawable);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(getIconFormat(inMap.getResources().getString(iconDrawable)), 100, stream);
		byte[] byteArray = stream.toByteArray();
		String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

		String javaScriptString = String.format("%s.setIconBase64(`%s`);", objectInstance, imageString);
		evaluate(javaScriptString, null);
	}

	private Bitmap.CompressFormat getIconFormat(String imageName) {
		switch (getFileExtension(imageName)) {
			case ".jpeg":
				return Bitmap.CompressFormat.JPEG;
			case ".png":
				return Bitmap.CompressFormat.PNG;
			case ".webp":
				return Bitmap.CompressFormat.WEBP;
			default:
				return Bitmap.CompressFormat.JPEG;
		}
	}


	private String getFileExtension(String fileName) {
		int lastIndexOf = fileName.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return fileName.substring(lastIndexOf);
	}


	/**
	 * @return icon set as a marker.
	 */
	public String getIcon() {
		return this.icon;
	}

	/**
	 * Erase object and its instance from frontend server, but do not destroys object class instance in your app.
	 */
	public void erase() {
		super.erase();
		this.inMap = null;
		this.point = null;
		this.label = null;
		this.icon = null;
		this.callbackId = 0;
	}

	public static class INMarkerBuilder {

		private INMarker inMarker;

		public INMarkerBuilder(INMap inMap) {
			this.inMarker = new INMarker(inMap);
		}

		public INMarkerBuilder setPosition(Point point) {
			inMarker.setPosition(point);
			return this;
		}

		public INMarkerBuilder setLabel(String label) {
			inMarker.setLabel(label);
			return this;
		}

		public INMarkerBuilder setIcon(String icon) {
			inMarker.setIcon(icon);
			return this;
		}

		public INMarkerBuilder setIcon(int icon) {
			inMarker.setIcon(icon);
			return this;
		}

		public INMarker build() {
			try {
				CountDownLatch latch = new CountDownLatch(1);
				inMarker.ready(data -> latch.countDown());

				latch.await();

				if (!inMarker.isTimeout) {
					inMarker.draw();
					return inMarker;
				}
			} catch (Exception e) {
				Log.e("Create object exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}
