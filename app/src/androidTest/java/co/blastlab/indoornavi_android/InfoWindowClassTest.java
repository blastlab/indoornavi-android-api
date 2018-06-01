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
import co.blastlab.indoornavi_api.objects.INInfoWindow;
import co.blastlab.indoornavi_api.objects.INMap;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class InfoWindowClassTest {

	private MainActivity activity;
	INInfoWindow inInfoWindow;

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
	public void INPolylineCreateTest()
	{
		inMap = getActivity().findViewById(R.id.webview);

		point = new Point(480, 480);

		Thread thread = new Thread(new Runnable() {
			public void run() {
				inInfoWindow = new INInfoWindow.INInfoWindowBuilder(inMap)
					.height(40)
					.width(40)
					.setInnerHTML("<h2>Lorem ipsum dolor sit amet</h2>")
					.setPosition(INInfoWindow.TOP)
					.build();

				Assert.assertNotNull(inInfoWindow);

				inInfoWindow.getID(new OnReceiveValueCallback<Long>() {
					@Override
					public void onReceiveValue(Long aLong) {
						Assert.assertNotNull(aLong);
					}
				});

				inInfoWindow.getPoints(new OnReceiveValueCallback<List<Point>>() {
					@Override
					public void onReceiveValue(List<Point> points) {
						Assert.assertNotNull(points);
					}
				});

				inInfoWindow.isWithin(new Coordinates(200, 400, (short) 10999, new Date()), new ValueCallback<Boolean>() {
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
