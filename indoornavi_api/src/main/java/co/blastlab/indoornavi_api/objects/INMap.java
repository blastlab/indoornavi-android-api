package co.blastlab.indoornavi_api.objects;

import android.content.Context;
import android.support.annotation.StringDef;
import android.util.AttributeSet;
import android.util.Log;

import android.webkit.WebView;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.interfaces.EventListenerInterface;
import co.blastlab.indoornavi_api.interfaces.INMarkerInterface;
import co.blastlab.indoornavi_api.interfaces.INObjectInterface;
import co.blastlab.indoornavi_api.interfaces.ReportInterface;
import co.blastlab.indoornavi_api.web_view.IndoorWebChromeClient;
import co.blastlab.indoornavi_api.web_view.IndoorWebViewClient;

/**
 * Class representing a map, creates the INMap object to communicate with frontend server.
 */
public class INMap extends WebView {

	INObjectInterface inObjectInterface;
	INMarkerInterface inMarkerInterface;
	ReportInterface reportInterface;
	EventListenerInterface eventInterface;

	private Context context;

	private String targetHost;
	private String apiKey;

	private int height, weight;

	public static final String AREA = "AREA";
	public static final String COORDINATES  = "COORDINATES";

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({AREA,COORDINATES})

	private @interface EventListner {}

	/**
	 * Constructs a new WebView with layout parameters.
	 *
	 * @param context a Context object used to access application assets
	 * @param attributeSet an AttributeSet passed to our parent
	 */
	public INMap(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		this.context = context;

		loadWebViewFromAssets();
		init();
		interfaceInit();
	}

	/**
	 * Load map of the floor with specific id.
	 *
	 * @param floorId - Id of specific floor.
	 * @param onObjectReadyCallback interface - trigger when object is successfully create.
	 */
	public void load(int floorId, OnObjectReadyCallback onObjectReadyCallback)
	{
		int promiseId = onObjectReadyCallback.hashCode();
		Controller.promiseCallbackMap.put(promiseId, onObjectReadyCallback);

		String javaScriptString = String.format(Locale.US, "navi.load(%d).then(() => inObjectInterface.ready(%d));", floorId, promiseId);
		this.evaluateJavascript(javaScriptString, null);
	}

	private void init()
	{
		this.setWebViewClient(new IndoorWebViewClient());
		this.setWebChromeClient(new IndoorWebChromeClient());

		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setLoadWithOverviewMode(true);

		this.getSettings().setAllowFileAccess(false);
		this.getSettings().setAllowFileAccessFromFileURLs(false);
		this.getSettings().setAllowUniversalAccessFromFileURLs(true);
		this.getSettings().setAllowContentAccess(false);
	}

	/**
	 * Create INMap object.
	 *
	 * @param targetHost address to the frontend server
	 * @param apiKey the API key created on server
	 */
	public void createMap(String targetHost, String apiKey, int height, int weight)
	{
		this.targetHost = targetHost;
		this.apiKey = apiKey;
		this.height = height;
		this.weight = weight;

		JS_InMapCreate();
	}

	/**
	 * Register a callback to be invoked when event occurs.
	 *
	 * @param event - type of event listener
	 * @param onEventListener interface - invoked when event occurs.
	 */
	public void addEventListener(@EventListner String event, OnEventListener onEventListener) {

		int eventId = onEventListener.hashCode();
		Controller.eventListenerMap.put(eventId, onEventListener);

		String javaScriptString = String.format(Locale.US, "navi.addEventListener(Event.LISTENER.%s, res => eventInterface.onEvent(%d, %s, stringify(res)));", event, eventId, event);
		this.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Toggle the tag visibility.
	 *
	 * @param tagId - Id of specific tag.
	 */
	public void toggleTagVisibility(short tagId) {
		String javaScriptString = String.format(Locale.US, "navi.toggleTagVisibility(%d);", tagId);
		this.evaluateJavascript(javaScriptString, null);
	}

	private void JS_InMapCreate() {
		String javaScriptString = String.format(Locale.US, "var navi = new INMap(\"%s\",\"%s\",\"map\",{width:%d,height:%d});", targetHost, apiKey, height, weight);
		this.evaluateJavascript(javaScriptString, null);
	}

	private void loadWebViewFromAssets() {
		String str = "";
		try {
			InputStream is =context.getAssets().open("index.html");
			StringBuilder builder = new StringBuilder();

			byte[] buffer = new byte[1024];
			while(is.read(buffer) != -1) {
				builder.append(new String(buffer));
			}
			is.close();
			 str = builder.toString();
		}
		catch (Exception e)
		{
			Log.e("Load assets exception :", e.toString());
		}
		this.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8",null);
	}

	private void interfaceInit()
	{
		inObjectInterface  = new INObjectInterface();
		this.addJavascriptInterface(inObjectInterface, "inObjectInterface");

		inMarkerInterface = new INMarkerInterface();
		this.addJavascriptInterface(inMarkerInterface, "inMarkerInterface");

		reportInterface = new ReportInterface();
		this.addJavascriptInterface(reportInterface, "reportInterface");

		eventInterface = new EventListenerInterface();
		this.addJavascriptInterface(eventInterface, "eventInterface");
	}
}
