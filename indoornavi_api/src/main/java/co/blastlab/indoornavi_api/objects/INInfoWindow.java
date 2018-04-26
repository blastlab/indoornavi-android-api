package co.blastlab.indoornavi_api.objects;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Class representing a info window, creates the INInfoWindow object in iframe that communicates with frontend server and adds info window to a given INObject child.
 */
public class INInfoWindow extends INObject{

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
	public INInfoWindow(INMap inMap) {
		super(inMap);
		this.inMap = inMap;
		this.objectInstance = String.format(Locale.US, "infoWindow%d", this.hashCode());

		String javaScriptString = String.format("var %s = new INInfoWindow(navi);", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Sets height dimension of info window. Use of this method is optional. Default dimensions for info window height is 250px.
	 *
	 * @param height - info window height given in pixels, min available dimension is 50px.
	 */
	public void height(int height) {
		String javaScriptString = String.format(Locale.US, "%s.height(%d);", objectInstance, height);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Sets width dimension of infoWindow. Use of this method is optional. Default dimensions for info window width is 250px.
	 *
	 * @param width - infoWindow width given in pixels, min available dimension is 50px.
	 */
	public void width(int width) {
		String javaScriptString = String.format(Locale.US, "%s.width(%d);", objectInstance, width);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Displays info window on {@link INObject} object.
	 *
	 * @param inObject - the object on which you want to display info window.
	 */
	public void open(INObject inObject) {
		String javaScriptString = String.format(Locale.US, "%s.open(%s);", objectInstance, inObject.objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Sets info window content.
	 *
	 * @param html - String containing text or html template. To reset info window content it is indispensable to call draw() method again.
	 */
	public void setInnerHTML(String html)
	{
		String javaScriptString = String.format("%s.setInnerHTML('%s');", objectInstance, html);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Sets info window position relative to the object. Use of this method is optional. Default position for info window is TOP.
	 *
	 * @param position - {@link Position}
	 */
	public void setPosition(@Position int position) {
		String javaScriptString = String.format(Locale.US, "%s.setPosition(%d);", objectInstance, position);
		inMap.evaluateJavascript(javaScriptString, null);
	}
}
