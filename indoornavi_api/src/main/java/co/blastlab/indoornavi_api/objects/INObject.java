package co.blastlab.indoornavi_api.objects;

import android.webkit.ValueCallback;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import co.blastlab.indoornavi_api.Constants;
import co.blastlab.indoornavi_api.callback.GetIdCallback;

public class INObject {

	public static final String INPOLYLINE_OBJECT = "poly";
	public static final String INAREA_OBJECT = "area";
	public static final String INMARKER_OBJECT = "marker";
	public static final String ININFOWINDOW_OBJECT = "infoWindow";

	public Promise checkReady(INMap inMap, String object)
	{

		Deferred deferred = new DeferredObject();
		Promise promise = deferred.promise();

		Constants.promiseMap.put(1, deferred);
		String javaScriptString = String.format("%s.ready().then(() => inObjectInterface.ready());", object);
		inMap.evaluateJavascript(javaScriptString, null);

		return promise;
	}

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
