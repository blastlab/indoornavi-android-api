package co.blastlab.indoornavi_api.interfaces;

import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;

public class INObjectInterface {

	@JavascriptInterface
	public void ready(int promiseId) {
		Controller.promiseMap.get(promiseId).resolve(null);
		Controller.promiseMap.remove(promiseId);
	}
}
