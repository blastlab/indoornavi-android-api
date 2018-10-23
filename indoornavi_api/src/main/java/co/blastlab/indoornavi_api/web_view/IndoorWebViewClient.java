package co.blastlab.indoornavi_api.web_view;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.callback.OnINMapReadyCallback;

public class IndoorWebViewClient extends WebViewClient {

	private LinkedBlockingQueue<HttpDownloadResource> downloadResourcesQueue;
	private ResourcesModule resourcesModule;

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		Log.e("shouldOverrideUrlLoad", request.getUrl().toString());
		return true;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		OnINMapReadyCallback mapReadyCallback;

		mapReadyCallback = (OnINMapReadyCallback) view.getContext();
		mapReadyCallback.onINMapReady((INMap) view);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
	                            String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	@SuppressWarnings("deprecated")
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		if (url.contains("indoorNavi.js")) {
			try {
				AssetManager assets = view.getContext().getAssets();
				Uri uri = Uri.parse(url);
				InputStream stream = assets.open(uri.getPath(), AssetManager.ACCESS_STREAMING);
				WebResourceResponse response = new WebResourceResponse("application/json", "UTF-8", stream);
				return response;
			} catch (IOException e) {
				Log.e("InputStream exception", e.toString());
			}
		}
		return super.shouldInterceptRequest(view, url);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

		String currentUrl = request.getUrl().toString();
		String saveDir = view.getContext().getFilesDir().getAbsolutePath();
		String requestHashCode = getRequestHashCode(request);

		HttpDownloadResource httpDownloadResource = new HttpDownloadResource(currentUrl, saveDir, request, requestHashCode);

		if (currentUrl == null || currentUrl.equals("about:blank") || currentUrl.contains("localhost")) {
			return super.shouldInterceptRequest(view, request);
		}

		if (isAlreadyDownload(currentUrl + requestHashCode, saveDir + "/urlConfig.txt")) {
			String fileUrl = getFileName(view.getContext().getFilesDir(), currentUrl.substring(currentUrl.lastIndexOf("/") + 1, currentUrl.length()), requestHashCode);
			try {

				File file = new File(saveDir + File.separator + fileUrl);
				InputStream fileInputStream = new FileInputStream(file);

				Map<String, String> responseHeaders = httpDownloadResource.getHeaderFile(saveDir, currentUrl + requestHashCode);

				return new WebResourceResponse("", "UTF-8", 200, "OK", responseHeaders, fileInputStream);

			} catch (IOException e) {
				Log.e("LocalResourceException", e.toString());
			}
		}
		ResourcesModule resourcesModule = new ResourcesModule(httpDownloadResource);
		resourcesModule.execute(currentUrl);

		return super.shouldInterceptRequest(view, request);
	}


	private String getRequestHashCode(WebResourceRequest request) {
		CRC32 crc32 = new CRC32();
		crc32.update((HttpDownloadResource.headerMapToString(request.getRequestHeaders()) + request.getUrl().toString()).getBytes());
		return "." + String.valueOf(crc32.getValue());
	}

	private String getFileName(File dir, String url, String requestHashCode) {
		try {
			String[] list = dir.list();
			for (String fileName : list) {
				if (fileName.contains(url) && fileName.contains(requestHashCode)) {
					return fileName;
				}
			}
		} catch (Exception e) {
			Log.e("getFileName", e.toString());
		}
		return null;
	}

	private boolean isAlreadyDownload(String url, String saveDir) {
		try {
			File file = new File(saveDir);
			if (!file.exists()) {
				return false;
			}

			InputStream inputStream = new FileInputStream(file);
			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;

			while ((line = rd.readLine()) != null) {
				if (line.equals(url)) {
					return true;
				}
			}
		} catch (Exception e) {
			Log.e("isAlreadyDownload", e.toString());
		}
		return false;
	}
}