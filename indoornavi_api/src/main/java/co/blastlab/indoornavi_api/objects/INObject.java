package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.webkit.ValueCallback;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.List;

import co.blastlab.indoornavi_api.Constants;
import co.blastlab.indoornavi_api.callback.GetIdCallback;
import co.blastlab.indoornavi_api.callback.GetPointsCallback;
import co.blastlab.indoornavi_api.model.INCoordinates;
import co.blastlab.indoornavi_api.utils.INCoordinatesUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class INObject is the root of the indoorNavi objects hierarchy. Every object has INObject as a superclass (except INMap).
 *
 * @author Agata Ziółkowska <achmielewska@blastlab.co>
 */

class INObject {

	INMap inMap;
	String objectInstance;

	/**
	 *
	 * @param inMap instance od INMap object.

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
		Constants.promiseMap.put(promiseId, deferred);

		String javaScriptString = String.format("%s.ready().then(() => inObjectInterface.ready(%d));", objectInstance, promiseId);
		inMap.evaluateJavascript(javaScriptString, null);

		return promise;
	}

	/**
	 * Return the id of the object.
	 *
	 * @param getIdCallback {@link GetIdCallback}
	 */
	public void getID( final GetIdCallback getIdCallback)
	{
		String javaScriptString = String.format("%s.getID();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				getIdCallback.onReceiveId(Integer.parseInt(s));
			}
		});
	}

	/**
	 * Receives coordinates of the given object.
	 *
	 * @param getPointsCallback Callback interface {@link GetPointsCallback}
	 */
	public void getPoints(final GetPointsCallback getPointsCallback)
	{
		String javaScriptString = String.format("%s.getPoints();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				List<Point> points;
				points = PointsUtil.stringToPoints(s);
				getPointsCallback.onReceivePoints(points);
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
		String javaScriptString = String.format("%s.isWithin(%s);", objectInstance, INCoordinatesUtil.coordsToString(inCoordinates));
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
