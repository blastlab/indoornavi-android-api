package co.blastlab.indoornavi_api.objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringDef;
import android.util.AttributeSet;
import android.util.Log;

import android.webkit.WebView;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.algorithm.model.Position;
import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.interfaces.ComplexInterface;
import co.blastlab.indoornavi_api.interfaces.DataInterface;
import co.blastlab.indoornavi_api.interfaces.EventListenerInterface;
import co.blastlab.indoornavi_api.interfaces.INMarkerInterface;
import co.blastlab.indoornavi_api.interfaces.INObjectInterface;
import co.blastlab.indoornavi_api.interfaces.ReportInterface;
import co.blastlab.indoornavi_api.model.Complex;
import co.blastlab.indoornavi_api.model.Scale;
import co.blastlab.indoornavi_api.utils.MapUtil;
import co.blastlab.indoornavi_api.web_view.IndoorWebChromeClient;
import co.blastlab.indoornavi_api.web_view.IndoorWebViewClient;

/**
 * Class represents a map, creates the INMap object to communicate with frontend server.
 */
public class INMap extends WebView {

	INObjectInterface inObjectInterface;
	INMarkerInterface inMarkerInterface;
	ReportInterface reportInterface;
	EventListenerInterface eventInterface;
	DataInterface dataInterface;
	ComplexInterface complexInterface;

	private Context context;

	private String targetHost;
	private String apiKey;
	private int height, weight;
	private int floorId;
	private Scale scale;

	public static final String AREA = "AREA";
	public static final String COORDINATES = "COORDINATES";

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({AREA, COORDINATES})
	public @interface EventListener {
	}

	/**
	 * Constructs a new WebView with layout parameters.
	 *
	 * @param context      a Context object used to access application assets
	 * @param attributeSet an AttributeSet passed to our parent
	 */
	public INMap(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.context = context;

		loadWebViewFromAssets();
		init();
		interfaceInit();
	}

