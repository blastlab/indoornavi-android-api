package co.blastlab.indoornavi_api.web_view;

import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class IndoorWebChromeClient extends WebChromeClient {

	@Override
	public boolean onJsAlert(WebView view, String url, String message,
	                         final JsResult result){
		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result){
		return true;
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
	                        String defaultValue, final JsPromptResult result){
		return true;
	}

}