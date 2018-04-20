package co.blastlab.indoornavi_api;

import org.jdeferred.Deferred;

import java.util.HashMap;
import java.util.Map;

import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;

public final class Controller {

	private Controller() {
	}

	public static volatile Map<Integer, OnMarkerClickListener> markerClickListenerMap = new HashMap<>();
	public static volatile Map<Integer, Deferred> promiseMap = new HashMap<>();
}
