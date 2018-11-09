package co.blastlab.indoornavi_api.service;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;

import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import co.blastlab.indoornavi_api.algorithm.Algorithm;
import co.blastlab.indoornavi_api.algorithm.model.Anchor;
import co.blastlab.indoornavi_api.algorithm.model.Position;

public class BluetoothScanService extends Service {

	public static final String TAG = "IndoorBluetoothService";
	public static final String CALCULATE_POSITION = "calculated position";
	public static final String FLOOR_CHANGE = "floor change";
	public static final int ACTION_BLUETOOTH_READY = 0;
	public static final int ACTION_BLUETOOTH_NOT_SUPPORTED = 1;
	public static final int ACTION_BLUETOOTH_NOT_ENABLED = 2;
	public static final int ACTION_BLUETOOTH_PERMISSION_NOT_GRANTED = 3;
	public static final int ACTION_LOCATION_PERMISSION_NOT_GRANTED = 4;
	public static final int ACTION_LOCATION_NOT_ENABLED = 5;
	public static final int ACTION_LOCATION_STATUS_CHANGE = 6;
	public static final int ACTION_POSITION = 7;
	public static final int ACTION_NO_SCAN_RESULTS = 8;
	public static final int ACTION_FLOOR_ID_CHANGE = 9;
	public static final int ACTION_DEVICES_OUT_OF_RANGE = 10;
	public static boolean SERVICE_CONNECTED = false;

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeScanner btScanner;
	private ScanSettings settings;
	private ParcelUuid uuid;
	private Algorithm algorithm;

	private Handler mHandler;
	private Context context;
	private IBinder binder = (IBinder) new BluetoothBinder();
	private Timer timer = new Timer();

	private SparseArray<Anchor> anchorMatrix = new SparseArray<>();
	private SparseArray<Anchor> anchorConfiguration = new SparseArray<>();
	private List<Position> positionsArray = new ArrayList<>();

	private boolean localization = false;
	private boolean isFistPosition = true;
	private int maxDistance = 12;
	private int actualFloorId = -1;

	private class FloorPair {

		private int floorId;
		private int floorCounter;

		private FloorPair(int floorId, int floorCounter) {
			this.floorId = floorId;
			this.floorCounter = floorCounter;
		}

	}

	private FloorPair floorChangeCounter = new FloorPair(-1, -1);

