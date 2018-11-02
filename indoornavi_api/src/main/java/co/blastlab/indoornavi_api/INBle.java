package co.blastlab.indoornavi_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.util.Log;

import java.util.Locale;

import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.service.BluetoothScanService;
import co.blastlab.indoornavi_api.utils.MapUtil;

/**
 * Class responsible for Bluetooth module, allows to listen to bluetooth events.
 */
public class INBle {

	private int floorId;
	private INMap inMap;
	private String targetHost;
	private String objectInstance;
	private boolean pulledPositionFlag = false;

	/**
	 * INBle object constructor.
	 *
	 * @param inMap INMap instance
	 * @param targetHost target host address
	 * @param floorId id of the floor on which module should listen for events
	 */
	public INBle(INMap inMap, String targetHost, int floorId) {
		this.objectInstance = String.format(Locale.US, "ble%d", this.hashCode());
		this.inMap = inMap;
		this.targetHost = targetHost;
		this.floorId = floorId;

		String javaScriptString = String.format(Locale.ENGLISH, "var %s = new INBle(%d, '%s', '%s');", objectInstance, this.floorId, this.targetHost, inMap.getApiKey());
		inMap.evaluateJavascript(javaScriptString, null);
	}

	/**
	 * Register a callback to be invoked when bluetooth coordinates are in the area created on frontend server.
	 *
	 * @param onEventListener interface - invoked when event occurs.
	 */
	public void addAreaEventListener(OnEventListener<AreaEvent> onEventListener) {

		int promiseId = onEventListener.hashCode();
		Controller.eventListenerMap.put(promiseId, onEventListener);

		String javaScriptString = String.format(Locale.US, "%s.addCallbackFunction(areaEvent => eventInterface.onBleAreaEvent(%d, JSON.stringify(areaEvent)));", objectInstance, promiseId);
		inMap.evaluateJavascript(javaScriptString, null);

		registerReceiver();
	}

	private void updatePosition(Point position) {

		Point positionInPixels = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), position);

		String javaScriptString = String.format(Locale.US, "%s.updatePosition({x: %d, y: %d});", objectInstance, positionInPixels.x, positionInPixels.y);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case BluetoothScanService.CALCULATE_POSITION:
					Point position = intent.getParcelableExtra("position");
					if (position != null) {
						if(pulledPositionFlag) {
							inMap.pullToPath(position, 0, new OnReceiveValueCallback<Point>() {
								@Override
								public void onReceiveValue(Point point) {
									updatePosition(point);
								}
							});
						} else {
							updatePosition(position);
						}
					}
					break;
			}
		}
	};

	/**
	 * Set flag which determines whether events should to be generated for a pulled or an absolute position
	 *
	 * @param pulledPositionFlag boolean value indicates whether it should be active.
	 */
	public void setPulledPositionFlag(boolean pulledPositionFlag) {
		this.pulledPositionFlag = pulledPositionFlag;
	}


	private void registerReceiver() {
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothScanService.CALCULATE_POSITION);
			this.inMap.getContext().registerReceiver(serviceReceiver, intentFilter);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
