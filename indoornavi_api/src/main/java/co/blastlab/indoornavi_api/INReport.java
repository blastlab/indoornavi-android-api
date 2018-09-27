package co.blastlab.indoornavi_api;

import android.util.Log;
import android.webkit.WebView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;

/**
 * Class represents the INReport object allows to obtain archived data.
 */
public class INReport {

	private  String objectInstance, targetHost, apiKey;
	private WebView webView;

	/**
	 * Reports object constructor.
	 *
	 * @param webView WebView instance
	 * @param targetHost address to the INMap backend server
	 * @param apiKey the API key created on INMap server
	 */
	public INReport(WebView webView, String targetHost, String apiKey) {
		this.objectInstance = String.format(Locale.US, "report%d",this.hashCode());
		this.targetHost = targetHost;
		this.apiKey = apiKey;
		this.webView = webView;

		String javaScriptString = String.format("var %s = new INReport('%s', '%s');", objectInstance, targetHost, apiKey);
		webView.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Retrieve list of archived Area events.
	 *
	 * @param floorId id of the floor you want to get area events from
	 * @param from start date of the period
	 * @param to end date of the period
	 * @param onObjectReadyCallback callback interface invoke when {@link AreaEvent} list is ready
	 */
	public void getAreaEvents(int floorId, Date from, Date to, OnObjectReadyCallback<List<AreaEvent>> onObjectReadyCallback) {

		if(to.after(from)) {
			int promiseId = onObjectReadyCallback.hashCode();
			Controller.promiseCallbackMap.put(promiseId, onObjectReadyCallback);

			String javaScriptString = String.format(Locale.US, "%s.getAreaEvents(%d, new Date(%d), new Date(%d)).then(res => inReportInterface.areaEvents(%d, JSON.stringify(res)));", objectInstance, floorId, from.getTime(), to.getTime(), promiseId);

			webView.evaluateJavascript(javaScriptString, null);
		}
		else {
			Log.e("Date range exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + " : " + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): Date \"to\" mast be after \"from\"");
		}
	}

	/**
	 * Retrieve list of archived coordinates.
	 *
	 * @param floorId id of the floor you want to get coordinates from
	 * @param from start date of the period
	 * @param to end date of the period
	 * @param onObjectReadyCallback callback interface invoke when {@link Coordinates} list is ready
	 */
	public void getCoordinates(int floorId, Date from, Date to, OnObjectReadyCallback<List<Coordinates>> onObjectReadyCallback) {

		if(to.after(from)) {
			int promiseId = onObjectReadyCallback.hashCode();
			Controller.promiseCallbackMap.put(promiseId, onObjectReadyCallback);

			String javaScriptString = String.format(Locale.US, "%s.getCoordinates(%d, new Date(%d), new Date(%d)).then(res => inReportInterface.coordinates(%d, JSON.stringify(res)));", objectInstance, floorId, from.getTime(), to.getTime(), promiseId);
			webView.evaluateJavascript(javaScriptString, null);
		}
		else {
			Log.e("Date range exception", "(" + Thread.currentThread().getStackTrace()[3].getFileName() + ":" + Thread.currentThread().getStackTrace()[3].getLineNumber() + "): Date \"to\" mast be after \"from\"");
		}
	}
}
