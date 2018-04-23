package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.webkit.ValueCallback;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.List;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.INCoordinates;
import co.blastlab.indoornavi_api.utils.CoordinatesUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class INObject is the root of the indoorNavi objects hierarchy. Every object has INObject as a superclass (except INMap).
 *
 * @author Agata Ziółkowska <achmielewska@blastlab.co>
 */

class INObject {

	private INMap inMap;
	String objectInstance;

	/**
	 *
	 * @param inMap instance INMap object.

	 */
	INObject(INMap inMap){
		this.inMap = inMap;
	}

	/**
	 * Checks whether the given object has been created.
	 *
	 * @return Promise - will be resolve when injected object is created.
	 */
	Promise checkReady()
	{
		Deferred deferred = new DeferredObject();
		Promise promise = deferred.promise();

		int promiseId = promise.hashCode();
		Controller.promiseMap.put(promiseId, deferred);

		String javaScriptString = String.format("%s.ready().then(() => inObjectInterface.ready(%d));", objectInstance, promiseId);
		inMap.evaluateJavascript(javaScriptString, null);

		return promise;
	}

	/**
	 * Return the id of the object.
	 *
	 * @param onReceiveValueCallback {@link OnReceiveValueCallback}
	 */
	public void getID( final OnReceiveValueCallback onReceiveValueCallback)
	{
		String javaScriptString = String.format("%s.getID();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				onReceiveValueCallback.onReceiveValue(Integer.parseInt(s));
			}
		});
	}

	/**
	 * Receives coordinates of the given object.
	 *
	 * @param onReceiveValueCallback Callback interface {@link OnReceiveValueCallback}
	 */
	public void getPoints(final OnReceiveValueCallback onReceiveValueCallback)
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
	 *
	 */
	public void remove()
	{
		String javaScriptString = String.format("%s.remove();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Checks if point of given coordinates is inside of the object.
	 *
	 * @param inCoordinates checking coordinates
	 * @param valueCallback Callback interface
	 */
	public void isWithin(INCoordinates inCoordinates, final ValueCallback<Boolean> valueCallback)
	{
		String javaScriptString = String.format("%s.isWithin(%s);", objectInstance, CoordinatesUtil.coordsToString(inCoordinates));
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
