package co.blastlab.indoornavi_api.interfaces;

import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;

public class INObjectInterface {

	@JavascriptInterface
	public void ready(final int promiseId) {
		Controller.promiseCallbackMap.get(promiseId).onReady(null);
		Controller.promiseCallbackMap.remove(promiseId);
	}
}
