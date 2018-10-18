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
	private OnNavigationMessageReceive<String> onNavigationMessageReceive;

	private Point startPoint;
	private Point destinationPoint;
	private int accuracy = 1;


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

	public void startNavigation(Point startPoint, Point destinationPoint, int accuracy, OnNavigationMessageReceive<String> onNavigationMessageReceive) {
		this.startPoint = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), startPoint);
		this.destinationPoint = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), destinationPoint);
		this.accuracy = accuracy;
		this.onNavigationMessageReceive = onNavigationMessageReceive;

		registerReceiver();

		int eventId = onNavigationMessageReceive.hashCode();
		Controller.navigationMessageMap.put(eventId, onNavigationMessageReceive);

		String javaScriptString = String.format(Locale.ENGLISH, "%s.start({x: %d, y: %d}, {x: %d, y: %d}, %d, action => inNavigationInterface.onMessageReceive(%d, JSON.stringify(action)));", objectInstance, this.startPoint.x, this.startPoint.y, this.destinationPoint.x, this.destinationPoint.y, accuracy, eventId);
		evaluate(javaScriptString, null);
	}

	private void updateActualLocation(Point position) {
		lastPosition = MapUtil.realDimensionsToPixels(this.inMap.getMapScale(), position);
		String javaScriptString = String.format(Locale.ENGLISH, "%s.updatePosition({x: %d, y: %d});", objectInstance, lastPosition.x, lastPosition.y);
		evaluate(javaScriptString, null);
	}

	public void stopNavigation() {
		Controller.navigationMessageMap.remove(onNavigationMessageReceive.hashCode());

		String javaScriptString = String.format(Locale.ENGLISH, "%s.stop();", objectInstance);
		evaluate(javaScriptString, null);
		unregisterReceiver();
	}

	public void restartNavigation() {
		if(this.lastPosition == null || this.destinationPoint == null)
		stopNavigation();
		startNavigation(lastPosition, destinationPoint, accuracy, onNavigationMessageReceive);
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
