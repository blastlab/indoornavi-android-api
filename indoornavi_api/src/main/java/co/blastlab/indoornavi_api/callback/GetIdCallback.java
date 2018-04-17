package co.blastlab.indoornavi_api.callback;
/**
 * A callback interface used to provide object ID asynchronously.
 */
public interface GetIdCallback {
	/**
	 * Invoked when the object's id is available.
	 * @param id The object's id.
	 */
	void onReceiveId(Integer id);
}
