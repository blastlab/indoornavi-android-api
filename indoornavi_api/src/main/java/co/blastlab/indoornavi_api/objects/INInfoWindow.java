package co.blastlab.indoornavi_api.objects;

import android.util.Log;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import static co.blastlab.indoornavi_api.objects.INInfoWindow.Position.TOP;

/**
 * Class represents an info window, creates the INInfoWindow object in iframe that communicates with frontend server and adds info window to a given INObject child.
 */
public class INInfoWindow extends INObject {

	private INMap inMap;
	private String content = "";
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
		if (height >= 50) {
			this.height = height;
			String javaScriptString = String.format(Locale.US, "%s.setHeight(%d);", objectInstance, height);
			evaluate(javaScriptString, null);
		} else {
			String javaScriptString = String.format(Locale.US, "%s.setHeight(%d);", objectInstance, 50);
			evaluate(javaScriptString, null);
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Height must be greater then 50px");
		}
	}

	/**
	 * @return height of the info window
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Sets width dimension of infoWindow. Using this method is optional. Default dimension for info window width is 250px.
	 *
	 * @param width infoWindow width given in pixels, min available dimension is 50px.
	 */
	public void setWidth(int width) {
		if (width >= 50) {
			this.width = width;
			String javaScriptString = String.format(Locale.US, "%s.setWidth(%d);", objectInstance, width);
			evaluate(javaScriptString, null);
		} else {
			String javaScriptString = String.format(Locale.US, "%s.setWidth(%d);", objectInstance, 50);
			evaluate(javaScriptString, null);
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Width must be greater then 50px");
		}
	}

	/**
	 * @return width of the info window
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets info window content.
	 *
	 * @param content String contains text or html template. To reset info window content it is indispensable to call draw() method again.
	 */
	public void setContent(String content) {
		this.content = content;
		String javaScriptString = String.format("%s.setContent('%s');", objectInstance, content);
		evaluate(javaScriptString, null);
	}

	/**
	 * @return info window content as a text or html template.
	 */
	public String getContent() {
		return this.content;
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
	 * @return {@link Position} of the info window
	 */
	public Position getPositionAt() {
		return this.position;
	}

	/**
	 * Erase object and its instance from frontend server, but do not destroys object class instance in your app.
	 */
	public void erase() {
		super.erase();
		this.inMap = null;
		this.position = null;
		this.content = null;
		this.height = 0;
		this.width = 0;
	}

	public static class INInfoWindowBuilder {

		private INInfoWindow inInfoWindow;

		public INInfoWindowBuilder(INMap inMap) {
			inInfoWindow = new INInfoWindow(inMap);
		}

		public INInfoWindowBuilder setPositionAt(Position position) {
			inInfoWindow.setPositionAt(position);
			return this;
		}

		public INInfoWindowBuilder setContent(String content) {
			inInfoWindow.setContent(content);
			return this;
		}

		public INInfoWindowBuilder setHeight(int height) {
			inInfoWindow.setHeight(height);
			return this;
		}

		public INInfoWindowBuilder setWidth(int width) {
			inInfoWindow.setWidth(width);
			return this;
		}

		public INInfoWindow build() {
			try {
				CountDownLatch latch = new CountDownLatch(1);
				inInfoWindow.ready(data -> latch.countDown());

				latch.await();

				if (!inInfoWindow.isTimeout) {
					return inInfoWindow;
				}
			} catch (Exception e) {
				Log.e("Create object exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
			}
			return null;
		}
	}
}
