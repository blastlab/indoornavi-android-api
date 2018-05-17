package co.blastlab.indoornavi_api.interfaces;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import co.blastlab.indoornavi_api.Controller;

public class INObjectInterface {

	@JavascriptInterface
	public void ready(final int promiseId) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Controller.promiseCallbackMap.get(promiseId).onReady(null);
				Controller.promiseCallbackMap.remove(promiseId);
			}
		});
	}
}
