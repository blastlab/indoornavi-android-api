# __Localization__

One of the functionality of the library is the possibility of user location via Bluetooth. 
This allows display lockaly current position of the phone and allows the recording of definite 
coordinates to the database for later analysis of the history coordinates.

## __Service__

To ensure a continuous location without affecting the operation of the main application,
a service was created, which is responsible for handling events related to the location determination.

## __Usage__

### __AndroidManifest__

Is necessary to add Bluetooth permissions to your AndroidManifest file.
```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
```


Adding service.
```xml
<application>
	<service
		android:name="co.blastlab.indoornavi_api.service.BluetoothScanService"
		android:enabled="true">
	</service>
</application>
```

### __Handle response from Service__

This is an example of Activity with `BluetoothScanService` with proper handling.

```java
public class MainActivity extends AppCompatActivity {

private BluetoothScanService bluetoothScanService;
	
private MyHandler mHandler;
	private final ServiceConnection bluetoothConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			bluetoothScanService = ((BluetoothScanService.BluetoothBinder) arg1).getService();
			bluetoothScanService.setHandler(mHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bluetoothScanService = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startService(BluetoothScanService.class, bluetoothConnection);
	}
	
	private void startService(Class<?> service, ServiceConnection serviceConnection) {
		if (!BluetoothScanService.SERVICE_CONNECTED) {
			Intent bindingIntent = new Intent(getApplicationContext(), service);
			getApplicationContext().bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
	}
	
	public void startLocalization() {
		bluetoothScanService.startLocalization();
	}
	
	public void stopLocalization() {
		bluetoothScanService.stopLocalization();
	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<MainActivity> mActivity;

		public MyHandler(MainActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case BluetoothScanService.ACTION_BLUETOOTH_READY:
					Log.e(BluetoothScanService.TAG, "Bluetooth Ready");
					break;
				case BluetoothScanService.ACTION_BLUETOOTH_NOT_SUPPORTED:
					Log.e(BluetoothScanService.TAG, "Bluetooth not supported");
					break;
				case BluetoothScanService.ACTION_BLUETOOTH_NOT_ENABLED:
					Log.e(BluetoothScanService.TAG, "Bluetooth not enable");
					mActivity.get().enableBluetooth();
					break;
				case BluetoothScanService.ACTION_LOCATION_NOT_ENABLED:
					Log.e(BluetoothScanService.TAG, "Location not enable");
					break;
				case BluetoothScanService.ACTION_BLUETOOTH_PERMISSION_NOT_GRANTED:
					Log.e(BluetoothScanService.TAG, "Bluetooth Permission not granted");
					break;
				case BluetoothScanService.ACTION_LOCATION_PERMISSION_NOT_GRANTED:
					Log.e(BluetoothScanService.TAG, "Location Permission not granted");
					break;
				case BluetoothScanService.ACTION_LOCATION_STATUS_CHANGE:
					Log.e(BluetoothScanService.TAG, "Location status change");
					break;
				case BluetoothScanService.ACTION_POSITION:
					Position position = (Position) msg.obj;
					Log.e(BluetoothScanService.TAG, "Position: x:" + position.x + ", y: " + position.y);
					break;
			}
		}
	}
}
```

