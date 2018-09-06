package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.utils.PointsUtil;

import static co.blastlab.indoornavi_api.objects.INInfoWindow.Position.TOP;

/**
 * Class represents an info window, creates the INInfoWindow object in iframe that communicates with frontend server and adds info window to a given INObject child.
 */
public class INInfoWindow extends INObject {

	private INMap inMap;
	private String html = "";
	private int height = 250, width = 250;
	private Position position = TOP;

	public enum Position {TOP, RIGHT, BOTTOM, LEFT, TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT}

	/**
	 * INInfoWindow constructor
	 *
	 * @param inMap INMap object instance
	 */
	private INInfoWindow(INMap inMap) {
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format(Locale.US, "infoWindow%d", this.hashCode());

		String javaScriptString = String.format("var %s = new INInfoWindow(navi);", objectInstance);
		evaluate(javaScriptString, null);
	}

	/**
	 * Sets height dimension of info window. Using of this method is optional. Default dimensions for info window height is 250px.
	 *
	 * @param height info window height given in pixels, min available dimension is 50px.
	 */
	public void setHeight(int height) {
		if(height >= 50) {
			this.height = height;
			String javaScriptString = String.format(Locale.US, "%s.setHeight(%d);", objectInstance, height);
			evaluate(javaScriptString, null);
		}
		else {
			String javaScriptString = String.format(Locale.US, "%s.setHeight(%d);", objectInstance, 50);
			evaluate(javaScriptString, null);
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Height must be greater then 50px");
		}
	}

	/**
	 * Receives height of the info window
	 *
	 * @param onReceiveValueCallback interface - invoked when info window height value is available. Return Integer value.
	 */
	public void getHeight(final OnReceiveValueCallback<Integer> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getHeight();", objectInstance);
		evaluate(javaScriptString, stringHeight -> {
			if(!stringHeight.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Integer.parseInt(stringHeight));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Sets width dimension of infoWindow. Using this method is optional. Default dimension for info window width is 250px.
	 *
	 * @param width infoWindow width given in pixels, min available dimension is 50px.
	 */
	public void setWidth(int width) {
		if(width >= 50) {
			this.width = width;
			String javaScriptString = String.format(Locale.US, "%s.setWidth(%d);", objectInstance, width);
			evaluate(javaScriptString, null);
		}
		else {
			String javaScriptString = String.format(Locale.US, "%s.setWidth(%d);", objectInstance, 50);
			evaluate(javaScriptString, null);
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Width must be greater then 50px");
		}
	}

	/**
	 * Receives width of the info window
	 *
	 * @param onReceiveValueCallback interface - invoked when info window width value is available. Return Integer value.
	 */
	public void getWidth( final OnReceiveValueCallback<Integer> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getWidth();", objectInstance);
		evaluate(javaScriptString, stringWidth -> {
			if(!stringWidth.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Integer.parseInt(stringWidth));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Sets info window content.
	 *
	 * @param html String contains text or html template. To reset info window content it is indispensable to call draw() method again.
	 */
	public void setContent(String html)
	{
		this.html = html;
		String javaScriptString = String.format("%s.setContent('%s');", objectInstance, html);
		evaluate(javaScriptString, null);
	}

	/**
	 * Receives info window content.
	 *
	 * @param onReceiveValueCallback interface - invoked when info window content value is available. Return String containing text or html template.
	 */
	public void getContent(final OnReceiveValueCallback<String> onReceiveValueCallback)
	{
		String javaScriptString = String.format("%s.getContent();", objectInstance);
		evaluate(javaScriptString, stringContent -> {
			if(!stringContent.equals("null")) {
				onReceiveValueCallback.onReceiveValue(stringContent);
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): points not set yet! ");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Sets info window position relative to the object. Using this method is optional. Default position for info window is TOP.
	 *
	 * @param position {@link Position}
	 */
	public void setPositionAt(Position position) {
		this.position = position;
		String javaScriptString = String.format(Locale.US, "%s.setPositionAt(%d);", objectInstance, position.ordinal());
		evaluate(javaScriptString, null);
	}

	/**
	 * Receives position of the info window
	 *
	 * @param onReceiveValueCallback interface - invoked when info window position value is available. Return {@link Position} enum value.
	 */
	public void getPositionAt( final OnReceiveValueCallback<Position> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getPositionAt();", objectInstance);
		evaluate(javaScriptString, stringPosition -> {
			if(!stringPosition.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Position.values()[Integer.parseInt(stringPosition)]);
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	public static class INInfoWindowBuilder  {

		private INMap inMap;
		private INInfoWindow inInfoWindow;

		public INInfoWindowBuilder(INMap inMap){
			inInfoWindow = new INInfoWindow(inMap);
		}

		public INInfoWindowBuilder setPositionAt(Position position)
		{
			inInfoWindow.setPositionAt(position);
			return this;
		}

		public INInfoWindowBuilder setContent(String html)
		{
			inInfoWindow.setContent(html);
			return this;
		}

		public INInfoWindowBuilder setHeight(int height)
		{
			inInfoWindow.setHeight(height);
			return this;
		}

		public INInfoWindowBuilder setWidth(int width)
		{
			inInfoWindow.setWidth(width);
			return this;
		}

		public INInfoWindow build() {
			try{
				CountDownLatch latch = new CountDownLatch(1);
				inInfoWindow.ready(data -> latch.countDown());

				latch.await();

				if(!inInfoWindow.isTimeout) {
					return inInfoWindow;
				}
			}
			catch (Exception e) {
				Log.e("Create object exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}
