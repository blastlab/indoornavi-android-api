package co.blastlab.indoornavi_api;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.InputStream;

import co.blastlab.indoornavi_api.callback.OnViewReadyCallback;
import co.blastlab.indoornavi_api.web_view.IndoorWebChromeClient;
import co.blastlab.indoornavi_api.web_view.JavaScriptInterface;

public class INMap extends WebView {

	private JavaScriptInterface JSInterface;
	OnViewReadyCallback mapReadyCallback;

	private Context context;

	private String targetHost;
	private String apiKey;


	public INMap(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		this.context = context;
		mapReadyCallback = (OnViewReadyCallback) context;

		loadWebViewFromAssets();
		init();
		setJSInterface();
	}

	public void load(int mapId)
	{
		String javaScriptString = String.format("navi.load(%d).then(enableButtons());", mapId);
		this.evaluateJavascript(javaScriptString, null);
	}

	private void init()
	{

		this.setWebViewClient(new WebViewClient() {

			@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				mapReadyCallback.onWebViewReady((INMap) view);
			}

		});

		this.setWebChromeClient(new IndoorWebChromeClient());

		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setDomStorageEnabled(true);
		//this.getSettings().setUseWideViewPort(true);
		this.getSettings().setLoadWithOverviewMode(true);
		this.getSettings().setDomStorageEnabled(true);

	}

	public void createMap(String targetHost, String apiKey)
	{
		this.targetHost = targetHost;
		this.apiKey = apiKey;

		JS_InMapCreate();
	}

	public void JS_InMapCreate() {
		float scaleX = getScaleX();
		float scaleY = getScaleY();
		Log.i(Constants.LOG, String.format("onReceiveValue: x = %f, y = %f", scaleX, scaleY));

		String javaScriptString = String.format(Constants.indoorNaviInitialization, targetHost, apiKey, 1200, 850);
		Log.i(Constants.LOG, "javaScriptString: " + javaScriptString);
		this.evaluateJavascript(javaScriptString, new ValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				Log.i(Constants.LOG, "onReceiveValue: " + s);
			}
		});
	}

	private void setJSInterface() {

		JSInterface = new JavaScriptInterface(context);
		this.addJavascriptInterface(JSInterface, "JSInterface");
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
			Log.e(Constants.LOG, "Loading assets exception" + e);
		}
		this.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8",null);
	}
}
