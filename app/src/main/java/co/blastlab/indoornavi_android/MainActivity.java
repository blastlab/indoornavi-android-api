package co.blastlab.indoornavi_android;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.blastlab.indoornavi_api.INReport;
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
import co.blastlab.indoornavi_api.utils.PointsUtil;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class MainActivity extends AppCompatActivity implements OnINMapReadyCallback {

	private INMap inMap;
	private INPolyline inPolyline;
	private INArea inArea;
	private INMarker inMarker1;
	private INInfoWindow inInfoWindow;

	private DrawerLayout mDrawerLayout;

	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static final int REQUEST_INTERNET= 1;
	private static String[] PERMISSIONS= {
		Manifest.permission.INTERNET,
		Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	INReport INReport;

	Button poly, area, marker, infoWindow, repo;

	List<Point> points  = new ArrayList<>();
	private Point lastTouch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inMap = (INMap) findViewById(R.id.webview);


		points.add(new Point(480, 480));
		points.add(new Point(1220, 480));
		points.add(new Point(1220,1220));
		points.add(new Point(480,1220));
		points.add(new Point(750,750));


		mDrawerLayout = findViewById(R.id.drawer_layout);

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(
			new NavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(MenuItem menuItem) {
					mDrawerLayout.closeDrawers();
					switch (menuItem.getItemId()) {
						case R.id.draw_polyline:
							drawPoly();
							return true;
						case R.id.draw_area:
							drawArea();
							return true;
						case R.id.draw_marker:
							drawMarker();
							return true;
						case R.id.create_report:
							createReport();
							return true;
					}

					return true;
				}
			});

		inMap.setOnLongClickListener(new WebView.OnLongClickListener() {

			public boolean onLongClick(View v) {
				WebView.HitTestResult hr = ((WebView)v).getHitTestResult();
				Log.i("Indoor", "x = "+ lastTouch.x + ", y =" + lastTouch.y);

				INMarker marker = new INMarker.INMarkerBuilder(inMap)
					.point(lastTouch)
					.build();

				int[] position = new int[2];
				inMap.getLocationOnScreen(position);

				Log.i("Indoor", "getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType());
				return false;
			}
		});

		inMap.setOnTouchListener(new WebView.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				lastTouch = new Point(Math.round(event.getX()*2), Math.round(event.getY()*2));
				return false;
			}

		});

	}

	public static void verifyStoragePermissions(Activity activity) {
		int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_EXTERNAL_STORAGE);
		}
	}

	public static void verifyInternetPermissions(Activity activity) {
		int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_INTERNET);
		}
	}

	public void onINMapReady(INMap mapView) {
		inMap.createMap("http://192.168.1.18:4200", "TestAdmin", 1200, 850);
		inMap.load(2);
	}

	public void drawPoly()
	{
		inPolyline = new INPolyline.INPolylineBuilder(inMap)
			.points(points)
			.setLineColor(Color.LTGRAY)
			.build();

		inPolyline.getID(id -> { Log.i("Indoor", "onReceiveValue: " + id); });
		inPolyline.getPoints(points -> Log.i("Indoor", "onReceiveValue: " +  PointsUtil.pointsToString(points)));
	}

	public void drawArea(){
		inArea = new INArea.INAreaBuilder(inMap)
			.points(points)
			.setFillColor(Color.GREEN)
			.setOpacity(0.3)
			.build();

		inArea.isWithin(new Coordinates(200, 800, (short)109999, new Date()), bool -> Log.i("Indoor", "Received value: " + bool));
	}

	public void drawMarker(){
		inMarker1 = new INMarker.INMarkerBuilder(inMap)
			.point(new Point(600, 600))
			.setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
			.setLabel("This is label")
			.build();

		drawInfoWindow();

		inMarker1.addEventListener(new OnMarkerClickListener() {
			@Override
			public void onClick() {
				show_toast();
			}
		});
	}

	public void drawInfoWindow(){
		inInfoWindow = new INInfoWindow.INInfoWindowBuilder(inMap)
			.height(40)
			.width(40)
			.setInnerHTML("<h2>Lorem ipsum dolor sit amet</h2>")
			.setPosition(INInfoWindow.TOP)
			.build();
	}

	public void show_toast()
	{
		Toast.makeText(this, "This is my toast",
			Toast.LENGTH_LONG).show();
		inMarker1.addInfoWindow(inInfoWindow);
	}

	public void createReport() {
		verifyStoragePermissions(this);

		INReport = new INReport(inMap, "http://192.168.1.18:90", "TestAdmin");
		INReport.getAreaEvents(2, new Date(1428105600) ,new Date(), new OnObjectReadyCallback<List<AreaEvent>>() {
				@Override
				public void onReady(List<AreaEvent> areaEvents) {
					ReportUtil.areaEventToCSV(areaEvents);
			}
		});

		INReport.getCoordinates(2, new Date(1428105600), new Date(), new OnObjectReadyCallback<List<Coordinates>>() {
			@Override
			public void onReady(List<Coordinates> coordinates) {
				ReportUtil.coordinatesToCSV(coordinates);
			}
		});
	}
}
