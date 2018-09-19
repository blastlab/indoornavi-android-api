package co.blastlab.indoornavi_api.interfaces;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;
import co.blastlab.indoornavi_api.utils.ComplexUtils;

public class ComplexInterface {

	@JavascriptInterface
	public void onComplexes(final int eventId, final String response) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(() ->
			Controller.ReceiveValueMap.get(eventId).onReceiveValue(ComplexUtils.getComplexesFromJSON(response))
		);
	}
}
