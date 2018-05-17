package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.webkit.ValueCallback;

import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.documentation.DocINObject;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.utils.CoordinatesUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class INObject is the root of the IndoorNavi objects hierarchy. Every IN object has INObject as a superclass (except INMap).
 */
public class INObject implements DocINObject {

	private INMap inMap;
	String objectInstance;

	/**
	 * INObject constructor.
	 *
	 * @param inMap instance INMap object.
	 */
	INObject(INMap inMap){
		this.inMap = inMap;
	}

	/**
	 * Method wait till object is create.
	 * Use of this method is indispensable to operate on the object.
	 *
	 * @param onObjectReadyCallback interface - trigger when object is successfully create.
	 */
	public void ready(OnObjectReadyCallback onObjectReadyCallback)
	{
		int promiseId = onObjectReadyCallback.hashCode();
		Controller.promiseCallbackMap.put(promiseId, onObjectReadyCallback);

		String javaScriptString = String.format(Locale.US, "%s.ready().then(() => inObjectInterface.ready(%d));", objectInstance, promiseId);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Return the id of the object.
	 *
	 * @param onReceiveValueCallback interface - invoked when object id is available.
	 */
	public void getID( final OnReceiveValueCallback<Long> onReceiveValueCallback)
	{
		String javaScriptString = String.format("%s.getID();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				onReceiveValueCallback.onReceiveValue(Long.parseLong(s.substring(0, s.length() - 2)));
			}
		});
	}

	/**
	 * Receives coordinates of the given object.
	 *
	 * @param onReceiveValueCallback interface - invoked when list of points is available.
	 */
	public void getPoints(final OnReceiveValueCallback<List<Point>> onReceiveValueCallback)
	{
		String javaScriptString = String.format("%s.getPoints();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				List<Point> points;
				points = PointsUtil.stringToPoints(s);
				onReceiveValueCallback.onReceiveValue(points);
			}
		});
	}

	/**
	 * Removes object and destroys it instance in the frontend server, but do not destroys object class instance in your app.
	 */
	public void remove()
	{
		String javaScriptString = String.format("%s.remove();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Checks if point of given coordinates is inside of the object.
	 *
	 * @param coordinates checking coordinates
	 * @param valueCallback interface - invoked when boolean value is available.
	 */
	public void isWithin(Coordinates coordinates, final ValueCallback<Boolean> valueCallback)
	{
		String javaScriptString = String.format("%s.isWithin(%s);", objectInstance, CoordinatesUtil.coordsToString(coordinates));
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				if(s != null) {
					valueCallback.onReceiveValue(Boolean.valueOf(s));
				}
			}
		});
	}
}
