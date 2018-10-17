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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;

import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import co.blastlab.indoornavi_api.algorithm.Algorithm;
import co.blastlab.indoornavi_api.algorithm.model.Anchor;
import co.blastlab.indoornavi_api.algorithm.model.Position;

public class BluetoothScanService extends Service {

	public static final String TAG = "IndoorBluetoothService";
	public static final int ACTION_BLUETOOTH_READY = 0;
	public static final int ACTION_BLUETOOTH_NOT_SUPPORTED = 1;
	public static final int ACTION_BLUETOOTH_NOT_ENABLED = 2;
	public static final int ACTION_BLUETOOTH_PERMISSION_NOT_GRANTED = 3;
	public static final int ACTION_LOCATION_PERMISSION_NOT_GRANTED = 4;
	public static final int ACTION_LOCATION_NOT_ENABLED = 5;
	public static final int ACTION_POSITION = 6;
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

	private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Log.e("Indoor action", action);

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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Stopping Service");
		BluetoothScanService.SERVICE_CONNECTED = false;
		stopScanning();
		unregisterBluetoothReceiver();

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

	private void unregisterBluetoothReceiver() {
		try {
			this.unregisterReceiver(mBluetoothReceiver);
			Log.i(TAG, "Bluetooth Receiver Unregistered Successfully");
		} catch (Exception e) {
			Log.i(TAG, "Bluetooth Receiver Already Unregistered. Exception : " + e.getLocalizedMessage());
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

	private void checkBluetoothPermission() {

		if (mHandler != null) {
			int localizationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

			if (localizationPermission != PackageManager.PERMISSION_GRANTED) {
				mHandler.obtainMessage(ACTION_LOCATION_PERMISSION_NOT_GRANTED, null).sendToTarget();
			}

			int permissionBluetooth = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);

			if (permissionBluetooth != PackageManager.PERMISSION_GRANTED) {
				mHandler.obtainMessage(ACTION_BLUETOOTH_PERMISSION_NOT_GRANTED, null).sendToTarget();
			}
		}
	}

	public void startLocalization() {

		if (!localization || btScanner == null) {
			localization = true;
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
		checkBluetoothPermission();
		checkBluetoothEnable();
		checkLocalizationEnable();

		if (btScanner == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = mBluetoothManager.getAdapter();

			btScanner = mBluetoothAdapter.getBluetoothLeScanner();
			algorithm = new Algorithm();
		}

		uuid = new ParcelUuid(UUID.fromString("2a49e0a7-8ad8-479d-834d-dc2e407dfd30"));

		settings = new ScanSettings.Builder()
			.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
			.build();
	}

	private void startScanning() {

		if (btScanner != null) {
			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, "Start scanning");
					btScanner.startScan(getScanFilters(), settings, mLeScanCallback);
				}
			});
		}
	}

	private void stopScanning() {
		if (btScanner != null) {
			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, "Stopping scanning");
					btScanner.stopScan(mLeScanCallback);
				}
			});
		}
	}

	@NonNull
	private List<ScanFilter> getScanFilters() {
		ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(uuid).build();
		List<ScanFilter> scanFilterList = new ArrayList<>();
		scanFilterList.add(scanFilter);
		return scanFilterList;
	}

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

	private void calculateFirstPosition() {
		isFistPosition = false;
		final Handler handler = new Handler();
		handler.postDelayed(() -> {
			Position position = algorithm.getPosition(Algorithm.LocalizationMethod.CROSSING_CIRCLE, anchorMatrix, maxDistance);
			if (position != null) {
				position.timestamp = new Date();
				positionsArray.add(position);
				sendPositionToactivity(position);
				executePeriodicTask();
			} else {
				isFistPosition = true;
			}
		}, 3000);
	}

	private void sendPositionToactivity(Position position) {
		if (mHandler != null) {
			mHandler.obtainMessage(ACTION_POSITION, position).sendToTarget();
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
					} catch (Exception e) {
						Log.e("Localization exception", "Position computing failed: " + e);
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 1500, 1500);
	}

	private void getPosition() {
		Position point = algorithm.getPosition(Algorithm.LocalizationMethod.CROSSING_CIRCLE, anchorMatrix, maxDistance);
		if (point != null) {
			Position newPosition = algorithm.getIntersectionCircleLine(getLastKnownPosition(), point);
			newPosition.timestamp = new Date();
			positionsArray.add(newPosition);
			sendPositionToactivity(newPosition);
		}
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


	private void addDefaultConf() {
		anchorConfiguration.append(65022, new Anchor(65022, new Position(32.12, 2.46, 3.00)));
		anchorConfiguration.append(65023, new Anchor(65023, new Position(36.81, 1.40, 3.00)));
		anchorConfiguration.append(65024, new Anchor(65024, new Position(32.20, 11.61, 3.00)));
		anchorConfiguration.append(65025, new Anchor(65025, new Position(37.49, 12.27, 3.00)));

		anchorConfiguration.append(65026, new Anchor(65026, new Position(24.60, 8.69, 3.00)));
		anchorConfiguration.append(65027, new Anchor(65027, new Position(24.45, 1.97, 3.00)));
		anchorConfiguration.append(65028, new Anchor(65028, new Position(29.91, 1.97, 3.00)));
		anchorConfiguration.append(65029, new Anchor(65029, new Position(29.91, 9.09, 3.00)));

		anchorConfiguration.append(65030, new Anchor(65030, new Position(34.61, 14.59, 3.00)));
		anchorConfiguration.append(65031, new Anchor(65031, new Position(24.34, 14.41, 3.00)));
//		anchorConfiguration.append(65014, new Anchor(65014, new Position(32.12, 2.46, 3.00)));
//		anchorConfiguration.append(65008, new Anchor(65008, new Position(36.81, 1.40, 3.00)));
//		anchorConfiguration.append(65021, new Anchor(65021, new Position(32.20, 11.61, 3.00)));
//		anchorConfiguration.append(65007, new Anchor(65007, new Position(37.49, 12.27, 3.00)));
//		anchorConfiguration.append(65012, new Anchor(65012, new Position(24.45, 1.97, 3.00)));
//		anchorConfiguration.append(65018, new Anchor(65018, new Position(29.91, 1.94, 3.00)));
//		anchorConfiguration.append(65016, new Anchor(65016, new Position(24.60, 8.69, 3.00)));
//		anchorConfiguration.append(65019, new Anchor(65019, new Position(29.91, 9.09, 3.00)));
//		anchorConfiguration.append(65015, new Anchor(65015, new Position(34.61, 14.59, 3.00)));
//		anchorConfiguration.append(65011, new Anchor(65011, new Position(24.34, 14.41, 3.00)));
//		anchorConfiguration.append(65017, new Anchor(65017, new Position(16.82, 14.44, 3.00)));
//		anchorConfiguration.append(65020, new Anchor(65020, new Position(1.17, 17.42, 3.00)));
//		anchorConfiguration.append(65003, new Anchor(65003, new Position(7.60, 16.44, 3.00)));
//		anchorConfiguration.append(65006, new Anchor(65006, new Position(1.26, 22.88, 3.00)));
//		anchorConfiguration.append(65009, new Anchor(65009, new Position(7.00, 23.22, 3.00)));
	}
}
