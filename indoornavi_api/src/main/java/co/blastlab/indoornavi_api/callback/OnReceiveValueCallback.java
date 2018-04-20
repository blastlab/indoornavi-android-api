package co.blastlab.indoornavi_api.callback;

/**
 * A callback interface used to provide List of points asynchronously.
 */
public interface OnReceiveValueCallback<T> {
	/**
	 * Invoked when value is available.
	 * @param t Received value.
	 */
	void onReceiveValue(T t);
}
