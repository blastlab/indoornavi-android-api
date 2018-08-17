package co.blastlab.indoornavi_api.interfaces;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.model.Path;
import co.blastlab.indoornavi_api.utils.PointsUtil;

public class DataInterface {

	@JavascriptInterface
	public void pathsData(int promiseId, String paths) {
		if(!paths.equals("[]") && !paths.equals("null")) {
			List<Path> pathList = new ArrayList<>();
			try {
				JSONArray jsonArray = new JSONArray(paths);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					pathList.add(new Path(PointsUtil.stringToPoint(jsonObject.getString("startPoint")), PointsUtil.stringToPoint(jsonObject.getString("endPoint"))));
				}
				Controller.ReceiveValueMap.get(promiseId).onReceiveValue(pathList);
			}
			catch(Exception e) {
				Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e.toString());
			}
		}
		Controller.ReceiveValueMap.get(promiseId).onReceiveValue(null);
	}
}
