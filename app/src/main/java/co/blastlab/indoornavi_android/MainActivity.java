package co.blastlab.indoornavi_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.blastlab.indoornavi_api.INMap;
import co.blastlab.indoornavi_api.callback.OnViewReadyCallback;

public class MainActivity extends AppCompatActivity implements OnViewReadyCallback {

	private INMap inMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inMap = (INMap) findViewById(R.id.webview);
	}

	@Override
	public void onWebViewReady(INMap mapView) {
		inMap.createMap("http://192.168.1.18:4200", "TestAdmin");
		inMap.load(2);
	}
}
