package co.blastlab.indoornavi_android;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import org.junit.Assert;

import co.blastlab.indoornavi_api.callback.OnReceiveValueCallback;
import co.blastlab.indoornavi_api.model.Border;
import co.blastlab.indoornavi_api.objects.INCircle;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.utils.PointsUtil;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class CircleClassTest {
	private MainActivity activity;
	INCircle inCircle;

	INMap inMap;


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

	public void checkCircleTest() {

		inMap = getActivity().findViewById(R.id.webview);
		Assert.assertNotNull(inMap);

		inCircle = new INCircle.INCircleBuilder(inMap)
			.setPosition(new Point(400, 400))
			.setRadius(30)
			.setOpacity(0.3)
			.setColor(Color.RED)
			.setBorder(new Border(30, Color.GREEN))
			.build();

		inCircle.getBorder(new OnReceiveValueCallback<Border>() {
			@Override
			public void onReceiveValue(Border border) {
				Log.i("Indoor", "onReceiveValue: " + border.color + border.width);
			}
		});

		inCircle.getColor(new OnReceiveValueCallback<Integer>() {
			@Override
			public void onReceiveValue(Integer color) {
				Log.i("Indoor", "onReceiveValue: " + color);
			}
		});

		inCircle.getOpacity(new OnReceiveValueCallback<String>() {
			@Override
			public void onReceiveValue(String s) {
				Log.i("Indoor", "onReceiveValue: " + s);
			}
		});

		inCircle.getPosition(new OnReceiveValueCallback<Point>() {
			@Override
			public void onReceiveValue(Point point) {
				Log.i("Indoor", "onReceiveValue: " + PointsUtil.pointToString(point));
			}
		});

		inCircle.getRadius(new OnReceiveValueCallback<Integer>() {
			@Override
			public void onReceiveValue(Integer integer) {
				Log.i("Indoor", "onReceiveValue: " + integer);
			}
		});
	}

}
