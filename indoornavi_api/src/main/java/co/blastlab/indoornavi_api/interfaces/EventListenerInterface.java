package co.blastlab.indoornavi_api.interfaces;

import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class EventListenerInterface {

	@JavascriptInterface
	public void onEvent(final int eventId, final String event, final String response) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() ->
			Controller.eventListenerMap.get(eventId).onEvent(event.equals("coordinates") ? ReportUtil.jsonToCoordinates(response) : ReportUtil.jsonToAreaEvent(response))
		);
	}

	@JavascriptInterface
	public void onClickEvent(final int eventId, final String response) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() ->
			Controller.eventListenerMap.get(eventId).onEvent(response)
		);
	}
}
