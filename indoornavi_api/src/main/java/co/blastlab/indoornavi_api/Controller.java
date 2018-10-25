package co.blastlab.indoornavi_api;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnINObjectClickListener;
import co.blastlab.indoornavi_api.callback.OnNavigationMessageReceive;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;

/**
 * Class Controller contains singletons used in library.
 */
public final class Controller {

	private Controller() {
	}

	public static volatile SparseArray<OnNavigationMessageReceive> navigationMessageMap = new SparseArray<>();
	public static volatile SparseArray<OnINObjectClickListener> inObjectClickListenerMap = new SparseArray<>();
	public static volatile SparseArray<OnObjectReadyCallback> promiseCallbackMap = new SparseArray<>();
	public static volatile SparseArray<OnReceiveValueCallback> ReceiveValueMap = new SparseArray<>();
	public static volatile SparseArray<OnEventListener> eventListenerMap = new SparseArray<>();
	public static volatile List<OnObjectReadyCallback> promiseMapReady = new ArrayList<>();
}
