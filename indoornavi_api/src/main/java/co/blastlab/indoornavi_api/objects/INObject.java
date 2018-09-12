package co.blastlab.indoornavi_api.objects;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ValueCallback;

import java.util.Locale;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;

/**
 * Class INObject is the root of the IndoorNavi objects hierarchy. Every IN object has INObject as a superclass (except INMap).
 */
public class INObject {

	private INMap inMap;
	String objectInstance;
	protected boolean isTimeout = false;

	/**
	 * INObject constructor.
	 *
	 * @param inMap instance INMap object.
	 */
	INObject(INMap inMap) {
		this.inMap = inMap;
	}

	/**
	 * Method waits till object is create.
	 * Using this method is indispensable to operate on the object.
	 *
	 * @param onObjectReadyCallback interface - trigger when object is successfully create.
	 */
	public void ready(OnObjectReadyCallback onObjectReadyCallback) {
		int promiseId = onObjectReadyCallback.hashCode();
		Controller.promiseCallbackMap.put(promiseId, onObjectReadyCallback);

		String javaScriptString = String.format(Locale.US, "%s.ready().then(() => inObjectInterface.ready(%d));", objectInstance, promiseId);
		evaluate(javaScriptString, null);
		setTimeout(promiseId);
	}

	private void setTimeout(int promiseId) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					if (Controller.promiseCallbackMap.indexOfKey(promiseId) > -1) {
						Log.e("Timeout ", " server " + inMap.getTargetHost() + " not responding");
						isTimeout = true;
						Controller.promiseCallbackMap.get(promiseId).onReady(null);
						Controller.promiseCallbackMap.remove(promiseId);
					}
				} catch (InterruptedException e) {
					Log.e("Indoor", "thread exception");
				}
			}
		};
		thread.start();
	}

	/**
	 * Returns the id of the object.
	 *
	 * @param onReceiveValueCallback interface - invoked when object id is available. Return Long value.
	 */
	public void getID(final OnReceiveValueCallback<Long> onReceiveValueCallback) {
		String javaScriptString = String.format("%s.getID();", objectInstance);
		evaluate(javaScriptString, stringID -> {
			if (!stringID.equals("null")) {
				onReceiveValueCallback.onReceiveValue(Long.parseLong(stringID.substring(0, stringID.length() - 2)));
			} else {
				Log.e("Null pointer Exception", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): object isn't created yet!");
				onReceiveValueCallback.onReceiveValue(null);
			}
		});
	}

	/**
	 * Removes object and its instance from frontend server, but do not destroys object class instance in your app.
	 */
	protected void erase() {
		String javaScriptString = String.format("%s.remove();", objectInstance);
		evaluate(javaScriptString, null);
	}

	protected void evaluate(String javaScriptString, ValueCallback<String> valueCallback) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			inMap.evaluateJavascript(javaScriptString, valueCallback);
		} else {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(() -> {
				inMap.evaluateJavascript(javaScriptString, valueCallback);
			});

		}
	}
}
