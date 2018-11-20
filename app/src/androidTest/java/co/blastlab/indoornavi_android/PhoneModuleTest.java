package co.blastlab.indoornavi_android;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import co.blastlab.indoornavi_api.PhoneModule;
import co.blastlab.indoornavi_api.model.Coordinates;
import co.blastlab.indoornavi_api.objects.INMap;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class PhoneModuleTest {

	private MainActivity activity;
	PhoneModule phoneModule;
	String backendServer = "http://192.168.1.29:90";
	INMap inMap;
	int phoneId;

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

	public void INMapCreate() {

		inMap = getActivity().findViewById(R.id.webview);
		Assert.assertNotNull(inMap);
		inMap.createMap( "http://192.168.1.29:4200", "TestAdmin", 200, 200);
		inMap.load(2);
		phoneModule = new PhoneModule(backendServer, inMap);

	}

	@Test
	public void PhoneRegisterTest() throws Exception {

		INMapCreate();
		phoneId = phoneModule.registerPhone("userData");
		Assert.assertNotEquals(phoneId, -1);

	}

	@Test
	public void PhonSaveCoordinatesTest() throws Exception {

		INMapCreate();
		phoneId = phoneModule.registerPhone("userData");
		Boolean success = phoneModule.saveCoordinates(new Coordinates(36,20, 20,  (short) phoneId, new Date()));
		Assert.assertNotNull(success);
	}
}