	private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
					BluetoothAdapter.ERROR);
				switch (state) {
					case BluetoothAdapter.STATE_OFF:
						Log.i(TAG, "Bluetooth Receiver State OFF");
						if (mHandler != null) {
							mHandler.obtainMessage(ACTION_BLUETOOTH_NOT_ENABLED, null).sendToTarget();
						}
						break;
					case BluetoothAdapter.STATE_ON:
						if (localization)
							startLocalization();
						Log.i(TAG, "Bluetooth Receiver State ON");
						if (mHandler != null) {
							mHandler.obtainMessage(ACTION_BLUETOOTH_READY, null).sendToTarget();
						}
						break;
				}
			}
		}
	};

	private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action != null && action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
				if (mHandler != null) {
					mHandler.obtainMessage(ACTION_LOCATION_STATUS_CHANGE, null).sendToTarget();
				}

				int localizationPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

				if (localization && localizationPermission == PackageManager.PERMISSION_GRANTED) {
					startLocalization();
				}
			}
		}
	};

	private ScanCallback mLeScanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {

			int id = getAnchorIdfromScanResult(result);

			if (anchorConfiguration.indexOfKey(id) >= 0) {
				if (anchorMatrix.indexOfKey(id) < 0) {
					anchorMatrix.append(id, getAnchor(id));
					anchorMatrix.get(id).rssiRef = getAnchorTxPowerfromScanResult(result);
					anchorMatrix.get(id).rssi_array.add(result.getRssi());
				} else {
					anchorMatrix.get(id).rssi_array.add(result.getRssi());
				}
				if (isFistPosition) calculateFirstPosition();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Binding Service");
		if (localization) {
			startScanning();
		}
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "unbinding Service");
		if (localization) {
			stopScanning();
		}
		return BluetoothScanService.SERVICE_CONNECTED = true;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = this;
		BluetoothScanService.SERVICE_CONNECTED = true;
		registerBluetoothReceiver();
		registerLocationReceiver();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Stopping Service");
		BluetoothScanService.SERVICE_CONNECTED = false;
		stopScanning();
		unregisterBluetoothReceiver();
		unregisterLocationReceiver();

		clearAll();
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
		addDefaultConf();
	}

	public class BluetoothBinder extends Binder {
		public BluetoothScanService getService() {
			return BluetoothScanService.this;
		}
	}

	private void registerBluetoothReceiver() {
		Log.i(TAG, "Registering Bluetooth Receiver");
		IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

		this.registerReceiver(mBluetoothReceiver, intentFilter);
	}

	private void registerLocationReceiver() {
		Log.i(TAG, "Registering Location Receiver");
		IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);

		this.registerReceiver(mLocationReceiver, intentFilter);
	}

	private void unregisterBluetoothReceiver() {
		try {
			this.unregisterReceiver(mBluetoothReceiver);
			Log.i(TAG, "Bluetooth Receiver Unregistered Successfully");
		} catch (Exception e) {
			Log.i(TAG, "Bluetooth Receiver Already Unregistered. Exception : " + e.getLocalizedMessage());
		}
	}

	private void unregisterLocationReceiver() {
		try {
			this.unregisterReceiver(mLocationReceiver);
			Log.i(TAG, "Location Receiver Unregistered Successfully");
		} catch (Exception e) {
			Log.i(TAG, "Location Receiver Already Unregistered. Exception : " + e.getLocalizedMessage());
		}
	}

	private void checkSupport() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) && mHandler != null) {
			mHandler.obtainMessage(ACTION_BLUETOOTH_NOT_SUPPORTED, null).sendToTarget();
			onDestroy();
		}
	}

	private void checkBluetoothEnable() {

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled() && mHandler != null) {
			mHandler.obtainMessage(ACTION_BLUETOOTH_NOT_ENABLED, null).sendToTarget();
		}
	}

	private void checkLocalizationEnable() {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (mHandler != null && locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mHandler.obtainMessage(ACTION_LOCATION_NOT_ENABLED, null).sendToTarget();
		}
	}

	private void checkPermission() {

		if (mHandler != null) {
			int localizationPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

			if (localizationPermission != PackageManager.PERMISSION_GRANTED) {
				mHandler.obtainMessage(ACTION_LOCATION_PERMISSION_NOT_GRANTED, null).sendToTarget();
			}

			int permissionBluetooth = ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);

			if (permissionBluetooth != PackageManager.PERMISSION_GRANTED) {
				mHandler.obtainMessage(ACTION_BLUETOOTH_PERMISSION_NOT_GRANTED, null).sendToTarget();
			}
		}
	}

	private void DeviceAvailability() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(6000);
					if (getLastKnownPosition() == null && mHandler != null) {
						mHandler.obtainMessage(ACTION_NO_SCAN_RESULTS, null).sendToTarget();
						stopScanning();
						startScanning();
					}
				} catch (InterruptedException e) {
					Log.e("Indoor", "thread exception");
				}
			}
		};
		thread.start();
	}

	public void startLocalization() {

		if (!localization) {
			localization = true;
		}
		if (btScanner == null) {
			init();
		}
		startScanning();
	}

	public void stopLocalization() {

		if (localization) {
			stopScanning();
			localization = false;
		}
	}

	private void init() {

		checkSupport();
		checkPermission();
		checkBluetoothEnable();
		checkLocalizationEnable();

		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();

		btScanner = mBluetoothAdapter.getBluetoothLeScanner();
		algorithm = new Algorithm();

		uuid = new ParcelUuid(UUID.fromString("2a49e0a7-8ad8-479d-834d-dc2e407dfd30"));

		ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();
		settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
		if (Build.VERSION.SDK_INT >= 23) {
			settingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
		}
		settings = settingsBuilder.build();
	}


	private void startScanning() {
		if (btScanner != null) {
			DeviceAvailability();
			AsyncTask.execute(() ->
				btScanner.startScan(getScanFilters(), settings, mLeScanCallback)
			);
		}
	}

	private void stopScanning() {
		if (btScanner != null) {
			AsyncTask.execute(() ->
				btScanner.stopScan(mLeScanCallback)
			);
		}
	}

	@NonNull
	private List<ScanFilter> getScanFilters() {
		ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(uuid).build();
		List<ScanFilter> scanFilterList = new ArrayList<>();
		scanFilterList.add(scanFilter);
		return scanFilterList;
	}

	private void calculateFirstPosition() {
		isFistPosition = false;
		new Handler().postDelayed(() -> {

			Pair<Integer, Position> nextPosition = algorithm.getPosition(Algorithm.LocalizationMethod.CROSSING_CIRCLE, anchorMatrix, maxDistance);

			if (nextPosition != null) {
				Position position = nextPosition.second;
				checkSuggestedFloor(nextPosition.first);

				position.timestamp = new Date();
				positionsArray.add(position);
				sendPositionToActivity(position);
				executePeriodicTask();
			} else {
				isFistPosition = true;
			}
		}, 3000);
	}

	private void sendPositionToActivity(Position position) {
		if (mHandler != null) {
			mHandler.obtainMessage(ACTION_POSITION, new Point((int) Math.round(position.x * 100), (int) Math.round(position.y * 100))).sendToTarget();
			sendBroadcastPosition(position);
		}
	}

	private void executePeriodicTask() {
		final Handler handler = new Handler();

		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(() -> {
					try {
						getPosition();
						if (anchorMatrix.size() <= 0 && mHandler != null) {
							mHandler.obtainMessage(ACTION_DEVICES_OUT_OF_RANGE, null).sendToTarget();
						}
					} catch (Exception e) {
						Log.e("Localization exception", "Position computing failed: " + e);
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 1500, 1500);
	}

	private void getPosition() {
		Pair<Integer, Position> nextPosition = algorithm.getPosition(Algorithm.LocalizationMethod.CROSSING_CIRCLE, anchorMatrix, maxDistance);

		if (nextPosition != null) {
			Position point = nextPosition.second;
			checkSuggestedFloor(nextPosition.first);

			Position newPosition = algorithm.getIntersectionCircleLine(getLastKnownPosition(), point);
			newPosition.timestamp = new Date();
			positionsArray.add(newPosition);
			sendPositionToActivity(newPosition);
		}
	}

	private void checkSuggestedFloor(int mostCommonFloor) {

		if (mostCommonFloor != -1 && actualFloorId != mostCommonFloor) {
			if (!incrementCounter(mostCommonFloor)) return;

			actualFloorId = mostCommonFloor;
			if (mHandler != null) {
				mHandler.obtainMessage(ACTION_FLOOR_ID_CHANGE, actualFloorId).sendToTarget();
				sendBroadcastFloorChange(actualFloorId);
			}
		}
	}

	private boolean incrementCounter(int floorId) {
		if (floorChangeCounter.floorId == floorId) {
			floorChangeCounter.floorCounter += 1;
		} else {
			floorChangeCounter.floorId = floorId;
			floorChangeCounter.floorCounter = 1;
		}

		if (floorChangeCounter.floorCounter >= 3) {
			floorChangeCounter.floorCounter = -1;
			floorChangeCounter.floorId = -1;
			return true;
	}
	return false;

}

	private int getAnchorIdfromScanResult(ScanResult result) {
		byte[] byteArray = result.getScanRecord().getBytes();
		return ((byteArray[25] & 0xff) << 8) + (byteArray[26] & 0xff);
	}

	private int getAnchorTxPowerfromScanResult(ScanResult result) {
		byte[] byteArray = result.getScanRecord().getBytes();
		return byteArray[29];
	}

	private Position getLastKnownPosition() {
		return positionsArray.isEmpty() ? null : positionsArray.get(positionsArray.size() - 1);
	}

	private Anchor getAnchor(int id) {
		return anchorConfiguration.get(id);
	}

	private void clearAll() {
		anchorMatrix.clear();
		positionsArray.clear();

		isFistPosition = true;
		timer.cancel();
	}

	private void sendBroadcastPosition(Position position) {
		try {
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(CALCULATE_POSITION);
			broadCastIntent.putExtra("position", new Point((int) Math.round(position.x * 100), (int) Math.round(position.y * 100)));
			sendBroadcast(broadCastIntent);

		} catch (Exception e) {
			Log.e("SendBroadcast Exception", e.getMessage());
		}
	}

	private void sendBroadcastFloorChange(int floorId) {
		try {
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(FLOOR_CHANGE);
			broadCastIntent.putExtra("floorId", floorId);
			sendBroadcast(broadCastIntent);

		} catch (Exception e) {
			Log.e("SendBroadcast Exception", e.getMessage());
		}
	}

	private void addDefaultConf() {
		anchorConfiguration.append(65050, new Anchor(65050, new Position(32.12, 2.46, 3.00), 2));
		anchorConfiguration.append(65045, new Anchor(65045, new Position(36.81, 1.40, 3.00), 2));
		anchorConfiguration.append(65049, new Anchor(65049, new Position(32.20, 11.61, 3.00), 2));
		anchorConfiguration.append(65048, new Anchor(65048, new Position(37.49, 12.27, 3.00), 2));

		anchorConfiguration.append(65051, new Anchor(65051, new Position(24.60, 8.69, 3.00), 3));
		anchorConfiguration.append(65044, new Anchor(65044, new Position(24.45, 1.97, 3.00), 3));
		anchorConfiguration.append(65052, new Anchor(65052, new Position(29.91, 1.97, 3.00), 3));
		anchorConfiguration.append(65043, new Anchor(65043, new Position(29.91, 9.09, 3.00), 3));

		anchorConfiguration.append(65047, new Anchor(65047, new Position(34.61, 14.59, 3.00), 2));
		anchorConfiguration.append(65046, new Anchor(65046, new Position(24.34, 14.41, 3.00), 3));
	}
}
