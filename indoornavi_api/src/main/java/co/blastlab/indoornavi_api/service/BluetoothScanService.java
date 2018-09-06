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
import co.blastlab.indoornavi_api.algorithm.model.Point;
import co.blastlab.indoornavi_api.algorithm.model.Position;

public class BluetoothScanService extends Service {

	public static final String TAG = "IndoorBluetoothService";
	public static final int ACTION_BLUETOOTH_READY = 0;
	public static final int ACTION_BLUETOOTH_NOT_SUPPORTED = 1;
	public static final int ACTION_BLUETOOTH_NOT_ENABLED = 2;
	public static final int ACTION_BLUETOOTH_PERMISSION_NOT_GRANTED = 3;
	public static final int ACTION_LOCATION_PERMISSION_NOT_GRANTED = 4;
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

			if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
					BluetoothAdapter.ERROR);
				switch (state) {
					case BluetoothAdapter.STATE_OFF:
						Log.d(TAG, "Bluetooth Receiver State OFF");
						Log.d(TAG, "Stopping Service");
						BluetoothScanService.this.stopSelf();
						break;
					case BluetoothAdapter.STATE_ON:
						if(localization)
							startLocalization();
						Log.d(TAG, "Bluetooth Receiver State ON");
						break;
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		BluetoothScanService.SERVICE_CONNECTED = true;
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = this;
		registerBluetoothReceiver();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		stopScanning();
		unregisterBluetoothReceiver();
		BluetoothScanService.SERVICE_CONNECTED = false;

		super.onDestroy();
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
		checkSupport();
		checkBluetoothPermission();
		checkBluetoothEnable();
	}

	public class BluetoothBinder extends Binder {
		public BluetoothScanService getService() {
			return BluetoothScanService.this;
		}
	}

	private void registerBluetoothReceiver() {
		Log.d(TAG, "Registering Bluetooth Receiver");
		IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

		this.registerReceiver(mBluetoothReceiver, intentFilter);
	}

	private void unregisterBluetoothReceiver() {
		try {
			this.unregisterReceiver(mBluetoothReceiver);
			Log.d(TAG, "Bluetooth Receiver Unregistered Successfully");
		} catch (Exception e) {
			Log.d(TAG,"Bluetooth Receiver Already Unregistered. Exception : " + e.getLocalizedMessage());
		}
	}

	private void checkSupport() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) && mHandler != null) {
			mHandler.obtainMessage(ACTION_BLUETOOTH_NOT_SUPPORTED, null).sendToTarget();
			onDestroy();
		}
	}

	private void checkBluetoothEnable() {
		if(btScanner != null && mHandler != null) {
			mHandler.obtainMessage(ACTION_BLUETOOTH_NOT_ENABLED, null).sendToTarget();
		}
	}

	private void checkBluetoothPermission() {

		if(mHandler != null) {
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

		init();
		startScanning();
		localization = true;
	}

	public void stopLocalization() {

		stopScanning();
		localization = false;
	}

	private void init(){
		if(btScanner == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = mBluetoothManager.getAdapter();

			btScanner = mBluetoothAdapter.getBluetoothLeScanner();
		}

		uuid = new ParcelUuid(UUID.fromString("2a49e0a7-8ad8-479d-834d-dc2e407dfd30"));

		settings = new ScanSettings.Builder()
			.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
			.build();
	}

	private void startScanning() {

		if(btScanner != null) {
			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG,"Start scanning");
					btScanner.startScan(getScanFilters(), settings, mLeScanCallback);
				}
			});
		}
	}

	private void stopScanning() {
		if(btScanner != null) {
			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG,"Stopping scanning");
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

			if(anchorConfiguration.indexOfKey(id) >= 0) {
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
			Point position = algorithm.getPosition(Algorithm.LocalizationMethod.CROSSING_CIRCLE, anchorMatrix, maxDistance);
				if(position != null) {
					positionsArray.add(new Position(position, new Date().getTime()));
					executePeriodicTask();
				} else {
					isFistPosition = true;
				}
		}, 3000);
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
						Log.e("Localization exception", "Position computing failed");
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 1500, 1500);
	}

	private void getPosition() {
		Point point = algorithm.getPosition(Algorithm.LocalizationMethod.CROSSING_CIRCLE, anchorMatrix, maxDistance);
		if(point != null) {
			positionsArray.add(new Position(algorithm.getIntersectionCircleLine(getLastKnownPosition(), point), new Date().getTime()));
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

	private Point getLastKnownPosition() {
		return positionsArray.isEmpty() ? null : positionsArray.get(positionsArray.size()-1).position;
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
}
