package co.blastlab.indoornavi_api.interfaces;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONObject;

import co.blastlab.indoornavi_api.Controller;

public class INNavigationInterface {

	@JavascriptInterface
	public void onMessageReceive(final int promiseId, String action) {
		if (Controller.navigationMessageMap.indexOfKey(promiseId) >= 0) {
			try {
				Controller.navigationMessageMap.get(promiseId).onMessageReceive(new JSONObject(action).getString("action"));
			} catch (Exception e) {
				Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Invalid message content");
			}
		}
	}
}
