package co.blastlab.indoornavi_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;

import java.util.Locale;

import co.blastlab.indoornavi_api.callback.OnNavigationMessageReceive;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.service.BluetoothScanService;

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
		inMap.evaluateJavascript(javaScriptString, null);
	}

	public void startNavigation(Point startPoint, Point destinationPoint, int accuracy, OnNavigationMessageReceive<String> onNavigationMessageReceive) {
		this.startPoint = startPoint;
		this.destinationPoint = destinationPoint;
		this.accuracy = accuracy;
		this.onNavigationMessageReceive = onNavigationMessageReceive;

		registerReceiver();

		int eventId = onNavigationMessageReceive.hashCode();
		Controller.navigationMessageMap.put(eventId, onNavigationMessageReceive);

		String javaScriptString = String.format(Locale.ENGLISH, "%s.start({x: %d, y: %d}, {x: %d, y: %d}, %d, action => inNavigationInterface.onMessageReceive(%d, JSON.stringify(action)));", objectInstance, startPoint.x, startPoint.y, destinationPoint.x, destinationPoint.y, accuracy, eventId);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	private void updateActualLocation(Point position) {
		lastPosition = position;
		String javaScriptString = String.format(Locale.ENGLISH, "%s.updatePosition({x: %d, y: %d});", objectInstance, position.x, position.y);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	public void stopNavigation() {
		this.context.unregisterReceiver(serviceReceiver);
		Controller.navigationMessageMap.remove(onNavigationMessageReceive.hashCode());

		String javaScriptString = String.format(Locale.ENGLISH, "%s.stop();", objectInstance);
		inMap.evaluateJavascript(javaScriptString, null);
	}

	public void restertNavigation() {
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
}
