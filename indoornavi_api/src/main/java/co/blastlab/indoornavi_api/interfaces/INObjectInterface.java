package co.blastlab.indoornavi_api.interfaces;

import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Constants;

public class INObjectInterface {

	@JavascriptInterface
	public void ready() {
		Constants.promiseMap.get(1).resolve("done");
	}
}
