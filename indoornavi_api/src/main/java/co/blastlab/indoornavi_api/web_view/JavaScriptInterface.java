package co.blastlab.indoornavi_api.web_view;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {

	Context mContext;

	public JavaScriptInterface(Context c) {
		mContext = c;
	}

	@JavascriptInterface
	public void toggle10999() {
		Toast.makeText(mContext, "toggle 10999", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void drawINPolyline() {
		Toast.makeText(mContext, "draw polyline", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void removeINPolyline() {
		Toast.makeText(mContext, "remove poly", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void drawINArea() {
		Toast.makeText(mContext, "draw area", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void removeINArea() {
		Toast.makeText(mContext, "remove area", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void addListener() {
		Toast.makeText(mContext, "add listner", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void addINMarker_1() {
		Toast.makeText(mContext, "add marker 1", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void removeINMarker_1() {
		Toast.makeText(mContext, "remove Marker 1", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void addINMarker_2() {
		Toast.makeText(mContext, "add marker 2", Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void removeINMarker_2() {
		Toast.makeText(mContext, "remove marker 2", Toast.LENGTH_SHORT).show();
	}

}