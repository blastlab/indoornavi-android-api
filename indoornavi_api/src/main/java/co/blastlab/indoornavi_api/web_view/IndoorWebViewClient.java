package co.blastlab.indoornavi_api.web_view;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.callback.OnINMapReadyCallback;

public class IndoorWebViewClient extends WebViewClient {

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
		if (url.contains("Navi.js")) {
			try {
				AssetManager assets = view.getContext().getAssets();
				Uri uri = Uri.parse(url);
				InputStream stream = assets.open(uri.getPath(), AssetManager.ACCESS_STREAMING);
				WebResourceResponse response = new WebResourceResponse("text/html", "UTF-8", stream);
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
		Log.e("shouldInterceptRequest", request.getUrl().toString());

		String currentUrl = request.getUrl().toString();
		String saveDir = view.getContext().getFilesDir().getAbsolutePath();

		CRC32 crc32 = new CRC32();
		crc32.update((HttpDownloadUtility.headerMapToString(request.getRequestHeaders()) + currentUrl).getBytes());
		String requestHashCode = "." +String.valueOf(crc32.getValue());

		if (currentUrl == null || currentUrl.equals("about:blank") || currentUrl.contains("localhost")) {
			return super.shouldInterceptRequest(view, request);
		}

		if (isAlreadyDownload(currentUrl + requestHashCode, saveDir + "/urlConfig.txt")) {
			String fileUrl = getFileName(view.getContext().getFilesDir(), currentUrl.substring(currentUrl.lastIndexOf("/") + 1, currentUrl.length()), requestHashCode);
			try {
				File file = new File(saveDir + File.separator + fileUrl);
				InputStream fileInputStream = new FileInputStream(file);

				String mimeType = HttpDownloadUtility.getMimeType(fileUrl);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					int statusCode = 200;
					String reasonPhase = "OK";

					Map<String, String> responseHeaders = HttpDownloadUtility.getHeaderFile(saveDir, currentUrl + requestHashCode);

					if (responseHeaders == null) {
						responseHeaders = new HashMap<>();
						responseHeaders.put("Access-Control-Allow-Origin", "*");
						responseHeaders.put("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
						responseHeaders.put("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
					}

					return new WebResourceResponse(mimeType, "UTF-8", statusCode, reasonPhase, responseHeaders, fileInputStream);
				}
				return new WebResourceResponse(mimeType, "UTF-8", fileInputStream);

			} catch (IOException e) {
				Log.e("LocalResourceException", e.toString());
			}
		}
		ResourcesModule resourcesModule = new ResourcesModule(view.getContext(), request.getRequestHeaders(), requestHashCode);
		resourcesModule.execute(currentUrl);

		return super.shouldInterceptRequest(view, request);
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