package co.blastlab.indoornavi_android;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.blastlab.indoornavi_api.INData;
import co.blastlab.indoornavi_api.INReport;
import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnINMapReadyCallback;
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INInfoWindow;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INMarker;
import co.blastlab.indoornavi_api.objects.INPolyline;
import co.blastlab.indoornavi_api.service.BluetoothScanService;
import co.blastlab.indoornavi_api.utils.MapUtil;
import co.blastlab.indoornavi_api.utils.PointsUtil;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class MainActivity extends AppCompatActivity implements OnINMapReadyCallback {

	private INMap inMap;
	private INPolyline inPolyline;
	private INArea inArea;
	private INMarker inMarker1;
	private INInfoWindow inInfoWindow;
	private INReport INReport;
	private BluetoothScanService bluetoothScanService;

	private int floorId = 2;
	private String frontendServer = "http://192.168.1.29:4200";
	private String backendServer = "http://192.168.1.29:90";
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static final int REQUEST_INTERNET= 1;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_ENABLE_LOCATION = 1;
	private static String[] PERMISSIONS= {
		Manifest.permission.INTERNET,
		Manifest.permission.ACCESS_COARSE_LOCATION,
		Manifest.permission.WRITE_EXTERNAL_STORAGE,
		Manifest.permission.BLUETOOTH,
	};

	List<Point> points_office_1  = new ArrayList<>();
	List<Point> points_office_2  = new ArrayList<>();
	List<Point> points_office_3  = new ArrayList<>();

	DrawerLayout mDrawerLayout;
	Vibrator vibrator;
	ExpandableListAdapter mMenuAdapter;
	ExpandableListView expandableList;
	List<ExpandedMenuModel> listDataHeader;
	HashMap<ExpandedMenuModel, List<String>> listDataChild;

	private MyHandler mHandler;
	private final ServiceConnection bluetoothConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Log.d("Indoor","Service connected");
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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		verifyInternetPermissions();
		mHandler = new MyHandler(this);

		inMap = (INMap) findViewById(R.id.webview);
		mDrawerLayout = findViewById(R.id.drawer_layout);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);


		NavigationView navigationView = findViewById(R.id.nav_view);
		if (navigationView != null) {
			setupDrawerContent(navigationView);
		}

		prepareListData();
		mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);
		expandableList.setAdapter(mMenuAdapter);

		setNavigationViewListener();
	}

	public void createPointsArrayForOffice1() {
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1347, 479)));
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1347, 27)));
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1088,27)));
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1088,292)));
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1082,292)));
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1082,417)));
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1187,417)));
		points_office_1.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1187,479)));
	}

	public void createPointsArrayForOffice2() {
		points_office_2.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1070, 349)));
		points_office_2.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1070, 290)));
		points_office_2.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1082,290)));
		points_office_2.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(1082,29)));
		points_office_2.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(818,30)));
		points_office_2.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(818,349)));
	}

	public void createPointsArrayForOffice3() {
		points_office_3.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(288, 560)));
		points_office_3.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(288, 835)));
		points_office_3.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(15,835)));
		points_office_3.add(MapUtil.pixelsToRealDimensions(inMap.scale, new Point(15,560)));
	}

	public void verifyStoragePermissions() {
		int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_EXTERNAL_STORAGE);
		}
	}

	public void
	verifyInternetPermissions() {
		int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_INTERNET);
		}
	}

	public void verifyBluetoothPermissions() {
		int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ENABLE_BT);
		}

		int localizationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

		if (localizationPermission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ENABLE_LOCATION);
		}
	}

	public void onINMapReady(INMap mapView) {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		inMap.createMap(frontendServer, "TestAdmin", metrics.widthPixels-250, metrics.heightPixels-200);
		inMap.load(floorId, new OnObjectReadyCallback() {
			@Override
			public void onReady(Object o) {
			}
		});

		inMap.addLongClickListener(new OnEventListener<Point>() {
			@Override
			public void onEvent(Point point) {
				vibrator.vibrate(500);
				new INMarker.INMarkerBuilder(inMap)
					.point(MapUtil.pixelsToRealDimensions(inMap.scale, point))
					.setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
					.build();
			}
		});

		inMap.waitUntilMapReady(data -> createPointsArrayForOffice1());
		inMap.waitUntilMapReady(data -> createPointsArrayForOffice2());
		inMap.waitUntilMapReady(data -> createPointsArrayForOffice3());

		inMap.waitUntilMapReady(data -> addAreaListeners());
	}

	public void addAreaListeners() {
		inMap.addEventListener(INMap.AREA, new OnEventListener<AreaEvent>() {
			@Override
			public void onEvent(AreaEvent areaEvent) {
				String msg = areaEvent.mode.equals("ON_ENTER") ? "You entered the area!" : "You left the area!";
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				showArea(areaEvent);
			}
		});
	}

	private void showArea(AreaEvent areaEvent) {

		if(areaEvent.mode.equals("ON_ENTER")) {
			int index;
			switch (areaEvent.areaName) {
				case "Office 1":
					index = 0;
					break;
				case "Office 2":
					index = 1;
					break;
				default:
					index = 2;
					break;
			}
			inArea = new INArea.INAreaBuilder(inMap)
				.points(getPointsSetByIndex(index))
				.setFillColor(Color.GREEN)
				.setOpacity(0.3)
				.build();
		} else {
			inArea.remove();
		}


	}

	public void drawPoly(int index)
	{
		inPolyline = new INPolyline.INPolylineBuilder(inMap)
			.points(getPointsSetByIndex(index))
			.setLineColor(Color.RED)
			.build();

		if(inPolyline!= null) {
			inPolyline.getID(id -> {
				Log.i("Indoor", "onReceiveValue: " + id);
			});
			inPolyline.getPoints(points -> Log.i("Indoor", "onReceiveValue: " + PointsUtil.pointsToString(points)));
		}
	}

	public void drawArea(int index){
		inArea = new INArea.INAreaBuilder(inMap)
			.points(getPointsSetByIndex(index))
			.setFillColor(Color.GREEN)
			.setOpacity(0.3)
			.build();

		if(inArea!= null) {
			inArea.isWithin(new Coordinates(200, 800, (short) 109999, new Date()), bool -> Log.i("Indoor", "Received value: " + bool));
		}
	}

	public List<Point> getPointsSetByIndex(int index) {
		switch (index) {
			case 0:
				return points_office_1;
			case 1:
				return  points_office_2;
			case 2:
				return points_office_3;
		}
		return points_office_1;
	}

	public void drawMarker(int index){
		inMarker1 = new INMarker.INMarkerBuilder(inMap)
			.point(getMarkerPoint(index))
			.setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
			.setLabel(getMarkerLabel(index))
			.build();

		if(inMarker1!= null) {
			drawInfoWindow();

			inMarker1.addEventListener(new OnMarkerClickListener() {
				@Override
				public void onClick() {
					show_toast();
				}
			});
		}
	}

	public String getMarkerLabel(int index) {
		switch(index) {
			case 0:
				return "Office 1";
			case 1:
				return "Office 2";
			case 2:
				return "Office 3";
		}
		return "";
	}

	public Point getMarkerPoint(int index) {
		switch(index) {
			case 0:
				return MapUtil.pixelsToRealDimensions(inMap.scale,new Point(1200, 227));
			case 1:
				return MapUtil.pixelsToRealDimensions(inMap.scale,new Point(946, 190));
			case 2:
				return MapUtil.pixelsToRealDimensions(inMap.scale,new Point(150, 705));
		}
		return new Point(600, 600);
	}

	public void drawInfoWindow(){
		inInfoWindow = new INInfoWindow.INInfoWindowBuilder(inMap)
			.height(150)
			.width(150)
			.setInnerHTML("<h3>Lorem ipsum dolor sit amet</h3>")
			.setPosition(INInfoWindow.TOP)
			.build();
	}

	public void show_toast()
	{
		Toast.makeText(this, "This is my toast",
			Toast.LENGTH_LONG).show();
		inMarker1.addInfoWindow(inInfoWindow);
	}

	public void createReport(int index) {
		verifyStoragePermissions();

		INReport = new INReport(inMap, backendServer, "TestAdmin");

		switch(index) {
			case 0:
				createAreaEventsReport(INReport);
				break;
			case 1:
				createCoordinatesReport(INReport);
				break;
			case 2:
				getPaths();
				break;
		}
	}

	public void createAreaEventsReport(INReport inReport) {
		INReport.getAreaEvents(floorId, new Date(1428105600) ,new Date(), new OnObjectReadyCallback<List<AreaEvent>>() {
			@Override
			public void onReady(List<AreaEvent> areaEvents) {
				String msg;
				if(areaEvents!= null) {
					ReportUtil.areaEventToCSV(areaEvents);
					msg = "Report created!";
				}
				else {
					msg = "No area events available";
				}
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	public void createCoordinatesReport(INReport inReport) {
		inReport.getCoordinates(floorId, new Date(1428105600), new Date(), new OnObjectReadyCallback<List<Coordinates>>() {
			@Override
			public void onReady(List<Coordinates> coordinates) {
				String msg;
				if(coordinates!= null) {
					ReportUtil.coordinatesToCSV(coordinates);
					msg = "Report created!";
				}
				else {
					msg = "No coordinates available";
				}
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	public void getPaths() {
		INData inData = new INData(inMap,backendServer, "TestAdmin" );
		inData.getPaths(floorId, paths -> {
				Log.i("Indoor", "Received path: " + paths);
			}
		);
	}

	public void getLocalization(int index) {

		verifyBluetoothPermissions();
		switch(index) {
			case 0:
				bluetoothScanService.startLocalization();
				break;
			case 1:
				bluetoothScanService.stopLocalization();
				break;
		}
	}

	public void setNavigationViewListener() {
		expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView, View view, int groupIndex, int itemIndex, long l) {
				mDrawerLayout.closeDrawers();
				switch(groupIndex) {
					case 0:
						drawPoly(itemIndex);
						break;
					case 1:
						drawArea(itemIndex);
						break;
					case 2:
						drawMarker(itemIndex);
						break;
					case 3:
						createReport(itemIndex);
						break;
					case 4:
						getLocalization(itemIndex);
						break;
				}
				expandableListView.collapseGroup(groupIndex);
				return false;
			}
		});
		expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
				return false;
			}
		});
	}

	private void prepareListData() {
		listDataHeader = new ArrayList<>();
		listDataChild = new HashMap<>();

		ExpandedMenuModel item1 = new ExpandedMenuModel();
		item1.setIconName(getString(R.string.draw_polyline));
		item1.setIconImg(R.drawable.polyline);
		listDataHeader.add(item1);

		ExpandedMenuModel item2 = new ExpandedMenuModel();
		item2.setIconName(getString(R.string.draw_area));
		item2.setIconImg(R.drawable.area);
		listDataHeader.add(item2);

		ExpandedMenuModel item3 = new ExpandedMenuModel();
		item3.setIconName(getString(R.string.add_marker));
		item3.setIconImg(R.drawable.marker);
		listDataHeader.add(item3);

		ExpandedMenuModel item4 = new ExpandedMenuModel();
		item4.setIconName(getString(R.string.create_report));
		item4.setIconImg(R.drawable.report);
		listDataHeader.add(item4);

		ExpandedMenuModel item5 = new ExpandedMenuModel();
		item5.setIconName(getString(R.string.localization));
		item5.setIconImg(R.drawable.localization);
		listDataHeader.add(item5);

		List<String> heading1 = new ArrayList<String>();
		heading1.add("Office 1");
		heading1.add("Office 2");
		heading1.add("Office 3");

		List<String> heading2 = new ArrayList<String>();
		heading2.add("Area events");
		heading2.add("Coordinates");
		heading2.add("Paths");

		List<String> heading3 = new ArrayList<String>();
		heading3.add(getString(R.string.start_localization));
		heading3.add(getString(R.string.stop_localization));

		listDataChild.put(listDataHeader.get(0), heading1);
		listDataChild.put(listDataHeader.get(1), heading1);
		listDataChild.put(listDataHeader.get(2), heading1);
		listDataChild.put(listDataHeader.get(3), heading2);
		listDataChild.put(listDataHeader.get(4), heading3);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(menuItem ->  {
			menuItem.setChecked(true);
			mDrawerLayout.closeDrawers();
			return true;
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		startService(BluetoothScanService.class, bluetoothConnection);
	}

	@Override
	public void onPause() {
		super.onPause();
		unbindService(bluetoothConnection);
	}

	private void startService(Class<?> service, ServiceConnection serviceConnection) {
		if (!BluetoothScanService.SERVICE_CONNECTED) {
			Intent bindingIntent = new Intent(this, service);
			bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
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
					Log.d(BluetoothScanService.TAG, "Bluetooth Ready");
					break;
				case BluetoothScanService.ACTION_BLUETOOTH_NOT_SUPPORTED:
					Log.d(BluetoothScanService.TAG, "Bluetooth not supported");
					break;
				case BluetoothScanService.ACTION_BLUETOOTH_NOT_ENABLED:
					Log.d(BluetoothScanService.TAG,"Bluetooth not enable");
					mActivity.get().enableBluetooth();
					break;
				case BluetoothScanService.ACTION_BLUETOOTH_PERMISSION_NOT_GRANTED:
					Log.d(BluetoothScanService.TAG,  "Bluetooth Permission not granted");
					break;
				case BluetoothScanService.ACTION_LOCATION_PERMISSION_NOT_GRANTED:
					Log.d(BluetoothScanService.TAG,  "Location Permission not granted");
					break;
			}
		}
	}

	private void enableBluetooth() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
}
