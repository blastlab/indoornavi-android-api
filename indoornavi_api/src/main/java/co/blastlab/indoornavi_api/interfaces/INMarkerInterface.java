package co.blastlab.indoornavi_api.interfaces;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;

public class INMarkerInterface {

	@JavascriptInterface
	public void onClick(final int callbackId) {
		Controller.markerClickListenerMap.get(callbackId).onClick();
	}
}
