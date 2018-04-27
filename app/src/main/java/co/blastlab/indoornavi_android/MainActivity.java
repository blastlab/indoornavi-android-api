package co.blastlab.indoornavi_android;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.blastlab.indoornavi_api.Report;
import co.blastlab.indoornavi_api.callback.OnEventListener;
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INInfoWindow;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INMarker;
import co.blastlab.indoornavi_api.objects.INPolyline;
import co.blastlab.indoornavi_api.callback.OnViewReadyCallback;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class MainActivity extends AppCompatActivity implements OnViewReadyCallback {

	private INMap inMap;
	private INPolyline inPolyline;
	private INArea inArea;
	private INMarker inMarker1;
	private INInfoWindow inInfoWindow;
	Report report;

	Button poly, area, marker, infoWindow, repo;

	List<Point> points  = new ArrayList<>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inMap = (INMap) findViewById(R.id.webview);
		poly = (Button) findViewById(R.id.b_poly);
		area = (Button) findViewById(R.id.b_area);
		marker = (Button) findViewById(R.id.b_marker);
		infoWindow = (Button) findViewById(R.id.b_info);
		repo = (Button) findViewById(R.id.b_report);

		poly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawPoly();
			}
		});
		area.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawArea();
			}
		});
		marker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawMarker();
			}
		});
		infoWindow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawInfoWindow();
			}
		});
		repo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createReport();
			}
		});

		points.add(new Point(480, 480));
		points.add(new Point(1220, 480));
		points.add(new Point(1220,1220));
		points.add(new Point(480,1220));
		points.add(new Point(750,750));
	}

	public void onWebViewReady(INMap mapView) {
		inMap.createMap("http://192.168.1.18:4200", "TestAdmin", 1200, 850);
		inMap.load(2, new OnObjectReadyCallback() {
			@Override
			public void onReady(Object o) {
				inMap.toggleTagVisibility((short)10999);
				inMap.addEventListener(INMap.AREA, new OnEventListener() {
					@Override
					public void onEvent(Object o) {
						Log.e("Indoor", "aaaaaa, ratunku!");
					}
				});
			}
		});
	}

	public void drawPoly()
	{

		inPolyline = new INPolyline(inMap);
		inPolyline.ready(new OnObjectReadyCallback<String>() {
			public void onReady(String result) {
				inPolyline.points(points);
				inPolyline.setLineColor("#12a3b5");
				inPolyline.draw();
				inPolyline.getID(new OnReceiveValueCallback<Long>() {
					@Override
					public void onReceiveValue(Long id) {
						Log.i("Indoor", "onReceiveValue: " + id);
					}
				});

				inPolyline.getPoints(new OnReceiveValueCallback<List<Point>>() {
					@Override
					public void onReceiveValue(List<Point> points) {

					}
				});
			}
		});
	}

	public void drawArea(){
		inArea = new INArea(inMap);
		inArea.ready(new OnObjectReadyCallback<String>() {
			@Override
			public void onReady(String result) {
				inArea.points(points);
				inArea.setFillColor("rgb(144,224,017)");
				inArea.setOpacity((float)0.3);
				inArea.draw();
			}
		});
	}

	public void drawMarker(){
		inMarker1 = new INMarker(inMap);
		inMarker1.ready(new OnObjectReadyCallback<String>() {
			@Override
			public void onReady(String result) {
				inMarker1.point(new Point(600, 600));
				inMarker1.setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png");
				inMarker1.setLabel("Lorem Ipsum");
				inMarker1.addEventListener(new OnMarkerClickListener() {
					@Override
					public void onClick() {
						show_toast();
					}
				});
				inMarker1.draw();
			}
		});
	}

	public void drawInfoWindow(){
		inInfoWindow = new INInfoWindow(inMap);
		inInfoWindow.ready(new OnObjectReadyCallback<String>() {
			@Override
			public void onReady(String result) {
				inInfoWindow.setInnerHTML("<h2>Lorem ipsum dolor sit amet</h2>");
				inInfoWindow.setPosition(INInfoWindow.TOP);
				inInfoWindow.height(200);
				inInfoWindow.width(100);
				inInfoWindow.open(inMarker1);
			}
		});
	}

	public void show_toast()
	{
		Toast.makeText(this, "This is my toast",
			Toast.LENGTH_LONG).show();
	}

	public void createReport() {
		report = new Report(inMap, "http://192.168.1.18:90", "TestAdmin");
		report.getAreaEvents(2, new Date(1428105600), new Date(), new OnObjectReadyCallback<List<AreaEvent>>() {
				@Override
				public void onReady(List<AreaEvent> areaEvents) {
					ReportUtil.areaEventToCSV(areaEvents);
			}
		});

		report.getCoordinates(2, new Date(1428105600), new Date(), new OnObjectReadyCallback<List<Coordinates>>() {
			@Override
			public void onReady(List<Coordinates> coordinates) {
				ReportUtil.coordinatesToCSV(coordinates);
			}
		});
	}
}
