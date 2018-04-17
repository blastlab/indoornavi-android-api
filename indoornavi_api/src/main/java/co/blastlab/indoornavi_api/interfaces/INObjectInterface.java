package co.blastlab.indoornavi_api.interfaces;

import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Constants;

public class INObjectInterface {

	@JavascriptInterface
	public void ready(int promiseId) {
		Constants.promiseMap.get(promiseId).resolve("done");
		Constants.promiseMap.remove(promiseId);
	}
}
