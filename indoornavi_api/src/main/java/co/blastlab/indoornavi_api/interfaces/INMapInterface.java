package co.blastlab.indoornavi_api.interfaces;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONObject;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.utils.ComplexUtils;
import co.blastlab.indoornavi_api.utils.PointsUtil;

public class INMapInterface {

	@JavascriptInterface
	public void onComplexes(final int eventId, final String response) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
			Controller.ReceiveValueMap.get(eventId).onReceiveValue(ComplexUtils.getComplexesFromJSON(response));
			Controller.ReceiveValueMap.remove(eventId);
		});
	}

	@JavascriptInterface
	public void pulledPoint(int promiseId, String jsonPoint) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
			if (!jsonPoint.equals("null")) {
				try {
					JSONObject jsonObject = new JSONObject(jsonPoint);
					String point = jsonObject.getString("calculatedPosition");
					Controller.ReceiveValueMap.get(promiseId).onReceiveValue(PointsUtil.stringToPoint(point));
				} catch (Exception e) {
					Controller.ReceiveValueMap.get(promiseId).onReceiveValue(null);
					Log.e("Data receive error: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
				}
			} else {
				Controller.ReceiveValueMap.get(promiseId).onReceiveValue(null);
			}
			Controller.ReceiveValueMap.remove(promiseId);
		});
	}

}
