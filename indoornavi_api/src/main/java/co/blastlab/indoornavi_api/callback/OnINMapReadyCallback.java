package co.blastlab.indoornavi_api.callback;

import co.blastlab.indoornavi_api.objects.INMap;
/**
 * A callback interface used to observe when page has finished loading.
 */
public interface OnINMapReadyCallback {
	/**
	 * Invoked when page has finished loading.
	 * @param inMap instance of INMap object
	 */
	void onINMapReady(INMap inMap);

	/**
	 * Invoked when occurs web resource loading error to the host application
	 * @param errorCode error code
	 * @param description error message
	 */
	void onReceivedError(int errorCode, String description);
}
