package co.blastlab.indoornavi_api.web_view;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.callback.OnINMapReadyCallback;

public class IndoorWebViewClient extends WebViewClient {

	public OnINMapReadyCallback mapReadyCallback;

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url){
		return true;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);

		mapReadyCallback = (OnINMapReadyCallback) view.getContext();
		mapReadyCallback.onINMapReady((INMap) view);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
	                          String description, String failingUrl){
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	@SuppressWarnings("deprecated")
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		if (url.contains("Navi.js")) {
			try {
				AssetManager assets = view.getContext().getAssets();
				Uri uri = Uri.parse(url);
				InputStream stream = assets.open(uri.getPath(), AssetManager.ACCESS_STREAMING);
				WebResourceResponse response = new WebResourceResponse("text/html", "UTF-8", stream);
				return response;
			} catch (IOException e) {
				Log.e("InputStream exception :", e.toString());
			}
		}
		return super.shouldInterceptRequest(view, url);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

		if (request.getUrl().getEncodedPath().contains("Navi.js")) {
			try {
				AssetManager assets = view.getContext().getAssets();
				Uri uri = Uri.parse(request.getUrl().getEncodedPath());
				InputStream stream = assets.open(uri.getPath(), AssetManager.ACCESS_STREAMING);
				WebResourceResponse response = new WebResourceResponse("text/html", "UTF-8", stream);
				return response;
			} catch (IOException e) {
				Log.e("InputStream exception :", e.toString());
			}

		}
		return super.shouldInterceptRequest(view, request);
	}
}