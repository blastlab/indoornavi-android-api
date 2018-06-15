package co.blastlab.indoornavi_api.objects;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ValueCallback;

import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.utils.CoordinatesUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

/**
 * Class INObject is the root of the IndoorNavi objects hierarchy. Every IN object has INObject as a superclass (except INMap).
 */
public class INObject {

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
	 * Method waits till object is create.
	 * Using this method is indispensable to operate on the object.
	 *
	 * @param onObjectReadyCallback interface - trigger when object is successfully create.
	 */
	public void ready(OnObjectReadyCallback onObjectReadyCallback)
	{
		int promiseId = onObjectReadyCallback.hashCode();
		Controller.promiseCallbackMap.put(promiseId, onObjectReadyCallback);

		String javaScriptString = String.format(Locale.US, "%s.ready().then(() => inObjectInterface.ready(%d));", objectInstance, promiseId);
		evaluate(javaScriptString, null);
	}

	/**
	 * Returns the id of the object.
	 *
	 * @param onReceiveValueCallback interface - invoked when object id is available.
	 */
	public void getID( final OnReceiveValueCallback<Long> onReceiveValueCallback)
	{
		String javaScriptString = String.format("%s.getID();", objectInstance);
		evaluate(javaScriptString, stringID -> {
			if(!stringID.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Long.parseLong(stringID.substring(0, stringID.length() - 2)));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
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
		evaluate(javaScriptString, stringPoints -> {
			if(!stringPoints.equals("null")) {
				List<Point> points;
				points = PointsUtil.stringToPoints(stringPoints);
				onReceiveValueCallback.onReceiveValue(points);
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): points not set yet! ");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Removes object and its instance from frontend server, but do not destroys object class instance in your app.
	 */
	public void remove()
	{
		String javaScriptString = String.format("%s.remove();", objectInstance);
		evaluate(javaScriptString, null);
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
		evaluate(javaScriptString, stringIsWithin -> {
			if(stringIsWithin != null) {
				valueCallback.onReceiveValue(Boolean.valueOf(stringIsWithin));
			}
			else {
				Log.e("Null pointer Exception","(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): The value can't be determined! ");
				valueCallback.onReceiveValue(null);
			}
		});
	}

	protected void evaluate(String javaScriptString, ValueCallback<String> valueCallback)
	{
		if(Looper.myLooper() == Looper.getMainLooper()) {
			inMap.evaluateJavascript(javaScriptString, valueCallback);
		}
		else {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(() -> {
				inMap.evaluateJavascript(javaScriptString, valueCallback);
			});

		}
	}
}
