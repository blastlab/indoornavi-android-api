package co.blastlab.indoornavi_android;

import android.app.Instrumentation;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Point;
import android.support.test.runner.AndroidJUnit4;
import android.webkit.ValueCallback;


import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INPolyline;

import static android.support.test.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
public class PolylineClassTest {

	private MainActivity activity;
	INPolyline inPolyline;

	INMap inMap;
	List<Point> points  = new ArrayList<>();

	public MainActivity getActivity() {
		if (activity == null) {
			Intent intent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
			getInstrumentation().getTargetContext().startActivity(intent);
			activity = (MainActivity) getInstrumentation().waitForMonitor(monitor);
		}
		Assert.assertNotNull(activity);
		return activity;
	}

	@Test
	public void INMapCreateTest() {

		inMap = getActivity().findViewById(R.id.webview);
		Assert.assertNotNull(inMap);
	}

	@Test
	public void INPolylineCreateTest()
	{
		inMap = getActivity().findViewById(R.id.webview);

		points.add(new Point(480, 480));
		points.add(new Point(1220, 480));
		points.add(new Point(1220,1220));
		points.add(new Point(480,1220));
		points.add(new Point(750,750));

		inMap.waitUntilMapReady( data -> {

			Thread thread = new Thread(new Runnable() {
				public void run() {
					inPolyline = new INPolyline.INPolylineBuilder(inMap)
						.setLineColor(Color.GREEN)
						.points(points)
						.build();

					Assert.assertNotNull(inPolyline);

					inPolyline.getID(new OnReceiveValueCallback<Long>() {
						@Override
						public void onReceiveValue(Long aLong) {
							Assert.assertNotNull(aLong);
						}
					});

					inPolyline.getPoints(new OnReceiveValueCallback<List<Point>>() {
						@Override
						public void onReceiveValue(List<Point> points) {
							Assert.assertNotNull(points);
						}
					});

					inPolyline.isWithin(new Coordinates(200, 400, (short) 10999, new Date()), new ValueCallback<Boolean>() {
						@Override
						public void onReceiveValue(Boolean value) {
							Assert.assertNotNull(value);
						}
					});
				}
			});

			thread.start();
		});
	}
}


