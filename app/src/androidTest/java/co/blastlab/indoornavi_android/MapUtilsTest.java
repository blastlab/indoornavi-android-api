package co.blastlab.indoornavi_android;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Point;

import org.junit.Assert;
import org.junit.Test;

import co.blastlab.indoornavi_api.model.Scale;
import co.blastlab.indoornavi_api.objects.INMap;
import co.blastlab.indoornavi_api.utils.MapUtil;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class MapUtilsTest {

	private MainActivity activity;

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

	@Test
	public void ScaleTest() {
		inMap = getActivity().findViewById(R.id.webview);

		inMap.waitUntilMapReady( data -> {

			inMap.scale = MapUtil.stringToScale("{measure: \"CENTYIMETERS\", realDistance: 100, start: {x: 0, y: 0}, stop: {x: 0, y: 250}}");
			Assert.assertNotNull(inMap.scale);
			Assert.assertEquals(inMap.scale.measure, Scale.Measure.CENTIMETERS);
			Assert.assertEquals(inMap.scale.realDistance, 100);
			Assert.assertEquals(inMap.scale.start.x, 0);
			Assert.assertEquals(inMap.scale.start.y, 0);
			Assert.assertEquals(inMap.scale.stop.x, 0);
			Assert.assertEquals(inMap.scale.stop.y, 250);
		});

	}

	@Test
	public void pixelsToRealDimensionsTest() {
		ScaleTest();

		Point point = new Point(10, 10);

		inMap.waitUntilMapReady( data -> {
			Assert.assertEquals(MapUtil.pixelsToRealDimensions(inMap.scale, point).x, 4);
			Assert.assertEquals(MapUtil.pixelsToRealDimensions(inMap.scale, point).y, 4);
		});
	}

	@Test
	public void realDimensionsToPixelsTest() {
		ScaleTest();

		Point point = new Point(10, 10);

		inMap.waitUntilMapReady( data -> {
			Assert.assertEquals(MapUtil.realDimensionsToPixels(inMap.scale, point).x, 25);
			Assert.assertEquals(MapUtil.realDimensionsToPixels(inMap.scale, point).x, 25);
		});
	}
}
