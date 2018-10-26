package co.blastlab.indoornavi_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.ValueCallback;

import java.util.Locale;

import co.blastlab.indoornavi_api.callback.OnNavigationMessageReceive;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.service.BluetoothScanService;
import co.blastlab.indoornavi_api.utils.MapUtil;

public class INNavigation {

	private  String objectInstance;
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
	private boolean navigationIsRunning = false;


	private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case BluetoothScanService.CALCULATE_POSITION:
					Point position = intent.getParcelableExtra("position");
					if(position != null) {
						updateActualLocation(position);
					}
					break;
			}
		}
	};

	public INNavigation(Context context, INMap inMap) {
		this.objectInstance = String.format(Locale.US, "navigation%d",this.hashCode());
		this.context = context;
		this.inMap = inMap;

		String javaScriptString = String.format("var %s = new INNavigation(navi);", objectInstance);
		evaluate(javaScriptString, null);
	}

	public void startNavigation(Point startPoint, Point destinationPoint, int accuracy) {
		this.startPoint = startPoint;
		this.destinationPoint = destinationPoint;

		this.startPointInPixels = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), startPoint);
		this.destinationPointInPixels = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), destinationPoint);
		this.accuracy = accuracy;

		registerReceiver();
		navigationIsRunning = true;

		String javaScriptString = String.format(Locale.ENGLISH, "%s.start({x: %d, y: %d}, {x: %d, y: %d}, %d);", objectInstance, this.startPointInPixels.x, this.startPointInPixels.y, this.destinationPointInPixels.x, this.destinationPointInPixels.y, accuracy);
		evaluate(javaScriptString, null);
	}

	public void addEventListener(OnNavigationMessageReceive<String> onNavigationMessageReceive) {
		this.onNavigationMessageReceive = onNavigationMessageReceive;

		int eventId = onNavigationMessageReceive.hashCode();
		Controller.navigationMessageMap.put(eventId, onNavigationMessageReceive);

		String javaScriptString = String.format(Locale.ENGLISH, "%s.addEventListener(action => inNavigationInterface.onMessageReceive(%d, JSON.stringify(action)));", objectInstance, eventId);
		evaluate(javaScriptString, null);
	}

	public void removeEventListener() {
		String javaScriptString = String.format(Locale.ENGLISH, "%s.removeEventListener();", objectInstance);
		evaluate(javaScriptString, null);
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
		navigationIsRunning = false;
	}

	public void restartNavigation() {
		if(this.lastPosition == null || this.destinationPoint == null) return;
		if(navigationIsRunning) stopNavigation();
		startNavigation(lastPosition, destinationPoint, accuracy);
	}

	private void registerReceiver() {
		try
		{
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothScanService.CALCULATE_POSITION);
			this.context.registerReceiver(serviceReceiver, intentFilter);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void unregisterReceiver() {
		try
		{
			if(serviceReceiver != null) {
				this.context.unregisterReceiver(serviceReceiver);
			}
		}
		catch (Exception ex)
		{
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