	/**
	 * Load map of the floor with specific id.
	 *
	 * @param floorId               - Id of specific floor.
	 * @param onObjectReadyCallback interface - trigger when object is successfully create.
	 */
	private void ready(int floorId, OnObjectReadyCallback onObjectReadyCallback) {
		int promiseId = onObjectReadyCallback.hashCode();
		Controller.promiseCallbackMap.put(promiseId, onObjectReadyCallback);

		String javaScriptString = String.format(Locale.US, "navi.load(%d).then(() => inObjectInterface.ready(%d));", floorId, promiseId);
		this.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Load map of the floor with specific id.
	 *
	 * @param floorId               - Id of specific floor.
	 * @param onObjectReadyCallback interface - trigger when map is successfully loaded.
	 */
	public void load(int floorId, OnObjectReadyCallback onObjectReadyCallback) {
		this.floorId = floorId;
		this.ready(floorId, (object) -> {
			waitUntilMapReady(onObjectReadyCallback);
			getMapDimensions();
		});
	}

	/**
	 * Add listener to react when the long click event occurs.
	 *
	 * @param onEventListener interface - trigger when the event occurs.
	 */
	public void addLongClickListener(OnEventListener onEventListener) {

		waitUntilMapReady((object) -> {
			int eventId = onEventListener.hashCode();
			Controller.eventListenerMap.put(eventId, onEventListener);

			String javaScriptString = String.format(Locale.US, "navi.addMapLongClickListener(res => eventInterface.onClickEvent(%s, JSON.stringify(res)));", eventId);
			this.evaluateJavascript(javaScriptString, null);
		});
	}

	private void getMapDimensions() {

		final INMap inMap = this;
		String javaScriptString = "navi.parameters;";
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
			inMap.evaluateJavascript(javaScriptString, data -> {
				inMap.scale = MapUtil.stringToScale(data);
				for (OnObjectReadyCallback readyCallback : Controller.promiseMapReady) {
					readyCallback.onReady(null);
				}
				Controller.promiseMapReady.clear();
			});
		});
	}

	/**
	 * Returns the list of complexes with all building and floors.
	 *
	 * @param onReceiveValueCallback interface - invoked when list of complex is available. Return {@link List<Complex>} or null if unsuccessful.
	 */
	public void getComplex(final OnReceiveValueCallback<List<Complex>> onReceiveValueCallback) {
		final INMap inMap = this;

		int callbackId = onReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(callbackId, onReceiveValueCallback);

		String javaScriptString = String.format(Locale.US, "navi.getComplexes(complexes => complexInterface.onComplexes(%d, JSON.stringify(complexes)));", callbackId);

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
			inMap.evaluateJavascript(javaScriptString, null);
		});
	}

	/**
	 * Get closest coordinates on floor path for given point
	 *
	 * @param position point coordinates in real dimensions
	 * @param onReceiveValueCallback interface - invoked when calculated point is available. Return {@link Point} or null if unsuccessful.
	 */
	public void pullToPath(Position position, int accuracy, final OnReceiveValueCallback<Point> onReceiveValueCallback) {
		final INMap inMap = this;

		int callbackId = onReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(callbackId, onReceiveValueCallback);

		String javaScriptString = String.format(Locale.US, "navi.pullToPath({x: %d, y: %d}, %d).then(pulledPoint => dataInterface.pulledPoint(%d, JSON.stringify(pulledPoint)));", Math.round(position.x), Math.round(position.y), accuracy, callbackId);

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
			inMap.evaluateJavascript(javaScriptString, null);
		});
	}

	/**
	 * @return Scale set to map object.
	 */
	public Scale getMapScale() {
		return this.scale;
	}

	public String getTargetHost() {
		return this.targetHost;
	}

	public String getApiKey() {
		return apiKey;
	}

	public int getFloorId() {
		return this.floorId;
	}

	public void waitUntilMapReady(OnObjectReadyCallback onObjectReadyCallback) {
		if (this.scale != null) {
			onObjectReadyCallback.onReady(null);
		} else {
			Controller.promiseMapReady.add(onObjectReadyCallback);
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init() {
		this.setWebViewClient(new IndoorWebViewClient());
		this.setWebChromeClient(new IndoorWebChromeClient());

		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setLoadWithOverviewMode(true);
		this.getSettings().setUseWideViewPort(true);

		this.getSettings().setAllowFileAccess(false);
		this.getSettings().setAllowFileAccessFromFileURLs(false);
		this.getSettings().setAllowUniversalAccessFromFileURLs(true);
		this.getSettings().setAllowContentAccess(false);
	}

	/**
	 * Create INMap object.
	 *
	 * @param targetHost address to the frontend server
	 * @param apiKey     the API key created on server
	 * @param height     height of the iframe in pixels
	 * @param weight     weight of the iframe in pixels
	 */
	public void createMap(String targetHost, String apiKey, int weight, int height) {
		this.targetHost = targetHost;
		this.apiKey = apiKey;
		this.weight = weight;
		this.height = height;

		JS_InMapCreate();
	}

	/**
	 * Register a callback to be invoked when event occurs.
	 *
	 * @param event           type of event listener
	 * @param onEventListener interface - invoked when event occurs.
	 */
	public void addEventListener(@EventListener String event, OnEventListener onEventListener) {

		waitUntilMapReady((object) -> {
			int eventId = onEventListener.hashCode();
			Controller.eventListenerMap.put(eventId, onEventListener);

			String javaScriptString = String.format(Locale.US, "navi.addEventListener(Event.LISTENER.%s, res => eventInterface.onEvent(%d, \"%s\", JSON.stringify(res)));", event, eventId, event);
			this.evaluateJavascript(javaScriptString, null);
		});
	}

	/**
	 * Toggle the tag visibility.
	 *
	 * @param tagId Id of specific tag.
	 */
	public void toggleTagVisibility(short tagId) {
		waitUntilMapReady((object) -> {
			String javaScriptString = String.format(Locale.US, "navi.toggleTagVisibility(%d);", tagId);
			this.evaluateJavascript(javaScriptString, null);
		});
	}

	private void JS_InMapCreate() {
		String javaScriptString = String.format(Locale.US, "var navi = new INMap(\"%s\",\"%s\",\"map\",{width:%d,height:%d});", targetHost, apiKey, weight, height);
		this.evaluateJavascript(javaScriptString, null);
	}

	private void loadWebViewFromAssets() {
		String str = "";
		try {
			InputStream is = context.getAssets().open("index.html");
			StringBuilder builder = new StringBuilder();

			byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1) {
				builder.append(new String(buffer));
			}
			is.close();
			str = builder.toString();
		} catch (Exception e) {
			Log.e("Load assets exception :", e.toString());
		}
		this.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8", null);
	}

	private void interfaceInit() {
		inObjectInterface = new INObjectInterface();
		this.addJavascriptInterface(inObjectInterface, "inObjectInterface");

		inMarkerInterface = new INMarkerInterface();
		this.addJavascriptInterface(inMarkerInterface, "inMarkerInterface");

		reportInterface = new ReportInterface();
		this.addJavascriptInterface(reportInterface, "reportInterface");

		eventInterface = new EventListenerInterface();
		this.addJavascriptInterface(eventInterface, "eventInterface");

		dataInterface = new DataInterface();
		this.addJavascriptInterface(dataInterface, "dataInterface");

		complexInterface = new ComplexInterface();
		this.addJavascriptInterface(complexInterface, "complexInterface");
	}
}
