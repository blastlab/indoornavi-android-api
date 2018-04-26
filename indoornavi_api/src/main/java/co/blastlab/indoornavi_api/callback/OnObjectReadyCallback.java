package co.blastlab.indoornavi_api.callback;
/**
 * A callback interface used to observe when object will be created.
 */
public interface OnObjectReadyCallback<T> {
	/**
	 * Invoked when object is ready.
	 * @param t Received value (could be null)
	 */
	void onReady(T t);
}
