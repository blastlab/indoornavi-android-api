package co.blastlab.indoornavi_api;

import android.content.Context;;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

import java.io.InputStream;

import co.blastlab.indoornavi_api.web_view.IndoorWebChromeClient;
import co.blastlab.indoornavi_api.web_view.IndoorWebViewClient;

public class INMap extends WebView {

	private Context context;

	private String targetHost;
	private String apiKey;


	public INMap(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		this.context = context;

		loadWebViewFromAssets();
		init();
	}

	public void load(int mapId)
	{
		String javaScriptString = String.format("navi.load(%d);", mapId);
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

	public void createMap(String targetHost, String apiKey)
	{
		this.targetHost = targetHost;
		this.apiKey = apiKey;

		JS_InMapCreate();
	}

	private void JS_InMapCreate() {
		String javaScriptString = String.format(Constants.indoorNaviInitialization, targetHost, apiKey, 1200, 850);
		Log.i(Constants.LOG, "javaScriptString: " + javaScriptString);
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
			Log.e(Constants.LOG, "Loading assets exception" + e);
		}
		this.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8",null);
	}
}
