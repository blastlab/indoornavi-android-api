package co.blastlab.indoornavi_api.objects;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import android.webkit.WebView;

import java.io.InputStream;

import co.blastlab.indoornavi_api.Constants;
import co.blastlab.indoornavi_api.interfaces.INObjectInterface;
import co.blastlab.indoornavi_api.web_view.IndoorWebChromeClient;
import co.blastlab.indoornavi_api.web_view.IndoorWebViewClient;

public class INMap extends WebView {

	public INObjectInterface inObjectInterface;
	private Context context;

	private String targetHost;
	private String apiKey;

	public INMap(Context context, AttributeSet attributeSet){
		super(context, attributeSet);
		this.context = context;

		loadWebViewFromAssets();
		init();
		interfaceInit();
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
	}
}
