package co.blastlab.indoornavi_api;

import android.util.SparseArray;

import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;


/**
 * Class Controller contains singletons used in library.
 */
public final class Controller {

	private Controller() {
	}

	public static volatile SparseArray<OnMarkerClickListener> markerClickListenerMap = new SparseArray<>();
	public static volatile SparseArray<OnObjectReadyCallback> promiseCallbackMap = new SparseArray<>();
	public static volatile SparseArray<OnEventListener> eventListenerMap = new SparseArray<>();
}
