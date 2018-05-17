package co.blastlab.indoornavi_api.callback;

import co.blastlab.indoornavi_api.objects.INMap;
/**
 * A callback interface used to observe when page has finished loading.
 */
public interface OnViewReadyCallback {
	/**
	 * Invoked when page has finished loading.
	 * @param inMap instance of INMap object
	 */
	void onWebViewReady(INMap inMap);
}
