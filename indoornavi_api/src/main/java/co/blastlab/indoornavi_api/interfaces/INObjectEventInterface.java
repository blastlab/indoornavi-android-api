package co.blastlab.indoornavi_api.interfaces;

import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;

public class INObjectEventInterface {

	@JavascriptInterface
	public void onClick(final int callbackId) {
		Controller.inObjectClickListenerMap.get(callbackId).onClick();
	}
}
