package co.blastlab.indoornavi_api;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ValueCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;


import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Border;
import co.blastlab.indoornavi_api.model.Path;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INMap;

import co.blastlab.indoornavi_api.utils.MapUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

public class INData {

	private String objectInstance, targetHost, apiKey;
	private INMap inMap;

	/**
	 * Data object constructor.
	 *
	 * @param inMap      INMap instance
	 * @param targetHost address to the INMap backend server
	 * @param apiKey     the API key created on INMap server
	 */
	public INData(INMap inMap, String targetHost, String apiKey) {
		this.objectInstance = String.format(Locale.US, "data%d", this.hashCode());
		this.targetHost = targetHost;
		this.apiKey = apiKey;
		this.inMap = inMap;

		String javaScriptString = String.format("var %s = new INData('%s', '%s');", objectInstance, targetHost, apiKey);
		evaluate(javaScriptString, null);
	}

	/**
	 * Retrieve list of paths.
	 *
	 * @param onReceiveValueCallback - callback interface invoke when {@link Path} list is ready
	 */
	public void getPaths(OnReceiveValueCallback<List<Path>> onReceiveValueCallback) {

		int promiseId = onReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(promiseId, onReceiveValueCallback);

		String javaScriptString = String.format(Locale.US, "%s.getPaths(%d).then(res => inDataInterface.pathsData(%d, JSON.stringify(res)));", objectInstance, this.inMap.getFloorId(), promiseId);
		evaluate(javaScriptString, null);
	}

	/**
	 * Returns the list of global areas for given floor.
	 *
	 * @param onReceiveValueCallback interface - invoked when list of areas is available. Return {@link List< INArea >} or null if unsuccessful.
	 */
	public void getAreas(final OnReceiveValueCallback<List<INArea>> onReceiveValueCallback) {

		OnReceiveValueCallback<String> innerReceiveValueCallback = new OnReceiveValueCallback<String>() {
			@Override
			public void onReceiveValue(String stringAreasJson) {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(() ->
					onReceiveValueCallback.onReceiveValue(getAreasFromJSON(stringAreasJson))
				);
			}
		};

		int promiseId = innerReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(promiseId, innerReceiveValueCallback);

		String javaScriptString = String.format(Locale.US, "%s.getAreas(%d).then(areas => inDataInterface.onAreas(%d, JSON.stringify(areas)));", objectInstance, this.inMap.getFloorId(), promiseId);
		evaluate(javaScriptString, null);
	}

	private List<INArea> getAreasFromJSON(String jsonString) {
		if(jsonString == null) return null;

		List<INArea> areas = new ArrayList<>();

		try {
			JSONArray jsonAreasList = new JSONArray(jsonString);

			for (int i = 0; i < jsonAreasList.length(); i++) {
				List<Point> points = new ArrayList<>();
				INArea inArea = INArea.createDefault(this.inMap);

				JSONObject area = jsonAreasList.getJSONObject(i);
				inArea.setName(area.getString("name"));
				inArea.setDatabaseId(area.getInt("id"));

				for(Point point : PointsUtil.stringToPoints(area.getString("points"))) {
					points.add(MapUtil.pixelsToRealDimensions(this.inMap.getMapScale(), point));
				}

				inArea.setPoints(points);
				inArea.setOpacity(0.2);
				inArea.setColor(Color.GREEN);
				inArea.setBorder(new Border(4, Color.GREEN));
				try {
					CountDownLatch latch = new CountDownLatch(1);
					inArea.ready(data -> latch.countDown());

					latch.await();
					areas.add(inArea);

				} catch (Exception e) {
					Log.e("Create object exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): " + e);
				}
			}
			return areas;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

	private void evaluate(String javaScriptString, ValueCallback<String> valueCallback) {
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
