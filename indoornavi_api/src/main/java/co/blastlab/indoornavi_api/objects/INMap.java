package co.blastlab.indoornavi_api.objects;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringDef;
import android.util.AttributeSet;
import android.util.Log;

import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.interfaces.INDataInterface;
import co.blastlab.indoornavi_api.interfaces.EventListenerInterface;
import co.blastlab.indoornavi_api.interfaces.INMapInterface;
import co.blastlab.indoornavi_api.interfaces.INObjectEventInterface;
import co.blastlab.indoornavi_api.interfaces.INNavigationInterface;
import co.blastlab.indoornavi_api.interfaces.INObjectInterface;
import co.blastlab.indoornavi_api.interfaces.INReportInterface;
import co.blastlab.indoornavi_api.model.Complex;
import co.blastlab.indoornavi_api.model.Scale;
import co.blastlab.indoornavi_api.service.BluetoothScanService;
import co.blastlab.indoornavi_api.utils.MapUtil;
import co.blastlab.indoornavi_api.web_view.IndoorWebChromeClient;
import co.blastlab.indoornavi_api.web_view.IndoorWebViewClient;

/**
 * Class represents a map, creates the INMap object to communicate with frontend server.
 */
public class INMap extends WebView {

	INObjectInterface inObjectInterface;
	INObjectEventInterface inObjectEventInterface;
	INReportInterface INReportInterface;
	EventListenerInterface eventInterface;
	INDataInterface INDataInterface;
	INMapInterface inMapInterface;
	INNavigationInterface inNavigationInterface;

	private Context context;

	private String targetHost;
	private String apiKey;
	private int floorId;
	private Scale scale;
	private boolean isAutoReload = false;

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
		this.evaluate(javaScriptString, null);
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
	 * Load map of the floor with specific id.
	 *
	 * @param floorId - Id of specific floor.
	 */
	public void load(int floorId) {
		this.floorId = floorId;
		this.ready(floorId, (object) -> {
			getMapDimensions();
		});
	}

	/**
	 * Add listener to react when the long click event occurs.
	 *
	 * @param onEventListener interface - trigger when the event occurs.
	 */
	public void addLongClickListener(OnEventListener onEventListener) {
		INMap inMap = this;

		waitUntilMapReady((object) -> {
			OnEventListener<Point> innerOnEventListener = new OnEventListener<Point>() {
				@Override
				public void onEvent(Point point) {
					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(() ->
						onEventListener.onEvent(point == null ? null : MapUtil.pixelsToRealDimensions(inMap.getMapScale(), point))
					);
				}
			};

			int eventId = innerOnEventListener.hashCode();
			Controller.eventListenerMap.put(eventId, innerOnEventListener);

			String javaScriptString = String.format(Locale.US, "navi.addMapLongClickListener(res => eventInterface.onClickEvent(%s, JSON.stringify(res)));", eventId);
			this.evaluate(javaScriptString, null);
		});
	}

	private void getMapDimensions() {

		final INMap inMap = this;
		String javaScriptString = "navi.parameters;";
		inMap.evaluate(javaScriptString, data -> {
			inMap.scale = MapUtil.stringToScale(data);
			clearReadyPromiseMap();
		});
	}

