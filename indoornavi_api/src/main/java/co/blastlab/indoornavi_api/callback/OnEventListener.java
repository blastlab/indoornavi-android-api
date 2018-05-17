package co.blastlab.indoornavi_api.callback;

/**
 * Interface definition for a callback to be invoked when an event occurs.
 */
public interface OnEventListener<T> {
	/**
	 * Called when the specific event occurs.
	 *
	 * @param t Received value. Value depends on the type of event (List of AreaEvent or List of Coordinates).
	 */
	void onEvent(T t);
}
