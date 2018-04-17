package co.blastlab.indoornavi_api.callback;

import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.web_view.IndoorWebViewClient;

/**
 * OnViewReadyCallback -  interface to observe when page has finished loading {@link IndoorWebViewClient} .
*/

public interface OnViewReadyCallback {
	void onWebViewReady(INMap inMap);
}
