package co.blastlab.indoornavi_api;

import java.util.HashMap;
import java.util.Map;

import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;


/**
 * Class Controller contains singletons used in library.
 */
public final class Controller {

	private Controller() {
	}

	public static volatile Map<Integer, OnMarkerClickListener> markerClickListenerMap = new HashMap<>();
	public static volatile Map<Integer, OnObjectReadyCallback> promiseCallbackMap = new HashMap<>();
	public static volatile Map<Integer, OnEventListener> eventListenerMap = new HashMap<>();
}
