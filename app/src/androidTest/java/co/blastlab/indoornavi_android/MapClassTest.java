package co.blastlab.indoornavi_android;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MapClassTest {

	MainActivity activity;

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
	public void useAppContext() throws Exception {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getTargetContext();

		assertEquals("co.blastlab.indoornavi_android", appContext.getPackageName());
	}

	@Test
	public void assertTest() {
		Context appContext = InstrumentationRegistry.getTargetContext();
		try {
			InputStream is = appContext.getAssets().open("index.html");
			Assert.assertNotNull(is);

			InputStream is1 = appContext.getAssets().open("indoorNavi.js");
			Assert.assertNotNull(is1);
		}
		catch (Exception e) {
			new AssertionError("Open file from assets error");
		}
	}
}
