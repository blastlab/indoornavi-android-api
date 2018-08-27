package co.blastlab.indoornavi_api.objects;

import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Class represents an info window, creates the INInfoWindow object in iframe that communicates with frontend server and adds info window to a given INObject child.
 */
public class INInfoWindow extends INObject {

	private INMap inMap;
	public static final int TOP = 0;
	public static final int RIGHT = 1;
	public static final int BOTTOM = 2;
	public static final int LEFT = 3;
	public static final int TOP_RIGHT = 4;
	public static final int TOP_LEFT = 5;
	public static final int BOTTOM_RIGHT = 6;
	public static final int BOTTOM_LEFT = 7;

	@IntDef({TOP, RIGHT, BOTTOM, LEFT, TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT})

	@Retention(RetentionPolicy.SOURCE)
	public @interface Position {}

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
	public void height(int height) {
		if(height >= 50) {
			String javaScriptString = String.format(Locale.US, "%s.height(%d);", objectInstance, height);
			evaluate(javaScriptString, null);
		}
		else {
			String javaScriptString = String.format(Locale.US, "%s.height(%d);", objectInstance, 50);
			evaluate(javaScriptString, null);
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Height must be greater then 50px");
		}
	}

	/**
	 * Sets width dimension of infoWindow. Using this method is optional. Default dimension for info window width is 250px.
	 *
	 * @param width infoWindow width given in pixels, min available dimension is 50px.
	 */
	public void width(int width) {
		if(width >= 50) {
			String javaScriptString = String.format(Locale.US, "%s.width(%d);", objectInstance, width);
			evaluate(javaScriptString, null);
		}
		else {
			String javaScriptString = String.format(Locale.US, "%s.width(%d);", objectInstance, 50);
			evaluate(javaScriptString, null);
			Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Width must be greater then 50px");
		}
	}

	/**
	 * Sets info window content.
	 *
	 * @param html String contains text or html template. To reset info window content it is indispensable to call draw() method again.
	 */
	public void setInnerHTML(String html)
	{
		String javaScriptString = String.format("%s.setInnerHTML('%s');", objectInstance, html);
		evaluate(javaScriptString, null);
	}

	/**
	 * Sets info window position relative to the object. Using this method is optional. Default position for info window is TOP.
	 *
	 * @param position {@link Position}
	 */
	public void setPosition(@Position int position) {
		String javaScriptString = String.format(Locale.US, "%s.setPosition(%d);", objectInstance, position);
		evaluate(javaScriptString, null);
	}

	public static class INInfoWindowBuilder  {

		private INMap inMap;
		private String html = "";
		private int height = 250, width = 250;
		private @Position int position = TOP;

		public INInfoWindowBuilder(INMap inMap){
			this.inMap = inMap;
		}

		public INInfoWindowBuilder setPosition(@Position int position)
		{
			this.position = position;
			return this;
		}

		public INInfoWindowBuilder setInnerHTML(String html)
		{
			this.html = html;
			return this;
		}

		public INInfoWindowBuilder height(int height)
		{
			this.height = height;
			return this;
		}

		public INInfoWindowBuilder width(int width)
		{
			this.width = width;
			return this;
		}

		public INInfoWindow build() {
			try{
				CountDownLatch latch = new CountDownLatch(1);

				INInfoWindow inInfoWindow = new INInfoWindow(inMap);
				inInfoWindow.ready(data -> latch.countDown());

				latch.await();

				if(!inInfoWindow.isTimeout) {
					inInfoWindow.setInnerHTML(html);
					inInfoWindow.setPosition(position);
					inInfoWindow.height(height);
					inInfoWindow.width(width);
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
