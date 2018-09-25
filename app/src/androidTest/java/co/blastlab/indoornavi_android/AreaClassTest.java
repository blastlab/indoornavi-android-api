package co.blastlab.indoornavi_android;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.webkit.ValueCallback;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.blastlab.indoornavi_api.INData;
import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INArea;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.utils.PointsUtil;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class AreaClassTest {

	private MainActivity activity;
	INArea inArea;

	INMap inMap;
	List<Point> points = new ArrayList<>();

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
	public void INAreaCreateTest() {
		final Object syncObject = new Object();

		inMap = getActivity().findViewById(R.id.webview);
		Assert.assertNotNull(inMap);

		points.add(new Point(480, 480));
		points.add(new Point(1220, 480));
		points.add(new Point(1220, 1220));
		points.add(new Point(480, 1220));
		points.add(new Point(750, 750));

		inMap.waitUntilMapReady(data ->
		{
			Thread thread = new Thread(new Runnable() {
				public void run() {
					inArea = new INArea.INAreaBuilder(inMap)
						.setColor(Color.LTGRAY)
						.setPoints(points)
						.setOpacity(0.3)
						.build();

					Assert.assertNotNull(inArea);


					inArea.getID(new OnReceiveValueCallback<Long>() {
						@Override
						public void onReceiveValue(Long aLong) {
							Assert.assertNotNull(aLong);
						}
					});

					inArea.isWithin(new Coordinates(1200, 500, 500, (short) 10999, new Date()), new ValueCallback<Boolean>() {
						@Override
						public void onReceiveValue(Boolean value) {
							Assert.assertTrue(value);

							synchronized (syncObject) {
								syncObject.notify();
							}
						}
					});

				}
			});

			thread.start();
		});

		try {
			synchronized (syncObject) {
				syncObject.wait();
			}
		} catch (Exception e) {
			Log.e("Indoornavi test", "Test fail!");
		}

	}
}