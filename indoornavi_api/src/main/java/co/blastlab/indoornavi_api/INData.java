package co.blastlab.indoornavi_api;

import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.model.Path;
import co.blastlab.indoornavi_api.objects.INMap;

public class INData {

	private  String objectInstance, targetHost, apiKey;
	private INMap inMap;

	/**
	 * Data object constructor.
	 *
	 * @param inMap INMap instance
	 * @param targetHost address to the INMap backend server
	 * @param apiKey the API key created on INMap server
	 */
	public INData(INMap inMap, String targetHost, String apiKey) {
		this.objectInstance = String.format(Locale.US, "data%d",this.hashCode());
		this.targetHost = targetHost;
		this.apiKey = apiKey;
		this.inMap = inMap;

		String javaScriptString = String.format("var %s = new INData('%s', '%s');", objectInstance, targetHost, apiKey);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Retrieve list of paths.
	 *
	 * @param floorId - id of the floor you want to get paths from
	 * @param onReceiveValueCallback - callback interface invoke when {@link Path} list is ready
	 */
	public void getPaths(int floorId, OnReceiveValueCallback<List<Path>> onReceiveValueCallback) {

		int promiseId = onReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(promiseId, onReceiveValueCallback);

		String javaScriptString = String.format(Locale.US, "%s.getPaths(%d).then(res => dataInterface.pathsData(%d, JSON.stringify(res)));", objectInstance, floorId, promiseId);
		inMap.evaluateJavascript(javaScriptString, null);
	}

}
