package co.blastlab.indoornavi_api.interfaces;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.model.Path;
import co.blastlab.indoornavi_api.utils.ComplexUtils;
import co.blastlab.indoornavi_api.utils.PointsUtil;

public class INDataInterface {

	@JavascriptInterface
	public void pathsData(int promiseId, String paths) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
			if (!paths.equals("[]") && !paths.equals("null")) {
				List<Path> pathList = new ArrayList<>();
				try {
					JSONArray jsonArray = new JSONArray(paths);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						pathList.add(new Path(PointsUtil.stringToPoint(jsonObject.getString("startPoint")), PointsUtil.stringToPoint(jsonObject.getString("endPoint"))));
					}
					Controller.ReceiveValueMap.get(promiseId).onReceiveValue(pathList);
				} catch (Exception e) {
					Controller.ReceiveValueMap.get(promiseId).onReceiveValue(null);
					Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
				}
			} else {
				Controller.ReceiveValueMap.get(promiseId).onReceiveValue(null);
			}
			Controller.ReceiveValueMap.remove(promiseId);
		});
	}

	@JavascriptInterface
	public void onAreas(final int eventId, final String response) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
			if (!response.equals("[]") && !response.equals("null")) {
				Controller.ReceiveValueMap.get(eventId).onReceiveValue(response);
			} else {
				Controller.ReceiveValueMap.get(eventId).onReceiveValue(null);
			}
			Controller.ReceiveValueMap.remove(eventId);
		});
	}
}