	private void setTimeout() {
		final INMap inMap = this;
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(20000);
					if (Controller.promiseMapReady.size() > 0) {
						Log.e("Timeout ", " server " + inMap.getTargetHost() + " not responding");
						clearReadyPromiseMap();
					}
				} catch (InterruptedException e) {
					Log.e("Indoor", "thread exception");
				}
			}
		};
		thread.start();
	}

	private void clearReadyPromiseMap() {
		for (OnObjectReadyCallback readyCallback : Controller.promiseMapReady) {
			readyCallback.onReady(null);
		}
		Controller.promiseMapReady.clear();
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

		String javaScriptString = String.format(Locale.US, "navi.getComplexes(complexes => inMapInterface.onComplexes(%d, JSON.stringify(complexes)));", callbackId);
		inMap.evaluate(javaScriptString, null);
	}

	/**
	 * Get closest coordinates on floor path for given point
	 *
	 * @param position               point coordinates in real dimensions
	 * @param onReceiveValueCallback interface - invoked when calculated point is available. Return {@link Point} or null if unsuccessful.
	 */
	public void pullToPath(Point position, int accuracy, final OnReceiveValueCallback<Point> onReceiveValueCallback) {
		final INMap inMap = this;

		OnReceiveValueCallback<Point> innerReceiveValueCallback = new OnReceiveValueCallback<Point>() {
			@Override
			public void onReceiveValue(Point point) {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(() ->
					onReceiveValueCallback.onReceiveValue(point == null ? null : MapUtil.pixelsToRealDimensions(inMap.getMapScale(), point))
				);
			}
		};

		int callbackId = innerReceiveValueCallback.hashCode();
		Controller.ReceiveValueMap.put(callbackId, innerReceiveValueCallback);
		Point positionInPixels = MapUtil.realDimensionsToPixels(inMap.getMapScale(), position);
		String javaScriptString = String.format(Locale.US, "navi.pullToPath({x: %d, y: %d}, %d).then(pulledPoint => inMapInterface.pulledPoint(%d, JSON.stringify(pulledPoint)));", positionInPixels.x, positionInPixels.y, accuracy, callbackId);
		inMap.evaluate(javaScriptString, null);
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

	public void setAutoReload(boolean state) {
		this.isAutoReload = state;

		if (state) {
			registerReceiver();
		} else {
			unregisterReceiver();
		}
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

		this.getSettings().setAppCachePath(this.context.getFilesDir().getAbsolutePath());
		this.getSettings().setAllowFileAccess(true);
		this.getSettings().setAppCacheEnabled(true);
		this.getSettings().setJavaScriptEnabled(true);

		this.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setLoadWithOverviewMode(true);

		this.getSettings().setAllowUniversalAccessFromFileURLs(true);
		this.getSettings().setAllowContentAccess(true);
		this.getSettings().setSaveFormData(true);
		this.getSettings().setSupportZoom(true);
		this.setLayerType(View.LAYER_TYPE_HARDWARE, null);

	}

	/**
	 * Create INMap object.
	 *
	 * @param targetHost address to the frontend server
	 * @param apiKey     the API key created on server
	 */
	public void createMap(String targetHost, String apiKey) {
		this.targetHost = targetHost;
		this.apiKey = apiKey;

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
			this.evaluate(javaScriptString, null);
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
			this.evaluate(javaScriptString, null);
		});
	}

	private void JS_InMapCreate() {
		String javaScriptString = String.format(Locale.US, "var navi = new INMap(\"%s\",\"%s\",\"map\");", targetHost, apiKey);
		this.evaluate(javaScriptString, null);
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

		inObjectEventInterface = new INObjectEventInterface();
		this.addJavascriptInterface(inObjectEventInterface, "inObjectEventInterface");

		INReportInterface = new INReportInterface();
		this.addJavascriptInterface(INReportInterface, "inReportInterface");

		eventInterface = new EventListenerInterface();
		this.addJavascriptInterface(eventInterface, "eventInterface");

		INDataInterface = new INDataInterface();
		this.addJavascriptInterface(INDataInterface, "inDataInterface");

		inMapInterface = new INMapInterface();
		this.addJavascriptInterface(inMapInterface, "inMapInterface");

		inNavigationInterface = new INNavigationInterface();
		this.addJavascriptInterface(inNavigationInterface, "inNavigationInterface");

	}

	private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case BluetoothScanService.FLOOR_CHANGE:
					int newFloorId = intent.getIntExtra("floorId", -1);
					if (newFloorId != -1 && newFloorId != floorId && isAutoReload) {
						load(newFloorId);
					}
					break;
			}
		}
	};

	private void registerReceiver() {
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothScanService.FLOOR_CHANGE);
			this.context.registerReceiver(serviceReceiver, intentFilter);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void unregisterReceiver() {
		try {
			if (serviceReceiver != null) {
				this.context.unregisterReceiver(serviceReceiver);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private void evaluate(String javaScriptString, ValueCallback<String> valueCallback) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			this.evaluateJavascript(javaScriptString, valueCallback);
		} else {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(() -> {
				this.evaluateJavascript(javaScriptString, valueCallback);
			});

		}
	}
}
