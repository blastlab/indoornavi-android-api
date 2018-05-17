package co.blastlab.indoornavi_android;

import android.graphics.Color;
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
import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.callback.OnObjectReadyCallback;
import co.blastlab.indoornavi_api.model.AreaEvent;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INInfoWindow;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INMarker;
import co.blastlab.indoornavi_api.objects.INPolyline;
import co.blastlab.indoornavi_api.callback.OnViewReadyCallback;
import co.blastlab.indoornavi_api.utils.PointsUtil;
import co.blastlab.indoornavi_api.utils.ReportUtil;

public class DemoMainActivity extends AppCompatActivity implements OnViewReadyCallback {

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
	}

	public void drawMarker(){
		inMarker1 = new INMarker.INMarkerBuilder(inMap)
			.point(new Point(600, 600))
			.setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
			.setLabel("This is label")
			.build();

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
		report = new Report(inMap, "http://192.168.1.18:90", "TestAdmin");
		report.getAreaEvents(2, new Date(1428105600) ,new Date(), new OnObjectReadyCallback<List<AreaEvent>>() {
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
