package co.blastlab.indoornavi_api;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;

/**
 * Class Controller contains singletons used in library.
 */
public final class Controller {

	private Controller() {
	}

	public static volatile SparseArray<OnMarkerClickListener> markerClickListenerMap = new SparseArray<>();
	public static volatile SparseArray<OnObjectReadyCallback> promiseCallbackMap = new SparseArray<>();
	public static volatile SparseArray<OnReceiveValueCallback> ReceiveValueMap = new SparseArray<>();
	public static volatile SparseArray<OnEventListener> eventListenerMap = new SparseArray<>();
	public static volatile List<OnObjectReadyCallback> promiseMapReady = new ArrayList<>();
}
