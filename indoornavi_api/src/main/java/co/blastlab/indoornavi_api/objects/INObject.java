package co.blastlab.indoornavi_api.objects;

import android.util.Log;
import android.webkit.ValueCallback;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import co.blastlab.indoornavi_api.Constants;
import co.blastlab.indoornavi_api.callback.GetIdCallback;

/**
 * Class INObject is the root of the indoorNavi objects hierarchy. Every object has INObject as a superclass (except INMap).
 *
 * @author Agata Ziółkowska <achmielewska@blastlab.co>
 */

public class INObject {

	public static final String INPOLYLINE_OBJECT = "poly";
	public static final String INAREA_OBJECT = "area";
	public static final String INMARKER_OBJECT = "marker";
	public static final String ININFOWINDOW_OBJECT = "infoWindow";

	/**
	 * Checks whether the given object has been created.
	 *
	 * @param inMap instance od INMap object.
	 * @param object  String contains javaScript object representation.
	 * @return Promise - will be resolve when injected object is created.
	 */
	public Promise checkReady(INMap inMap, String object)
	{

		Deferred deferred = new DeferredObject();
		Promise promise = deferred.promise();

		int promiseId = promise.hashCode();
		Constants.promiseMap.put(promiseId, deferred);

		String javaScriptString = String.format("%s.ready().then(() => inObjectInterface.ready(%d));", object, promiseId);
		inMap.evaluateJavascript(javaScriptString, null);

		return promise;
	}

	/**
	 * Return the id of the object.
	 *
	 * @param inMap instance od INMap object.
	 * @param object String contains javaScript object representation.
	 * @param getIdCallback {@link GetIdCallback}
	 */
	public void getID(INMap inMap, String object, final GetIdCallback getIdCallback)
	{
		String javaScriptString = String.format("%s.getID();", object);
		inMap.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				getIdCallback.onReceiveId(Integer.parseInt(s));
			}
		});
	}
}
