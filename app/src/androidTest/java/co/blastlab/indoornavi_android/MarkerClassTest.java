package co.blastlab.indoornavi_android;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Point;
import android.webkit.ValueCallback;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.objects.INMarker;


import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class MarkerClassTest {

	private MainActivity activity;
	INMarker inMarker;

	INMap inMap;
	Point point;

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
	public void INMarkerCreateTest()
	{
		inMap = getActivity().findViewById(R.id.webview);

		point = new Point(480, 480);

		Thread thread = new Thread(new Runnable() {
			public void run() {
				inMarker = new INMarker.INMarkerBuilder(inMap)
					.setLabel("Label")
					.setIcon("https://cdn0.iconfinder.com/data/icons/small-n-flat/24/678111-map-marker-512.png")
					.point(point)
					.build();

				Assert.assertNotNull(inMarker);

				inMarker.getID(new OnReceiveValueCallback<Long>() {
					@Override
					public void onReceiveValue(Long aLong) {
						Assert.assertNotNull(aLong);
					}
				});

				inMarker.getPoints(new OnReceiveValueCallback<List<Point>>() {
					@Override
					public void onReceiveValue(List<Point> points) {
						Assert.assertNotNull(points);
					}
				});

				inMarker.isWithin(new Coordinates(200, 400, (short) 10999, new Date()), new ValueCallback<Boolean>() {
					@Override
					public void onReceiveValue(Boolean value) {
						Assert.assertNotNull(value);
					}
				});

			}
		});

		thread.start();
	}
}
