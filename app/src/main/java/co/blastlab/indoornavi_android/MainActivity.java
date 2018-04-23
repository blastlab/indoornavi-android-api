package co.blastlab.indoornavi_android;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.jdeferred.DoneCallback;

import java.util.ArrayList;
import java.util.List;

import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INPolyline;
import co.blastlab.indoornavi_api.callback.OnViewReadyCallback;

public class MainActivity extends AppCompatActivity implements OnViewReadyCallback {

	private INMap inMap;
	private INPolyline inPolyline;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inMap = (INMap) findViewById(R.id.webview);
		button = (Button) findViewById(R.id.button);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawPoly();
			}
		});
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
				List<Point> list = new ArrayList<>();
				list.add(new Point(480, 480));
				list.add(new Point(1220, 480));
				list.add(new Point(1220,1220));
				list.add(new Point(480,1220));
				list.add(new Point(750,750));

				inPolyline.points(list);
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
}
