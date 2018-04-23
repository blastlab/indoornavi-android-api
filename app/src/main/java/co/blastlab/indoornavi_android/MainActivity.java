package co.blastlab.indoornavi_android;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jdeferred.DoneCallback;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.callback.OnMarkerClickListener;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INInfoWindow;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INMarker;
import co.blastlab.indoornavi_api.objects.INPolyline;
import co.blastlab.indoornavi_api.callback.OnViewReadyCallback;

public class MainActivity extends AppCompatActivity implements OnViewReadyCallback {

	private INMap inMap;
	private INPolyline inPolyline;
	private INArea inArea;
	private INMarker inMarker1, inMarker2;
	private INInfoWindow inInfoWindow;

	Button poly, area, marker, infoWindow;

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


		points.add(new Point(480, 480));
		points.add(new Point(1220, 480));
		points.add(new Point(1220,1220));
		points.add(new Point(480,1220));
		points.add(new Point(750,750));
	}

	@Override
	public void onWebViewReady(INMap mapView) {
		inMap.createMap("http://192.168.1.18:4200", "TestAdmin");
		inMap.load(2);
	}

	public void drawPoly()
	{

		inPolyline = new INPolyline(inMap);
		inPolyline.ready(new DoneCallback<String>() {
			public void onDone(String result) {
				inPolyline.points(points);
				inPolyline.setLineColor("#12a3b5");
				inPolyline.draw();
				/*inPolyline.getID(new GetIdCallback() {
					@Override
					public void onReceiveId(Integer id) {
						Log.i(Constants.LOG, "onReceiveValue: " + id);
					}
				});*/

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
		inArea.ready(new DoneCallback<String>() {
			@Override
			public void onDone(String result) {
				inArea.points(points);
				inArea.setFillColor("rgb(144,224,017)");
				inArea.setOpacity((float)0.3);
				inArea.draw();
			}
		});
	}

	public void drawMarker(){
		inMarker1 = new INMarker(inMap);
		inMarker1.ready(new DoneCallback<String>() {
			@Override
			public void onDone(String result) {
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
		inInfoWindow.ready(new DoneCallback<String>() {
			@Override
			public void onDone(String result) {
				inInfoWindow.setInnerHTML("<h2>Lorem ipsum dolor sit amet</h2>");
				inInfoWindow.setPosition(INInfoWindow.Position.TOP);
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
}
