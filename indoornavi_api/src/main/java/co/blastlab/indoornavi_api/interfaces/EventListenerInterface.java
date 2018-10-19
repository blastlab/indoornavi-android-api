package co.blastlab.indoornavi_api.interfaces;

import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.utils.EventsUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;

public class EventListenerInterface {

	@JavascriptInterface
	public void onEvent(final int eventId, final String event, final String response) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() ->
			Controller.eventListenerMap.get(eventId).onEvent(event.toLowerCase().equals("coordinates") ? EventsUtil.jsonEventToCoordinates(response) : EventsUtil.jsonEventToAreaEvent(response))
		);
	}

	@JavascriptInterface
	public void onClickEvent(final int eventId, final String response) {
		Point point = PointsUtil.stringToPoint(response);
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() -> {
				if (point != null) {
					Controller.eventListenerMap.get(eventId).onEvent(point);
				} else {
					Controller.eventListenerMap.get(eventId).onEvent(null);
				}
			}
		);
	}

	@JavascriptInterface
	public void onBleAreaEvent(final int eventId, final String event) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() ->
			Controller.eventListenerMap.get(eventId).onEvent(event)
		);
	}
}
