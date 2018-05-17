package co.blastlab.indoornavi_api.interfaces;

import android.os.Handler;
import android.os.Looper;

import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class EventListenerInterface {

	@JavascriptInterface
	public void onEvent(final int eventId, final String event, final String response) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Controller.eventListenerMap.get(eventId).onEvent(event.equals("coordinates") ? ReportUtil.jsonToCoordinates(response) : ReportUtil.jsonToAreaEvent(response));
			}
		});
	}
}
