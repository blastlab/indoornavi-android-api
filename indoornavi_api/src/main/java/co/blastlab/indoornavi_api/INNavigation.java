package co.blastlab.indoornavi_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ValueCallback;

import org.json.JSONObject;

import java.util.Locale;

import co.blastlab.indoornavi_api.callback.OnNavigationMessageReceive;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.service.BluetoothScanService;
import co.blastlab.indoornavi_api.utils.MapUtil;

public class INNavigation {

	private String objectInstance;
	private Context context;
	private INMap inMap;
	private Point lastPosition;
	private Point lastPositionInPixel;
	private OnNavigationMessageReceive<String> onNavigationMessageReceive;

	private Point startPoint;
	private Point destinationPoint;
	private Point startPointInPixels;
	private Point destinationPointInPixels;
	private int accuracy = 1;
	private int pathLength = -1;

	private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case BluetoothScanService.CALCULATE_POSITION:
					Point position = intent.getParcelableExtra("position");
					if (position != null) {
						updateActualLocation(position);
					}
					break;
			}
		}
	};

	public INNavigation(Context context, INMap inMap) {
		this.objectInstance = String.format(Locale.US, "navigation%d", this.hashCode());
		this.context = context;
		this.inMap = inMap;

		String javaScriptString = String.format("var %s = new INNavigation(navi);", objectInstance);
		evaluate(javaScriptString, null);
	}

	public void startNavigation(Point startPoint, Point destinationPoint, int accuracy, OnNavigationMessageReceive<String> onNavigationMessageReceive) {
		this.startPoint = startPoint;
		this.destinationPoint = destinationPoint;

		this.startPointInPixels = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), startPoint);
		this.destinationPointInPixels = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), destinationPoint);
		this.accuracy = accuracy;
		this.onNavigationMessageReceive = onNavigationMessageReceive;

		registerReceiver();

		OnNavigationMessageReceive<String> innerNavigationMessageReceive = new OnNavigationMessageReceive<String>() {
			@Override
			public void onMessageReceive(String message) {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(() -> {

					try {
						String action = new JSONObject(message).getString("action");
						if (action.equals("created")) {
							pathLength = new JSONObject(message).getInt("pathLength");
						}
						onNavigationMessageReceive.onMessageReceive(action);
					} catch (Exception e) {
						Log.e("Exception ", "(" + Thread.currentThread().getStackTrace()[4].getFileName() + ":" + Thread.currentThread().getStackTrace()[4].getLineNumber() + "): Invalid message content");
					}
				});
			}
		};

		int eventId = innerNavigationMessageReceive.hashCode();
		Controller.navigationMessageMap.put(eventId, innerNavigationMessageReceive);

		String javaScriptString = String.format(Locale.ENGLISH, "%s.start({x: %d, y: %d}, {x: %d, y: %d}, %d, action => {inNavigationInterface.onMessageReceive(%d, JSON.stringify(action)); });", objectInstance, this.startPointInPixels.x, this.startPointInPixels.y, this.destinationPointInPixels.x, this.destinationPointInPixels.y, accuracy, eventId);
		evaluate(javaScriptString, null);
	}

	public int getPathLength() {
		return this.pathLength;
	}

	private void updateActualLocation(Point position) {
		lastPosition = position;
		lastPositionInPixel = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), position);
		String javaScriptString = String.format(Locale.ENGLISH, "%s.updatePosition({x: %d, y: %d});", objectInstance, lastPositionInPixel.x, lastPositionInPixel.y);
		evaluate(javaScriptString, null);
	}

	public void stopNavigation() {
		Controller.navigationMessageMap.remove(onNavigationMessageReceive.hashCode());

		String javaScriptString = String.format(Locale.ENGLISH, "%s.stop();", objectInstance);
		evaluate(javaScriptString, null);
		unregisterReceiver();
	}

	public void restartNavigation() {
		if (this.lastPosition == null || this.destinationPoint == null) return;
		stopNavigation();
		startNavigation(lastPosition, destinationPoint, accuracy, onNavigationMessageReceive);
	}

	private void registerReceiver() {
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothScanService.CALCULATE_POSITION);
			this.context.registerReceiver(serviceReceiver, intentFilter);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void unregisterReceiver() {
		try {
			if (serviceReceiver != null) {
				this.context.unregisterReceiver(serviceReceiver);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void evaluate(String javaScriptString, ValueCallback<String> valueCallback) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			inMap.evaluateJavascript(javaScriptString, valueCallback);
		} else {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(() -> {
				inMap.evaluateJavascript(javaScriptString, valueCallback);
			});

		}
	}
}
