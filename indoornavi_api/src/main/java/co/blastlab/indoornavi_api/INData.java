package co.blastlab.indoornavi_api;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.model.Path;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INObject;
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
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Retrieve list of paths.
	 *
	 * @param floorId                - id of the floor you want to get paths from
	 * @param onReceiveValueCallback - callback interface invoke when {@link Path} list is ready
	 */
	public void getPaths(int floorId, OnReceiveValueCallback<List<Path>> onReceiveValueCallback) {

		int promiseId = onReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(promiseId, onReceiveValueCallback);

		String javaScriptString = String.format(Locale.US, "%s.getPaths(%d).then(res => inDataInterface.pathsData(%d, JSON.stringify(res)));", objectInstance, floorId, promiseId);
		inMap.evaluateJavascript(javaScriptString, null);
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
				onReceiveValueCallback.onReceiveValue(getAreasFromJSON(stringAreasJson));
			}
		};

		int promiseId = innerReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(promiseId, innerReceiveValueCallback);

		String javaScriptString = String.format(Locale.US, "%s.getAreas(%d).then(areas => inDataInterface.onAreas(%d, JSON.stringify(areas)));", objectInstance, this.inMap.getFloorId(), promiseId);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	private List<INArea> getAreasFromJSON(String jsonString) {
		List<INArea> areas = new ArrayList<>();

		try {
			JSONArray jsonAreasList = new JSONArray(jsonString);

			for (int i = 0; i < jsonAreasList.length(); i++) {
				INArea inArea = INArea.createDefault(this.inMap);

				JSONObject area = jsonAreasList.getJSONObject(i);
				inArea.setName(area.getString("name"));
				inArea.setPoints(PointsUtil.stringToPoints(area.getString("points")));
				inArea.setOpacity(0.3);
				inArea.setColor(Color.GREEN);
				areas.add(inArea);
			}
			return areas;
		} catch (Exception e) {
			Log.e("Json parse exception: ", "(" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "): " + e.toString());
		}
		return null;
	}

}
